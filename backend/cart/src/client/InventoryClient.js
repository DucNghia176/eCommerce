const axiosClient = require('./axiosClient');

const baseUrl = "http://localhost:8085/api/inventory";

class InventoryClient {
    // Nếu muốn có thể thêm token ở đây
    constructor() {
    }

    async checkInventory(productId, quantity) {

        const response = await axiosClient.get(`${baseUrl}/checkQuantity`, {
            params: {productId, quantity}
        });
        return response.data;
    }
}

module.exports = new InventoryClient();
