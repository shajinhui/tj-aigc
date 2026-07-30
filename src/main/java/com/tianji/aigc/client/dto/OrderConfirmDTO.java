package com.tianji.aigc.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderConfirmDTO {
    private Long orderId;
    private Integer totalAmount;
    private Integer discountAmount;
    private List<OrderCourseDTO> courses;
}
