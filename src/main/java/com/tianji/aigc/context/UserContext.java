package com.tianji.aigc.context;

public final class UserContext {

    private static final ThreadLocal<Long> USER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void setUser(Long userId) {
        USER.set(userId);
    }

    public static Long getUser() {
        return USER.get();
    }

    public static void removeUser() {
        USER.remove();
    }
}
