package com.atguigu.app.function;

import com.alibaba.fastjson.JSONObject;

import java.text.ParseException;

public interface DimAsyncJoinFunction<T> {

    String getKey(T input);

    void join(T input, JSONObject dimInfo) throws ParseException;

}
