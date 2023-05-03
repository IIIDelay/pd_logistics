package org.iiidev.pinda.service;

import org.iiidev.pinda.common.utils.RespResult;

import java.util.List;
import java.util.Map;

public interface DruidService {

    RespResult queryAllTruckLast(Map<String, Object> params);

    RespResult queryOneTruck(Map<String, Object> params);

    RespResult queryAll(List<Map<String, Object>> params);
}
