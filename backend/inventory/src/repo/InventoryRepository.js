const {AppDataSource} = require('../config/data-source');
const Inventory = require('../entity/Inventory');
const {In} = require("typeorm");

class InventoryRepository {
    repo() {
        return AppDataSource.getRepository(Inventory);
    }

    async findAll() {
        return await this.repo().find();
    }

    async findOneBy(condition) {
        return await this.repo().findOneBy(condition);
    }


    async save(Inventory) {
        return await this.repo().save(Inventory);
    }

    async delete(id) {
        return await this.repo().delete({id});
    }

    async findBySkuCodeIn(skuCodes) {
        return await this.repo().find({
            where: {skuCode: In(skuCodes)},
            select: ['skuCode', 'quantity']
        });
    }
}

module.exports = new InventoryRepository();