package com.tuling.datasource.mapper;

import com.tuling.datasource.entity.Storage;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @author Fox
 */

@Repository
public interface StorageMapper {
    
    /**
     * 获取库存
     * @param commodityCode 商品编号
     * @return
     */
    @Select("SELECT id,commodity_code,count FROM storage_tbl WHERE commodity_code = #{commodityCode}")
    Storage findByCommodityCode(@Param("commodityCode") String commodityCode);
    
    /**
     * 扣减库存
     * @param commodityCode 商品编号
     * @param count  要扣减的库存
     * @return
     */
    @Update("UPDATE storage_tbl SET count = count - #{count} WHERE commodity_code = #{commodityCode}")
    int reduceStorage(@Param("commodityCode") String commodityCode,@Param("count") Integer count);

    @Insert( "insert into storage_tbl (commodity_code,count) values (#{commodityCode},#{count})")
    void insertOne(@Param("commodityCode") String commodityCode,@Param("count") Integer count);
}
