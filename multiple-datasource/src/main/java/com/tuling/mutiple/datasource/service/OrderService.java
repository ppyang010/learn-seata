package com.tuling.mutiple.datasource.service;


import com.tuling.mutiple.datasource.entity.Order;
import com.tuling.mutiple.datasource.vo.OrderVo;

public interface OrderService {

    /**
     * 保存订单
     */
    Order saveOrder(OrderVo orderVo);
}