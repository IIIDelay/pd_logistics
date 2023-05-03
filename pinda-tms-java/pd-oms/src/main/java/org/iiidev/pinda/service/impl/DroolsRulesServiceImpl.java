package org.iiidev.pinda.service.impl;

import org.iiidev.pinda.entity.fact.AddressRule;
import org.iiidev.pinda.service.DroolsRulesService;

import java.math.BigDecimal;

public class DroolsRulesServiceImpl implements DroolsRulesService {
    //根据条件计算订单价格
    public String calcFee(AddressRule addressRule) {
        BigDecimal lost = new BigDecimal(addressRule.getTotalWeight()).subtract(new BigDecimal(addressRule.getFirstWeight()));
        lost = lost.setScale(0,BigDecimal.ROUND_DOWN);
        BigDecimal continuedFee = lost.multiply(new BigDecimal(addressRule.getContinuedFee()));
        return continuedFee.add(new BigDecimal(addressRule.getFirstFee())).toString();
    }
}