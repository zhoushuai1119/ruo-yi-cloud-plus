DROP TABLE IF EXISTS `metric`;
CREATE TABLE `metric`
(
    `id`            bigint         NOT NULL AUTO_INCREMENT COMMENT 'id',
    `app`           varchar(255)   NULL DEFAULT NULL COMMENT '应用名称',
    `resource`      varchar(255)   NULL DEFAULT NULL COMMENT '资源名称',
    `timestamp`     datetime       NULL DEFAULT NULL COMMENT '监控信息时间戳',
    `gmt_create`    datetime       NULL DEFAULT NULL COMMENT '创建时间',
    `gmt_modified`  datetime       NULL DEFAULT NULL COMMENT '修改时间',
    `pass_qps`      bigint         NULL DEFAULT NULL COMMENT '通过QPS',
    `success_qps`   bigint         NULL DEFAULT NULL COMMENT '成功QPS',
    `block_qps`     bigint         NULL DEFAULT NULL COMMENT '限流QPS',
    `exception_qps` bigint         NULL DEFAULT NULL COMMENT '异常QPS',
    `rt`            decimal(10, 2) NULL DEFAULT NULL COMMENT '资源的平均响应时间',
    `count`         int            NULL DEFAULT NULL COMMENT '本次聚合的总条数',
    `resource_code` int            NULL DEFAULT NULL COMMENT '资源hashcode',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_app_timestamp` (`app` ASC, `timestamp` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 COMMENT = 'Sentinel监控信息表';
