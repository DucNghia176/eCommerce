const express = require('express');
const router = express.Router();
const paymentController = require('../controller/PaymentController');
const bodyParser = require('body-parser');

router.post('/checkout', paymentController.checkout);
router.get('/success', paymentController.paymentSuccess);
router.get('/cancel', paymentController.paymentCancel);
router.get('/amount', paymentController.extractAmount);
router.get("/orders", (req, res) => paymentController.extractPaymentStatus(req, res));
router.get('/:orderId', paymentController.createPayment);

router.post(
    '/webhook',
    bodyParser.raw({type: 'application/json'}),
    paymentController.handleWebhook
);

module.exports = router;