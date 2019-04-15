package com.xmcc.utils;

/**
 * @author 张兴林
 * @date 2019-03-21 19:10
 */
public class LevelUtil {

    //表示第一层级
    public final static String ROOT = "0";


    public final static String POINT = ".";

    public static String calculate(String parentLevel,Integer parentId){
        if (parentLevel == null){
            return ROOT;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(parentLevel).append(POINT).append(parentId);
        return sb.toString();
    }

}
