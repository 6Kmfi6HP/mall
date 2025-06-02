package com.macro.mall.mapper;

import com.macro.mall.model.MerMerchant;
import com.macro.mall.model.MerMerchantExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MerMerchantMapper {
    long countByExample(MerMerchantExample example);

    int deleteByExample(MerMerchantExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MerMerchant row);

    int insertSelective(MerMerchant row);

    List<MerMerchant> selectByExample(MerMerchantExample example);

    MerMerchant selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") MerMerchant row, @Param("example") MerMerchantExample example);

    int updateByExample(@Param("row") MerMerchant row, @Param("example") MerMerchantExample example);

    int updateByPrimaryKeySelective(MerMerchant row);

    int updateByPrimaryKey(MerMerchant row);
}
