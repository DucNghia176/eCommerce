package ecommerce.paymentservice.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaypalService {

    private final APIContext apiContext;

    public String createPayment(BigDecimal total, String currency, String method,
                                String intent, String description,
                                String cancelUrl, String successUrl) throws PayPalRESTException {

        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format("%.2f", total));

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        Payer payer = new Payer();
        payer.setPaymentMethod(method.toLowerCase());

        Payment payment = new Payment();
        payment.setIntent(intent.toLowerCase());
        payment.setPayer(payer);
        payment.setTransactions(List.of(transaction));

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);

        Payment createdPayment = payment.create(apiContext);
        for (Links link : createdPayment.getLinks()) {
            if ("approval_url".equals(link.getRel())) {
                return link.getHref();
            }
        }

        throw new RuntimeException("Không tìm thấy approval_url từ PayPal response.");
    }
}

