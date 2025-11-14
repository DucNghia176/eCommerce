// src/client/axiosClient.js
const axios = require('axios');

const axiosClient = axios.create();

// Thêm interceptor request
axiosClient.interceptors.request.use(
    (config) => {
        // đảm bảo headers luôn có giá trị
        config.headers = config.headers || {};

        // Gắn token vào header Authorization nếu có
        if (globalThis.jwtToken) {
            config.headers['Authorization'] = globalThis.jwtToken;
        }

        return config;
    },
    (error) => Promise.reject(error)
);

module.exports = axiosClient;
