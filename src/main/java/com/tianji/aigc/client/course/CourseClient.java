package com.tianji.aigc.client.course;

import com.tianji.aigc.client.dto.CourseBaseInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        contextId = "aigcCourseClient",
        name = "aigcCourseClient",
        url = "${tj.remote.gateway-url:http://127.0.0.1:10010}",
        path = "/cs")
public interface CourseClient {

    @GetMapping("/course/baseInfo/{id}")
    CourseBaseInfoDTO baseInfo(
            @PathVariable("id") Long id,
            @RequestParam(value = "see", defaultValue = "true") boolean see);
}
