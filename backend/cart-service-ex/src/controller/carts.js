var express = require('express');
const db = require("../config/db");
var router = express.Router();

router.get('/getAll', async (req, res) => {
    try {
        const rows = await db.executeQuery('SELECT * FROM CART');
        res.json(rows);
    } catch (err) {
        res.status(500).json({error: err.message});
    }
});

module.exports = router;