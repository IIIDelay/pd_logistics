package org.iiidev.pinda.service;

import org.iiidev.pinda.vo.BusinessDataVO;
import org.iiidev.pinda.vo.DishOverViewVO;
import org.iiidev.pinda.vo.OrderOverViewVO;
import org.iiidev.pinda.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

public interface WorkspaceService {

    /**
     * 根据时间段统计营业数据
     *
     * @param begin
     * @param end
     * @return
     */
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);

    /**
     * 查询订单管理数据
     *
     * @return
     */
    OrderOverViewVO getOrderOverView();

    /**
     * 查询菜品总览
     *
     * @return
     */
    DishOverViewVO getDishOverView();

    /**
     * 查询套餐总览
     *
     * @return
     */
    SetmealOverViewVO getSetmealOverView();

}
