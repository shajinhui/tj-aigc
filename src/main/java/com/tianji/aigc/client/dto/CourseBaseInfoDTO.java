package com.tianji.aigc.client.dto;

import lombok.Data;

@Data
public class CourseBaseInfoDTO {
    private Long id;
    private String name;
    private Integer price;
    private Integer validDuration;
    private String usePeople;
    private String detail;
}
