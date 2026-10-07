package com.amar.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RazorpayOrderResponse {
    private String keyId;
    private String razorpayOrderId;
    private Long amount;
    private String currency;
    private Long paymentOrderId;
}
