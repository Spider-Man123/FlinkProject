package com.atguigu.gmallpublishertest.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmallpublishertest.service.SugarService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/sugar")
public class SugarController {

    @Autowired
    private SugarService sugarService;

    @RequestMapping("/tm")
    public String getGmvByTm(@RequestParam(value = "date", defaultValue = "0") int date,
                             @RequestParam(value = "limit", defaultValue = "5") int limit) {

        if (date == 0) {
            date = getToday();
        }

        Map gmvByTm = sugarService.getGmvByTm(date, limit);
        Set keySet = gmvByTm.keySet();
        Collection values = gmvByTm.values();

        return "{ " +
                "  \"status\": 0, " +
                "  \"msg\": \"\", " +
                "  \"data\": { " +
                "    \"categories\": [\"" +
                StringUtils.join(keySet,"\",\"") +
                "\"], " +
                "    \"series\": [ " +
                "      { " +
                "        \"name\": \"商品品牌\", " +
                "        \"data\": [" +
                StringUtils.join(values,",") +
                "] " +
                "      } " +
                "    ] " +
                "  } " +
                "}";
    }

    /**
     * {
     * "status": 0,
     * "msg": "",
     * "data": 1201059.128122374
     * }
     */
    @RequestMapping("/gmv")
    public String getGmv(@RequestParam(value = "date", defaultValue = "0") int date) {

        if (date == 0) {
            date = getToday();
        }

        HashMap<String, Object> result = new HashMap<>();
        result.put("status", 0);
        result.put("msg", "");
        result.put("data", sugarService.getGmv(date));

//        return "        { " +
////                "          \"status\": 0, " +
////                "          \"msg\": \"\", " +
////                "          \"data\": " + sugarService.getGmv(date) + " " +
////                "        }";

        return JSON.toJSONString(result);
    }

    private int getToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateTime = sdf.format(System.currentTimeMillis());
        return Integer.parseInt(dateTime);
    }

}
