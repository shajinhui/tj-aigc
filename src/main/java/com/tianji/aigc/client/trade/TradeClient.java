package com.tianji.aigc.client.trade;

import com.tianji.aigc.client.dto.OrderConfirmDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        contextId = "aigcTradeClient",
        name = "aigcTradeClient",
        url = "${tj.remote.gateway-url:http://127.0.0.1:10010}",
        path = "/ts")
public interface TradeClient {

    @GetMapping("/orders/prePlaceOrder")
    OrderConfirmDTO prePlaceOrder(@RequestParam("courseIds") List<Long> courseIds);
}
