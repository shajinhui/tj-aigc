package com.tianji.aigc.config;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Properties;

@Configuration
@EnableConfigurationProperties(NacosClientProperties.class)
public class NacosClientConfig {

    @Bean(destroyMethod = "shutDown")
    public ConfigService nacosConfigService(NacosClientProperties properties) throws Exception {
        Properties nacos = baseProperties(properties);
        if (StringUtils.hasText(properties.getConfig().getNamespace())) {
            nacos.setProperty(PropertyKeyConst.NAMESPACE, properties.getConfig().getNamespace());
        }
        return ConfigFactory.createConfigService(nacos);
    }

    @Bean(destroyMethod = "shutDown")
    public NamingService nacosNamingService(NacosClientProperties properties) throws Exception {
        Properties nacos = baseProperties(properties);
        if (StringUtils.hasText(properties.getDiscovery().getNamespace())) {
            nacos.setProperty(PropertyKeyConst.NAMESPACE, properties.getDiscovery().getNamespace());
        }
        return NamingFactory.createNamingService(nacos);
    }

    private static Properties baseProperties(NacosClientProperties source) {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, source.getServerAddr());
        if (StringUtils.hasText(source.getUsername())) {
            properties.setProperty(PropertyKeyConst.USERNAME, source.getUsername());
        }
        if (StringUtils.hasText(source.getPassword())) {
            properties.setProperty(PropertyKeyConst.PASSWORD, source.getPassword());
        }
        return properties;
    }
}
