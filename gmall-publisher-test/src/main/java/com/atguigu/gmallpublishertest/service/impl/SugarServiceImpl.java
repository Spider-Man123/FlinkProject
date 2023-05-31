package com.atguigu.gmallpublishertest.service.impl;

import com.atguigu.gmallpublishertest.mapper.ProductStatsMapper;
import com.atguigu.gmallpublishertest.service.SugarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SugarServiceImpl implements SugarService {

    @Autowired
    private ProductStatsMapper productStatsMapper;

    @Override
    public BigDecimal getGmv(int date) {
        return productStatsMapper.selectGmv(date);
    }


    /**
     *List[
     * Map[("tm_name"->"苹果"),(order_amount->279501)],
     * Map[("tm_name"->"华为"),(order_amount->206697)],
     * Map[("tm_name"->"TCL"),(order_amount->279501)],
     * Map[("tm_name"->"小米"),(order_amount->279501)]
     * ]
     * ==>
     * Map[("苹果"->279501),("华为"->206697),...]
     */
    @Override
    public Map getGmvByTm(int date, int limit) {

        //查询数据
        List<Map> mapList = productStatsMapper.selectGmvByTm(date, limit);

        //创建Map用于存放结果数据
        HashMap<String, BigDecimal> result = new HashMap<>();

        //遍历mapList,将数据取出放入result   Map[("tm_name"->"苹果"),(order_amount->279501)]
        for (Map map : mapList) {
            result.put((String) map.get("tm_name"), (BigDecimal) map.get("order_amount"));
        }

        //返回结果集合
        return result;
    }
}
