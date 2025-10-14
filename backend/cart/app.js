const createError = require('http-errors');
const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser');
const {AppDataSource} = require('./src/config/data-source');
require('dotenv').config();
const cartRouter = require('./src/router/CartRouter');
const jwtAuthentication = require("./src/middleware/jwtAuthentication");
const TokenInfo = require('./src/middleware/TokenInfo');
const app = express();

// view engine setup
app.use(express.json());
app.use(express.urlencoded({extended: false}));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));


app.use(TokenInfo.attach());
app.use(jwtAuthentication);

app.use('/api/cartNodejs', cartRouter);

AppDataSource.initialize()
    .then(() =>
        console.log('Kết nối db thành công')
    )
    .catch(err => console.log(err))
// catch 404 and forward to error handler
app.use(function (req, res, next) {
    res.status(404).json({
        success: false,
        message: 'API not found'
    });
});

// global error handler
app.use(function (err, req, res, next) {
    console.error('Error:', err);

    res.status(err.status || 500).json({
        success: false,
        message: err.message || 'Internal Server Error',
        stack: process.env.NODE_ENV === 'development' ? err.stack : undefined
    });
});

module.exports = app;