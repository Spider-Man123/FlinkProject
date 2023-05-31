package com.atguigu.gmallpublishertest.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductStatsMapper {

    @Select("select sum(order_amount) from product_stats_210325 where toYYYYMMDD(stt)=#{date}")
    BigDecimal selectGmv(int date);

    /**
     * ┌─tm_name─┬─order_amount─┐
     * │ 苹果    │    279501.00 │
     * │ 华为    │    206697.00 │
     * │ TCL     │    182378.00 │
     * │ 小米    │    100278.00 │
     * │ Redmi   │     31872.00 │
     * └─────────┴──────────────┘
     *
     * Map[("tm_name"->"苹果"),(order_amount->279501)]  ==> [("苹果"->279501),...]
     */
    @Select("select tm_name,sum(order_amount) order_amount from product_stats_210325 where toYYYYMMDD(stt)=#{date} group by tm_name order by order_amount desc limit #{limit}")
    List<Map> selectGmvByTm(@Param("date") int date, @Param("limit") int limit);

}
