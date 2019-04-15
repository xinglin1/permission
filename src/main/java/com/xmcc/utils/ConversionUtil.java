package com.xmcc.utils;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张兴林
 * @date 2019-03-28 14:26
 */
@Service
public class ConversionUtil {

    public static List<Integer> string2List(String s){
        if (s.length() == 0 || s == null || s == ""){
            return new ArrayList<>();
        }
        String[] split = s.split(",");
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            list.add(Integer.valueOf(split[i]));
        }
        return list;
    }

}
