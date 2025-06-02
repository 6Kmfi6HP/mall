package com.macro.mall.mapper;

import com.macro.mall.model.MerMerchantLevel;
import com.macro.mall.model.MerMerchantLevelExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MerMerchantLevelMapper {
    long countByExample(MerMerchantLevelExample example);

    int deleteByExample(MerMerchantLevelExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MerMerchantLevel row);

    int insertSelective(MerMerchantLevel row);

    List<MerMerchantLevel> selectByExampleWithBLOBs(MerMerchantLevelExample example);

    List<MerMerchantLevel> selectByExample(MerMerchantLevelExample example);

    MerMerchantLevel selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") MerMerchantLevel row, @Param("example") MerMerchantLevelExample example);

    int updateByExampleWithBLOBs(@Param("row") MerMerchantLevel row, @Param("example") MerMerchantLevelExample example);

    int updateByExample(@Param("row") MerMerchantLevel row, @Param("example") MerMerchantLevelExample example);

    int updateByPrimaryKeySelective(MerMerchantLevel row);

    int updateByPrimaryKeyWithBLOBs(MerMerchantLevel row);

    int updateByPrimaryKey(MerMerchantLevel row);
}
