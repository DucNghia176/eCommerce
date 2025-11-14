const {Eureka} = require('eureka-js-client');

const client = new Eureka({
    instance: {
        app: 'PAYMENT-SERVICE',
        hostName: 'localhost',
        ipAddr: '127.0.0.1',
        statusPageUrl: 'http://localhost:3002/info',
        healthCheckUrl: 'http://localhost:3002/health',
        port: {
            '$': 3002,
            '@enabled': true,
        },
        vipAddress: 'PAYMENT-SERVICE',
        dataCenterInfo: {
            '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
            name: 'MyOwn',
        },
    },
    eureka: {
        host: 'localhost',
        port: 8762,
        servicePath: '/eureka/apps/'
    },
});

client.start((error) => {
    console.log(error || '✅ Inventory-Service registered with Eureka!');
});

module.exports = client;
