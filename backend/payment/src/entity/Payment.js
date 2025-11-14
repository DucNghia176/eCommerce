const {EntitySchema} = require('typeorm');

module.exports = new EntitySchema({
    name: 'Payment',
    tableName: 'PAYMENT',
    columns: {
        skuCode: {
            type: Number,
            primary: true,
            name: 'PAYMENT_ID',
        },
        orderId: {
            type: Number,
            name: 'ORDER_ID',
        },
        orderCode: {
            type: String,
            name: 'ORDER_CODE',
        },
        userId: {
            type: Number,
            name: 'USER_ID',
        },
        paymentDate: {
            type: 'timestamp',
            name: "PAYMENT_DATE",
        },
        amountPaid: {
            type: Number,
            name: "AMOUNT_PAID",
        },
        paymentMethod: {
            type: String,
            name: "PAYMENT_METHOD",
        },
        status: {
            type: String,
            name: "STATUS",
        },
        createdAt: {
            type: 'timestamp',
            name: "CREATED_AT",
            createDate: true,
            default: () => 'CURRENT_TIMESTAMP',
        },
        updatedAt: {
            type: 'timestamp',
            name: "UPDATED_AT",
            updatedAt: true,
            onUpdate: () => 'CURRENT_TIMESTAMP',
        }
    }
});
