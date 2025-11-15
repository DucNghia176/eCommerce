require('reflect-metadata');
const {DataSource} = require('typeorm');
require('dotenv').config();
const path = require('path');

const AppDataSource = new DataSource({
    type: 'oracle',
    username: 'INVENTORY_DB',
    password: process.env.DB_PASSWORD,
    connectString: process.env.DB_CONNECT_STRING,
    synchronize: false,
    entities: [path.join(__dirname, '..', 'entity', '*.js')],
    logging: true,
    extra: {
        poolMin: 1,
        poolMax: 5,
        poolIncrement: 1
    }
});

module.exports = {AppDataSource};