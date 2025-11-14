const {Eureka} = require('eureka-js-client');

const client = new Eureka({
    instance: {
        app: 'INVENTORY-SERVICE',
        hostName: '172.31.192.1',
        ipAddr: '127.0.0.1',
        statusPageUrl: 'http://localhost:3001/info',
        healthCheckUrl: 'http://localhost:3001/health',
        port: {
            '$': 3001,
            '@enabled': true,
        },
        vipAddress: 'INVENTORY-SERVICE',
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
