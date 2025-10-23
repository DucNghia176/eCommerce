const {EntitySchema} = require('typeorm');

module.exports = new EntitySchema({
    name: 'Inventory',
    tableName: 'INVENTORY',
    columns: {
        skuCode: {
            type: String,
            primary: true,
            name: 'SKU_CODE',
        },
        productId: {
            type: Number,
            name: 'PRODUCT_ID',
        },
        name: {
            type: String,
            name: "NAME",
        },
        quantity: {
            type: Number,
            name: "QUANTITY",
            default: 0,
        },
        reservedQuantity: {
            type: Number,
            name: "RESERVED_QUANTITY",
            default: 0,
        },
        importPrice: {
            type: Number,
            name: "IMPORT_PRICE",
        },
        importAt: {
            type: 'timestamp',
            name: "IMPORT_PRICE",
            createDate: true,
            default: () => 'CURRENT_TIMESTAMP',
        }
    }
});
