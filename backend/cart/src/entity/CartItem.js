const {EntitySchema} = require('typeorm');

module.exports = new EntitySchema({
    name: 'CartItem',
    tableName: 'CART_ITEM',
    columns: {
        id: {
            type: Number,
            primary: true,
            name: 'ID',
        },
        cartId: {
            type: Number,
            nullable: false,
            name: 'CART_ID',
        },
        productId: {
            type: Number,
            nullable: false,
            name: 'PRODUCT_ID',
        },
        quantity: {
            type: Number,
            name: 'QUANTITY',
        },
        unitPrice: {
            type: 'number',
            precision: 38,
            scale: 2,
            name: 'UNIT_PRICE',
        },
        discount: {
            type: 'number',
            precision: 38,
            scale: 2,
            name: 'DISCOUNT',
        },
    },
    relations: {
        cart: {
            type: 'many-to-one',
            target: 'Cart',
            joinColumn: {
                name: 'CART_ID',
            },
            inverseSide: 'items',
        },
    },
});