package com.petboarding.controllers;


import com.petboarding.models.dto.CreatePayment;
import com.petboarding.models.dto.CreatePaymentResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


@RestController
public class PaymentController{

//    successful payment 4242424242424242
//    Generic decline	4000000000000002	card_declined
//    Insufficient funds decline	4000000000009995
//    Lost card decline	4000000000009987
//    Stolen card decline	4000000000009979
//    Expired card decline	4000000000000069
//    Incorrect CVC decline	4000000000000127
//    Processing error decline	4000000000000119
//    Incorrect number decline	4242424242424241

    @Value("${stripe.api.secureKey}")
    private String stripeApiKey;

    @PostMapping("/create-payment-intent")
    public CreatePaymentResponse createPaymentIntent(@RequestBody CreatePayment createPayment, HttpSession session) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        PaymentIntentCreateParams paymentParams = new PaymentIntentCreateParams.Builder()
                .setAmount(createPayment.getAmount())
                .setCurrency("usd")
                .setReceiptEmail(createPayment.getEmail())
                .putMetadata("invoiceId", createPayment.getInvoiceId())
                .build();
        PaymentIntent paymentIntent = PaymentIntent.create(paymentParams);

        session.setAttribute("stripe.clientSecret", paymentIntent.getClientSecret()); // save last payment intent
        session.setAttribute("stripe.invoiceId", createPayment.getInvoiceId());
        session.setAttribute("stripe.amount",(double) createPayment.getAmount() / 100);

        return new CreatePaymentResponse(paymentIntent.getClientSecret());
    }
}
