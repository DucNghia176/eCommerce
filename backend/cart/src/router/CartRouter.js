const express = require('express');
const router = express.Router();
const cartController = require('../controller/CartController');
const jwtAuthentication = require('../middleware/jwtAuthentication');

router.post('/create', cartController.create);
router.post('/update', cartController.update);
router.get('/', cartController.getCartByUser);
router.post('/remove', cartController.remove);
module.exports = router;