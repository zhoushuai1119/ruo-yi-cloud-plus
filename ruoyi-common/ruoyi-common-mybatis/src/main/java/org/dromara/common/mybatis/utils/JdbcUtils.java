package org.dromara.common.mybatis.utils;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author: zhou shuai
 * @date: 2023/11/13 21:38
 * @version: v1
 */
public class JdbcUtils {

    /**
     * 判断连接是否正确
     *
     * @param url      数据源连接
     * @param username 账号
     * @param password 密码
     * @return 是否正确
     */
    public static boolean isConnectionOK(String url, String username, String password) {
        try (Connection ignored = DriverManager.getConnection(url, username, password)) {
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
