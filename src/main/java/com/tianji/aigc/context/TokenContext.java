package com.tianji.aigc.context;

public final class TokenContext {

    private static final ThreadLocal<String> TOKEN = new ThreadLocal<>();

    private TokenContext() {
    }

    public static void setToken(String token) {
        TOKEN.set(token);
    }

    public static String getToken() {
        return TOKEN.get();
    }

    public static void removeToken() {
        TOKEN.remove();
    }
}
