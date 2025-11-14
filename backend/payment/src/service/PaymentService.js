const paymentRepository = require('../repo/PaymentRepository');
const redisClient = require('../config/redis');
const CACHE_PREFIX = 'cache:';
const stripe = require('../config/stripe');
const orderClient = require('../client/OrderClient');

class PaymentService {
    async createCheckoutSession(orderId, amount) {
        try {
            const amountInCents = Math.round(Number(amount));
            const successUrl = `http://localhost:4200/user/payment-success?orderId=${orderId}&session_id={CHECKOUT_SESSION_ID}`;
            const cancelUrl = `http://localhost:4200/user/payment-cancel?orderId=${orderId}&session_id={CHECKOUT_SESSION_ID}`;
            console.log('Stripe success_url =', successUrl);
            console.log('Stripe cancel_url  =', cancelUrl);
            const session = await stripe.checkout.sessions.create({
                mode: 'payment',
                success_url: successUrl,
                cancel_url: cancelUrl,
                line_items: [
                    {
                        quantity: 1,
                        price_data: {
                            currency: 'vnd',
                            unit_amount: amountInCents,
                            product_data: {
                                name: `Order #${orderId}`,
                            },
                        },
                    },
                ],
                metadata: {orderId: orderId.toString()}
            });

            return session.url;
        } catch (error) {
            console.error('Lỗi khi tạo Checkout Session:', error);
            throw new Error('Không thể tạo session thanh toán');
        }
    }

    async createPaymentIntent(orderId, totalAmount) {
        if (!orderId) {
            throw new Error('Thiếu orderId');
        }

        const numericOrderId = Number(orderId);
        const resolvedAmount = await this.resolveAmount(numericOrderId, totalAmount);
        const checkoutUrl = await this.createCheckoutSession(numericOrderId, resolvedAmount);
        return {
            orderId: numericOrderId,
            totalAmount: resolvedAmount,
            checkoutUrl,
        };
    }


    async handlePaymentSuccess(orderId, sessionId) {
        try {
            // Lấy lại thông tin order
            const order = await orderClient.getOrderById(orderId);

            // Tạo payment entity
            const payment = ({
                orderId: order.orderId,
                orderCode: order.orderCode,
                userId: order.userId,
                amountPaid: order.totalAmount,
                status: 'SUCCESS',
                paymentMethod: 'PAYPAL',
                paymentDate: new Date()
            });

            // Lưu vào DB
            await paymentRepository.save(payment);

            await orderClient.updateOrderStatus(orderId, 'CONFIRMED');

            return {message: 'Thanh toán thành công', orderId: orderId};
        } catch (error) {
            console.error('Lỗi khi lưu thanh toán:', error);
            throw new Error('Không thể lưu thông tin thanh toán');
        }
    }

    async handlePaymentCancel(orderId) {
        try {
            const order = await orderClient.getOrderById(orderId);
            if (!order) {
                throw new Error(`Không tìm thấy orderId ${orderId}`);
            }

            const resolvedOrderId = order.id ?? order.orderId;

            // Lưu trạng thái Payment
            const payment = {
                orderId: resolvedOrderId,
                orderCode: order.orderCode,
                userId: order.userId,
                amountPaid: Number(order.totalAmount || 0),
                status: 'CANCELLED',
                paymentMethod: 'PAYPAL',
                paymentDate: new Date()
            };

            await paymentRepository.save(payment);

            // Update status order
            await orderClient.updateOrderStatus(resolvedOrderId, 'FAILED');

            return {
                message: 'Thanh toán bị hủy',
                orderId: resolvedOrderId
            };

        } catch (err) {
            console.error('Lỗi khi xử lý hủy thanh toán:', err);
            throw new Error('Không thể xử lý hủy thanh toán');
        }
    }


    async resolveAmount(orderId, amount) {
        const numericAmount = Number(amount);
        if (!Number.isNaN(numericAmount) && numericAmount > 0) {
            return numericAmount;
        }

        const order = await orderClient.getOrderById(orderId);
        if (!order || typeof order.totalAmount === 'undefined') {
            throw new Error('Không thể xác định tổng tiền của đơn hàng');
        }

        return Number(order.totalAmount);
    }

    async extractAmount(userIds) {
        const results = await paymentRepository.totalAmountByUserIds(userIds);

        const response = {};
        results.forEach(r => {
            response[r.userId] = Number(r.totalAmount);
        });

        return response;
    }

    async extractStatus(orderIds) {
        try {
            const payments = await paymentRepository.findByOrderIds(orderIds);

            const responses = {};
            payments.forEach(p => {
                responses[p.orderId] = p.status;
            });

            return responses;
        } catch (err) {
            console.error("DB error in extractStatus:", err);
            return {};
        }
    }
}

module.exports = new PaymentService();