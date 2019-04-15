package com.xmcc.utils;

import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyDateConverter implements Converter<String,Date> {

    @Override
    public Date convert(String source) {
        if(source != null){

            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = format.parse(source);
                return date;
            } catch (ParseException e) {
                e.printStackTrace();
                throw new RuntimeException("类型转换错误");
            }
        }
       throw new RuntimeException("参数不能为空");
    }
}
