package com.ysk.maker.generator.file;

import cn.hutool.core.io.FileUtil;

/**
 * @author 袁赛阔
 * @date 2023-11-09 15:09
 *
 * 静态文件生成器
 */
public class StaticFileGenerator {

    /**
     * 拷贝文件 Hutool实现，会将输入目录完整拷贝到输出目录下
     * @param inputPath 输入目录
     * @param outputPath 输出目录
     */
    public static void copyFilesByHutool(String inputPath, String outputPath) {
        FileUtil.copy(inputPath,outputPath,false);
    }

}
