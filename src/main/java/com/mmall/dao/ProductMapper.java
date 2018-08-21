package com.mmall.dao;

import com.mmall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectList();

    List<Product> selectByNameAndProductId(@Param("productName")String productName,@Param("productId") Integer productId);

    List<Product> selectByNameAndCategoryIds(@Param("productName")String productName,@Param("categoryIdList")List<Integer> categoryIdList);

    //用用int，因为查不到就会返回错误，
    // 如果用Integer，查不到就会返回Null类型,因为可能有的商品已经下架了，查不到对应的商品库存
    //用主键的悲观锁，为了保证库存的一致性

    /**
     * 运用锁表的悲观锁，保证数据的一致性，但是一定要用主键where语句，否则将会导致锁表，同时还应该直接MySql的InnoDB引擎
     * @param id
     * @return
     */
     Integer selectStockByProductId(Integer id);


}