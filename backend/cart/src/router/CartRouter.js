const express = require('express');
const router = express.Router();
const cartController = require('../controller/CartController');
const jwtAuthentication = require('../middleware/jwtAuthentication');

router.get('/', cartController.getAll);
router.post('/create', jwtAuthentication, cartController.create);
router.post('/update', jwtAuthentication, cartController.update);
router.get('/get', jwtAuthentication, cartController.getCartByUser);
router.post('/remove', jwtAuthentication, cartController.remove);
module.exports = router;