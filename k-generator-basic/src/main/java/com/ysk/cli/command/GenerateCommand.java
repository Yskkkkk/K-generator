package com.ysk.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.ysk.generator.MainGenerator;
import com.ysk.model.MainTemplateConfig;
import lombok.Data;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

/**
 * @author 袁赛阔
 * @date 2023-11-26 18:02
 */

@Command(name = "generate",description = "生成代码",mixinStandardHelpOptions = true)
@Data
public class GenerateCommand implements Callable<Integer> {

    /**
     * 是否生成循环
     */
    @CommandLine.Option(names = {"-l", "--loop"},  description = "是否循环",arity = "0..1", interactive = true,echo = true)
    private boolean loop;
    /**
     * 作者名称
     */
    @CommandLine.Option(names = {"-a", "--author"},  description = "作者名称",arity = "0..1", interactive = true,echo = true)
    private String author = "ysk";

    /**
     * 输出信息
     */
    @CommandLine.Option(names = {"-o", "--outputText"},  description = "输出文本",arity = "0..1", interactive = true,echo = true)
    private String outputText = " sum = ";


    @Override
    public Integer call() throws Exception {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        BeanUtil.copyProperties(this,mainTemplateConfig);
        System.out.println("配置信息：" + mainTemplateConfig);
        MainGenerator.doGenerate(mainTemplateConfig);
        return 0;
    }
}
