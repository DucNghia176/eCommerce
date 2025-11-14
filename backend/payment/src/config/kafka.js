const {Kafka} = require('kafkajs');
const dotenv = require('dotenv');

dotenv.config();

const kafka = new Kafka({
    clientId: process.env.KAFKA_CLIENT_ID,
    brokers: [process.env.KAFKA_BROKER],
});

const producer = kafka.producer();
const consumer = kafka.consumer({groupId: process.env.KAFKA_GROUP_ID});

// Kết nối Kafka (chạy nền)
(async () => {
    try {
        await producer.connect();
        await consumer.connect();
        console.log('✅ Kafka connected');
    } catch (err) {
        console.error('❌ Kafka connection error:', err);
    }
})();

module.exports = {producer, consumer};
