const inventoryRepository = require('../repo/InventoryRepository');
const redisClient = require('../config/redis');
const CACHE_PREFIX = 'cache:';

class InventoryService {
    async isInStock(skuCode, quantity) {
        const inventory = await inventoryRepository.findOneBy({skuCode});
        if (!inventory) {
            throw new Error(`Không tìm thấy sản phẩm với mã SKU: ${skuCode}`);
        }
        return inventory.quantity >= quantity;
    }

    async checkQuantity(productId, quantity) {
        const inventory = await inventoryRepository.findOneBy({productId});
        if (!inventory) {
            throw new Error(`Không tìm thấy sản phẩm với mã: ${productId}`);
        }
        return inventory.quantity >= quantity;
    }

    async findBySkuCode(skuCode) {
        return await inventoryRepository.findOneBy({skuCode});
    }

    async importQuantity(request) {
        const inventory = await inventoryRepository.findOneBy({skuCode: request.skuCode});
        if (!inventory) {
            throw new Error('Không tìm thấy sanr phẩm');
        }

        inventory.quantity += request.quantity;
        inventory.importAt = new Date();

        await inventoryRepository.save(inventory);

        const response = {
            quantity: inventory.quantity,
            importAt: inventory.importAt,
            importPrice: inventory.importPrice,
        }
        return response;
    }

    async updateInventoryForCart(request) {
        const inventory = await inventoryRepository.findOneBy({skuCode: request.skuCode});
        if (!inventory) {
            throw new Error('Không tìm thấy sản phẩm');
        }

        const response = {
            skuCode: inventory.skuCode,
            quantity: inventory.quantity,
            reservedQuantity: inventory.reservedQuantity
        };

        return response;
    }

    async extractSkuCodes(skuCodes) {
        try {
            // Tìm tất cả inventory có skuCode nằm trong danh sách skuCodes
            const inventories = await inventoryRepository.findBySkuCodeIn(skuCodes);

            // Chuyển danh sách -> map { skuCode: quantity }
            const responses = {};
            inventories.forEach(item => {
                responses[item.skuCode] = item.quantity;
            });

            return responses;
        } catch (error) {
            throw new Error(`Lỗi khi truy xuất dữ liệu: ${error.message}`);
        }
    }

    async getQuantity(skuCode) {
        try {
            const inventory = await inventoryRepository.findOneBy({skuCode});
            if (!inventory) {
                throw new Error('Không tìm thấy sản phẩm');
            }
            return inventory.quantity;
        } catch (error) {
            return 0;
        }
    }
}

module.exports = new InventoryService();