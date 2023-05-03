package org.iiidev.pinda.service;

import org.iiidev.pinda.entity.fact.AddressRule;

public interface DroolsRulesService {
    //根据条件计算订单价格
    String calcFee(AddressRule addressRule);
}