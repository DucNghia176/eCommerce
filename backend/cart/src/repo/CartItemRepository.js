const {AppDataSource} = require('../config/data-source');
const CartItem = require('../entity/CartItem');

class CartItemRepository {
    repo() {
        return AppDataSource.getRepository(CartItem);
    }

    //lấy tất
    async findAll() {
        return await this.repo().find();
    }

    async findAllBy(id) {
        return await this.repo().findBy(id);
    }

    async findOneBy(criteria) {
        return await this.repo().findOneBy(criteria);
    }

    async save(cartItem) {
        return await this.repo().save(cartItem);
    }

    async delete(id) {
        return await this.repo().delete({id});
    }

    async findAllByCartAndProductIds({cartId, productIds}) {
        if (!Array.isArray(productIds) || productIds.length === 0) return [];

        const ids = productIds.map(id => Number(id));

        return await this.repo()
            .createQueryBuilder('cart_item')
            .where('cart_item.CART_ID = :cartId', {cartId})
            .andWhere('cart_item.PRODUCT_ID IN (:...ids)', {ids})
            .getMany(); // Trả về entity, có productId
    }
}

module.exports = new CartItemRepository();