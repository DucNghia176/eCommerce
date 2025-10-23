const inventoryService = require('../service/InventoryService');

class InventoryController {
    async isInStock(req, res) {
        try {
            const {skuCode, quantity} = req.body;
            const inStock = await inventoryService.isInStock(skuCode, quantity);
            res.status(200).json({
                code: 200,
                message: inStock ? 'Còn hàng' : 'Hết hàng',
                data: inStock
            });
        } catch (e) {
            res.status(400).json({code: 400, message: e.message});
        }
    }

    async checkQuantity(req, res) {
        try {
            const {productId, quantity} = req.query;
            const enough = await inventoryService.checkQuantity(productId, quantity);
            res.status(200).json(enough);
        } catch (e) {
            res.status(400).json({code: 400, message: e.message});
        }
    }

    async importQuantity(req, res) {
        try {
            const result = await inventoryService.importQuantity(req.body);
            res.status(200).json({
                code: 200,
                message: 'Thêm số lượng thành công',
                data: result
            });
        } catch (e) {
            res.status(400).json({code: 400, message: e.message});
        }
    }

    async updateInventoryForCart(req, res) {
        try {
            const result = await inventoryService.updateInventoryForCart(req.body);
            res.status(200).json({
                code: 200,
                message: 'Có thể thêm vào giỏ hàng',
                data: result
            });
        } catch (e) {
            res.status(400).json({code: 400, message: e.message});
        }
    }

    async getQuantity(req, res) {
        try {
            const {skuCode} = req.params;
            const quantity = await inventoryService.getQuantity(skuCode);
            res.status(200).json({
                code: 200,
                message: 'Lấy số lượng thành công',
                data: quantity
            });
        } catch (e) {
            res.status(400).json({code: 400, message: e.message});
        }
    }


    async extractSkuCodes(req, res) {
        try {
            const skuCodes = Array.isArray(req.query.skuCodes)
                ? req.query.skuCodes
                : [req.query.skuCodes];
            const result = await inventoryService.extractSkuCodes(skuCodes);
            res.status(200).json(result);
        } catch (e) {
            res.status(400).json({code: 400, message: e.message});
        }
    }
}

module.exports = new InventoryController();
