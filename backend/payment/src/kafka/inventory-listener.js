const {consumer} = require('../config/kafka');
const InventoryRepository = require('../repo/PaymentRepository');

class InventoryListener {
    async createInventoryForNewProduct() {
        try {
            await consumer.subscribe({topic: 'product-create', fromBeginning: false});

            console.log('📡 Đang lắng nghe topic product-create...');

            await consumer.run({
                eachMessage: async ({topic, partition, message}) => {
                    try {
                        const event = JSON.parse(message.value.toString());
                        console.log('📦 Nhận event tạo sản phẩm:', event);

                        const inventory = {
                            skuCode: event.skuCode,
                            productId: event.productId,
                            name: event.name,
                            quantity: 0,
                            reservedQuantity: 0,
                            importPrice: event.importPrice,
                            importAt: new Date(),
                        };

                        await InventoryRepository.save(inventory);
                        console.log(`✅ Đã lưu kho cho SKU: ${event.skuCode}`);
                    } catch (err) {
                        console.error('❌ Lỗi xử lý event Kafka:', err);
                    }
                },
            });
        } catch (err) {
            console.error('❌ Kafka listener error:', err);
        }
    }
}

module.exports = new InventoryListener;