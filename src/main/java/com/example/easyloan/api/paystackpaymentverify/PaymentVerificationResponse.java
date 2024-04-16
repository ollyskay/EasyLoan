
package com.example.easyloan.api.paystackpaymentverify;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "status",
    "message",
    "data"
})
public class PaymentVerificationResponse {
    @JsonProperty("status")
    private Boolean status;
    @JsonProperty("message")
    private String message;
    @JsonProperty("data")
    private Data data;

}
