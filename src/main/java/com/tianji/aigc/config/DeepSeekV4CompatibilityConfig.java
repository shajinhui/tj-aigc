package com.tianji.aigc.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;

/**
 * Spring AI 1.0 的 DeepSeek 请求模型早于 DeepSeek V4，缺少 thinking 字段。
 * 现有智能体依赖连续工具调用，因此统一使用 V4 非思考模式。
 */
@Configuration
public class DeepSeekV4CompatibilityConfig {

    @Bean
    public Module deepSeekV4RequestModule() {
        var module = new SimpleModule("deepseek-v4-request");
        module.addSerializer(
                DeepSeekApi.ChatCompletionRequest.class,
                new DeepSeekChatCompletionRequestSerializer());
        return module;
    }

    static class DeepSeekChatCompletionRequestSerializer
            extends JsonSerializer<DeepSeekApi.ChatCompletionRequest> {

        @Override
        public void serialize(
                DeepSeekApi.ChatCompletionRequest value,
                JsonGenerator generator,
                SerializerProvider serializers) throws IOException {
            generator.writeStartObject();
            writeField(generator, "messages", value.messages());
            writeField(generator, "model", value.model());
            writeField(generator, "frequency_penalty", value.frequencyPenalty());
            writeField(generator, "max_tokens", value.maxTokens());
            writeField(generator, "presence_penalty", value.presencePenalty());
            writeField(generator, "response_format", value.responseFormat());
            writeField(generator, "stop", value.stop());
            writeField(generator, "stream", value.stream());
            writeField(generator, "temperature", value.temperature());
            writeField(generator, "top_p", value.topP());
            writeField(generator, "logprobs", value.logprobs());
            writeField(generator, "top_logprobs", value.topLogprobs());
            writeField(generator, "tools", value.tools());
            writeField(generator, "tool_choice", value.toolChoice());
            generator.writeObjectField("thinking", Map.of("type", "disabled"));
            generator.writeEndObject();
        }

        private void writeField(JsonGenerator generator, String name, Object value) throws IOException {
            if (value != null) {
                generator.writeObjectField(name, value);
            }
        }
    }
}
