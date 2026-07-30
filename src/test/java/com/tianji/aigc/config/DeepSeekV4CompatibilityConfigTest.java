package com.tianji.aigc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.ai.deepseek.api.DeepSeekApi;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DeepSeekV4CompatibilityConfigTest {

    @Test
    void shouldDisableThinkingAndPreserveRequestFields() throws Exception {
        var mapper = new ObjectMapper();
        mapper.registerModule(new DeepSeekV4CompatibilityConfig().deepSeekV4RequestModule());
        var message = new DeepSeekApi.ChatCompletionMessage(
                "你好",
                DeepSeekApi.ChatCompletionMessage.Role.USER);
        var request = new DeepSeekApi.ChatCompletionRequest(
                List.of(message),
                "deepseek-v4-flash",
                0.3,
                true);

        var json = mapper.readTree(mapper.writeValueAsBytes(request));

        assertThat(json.path("model").asText()).isEqualTo("deepseek-v4-flash");
        assertThat(json.path("stream").asBoolean()).isTrue();
        assertThat(json.path("temperature").asDouble()).isEqualTo(0.3);
        assertThat(json.path("messages").get(0).path("content").asText()).isEqualTo("你好");
        assertThat(json.path("thinking").path("type").asText()).isEqualTo("disabled");
    }
}
