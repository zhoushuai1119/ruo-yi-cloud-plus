package org.dromara.system.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.system.service.IDatabaseDocService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;

/**
 * @description:
 * @author: zhou shuai
 * @date: 2023/11/21 16:19
 * @version: v1
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DatabaseDocServiceImpl implements IDatabaseDocService {

    private final DynamicDataSourceProperties dynamicDataSourceProperties;

    private static final String FILE_OUTPUT_DIR = System.getProperty("java.io.tmpdir") + File.separator + "db-doc";
    private static final String DOC_FILE_NAME = "数据库文档";
    private static final String DOC_VERSION = "2.1.1";
    private static final String DOC_DESCRIPTION = "ruoyi-cloud-plus数据库文档";

    /**
     * 导出数据库文档
     *
     * @author: zhou shuai
     * @date: 2023/11/21 16:20
     * @param: fileOutputType
     */
    @Override
    public void doExportFile(EngineFileType fileOutputType) {
        HttpServletResponse response = ServletUtils.getResponse();
        String docFileName = DOC_FILE_NAME + "_" + IdUtil.fastSimpleUUID();
        String filePath = doExportFile(fileOutputType, docFileName);
        //下载后的文件名
        String downloadFileName = DOC_FILE_NAME + fileOutputType.getFileSuffix();
        try {
            // 读取，返回
            JakartaServletUtil.write(response, FileUtil.getInputStream(filePath), MediaType.APPLICATION_OCTET_STREAM_VALUE, downloadFileName);
        } finally {
            handleDeleteFile(filePath);
        }
    }

    /**
     * 输出文件，返回文件路径
     *
     * @param fileOutputType 文件类型
     * @param fileName       文件名, 无需 ".docx" 等文件后缀
     * @return 生成的文件所在路径
     */
    private String doExportFile(EngineFileType fileOutputType, String fileName) {
        try (HikariDataSource dataSource = buildDataSource()) {
            // 创建 screw 的配置
            Configuration config = Configuration.builder()
                .version(DOC_VERSION)  // 版本
                .description(DOC_DESCRIPTION) // 描述
                .dataSource(dataSource) // 数据源
                .engineConfig(buildEngineConfig(fileOutputType, fileName)) // 引擎配置
                .produceConfig(buildProcessConfig()) // 处理配置
                .build();

            // 执行 screw，生成数据库文档
            new DocumentationExecute(config).execute();
            return FILE_OUTPUT_DIR + File.separator + fileName + fileOutputType.getFileSuffix();
        }
    }

    /**
     * 创建数据源
     */
    private HikariDataSource buildDataSource() {
        String dbKey = DynamicDataSourceContextHolder.peek();
        DataSourceProperty dataSourceProperty = dynamicDataSourceProperties.getDatasource().get(dbKey);
        // 创建 HikariConfig 配置类
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(dataSourceProperty.getUrl());
        hikariConfig.setUsername(dataSourceProperty.getUsername());
        hikariConfig.setPassword(dataSourceProperty.getPassword());
        //设置可以获取tables remarks信息
        hikariConfig.addDataSourceProperty("useInformationSchema", "true");
        // 创建数据源
        return new HikariDataSource(hikariConfig);
    }

    /**
     * 创建 screw 的引擎配置
     */
    private EngineConfig buildEngineConfig(EngineFileType fileOutputType, String docFileName) {
        return EngineConfig.builder()
            .fileOutputDir(FILE_OUTPUT_DIR) // 生成文件路径
            .openOutputDir(false) // 打开目录
            .fileType(fileOutputType) // 文件类型
            .produceType(EngineTemplateType.freemarker) // 文件类型
            .fileName(docFileName) // 自定义文件名称
            .build();
    }

    /**
     * 创建 screw 的处理配置，一般可忽略
     * 指定生成逻辑、当存在指定表、指定表前缀、指定表后缀时，将生成指定表，其余表不生成、并跳过忽略表配置
     */
    private static ProcessConfig buildProcessConfig() {
        //忽略表
        ArrayList<String> ignoreTableName = new ArrayList<>();
        ignoreTableName.add("test_user");
        ignoreTableName.add("test_group");
        //忽略表前缀
        ArrayList<String> ignorePrefix = new ArrayList<>();
        ignorePrefix.add("test_");
        //忽略表后缀
        ArrayList<String> ignoreSuffix = new ArrayList<>();
        ignoreSuffix.add("_test");
        ProcessConfig processConfig = ProcessConfig.builder()
            //指定生成逻辑、当存在指定表、指定表前缀、指定表后缀时，将生成指定表，其余表不生成、并跳过忽略表配置
            //根据名称指定表生成
            .designatedTableName(new ArrayList<>())
            //根据表前缀生成
            .designatedTablePrefix(new ArrayList<>())
            //根据表后缀生成
            .designatedTableSuffix(new ArrayList<>())
            //忽略表名
            .ignoreTableName(ignoreTableName)
            //忽略表前缀
            .ignoreTablePrefix(ignorePrefix)
            //忽略表后缀
            .ignoreTableSuffix(ignoreSuffix).build();
        return processConfig;
    }

    private void handleDeleteFile(String filePath) {
        try {
            FileUtil.del(filePath);
        } catch (Exception e) {
            // ingore
        }
    }

}
