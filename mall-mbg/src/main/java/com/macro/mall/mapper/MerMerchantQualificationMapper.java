package com.macro.mall.mapper;

import com.macro.mall.model.MerMerchantQualification;
import com.macro.mall.model.MerMerchantQualificationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MerMerchantQualificationMapper {
    long countByExample(MerMerchantQualificationExample example);

    int deleteByExample(MerMerchantQualificationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MerMerchantQualification row);

    int insertSelective(MerMerchantQualification row);

    List<MerMerchantQualification> selectByExample(MerMerchantQualificationExample example);

    MerMerchantQualification selectByPrimaryKey(Long id);
    
    MerMerchantQualification selectByMerchantId(Long merchantId); // Custom method

    int updateByExampleSelective(@Param("row") MerMerchantQualification row, @Param("example") MerMerchantQualificationExample example);

    int updateByExample(@Param("row") MerMerchantQualification row, @Param("example") MerMerchantQualificationExample example);

    int updateByPrimaryKeySelective(MerMerchantQualification row);

    int updateByPrimaryKey(MerMerchantQualification row);
}
