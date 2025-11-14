const cartService = require('../service/CartService');

class CartController {
    async create(req, res) {
        try {
            const userId = req.user.userId;
            const { productId, quantity } = req.body;
            if (quantity > 0) {
                const cartData = await cartService.addProductToCart(userId, productId, quantity);
                res.status(200).json({ code: 200, message: 'Thêm sản phẩm vào giỏ hàng thành công', data: cartData });
            } else {
                res.status(400).json({ code: 400, message: 'số lượng phải lớn hơn 0', data: null });
            }

        } catch (e) {
            res.status(400).json({ message: e.message });
        }
    }

    async update(req, res) {
        try {
            const userId = req.user.userId;
            const { productId, quantity } = req.body;
            if (quantity > 0) {
                const cartData = await cartService.updateCart(userId, productId, quantity);
                res.status(200).json({ code: 200, message: 'Cập nhật giỏ hàng thành công', data: cartData });
            } else {
                res.status(400).json({ code: 400, message: 'số lượng phải lớn hơn 0', data: null });
            }

        } catch (e) {
            res.status(400).json({ message: e.message });
        }
    }

    async getCartByUser(req, res) {
        try {
            const userId = req.user.userId;
            const cartData = await cartService.getCartByUser(userId);
            res.status(200).json({ code: 200, message: 'Lấy giỏ hàng thành công', data: cartData });

        } catch (e) {
            res.status(400).json({ message: e.message });
        }
    }

    async remove(req, res) {
        try {
            const userId = req.user.userId;
            const productIds = Array.isArray(req.body) ? req.body : req.body.productIds;

            if (!Array.isArray(productIds) || productIds.length === 0) {
                return res.status(400).json({ message: 'Vui lòng cung cấp danh sách productIds' });
            }
            const cartData = await cartService.removeProductsFromCart(userId, productIds);
            res.status(200).json({ code: 200, message: 'xóa hàng thành công', data: cartData });
        } catch (e) {
            res.status(400).json({ message: e.message });
        }
    }

    async getSelectedItems(req, res) {
        try {
            const userId = req.user.userId;
            let selectedIds = [];

            if (Array.isArray(req.body)) {
                selectedIds = req.body.map(id => Number(id)).filter(id => !isNaN(id));
            } else if (req.body && typeof req.body === 'object') {
                selectedIds = Object.entries(req.body)
                    .filter(([, value]) => value === true || value === 'true')
                    .map(([key]) => Number(key))
                    .filter(id => !isNaN(id));
            }

            if (!selectedIds.length) {
                return res.status(400).json({ message: 'Vui lòng chọn sản phẩm hợp lệ' });
            }

            const items = await cartService.getSelectedCartItems(userId, selectedIds);
            res.status(200).json({ code: 200, message: 'Lấy sản phẩm thành công', data: items });
        } catch (e) {
            res.status(400).json({ message: e.message });
        }
    }
}

module.exports = new CartController();