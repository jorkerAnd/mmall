package com.mmall.dao;

import com.mmall.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);


    Order selectByOrderNo(Long orderNo);


    List<Order> selectByUserId(Integer userId);


    List<Order> selectAllOrder();


    //二期新增定时关单

    /**
     *Ps:很重要
     * xml文件中中的<  >是非法的，需要将其进行转移，用CDATA进行转义
     * @param status
     * @param date
     * @return
     */
    //todo 可以用String类型的时间和数据库中的Date进行比较

    List<Order> selectOrderStatusByCreateTime(@Param("status") Integer status, @Param("date") String date);


    int closeOrderByOrderId(Integer orderNo);


}