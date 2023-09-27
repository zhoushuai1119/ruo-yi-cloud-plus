package org.dromara.demo.service;

/**
 * 测试分布式事务Seata Service接口
 *
 * @author Zhou Shuai
 */
public interface ITestSeataService {

    /**
     * 测试分布式事务Seata
     */
    void seataTest(String value);

}
