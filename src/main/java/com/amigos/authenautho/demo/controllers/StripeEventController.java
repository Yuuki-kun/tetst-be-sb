package com.amigos.authenautho.demo.controllers;

import com.amigos.authenautho.demo.entities.*;
import com.amigos.authenautho.demo.entities.Customer;
import com.amigos.authenautho.demo.entities.Order;
import com.amigos.authenautho.demo.entities.OrderItem;
import com.amigos.authenautho.demo.repositories.*;
import com.amigos.authenautho.demo.services.BookServices;
import com.amigos.authenautho.demo.services.CheckoutService;
import com.amigos.authenautho.demo.dto.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.SetupIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.ShippingAddressCollection;

import com.stripe.param.checkout.SessionCreateParams.LineItem;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/stripe/api")
@RequiredArgsConstructor
public class StripeEventController {
    private final BookServices bookServices;
    private final CheckoutService checkoutService;
    private final CustomerRepository customerRepository;
    private final StatusRepository statusRepository;
    private final OrderRepository orderRepository;
    private final CartRepsitory cartRepsitory;
    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;

    //listening stripe events
    @PostMapping("/webhook")
    public void handleWebhook (@RequestBody String payload, @RequestHeader("Stripe-Signature" ) String sigHeader) throws Exception {
        String endpointSecret = "whsec_4ce717322278dd8ae2cdf2226528baeb2a0c109a7d779c9d153eb46548afa798";  // Thay thế bằng webhook secret của bạn
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            // Xử lý khi xác thực không thành công
            System.out.println(e);
            return;
        }
//        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
//        StripeObject stripeObject;
//        if (dataObjectDeserializer.getObject().isPresent()) {
//            stripeObject = dataObjectDeserializer.getObject().get();
//            System.out.println("stripe object = "+stripeObject);
//        } else {
//            // Deserialization failed, probably due to an API version mismatch.
//            // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
//            // instructions on how to handle this case, or return an error here.
//            System.out.println("failed?");
//        }
        // Xử lý sự kiện dựa trên loại
        switch (event.getType()) {
            case "checkout.session.completed":

                System.out.println("COMPLETE");

                String payloadJson = payload;
                JsonObject jsonObject = JsonParser.parseString(payloadJson).getAsJsonObject();
                JsonObject dataobject = jsonObject.getAsJsonObject("data");
                String csid = dataobject.getAsJsonObject("object").get("id").getAsString();

                Session session = Session.retrieve(csid);
                Map<String, Object> params = new HashMap<>();
                params.put("limit", 100);
                LineItemCollection lineItems = session.listLineItems(params);
                List<String> products = new ArrayList<>();
                lineItems.getData().forEach(item ->{
                    products.add(item.getPrice().getProduct());
                });

                lineItems.getData().forEach(lineItem -> {
                    String desc = lineItem.getDescription();
                    Long amountTotal = lineItem.getAmountTotal();
                    Long qty = lineItem.getQuantity();
                });

                Long amountTotal = session.getAmountTotal();

                List<Product> productslist = new ArrayList<>();
                for (String product : products) {
                    productslist.add(Product.retrieve(product));
                }

                ShippingDetails shippingDetails = session.getShipping();
                String city = shippingDetails.getAddress().getCity();
                String country = shippingDetails.getAddress().getCountry();
                String line1 = shippingDetails.getAddress().getLine1();
                String line2 = shippingDetails.getAddress().getLine2();
                String postalCode = shippingDetails.getAddress().getPostalCode();
                String state = shippingDetails.getAddress().getState();
                String name = shippingDetails.getName();

                List<ShortBookDto> itemList = new ArrayList<>();
                for (int i=0; i<productslist.size(); i++){

                    itemList.add(ShortBookDto.builder().name(productslist.get(i).getName())
                                                       .price(lineItems.getData().get(i).getAmountTotal())
                                                       .qty(lineItems.getData().get(i).getQuantity())
                                                    .build());

                }
                PaymentAddressDto paymentAddressDto = PaymentAddressDto.builder().dcghNgayGiao(new Date()).dcghThanhPho(city).dcghDiaChi("line1: "+line1+", line2: "+line2).dcghTinh(state).dcghQuocGia(country).postalCode(postalCode).build();
                PaymentDto paymentInfo = PaymentDto.builder().paymentMethod(session.getPaymentMethodTypes().get(0)).paymentDate(new Date()).totalAmount(session.getAmountTotal()).build();
                String piid = session.getPaymentIntent();
                checkoutService.saveDH(session.getCustomerEmail(), session.getAmountTotal(), itemList, paymentAddressDto, paymentInfo, csid, piid);
                break;
            case "invoice.payment_succeeded":
                System.out.println("INVOICE");
                break;
//            default:
//                System.out.println("Unhandled event type: " + event.getType());
        }
    }

    //key vers
    @PostMapping("/checkout-card")
    public String createCheckoutSession(@RequestBody PurchaseInfo purchaseInfo) throws Exception {
        List<StripeProductPrice> listPriceIds = bookServices.getPriceIds(purchaseInfo.getProductPurchases());

        String YOUR_DOMAIN = "http://localhost:3000/payment";

        List<LineItem> lineItems = new ArrayList<>();
        listPriceIds.forEach(proId->{
            lineItems.add(LineItem.builder().setPrice(proId.getPrice()).setQuantity((long) (proId.getQuantity())).build());
        });

//        CustomerUpdate customerUpdate = CustomerUpdate.builder().setName(CustomerUpdate.Name.AUTO).setAddress(CustomerUpdate.Address.AUTO).setShipping(CustomerUpdate.Shipping.AUTO).build();
        List<ShippingAddressCollection.AllowedCountry> allowedCountries = new ArrayList<>();
        allowedCountries.add(ShippingAddressCollection.AllowedCountry.VN);
        allowedCountries.add(ShippingAddressCollection.AllowedCountry.CN);
        allowedCountries.add(ShippingAddressCollection.AllowedCountry.US);

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(YOUR_DOMAIN + "/success")
                        .setCancelUrl(YOUR_DOMAIN + "/cancel")
                        .addAllLineItem(lineItems).setCustomerEmail(purchaseInfo.getCustomerEmail()).setShippingAddressCollection(ShippingAddressCollection.builder().addAllAllowedCountry(allowedCountries).build())
                        .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder().setCaptureMethod(SessionCreateParams.PaymentIntentData.CaptureMethod.MANUAL).build())
                        .build();

        Session session = Session.create(params);

     return session.getUrl();

    }

    @PostMapping("/checkout-cash")
    public String checkoutByCashHandler(@RequestBody PurchaseByCashInfo purchaseInfo) throws Exception {
        System.out.println("pbci="+purchaseInfo);

        List<ShortBookDto> itemList = new ArrayList<>();
        float total = 0;
        for(ProductPurchase productPurchase : purchaseInfo.getProductPurchases()){
            Book b = bookRepository.findById((long) productPurchase.getItemId()).orElseThrow(()->new Exception(("")));
            itemList.add(ShortBookDto.builder().name(b.getTen()).qty(productPurchase.getSoluong()).price(b.getDon_gia()*1000).build());
            total += productPurchase.getSoluong()*b.getDon_gia();
        }

        PaymentAddressDto paymentAddressDto = PaymentAddressDto.builder().dcghDiaChi(purchaseInfo.getAddress())
                .dcghTinh(purchaseInfo.getProvince())
                .dcghThanhPho(purchaseInfo.getCity())
                .dcghQuocGia("VN")
                .postalCode(purchaseInfo.getPostalCode())
                .dcghNgayGiao(new Date()).build();

        PaymentDto paymentInfo = PaymentDto.builder().paymentMethod("cash").paymentDate(new Date()).totalAmount(total).build();

        checkoutService.saveDH(purchaseInfo.getCustomerEmail(), total*1000, itemList, paymentAddressDto, paymentInfo, "", "");

        return "OK";
    }

}
