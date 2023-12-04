package com.ysk.maker.model;

import lombok.Data;

/**
 * 动态模板配置
 */
@Data
public class DataModel {

    /**
     * 作者（字符串，填充值）
     */
    private String author = "ysk";

    /**
     * 输出信息
     */
    private String outputText = " sum = ";

    /**
     * 是否生成循环
     */
    private boolean loop;
}
