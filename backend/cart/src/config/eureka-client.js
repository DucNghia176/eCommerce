const {Eureka} = require('eureka-js-client');

// Tạo client
const client = new Eureka({
    instance: {
        app: 'cart-service-nodejs',
        hostName: 'localhost',
        ipAddr: '127.0.0.1',
        statusPageUrl: 'http://localhost:3000/info',
        healthCheckUrl: 'http://localhost:3000/health',
        port: {
            '$': 3000,
            '@enabled': true,
        },
        vipAddress: 'cart-service-nodejs',
        dataCenterInfo: {
            '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
            name: 'MyOwn',
        },
    },
    eureka: {
        host: 'localhost',
        port: 8761,
        servicePath: '/eureka/apps/'
    },
});

// Kết nối
client.start((error) => {
    console.log(error || '✅ Cart-Service registered with Eureka!');
});

module.exports = client;
