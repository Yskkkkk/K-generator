package com.ysk.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ysk.maker.meta.Meta;
import com.ysk.maker.meta.enums.FileGenerateTypeEnum;
import com.ysk.maker.meta.enums.FileTypeEnum;

import javax.management.modelmbean.ModelMBeanInfo;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TemplateMaker {

    private static long makeTemplate(Meta newMeta, String originProjectPath, String inputFilePath, Meta.ModelConfig.ModelInfo modelInfo,String searchStr,Long id){
        // 没有 id 则生成
        if(id == null){
            id = IdUtil.getSnowflakeNextId();
        }

        // 复制目录
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;

        // 是否为首次制作模板
        // 目录不存在，则是首次制作
        if(!FileUtil.exist(templatePath)){
            FileUtil.mkdir(templatePath);
            FileUtil.copy(originProjectPath,templatePath,true);
        }

        // 一、输入信息
        // 输入文件信息
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();
        // 对路径进行转义
        sourceRootPath = FileUtil.normalize(sourceRootPath);

        String fileInputPath = inputFilePath;
        String fileOutputPath = fileInputPath + ".ftl";

        // 二、使用字符串替换，生成模板文件
        String fileInputAbsolutePath = sourceRootPath + File.separator + fileInputPath;
        String fileOutputAbsolutePath = sourceRootPath + File.separator + fileOutputPath;

        String fileContent = null;
        // 如果已有模板文件，说明不是第一次制作，则再模板基础上再次挖坑
        if(FileUtil.exist(fileOutputAbsolutePath)){
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else{
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        String replacement = String.format("${%s}",modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent,searchStr,replacement);

        // 输出模板文件
        FileUtil.writeUtf8String(newFileContent,fileOutputAbsolutePath);

        // 文件配置信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        // 三、生成配置文件
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";

        // 如果已有 meta 文件，说明不是第一次制作，则在 meta 基础上进行修改
        if(FileUtil.exist(metaOutputPath)){
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath),Meta.class);
            BeanUtil.copyProperties(newMeta,oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;

            // 追加配置参数
            List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
            fileInfoList.add(fileInfo);

            List<Meta.ModelConfig.ModelInfo> modelInfoList = newMeta.getModelConfig().getModels();
            modelInfoList.add(modelInfo);

            // 配置去重
            newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));
        } else{
            // 构造配置参数
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            newMeta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);

            fileInfoList.add(fileInfo);

            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            newMeta.setModelConfig(modelConfig);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);
            modelInfoList.add(modelInfo);
        }

        // 输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta),metaOutputPath);
        return id;
    }


    public static void main(String[] args) {
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "k-generator-demo-projects/acm-template";
        String inputFilePath = "src/com/yupi/acm/MainTemplate.java";

//        // 模型参数信息（首次）
//        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
//        modelInfo.setFieldName("outputText");
//        modelInfo.setType("String");
//        modelInfo.setDefaultValue("sum = ");

        // 模型参数信息（第二次）
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("className");
        modelInfo.setType("String");

        // 替换变量（首次）
//        String searchStr = "Sum: ";
        // 替换变量（第二次）
        String searchStr = "MainTemplate";
        long id = makeTemplate(meta,originProjectPath,inputFilePath,modelInfo,searchStr,1L);
        System.out.println(id);
    }

    /**
     * 模型去重
     *
     * @param modelInfoList
     * @return
     */
    private static  List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList){
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(modelInfoList.stream().collect(
                Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName,o -> o,(e,r) -> r)).values());
        return newModelInfoList;
    }

    /**
     * 文件去重
     *
     * @param modelInfoList
     * @return
     */
    private static  List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList){
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(fileInfoList.stream().collect(
                Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath,o -> o,(e,r) -> r)).values());
        return newFileInfoList;
    }
}
