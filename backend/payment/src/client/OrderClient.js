const axiosClient = require('./axiosClient');

const baseUrl = "http://localhost:8085/api/orders";

class OrderClient {
    // Nếu muốn có thể thêm token ở đây
    constructor() {
    }

    async getOrderById(orderId) {
        try {
            const response = await axiosClient.get(`${baseUrl}/${orderId}`);
            console.log("OrderService trả về =", response.data);
            return response.data;
        } catch (err) {
            console.error("getOrderById error:", err.response?.data || err.message);
            return null; // để tránh crash
        }
    }

    async updateOrderStatus(orderId, orderStatus) {
        try {
            const response = await axiosClient.put(`${baseUrl}/update-status`, {
                orderId,
                orderStatus
            });
            return response.data;
        } catch (err) {
            console.error("updateOrderStatus error:", err.response?.data || err.message);
            throw err;
        }
    }
}

module.exports = new OrderClient();
