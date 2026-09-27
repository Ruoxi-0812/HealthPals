package cn.kmbeast.context;

import java.util.HashMap;
import java.util.Map;

/**
 * User Identity Support Utility
 */
public class LocalThreadHolder {

    private static final ThreadLocal<Map<String, Integer>> USER_HOLDER = new ThreadLocal<>();

    /**
     * Store user information
     *
     * @param userId   User ID
     * @param userRole User Role
     */
    public static void setUserId(Integer userId, Integer userRole) {
        Map<String, Integer> map = new HashMap<>();
        map.put("userId", userId);
        map.put("userRole", userRole);
        USER_HOLDER.set(map);
    }

    /**
     * Retrieve the user ID
     *
     * @return Integer
     */
    public static Integer getUserId() {
        return USER_HOLDER.get() == null ? null : USER_HOLDER.get().get("userId");
    }

    /**
     * Retrieve the user role
     *
     * @return Integer
     */
    public static Integer getRoleId() {
        return USER_HOLDER.get() == null ? null : USER_HOLDER.get().get("userRole");
    }

    /**
     * Prevent memory leaks by releasing resources when the current thread ends
     */
    public static void clear() {
        USER_HOLDER.remove();
    }

}
