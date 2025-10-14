const axiosClient = require('./axiosClient');

const baseUrl = "http://localhost:8085/api/product";

class ProductClient {
    // Nếu muốn có thể thêm token ở đây
    constructor() {
    }

    async getProductPrice(productId) {
        const response = await axiosClient.get(`${baseUrl}/price/${productId}`);
        return response.data;
    }

    async checkProduct(ids) {
        const response = await axiosClient.post(`${baseUrl}/exists`, ids);
        const data = response.data;
        const result = {};
        ids.forEach(id => {
            result[id] = data[id] ?? false;
        });

        return result;
    }
}

module.exports = new ProductClient();
