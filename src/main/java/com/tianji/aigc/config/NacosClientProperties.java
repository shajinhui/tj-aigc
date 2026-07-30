package com.tianji.aigc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.cloud.nacos")
public class NacosClientProperties {

    private String serverAddr;
    private String username;
    private String password;
    private Discovery discovery = new Discovery();
    private Config config = new Config();

    @Data
    public static class Discovery {
        private String namespace;
        private String group = "DEFAULT_GROUP";
        private String ip;
    }

    @Data
    public static class Config {
        private String namespace;
    }
}
