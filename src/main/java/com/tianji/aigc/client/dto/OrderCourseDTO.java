package com.tianji.aigc.client.dto;

import lombok.Data;

@Data
public class OrderCourseDTO {
    private Long id;
    private String name;
    private String coverUrl;
    private Integer price;
}
