package com.macro.mall.mapper;

import com.macro.mall.model.OmsOrderItemCommission;
import com.macro.mall.model.OmsOrderItemCommissionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OmsOrderItemCommissionMapper {
    long countByExample(OmsOrderItemCommissionExample example);

    int deleteByExample(OmsOrderItemCommissionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsOrderItemCommission row);

    int insertSelective(OmsOrderItemCommission row);

    List<OmsOrderItemCommission> selectByExample(OmsOrderItemCommissionExample example);

    OmsOrderItemCommission selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") OmsOrderItemCommission row, @Param("example") OmsOrderItemCommissionExample example);

    int updateByExample(@Param("row") OmsOrderItemCommission row, @Param("example") OmsOrderItemCommissionExample example);

    int updateByPrimaryKeySelective(OmsOrderItemCommission row);

    int updateByPrimaryKey(OmsOrderItemCommission row);

    // Consider adding a batchInsert method if performance for many items is critical
    // int batchInsert(@Param("list") List<OmsOrderItemCommission> list);
}
