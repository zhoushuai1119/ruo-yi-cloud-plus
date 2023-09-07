package org.dromara.demo;

import com.baomidou.dynamic.datasource.toolkit.CryptoUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @description:
 * @author: zhou shuai
 * @date: 2023/9/6 14:15
 * @version: v1
 */
public class CryptoUtilTest {

    /**
     * https://www.kancloud.cn/tracy5546/dynamic-datasource/2280963
     *
     * @author: zhou shuai
     * @date: 2023/9/6 14:32
     */
    @DisplayName("测试 mysql加密 方法")
    @Test
    public void testCryptoUtil() throws Exception {
        String[] arr = CryptoUtils.genKeyPair(512);
        System.out.println("privateKey:  " + arr[0]);
        System.out.println("publicKey:  " + arr[1]);
        System.out.println("username:  " + CryptoUtils.encrypt(arr[0], "root"));
        System.out.println("password:  " + CryptoUtils.encrypt(arr[0], "Zs11195310"));
    }

}
