package com.atguigu.gmallpublishertest.service;

import java.math.BigDecimal;
import java.util.Map;

public interface SugarService {

    BigDecimal getGmv(int date);

    Map getGmvByTm(int date, int limit);

}
