const cartRepository = require('../repo/CartRepository');
const cartItemRepository = require('../repo/CartItemRepository');
const inventoryClient = require('../client/InventoryClient');
const productClient = require('../client/ProductClient');

class CartService {
    async _buildCartResponse(cart) {
        if (!cart) return null;
        const items = await cartItemRepository.findAllBy({cartId: cart.id});

        const productIds = items.map(i => i.productId);
        let imageMap = {};
        if (productIds && productIds.length > 0) {
            imageMap = await productClient.getImageUrl(productIds);
        }

        const itemsWithImage = items.map(i => ({
            cart_item_id: i.id,
            productId: i.productId,
            quantity: i.quantity,
            unitPrice: i.unitPrice,
            name: imageMap[i.productId]?.name || null,
            imageUrl: imageMap[i.productId]?.imageUrl || null,
        }));

        const totalAmount = items.reduce((sum, i) => sum + i.quantity * i.unitPrice, 0);

        return {
            id: cart.id,
            userId: cart.userId,
            items: itemsWithImage,
            totalAmount,
        };
    }

    async addProductToCart(userId, productId, quantity) {
        if (!userId) throw new Error('Vui lòng đăng nhập');

        // Lấy hoặc tạo giỏ hàng
        let cart = await cartRepository.findOneBy({userId});

        if (!cart) {
            cart = {
                userId: userId,
            }
            cart = await cartRepository.save(cart);
        }

        const existsMap = await productClient.checkProduct([productId]);
        if (!existsMap[productId]) {
            throw new Error("Sản phẩm với id = " + productId + " không tồn tại");
        }

        // Kiểm tra cart item có tồn tại
        let cartItem = await cartItemRepository.findOneBy({cartId: cart.id, productId});
        // let totalQuantity = quantity;
        // if (cartItem) totalQuantity += cartItem.quantity;
        // Check inventory
        const inStock = await inventoryClient.checkInventory(productId, quantity);
        if (!inStock) throw new Error('Sản phẩm không đủ số lượng trong kho');

        // Lấy giá sản phẩm
        const {finalPrice} = await productClient.getProductPrice(productId);

        // Tạo hoặc cập nhật cart item
        if (cartItem) {
            cartItem.quantity += quantity;
            cartItem.unitPrice = finalPrice;
        } else {
            cartItem = {
                cartId: cart.id,
                productId: productId,
                quantity: quantity,
                unitPrice: finalPrice
            };
        }

        await cartItemRepository.save(cartItem);

        // Trả về giỏ hàng mới
        return await this._buildCartResponse(cart);
    }

    async updateCart(userId, productId, quantity) {
        let cart = await cartRepository.findOneBy({userId});
        if (!cart) throw new Error("Không tìm thấy giỏ hàng cho người dùng")

        let cartItem = await cartItemRepository.findOneBy({cartId: cart.id, productId: productId});
        if (!cartItem) throw new Error("Sản phẩm không tồn tại trong giỏ hàng");

        if (quantity <= 0) {
            await cartItemRepository.delete(cartItem.id);
        } else {
            cartItem.quantity = quantity;
            await cartItemRepository.save(cartItem);
        }
        await cartRepository.save(cart);

        return await this._buildCartResponse(cart);
    }

    async getCartByUser(userId) {
        if (!userId) throw new Error('Vui lòng đăng nhập');
        let cart = await cartRepository.findOneBy({userId});
        return await this._buildCartResponse(cart);
    }

    async removeProductsFromCart(userId, productIds) {
        if (!userId) throw new Error('Vui lòng đăng nhập');

        const cart = await cartRepository.findOneBy({userId});
        if (!cart) throw new Error('Không tìm thấy giỏ hàng cho người dùng');
        console.log(productIds);
        // Lấy tất cả cartItem cần xóa
        const cartItems = await cartItemRepository.findAllByCartAndProductIds({
            cartId: cart.id,
            productIds: productIds
        });

        const foundIds = cartItems.map(item => item.productId);
        const missingIds = productIds.filter(id => !foundIds.includes(id));

        if (missingIds.length > 0) {
            throw new Error(`Các sản phẩm sau không tồn tại trong giỏ hàng: ${missingIds.join(', ')}`);
        }

        // Xóa tất cả cartItem
        for (const item of cartItems) {
            await cartItemRepository.delete(item.id);
        }

        // Trả về giỏ hàng mới
        return this._buildCartResponse(cart);
    }
}

module.exports = new CartService();