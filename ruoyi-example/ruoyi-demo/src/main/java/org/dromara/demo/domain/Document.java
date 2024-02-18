package org.dromara.demo.domain;

import lombok.Data;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.IdType;

/**
 * 文档实体
 */
@Data
@IndexName("demo_doc")
public class Document {

    /**
     * es中的唯一id
     */
    @IndexId(type = IdType.NONE)
    private String id;

    /**
     * 文档标题
     */
    @IndexField("title")
    private String title;

    /**
     * 文档内容
     */
    @IndexField("content")
    private String content;
}
