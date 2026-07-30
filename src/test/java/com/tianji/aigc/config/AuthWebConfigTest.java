package com.tianji.aigc.config;

import com.tianji.aigc.context.TokenContext;
import com.tianji.aigc.context.UserContext;
import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthWebConfigTest {

    private final AuthWebConfig authWebConfig = new AuthWebConfig();

    @AfterEach
    void clearContext() {
        UserContext.removeUser();
        TokenContext.removeToken();
    }

    @Test
    void shouldRelayUserAndAuthorizationHeaders() {
        UserContext.setUser(123L);
        TokenContext.setToken("Bearer test-token");
        var template = new RequestTemplate();

        authWebConfig.authenticationRelayInterceptor().apply(template);

        assertThat(template.headers().get("user-info")).containsExactly("123");
        assertThat(template.headers().get("authorization")).containsExactly("Bearer test-token");
    }

    @Test
    void shouldNotCreateHeadersWithoutAuthContext() {
        var template = new RequestTemplate();

        authWebConfig.authenticationRelayInterceptor().apply(template);

        assertThat(template.headers()).doesNotContainKeys("user-info", "authorization");
    }
}
