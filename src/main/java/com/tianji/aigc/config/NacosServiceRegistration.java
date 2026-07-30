package com.tianji.aigc.config;

import com.alibaba.nacos.api.naming.NamingService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetAddress;

@Slf4j
@Component
@RequiredArgsConstructor
public class NacosServiceRegistration implements ApplicationRunner {

    private final NamingService namingService;
    private final NacosClientProperties nacosProperties;
    private final Environment environment;

    private String serviceName;
    private String group;
    private String ip;
    private int port;
    private boolean registered;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        serviceName = environment.getRequiredProperty("spring.application.name");
        group = nacosProperties.getDiscovery().getGroup();
        ip = nacosProperties.getDiscovery().getIp();
        if (!StringUtils.hasText(ip)) {
            ip = InetAddress.getLocalHost().getHostAddress();
        }
        port = environment.getRequiredProperty("server.port", Integer.class);
        namingService.registerInstance(serviceName, group, ip, port);
        registered = true;
        log.info("AIGC service registered in Nacos: group={}, service={}, endpoint={}:{}",
                group, serviceName, ip, port);
    }

    @PreDestroy
    public void deregister() {
        if (!registered) {
            return;
        }
        try {
            namingService.deregisterInstance(serviceName, group, ip, port);
        } catch (Exception e) {
            log.warn("Failed to deregister AIGC service from Nacos", e);
        }
    }
}
