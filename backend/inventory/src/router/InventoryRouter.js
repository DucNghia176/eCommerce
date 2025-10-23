const express = require('express');
const router = express.Router();
const inventoryController = require('../controller/InventoryController');

router.get('/quantity', inventoryController.getQuantity);
router.get('/quantities', inventoryController.extractSkuCodes);
router.get('/check', inventoryController.isInStock);
router.get('/checkQuantity', inventoryController.checkQuantity);
router.post('/update/quantity', inventoryController.importQuantity);
router.put('/cart', inventoryController.updateInventoryForCart);

module.exports = router;