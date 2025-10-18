const express = require('express');
const cookieParser = require('cookie-parser');
const {AppDataSource} = require('./src/config/data-source');
const cartRouter = require('./src/router/CartRouter');
const jwtAuthentication = require("./src/middleware/jwtAuthentication");
const TokenInfo = require('./src/middleware/TokenInfo');
const app = express();
const cors = require('cors');
app.use(cors());

// view engine setup
app.use(express.json());
app.use(express.urlencoded({extended: false}));
app.use(cookieParser());

app.use(TokenInfo.attach());

app.use('/api/cart', jwtAuthentication, cartRouter);

AppDataSource.initialize()
    .then(() =>
        console.log('Kết nối db thành công')
    )
    .catch(err => console.log(err))

app.use(function (req, res) {
    res.status(404).json({
        success: false,
        message: 'API not found'
    });
});
module.exports = app;