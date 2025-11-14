const {EntitySchema} = require('typeorm');

module.exports = new EntitySchema({
    name: 'Cart',
    tableName: 'CART',
    columns: {
        id: {
            type: Number,
            primary: true,
            name: 'ID',
        },
        userId: {
            type: Number,
            nullable: false,
            name: "USER_ID",
        },
        createdAt: {
            type: 'timestamp',
            name: 'CREATED_AT',
            createDate: true,
            default: () => 'CURRENT_TIMESTAMP',
        },
        updatedAt: {
            type: 'timestamp',
            name: 'UPDATED_AT',
            updateDate: true,
            onUpdate: () => 'CURRENT_TIMESTAMP',
        },
    },
    relations: {
        items: {
            type: 'one-to-many',
            target: 'CartItem',
            inverseSide: 'cart',
            cascade: true,
        },
    },
});
