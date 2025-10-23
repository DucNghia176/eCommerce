const {createClient} = require('redis');

const redisClient = createClient({
    url: 'redis://:123456aA@@127.0.0.1:6379'
});

redisClient.on('connect', () => {
    console.log('✅ Connected to Redis');
});

redisClient.on('error', (err) => {
    console.error('❌ Redis connection error:', err);
});

// Kết nối Redis (chạy nền, không block)
(async () => {
    try {
        await redisClient.connect();
    } catch (err) {
        console.error('Redis connect error:', err);
    }
})();

module.exports = redisClient;