const {AppDataSource} = require('../config/data-source');
const Cart = require('../entity/Cart');

class CartRepository {
    repo() {
        return AppDataSource.getRepository(Cart);
    }

    //lấy tất
    async findAll() {
        return await this.repo().find();
    }

    async findOneBy(condition) {
        return await this.repo().findOneBy(condition);
    }

    async findOneByUserId(userId) {
        return await this.repo()
            .createQueryBuilder('cart')
            .where('cart.USER_ID = :userId', {userId})
            .getOne();
    }


    async save(cart) {
        return await this.repo().save(cart);
    }

    async delete(id) {
        return await this.repo().delete({id});
    }
}

module.exports = new CartRepository();