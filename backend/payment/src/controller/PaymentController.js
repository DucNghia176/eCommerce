const paymentService = require('../service/PaymentService');
const stripe = require('../config/stripe');

class PaymentController {
    async createPayment(req, res) {
        try {
            const {orderId} = req.params;
            const amount = req.query.amount || req.query.totalAmount;
            const result = await paymentService.createPaymentIntent(orderId, amount);

            res.status(200).json({
                code: 200,
                message: 'Tạo phiên thanh toán thành công',
                data: result
            });
        } catch (error) {
            res.status(400).json({
                code: 400,
                message: error.message
            });
        }
    }

    async checkout(req, res) {
        try {
            const {orderId, amount} = req.body || {};
            if (!orderId) {
                return res.status(400).json({
                    code: 400,
                    message: 'Thiếu orderId để tạo phiên thanh toán'
                });
            }

            const result = await paymentService.createPaymentIntent(orderId, amount);
            res.status(200).json({
                code: 200,
                message: 'Tạo phiên thanh toán thành công',
                data: result
            });
        } catch (error) {
            res.status(400).json({
                code: 400,
                message: error.message
            });
        }
    }

    async extractAmount(req, res) {
        try {
            let {userIds} = req.query;

            if (!userIds) return res.json({});

            if (!Array.isArray(userIds)) {
                userIds = [userIds];
            }

            userIds = userIds
                .map(id => Number(id))
                .filter(id => Number.isFinite(id));

            if (userIds.length === 0) return res.json({});

            const data = await paymentService.extractAmount(userIds);

            return res.json(data);

        } catch (err) {
            console.error(err);
            return res.status(500).json({message: "Server error"});
        }
    }

    async extractPaymentStatus(req, res) {
        try {
            let {orderIds} = req.query;
            console.log("RAW orderIds =", orderIds);

            if (!orderIds) {
                return res.json({});
            }

            if (!Array.isArray(orderIds)) {
                orderIds = [orderIds];
            }

            const parsedIds = orderIds
                .map(id => Number(id))
                .filter(id => Number.isFinite(id));

            // Nếu sau khi lọc không còn id hợp lệ → trả map rỗng
            if (parsedIds.length === 0) {
                return res.json({});
            }

            const data = await paymentService.extractStatus(parsedIds);

            return res.json(data);
        } catch (err) {
            console.error("extractPaymentStatusQuery error:", err);
            return res.status(500).json({message: "Server error"});
        }
    }


    async handleWebhook(req, res) {
        const sig = req.headers['stripe-signature'];
        let event;

        try {
            event = stripe.webhooks.constructEvent(
                req.body,
                sig,
                process.env.STRIPE_WEBHOOK_SECRET
            );
        } catch (err) {
            console.error('Webhook signature error:', err.message);
            return res.status(400).send('Webhook Error: ');
        }

        if (event.type === 'checkout.session.completed') {
            const session = event.data.object;
            try {
                await paymentService.handlePaymentSuccess(session.client_reference_id, session.id);
            } catch (err) {
                console.error('Payment processing error:', err.message);
            }
        }

        res.json({received: true});
    }

    async paymentSuccess(req, res) {
        const {orderId, session_id} = req.query;

        try {
            const result = await paymentService.handlePaymentSuccess(orderId, session_id);

            res.json({
                code: 200,
                message: 'Thanh toán thành công',
                data: result
            });
        } catch (err) {
            res.status(400).json({code: 400, message: err.message});
        }
    }

    async paymentCancel(req, res) {
        try {
            const {orderId} = req.query;

            const result = await paymentService.handlePaymentCancel(orderId);

            res.status(200).json({
                code: 200,
                message: 'Hủy thanh toán thành công',
                data: result
            });
        } catch (err) {
            console.error(err);
            res.status(400).json({
                code: 400,
                message: err.message
            });
        }
    }

}

module.exports = new PaymentController();