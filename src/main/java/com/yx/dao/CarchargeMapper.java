package com.yx.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yx.model.Carcharge;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author kappy
 * @since 2020-11-08
 */
@Component("carchargeDao")
public interface CarchargeMapper extends BaseMapper<Carcharge> {

    List<Carcharge> queryCarChargeAll(Carcharge carcharge);

    List<Carcharge> queryCarChargeAll2(Carcharge carcharge);

}
