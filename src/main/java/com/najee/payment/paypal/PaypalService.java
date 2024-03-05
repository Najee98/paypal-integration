package com.najee.payment.paypal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PaypalService {

    private final ApiContext apiContext;

    //Paymeny class is in com.paypal.api.payments
    public Payment createPayment(double total,
                                 String currency,
                                 String method,
                                 String intent,
                                 String description,
                                 String cancelUrl,
                                 String successUrl) throws PaypalRESTException {

        Amount amount = new Amount();
        amount.setCurrency(currency);
        //the locale is to format the paymeny based on the currency used
        //ex: 99.9$ for USD and 99,9 for EUR
        amount.setTotal(String.format(Locale.forLanguageTag(currency), "%.2f", total));

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        payment.setRedirectUrls(redirectUrls);

        //the PaypalRESTException would be thrown after returning this value
        //this is why the exception is added to the method signature.
        return payment.create(apiContext);
    }

    public Payment executePayment(
            String paymentId,
            String payerId
    ) throws PaypalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        return payment.execute(apiContext, paymentExecution);
    }
}
