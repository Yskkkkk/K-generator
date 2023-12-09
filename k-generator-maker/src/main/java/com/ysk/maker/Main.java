package com.ysk.maker;


import com.ysk.maker.generator.main.MainGenerator;
import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * @author 袁赛阔
 */
public class Main {

    public static void main(String[] args) throws TemplateException, IOException, InterruptedException, TemplateException {
        MainGenerator mainGenerator = new MainGenerator();
        mainGenerator.doGenerate();
    }
}