const {AppDataSource} = require('../config/data-source');
const Payment = require('../entity/Payment');
const {In} = require("typeorm");

class PaymentRepository {
    repo() {
        return AppDataSource.getRepository(Payment);
    }

    async findAll() {
        return await this.repo().find();
    }

    async findOneBy(condition) {
        return await this.repo().findOneBy(condition);
    }

    async save(paymentData) {
        const payment = this.repo().create(paymentData);
        return await this.repo().save(payment);
    }

    async delete(paymentId) {
        return await this.repo().delete({skuCode: paymentId});
    }

    async findByOrderIds(orderIds) {
        return await this.repo().find({
            where: {orderId: In(orderIds)}
        });
    }

    async totalAmountByUserIds(userIds) {
        return await this.repo()
            .createQueryBuilder("payment")
            .select("payment.userId", "userId")
            .addSelect("SUM(payment.amountPaid)", "totalAmount")
            .where("payment.userId IN (:...userIds)", {userIds})
            .groupBy("payment.userId")
            .getRawMany();
    }
}

module.exports = new PaymentRepository();
