package com.macro.mall.mapper;

import com.macro.mall.model.MerMerchantPackage;
import com.macro.mall.model.MerMerchantPackageExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MerMerchantPackageMapper {
    long countByExample(MerMerchantPackageExample example);

    int deleteByExample(MerMerchantPackageExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MerMerchantPackage row);

    int insertSelective(MerMerchantPackage row);

    List<MerMerchantPackage> selectByExample(MerMerchantPackageExample example);

    MerMerchantPackage selectByPrimaryKey(Long id);
    
    List<MerMerchantPackage> selectByMerchantId(Long merchantId); // Custom

    int updateByExampleSelective(@Param("row") MerMerchantPackage row, @Param("example") MerMerchantPackageExample example);

    int updateByExample(@Param("row") MerMerchantPackage row, @Param("example") MerMerchantPackageExample example);

    int updateByPrimaryKeySelective(MerMerchantPackage row);

    int updateByPrimaryKey(MerMerchantPackage row);
}
