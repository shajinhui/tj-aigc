package com.tianji.aigc.config;

import com.tianji.aigc.context.TokenContext;
import com.tianji.aigc.context.UserContext;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuthWebConfig implements WebMvcConfigurer {

    private static final String USER_HEADER = "user-info";
    private static final String AUTHORIZATION_HEADER = "authorization";

    @Value("${tj.auth.resource.enable:true}")
    private boolean authEnabled;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
                    @Override
                    public boolean preHandle(
                            HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) throws Exception {
                        String userId = request.getHeader(USER_HEADER);
                        if (userId != null && !userId.isBlank()) {
                            try {
                                UserContext.setUser(Long.valueOf(userId));
                            } catch (NumberFormatException ignored) {
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "用户身份格式错误");
                                return false;
                            }
                        }
                        String token = request.getHeader(AUTHORIZATION_HEADER);
                        if (token != null && !token.isBlank()) {
                            TokenContext.setToken(token);
                        }
                        if (authEnabled && UserContext.getUser() == null) {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未登录用户无法访问");
                            return false;
                        }
                        return true;
                    }

                    @Override
                    public void afterCompletion(
                            HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler,
                            Exception ex) {
                        UserContext.removeUser();
                        TokenContext.removeToken();
                    }
                })
                .excludePathPatterns(
                        "/error",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/doc.html",
                        "/webjars/**",
                        "/actuator/health");
    }

    @Bean
    public RequestInterceptor authenticationRelayInterceptor() {
        return template -> {
            Long userId = UserContext.getUser();
            if (userId != null) {
                template.header(USER_HEADER, userId.toString());
            }
            String token = TokenContext.getToken();
            if (token != null && !token.isBlank()) {
                template.header(AUTHORIZATION_HEADER, token);
            }
        };
    }
}
