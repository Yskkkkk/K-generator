package com.ysk.model;

import lombok.Data;

/**
 * @author 袁赛阔
 * @date 2023-11-10 11:21
 * 静态模板配置
 */
@Data
public class MainTemplateConfig {

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
