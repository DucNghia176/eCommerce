const oracledb = require('oracledb');

async function init() {
    try {
        // Cấu hình pool connection
        await oracledb.createPool({
            user: 'CART_DB',
            password: '123456aA@',
            connectString: 'localhost:1521/XEPDB1',
            poolMin: 1,
            poolMax: 5,
            poolIncrement: 1
        });

        console.log('✅ Oracle DB pool started');
    } catch (err) {
        console.error('❌ Error starting Oracle DB pool', err);
        process.exit(1);
    }
}

async function executeQuery(query, params = []) {
    let connection;
    try {
        connection = await oracledb.getConnection();
        const result = await connection.execute(query, params, {outFormat: oracledb.OUT_FORMAT_OBJECT});
        return result.rows;
    } catch (err) {
        console.error('DB error:', err);
        throw err;
    } finally {
        if (connection) {
            try {
                await connection.close();
            } catch (err) {
                console.error('Error closing connection', err);
            }
        }
    }
}

module.exports = {init, executeQuery};