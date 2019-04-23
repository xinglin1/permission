package com.xmcc.service;

import java.io.*;

/**
 * @author 张兴林
 * @date 2019-04-23 16:39
 */

/**
 * 敏感词汇屏蔽工具类
 */
public class ShiledUtil {

    public static String shiled(String content) throws IOException {
        File file = new File("C:\\Users\\zhang\\Desktop\\论文等资料\\敏感词汇.txt");
        FileInputStream fis = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis,"utf-8"));
        String line = null;
        while ((line = reader.readLine()) != null){
            content = content.replaceAll(line, "**");
        }
        return content;
    }

}
