package com.tianji.aigc.config;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Getter
@Configuration
@RequiredArgsConstructor
public class SystemPromptConfig {

    private final ConfigService configService;
    private final AIProperties aiProperties;

    // 使用原子引用，保证线程安全
    private final AtomicReference<String> chatSystemMessage = new AtomicReference<>();
    private final AtomicReference<String> routeAgentSystemMessage = new AtomicReference<>();
    private final AtomicReference<String> recommendAgentSystemMessage = new AtomicReference<>();
    private final AtomicReference<String> buyAgentSystemMessage = new AtomicReference<>();
    private final AtomicReference<String> textSystemMessage = new AtomicReference<>();

    @PostConstruct // 初始化时加载配置
    public void init() {
        // 读取配置文件
        loadConfig(aiProperties.getSystem().getChat(), chatSystemMessage);
        loadConfig(aiProperties.getSystem().getRouteAgent(), routeAgentSystemMessage);
        loadConfig(aiProperties.getSystem().getRecommendAgent(), recommendAgentSystemMessage);
        loadConfig(aiProperties.getSystem().getBuyAgent(), buyAgentSystemMessage);
        loadConfig(aiProperties.getSystem().getText(), textSystemMessage);
    }

    private void loadConfig(AIProperties.System.Chat chatConfig, AtomicReference<String> target) {
        try {
            var dataId = chatConfig.getDataId();
            var group = chatConfig.getGroup();
            var timeoutMs = chatConfig.getTimeoutMs();

            // 读取配置文件中的内容
            var config = configService.getConfig(dataId, group, timeoutMs);
            target.set(StringUtils.hasText(config) ? config : chatConfig.getFallback());
            if (StringUtils.hasText(config)) {
                log.info("读取系统提示词成功，dataId={}，group={}", dataId, group);
            } else {
                log.warn("Nacos 中不存在系统提示词，使用本地默认值，dataId={}，group={}", dataId, group);
            }

            // 设置监听事件，用于热更新
            configService.addListener(dataId, group, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String info) {
                    if (StringUtils.hasText(info)) {
                        target.set(info);
                        log.info("更新系统提示词成功，dataId={}，group={}", dataId, group);
                    }
                }
            });
        } catch (Exception e) {
            target.set(chatConfig.getFallback());
            log.warn("加载系统提示词失败，使用本地默认值，dataId={}，group={}",
                    chatConfig.getDataId(), chatConfig.getGroup(), e);
        }
    }

}
