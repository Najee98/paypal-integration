package com.najee.payment.Controllers;

import com.najee.payment.paypal.PaypalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.w3c.dom.stylesheets.LinkStyle;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaypalControllers {

    private final PaypalService paypalService;

    @GetMapping("/")
    public String home(){
        return "index";
    }

    @PostMapping("/payment/create")
    public RedirectView createPayment(
            @RequestParam("method") String method,
            @RequestParam("amount") String amount,
            @RequestParam("currency") String currency,
            @RequestParam("description") String description
    ){
        try{
            String cancelUrl = "http://localhost:8080/payment/cancel";
            String successUrl = "http://localhost:8080/payment/success";
            Payment payment = paypalService.createPayment(
                    Double.valueOf(amount),
                    currency,
                    method,
                    "sale",
                    description,
                    cancelUrl,
                    successUrl
            );

            for (Links links : payment.getLinks()){
                if(links.getRel().equals("approval_url")){
                    return new RedirectView(links.getHref());
                }
            }
        }catch (PaypalRESTException e){
            log.error("Error occured:: ", e);
        }

        return new RedirectView("/payment/error");
    }

    @GetMapping("/payment/success")
    public String paymentSuccess(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId
    ) {
        try{
            Payment payment = paypalService.executePayment(paymentId, payerId);
            if(payment.getState().equels("approved")){
                return "paymentSuccess";
            }
        }catch (PaypalRESTException e){
            log.error("Error occured:: ", e);
        }

        return "paymentSuccess";
    }

    @GetMapping("/payment/cancel")
    public String paymentCancel(){
        return "paymentCancel";
    }

    @GetMapping("/payment/error")
    public String paymentCancel(){
        return "paymentError";
    }
}
