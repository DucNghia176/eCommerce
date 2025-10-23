var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
const {AppDataSource} = require('./src/config/data-source');
const jwtAuthentication = require("./src/middleware/jwtAuthentication");
const inventoryRouter = require('./src/router/InventoryRouter');
var app = express();

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({extended: false}));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/api/inventory/', jwtAuthentication, inventoryRouter);
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
