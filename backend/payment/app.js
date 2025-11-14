var express = require('express');
var path = require('path');
const {AppDataSource} = require('./src/config/data-source');
const jwtAuthentication = require("./src/middleware/jwtAuthentication");
const paymentRouter = require('./src/router/PaymentRouter');
var app = express();

app.use(express.json());
app.use(express.urlencoded({extended: false}));
app.use(express.static(path.join(__dirname, 'public')));

app.use('/api/payment', jwtAuthentication, paymentRouter);
AppDataSource.initialize()
    .then(() => {
            console.log('Kết nối db thành công');
        }
    )
    .catch(err => console.log(err))

app.use(function (req, res) {
    res.status(404).json({
        success: false,
        message: 'API not found'
    });
});
module.exports = app;