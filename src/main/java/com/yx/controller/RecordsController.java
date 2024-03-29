package com.yx.controller;


import com.github.pagehelper.PageInfo;
import com.yx.model.PropertyInfo;
import com.yx.model.PropertyType;
import com.yx.model.RecordVo;
import com.yx.model.Records;
import com.yx.service.IPropertyInfoService;
import com.yx.service.IPropertyTypeService;
import com.yx.service.IRecordsService;
import com.yx.util.JsonObject;
import com.yx.util.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/*
 * @Author 刘东祥
 * @Date 2022/9/21
 * @Description 抄表管理
 * @Since version-1.0
 */

@RestController
@RequestMapping("/records")
public class RecordsController {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    private IRecordsService recordsService;

    @Resource
    private IPropertyTypeService propertyTypeService;

    @Resource
    private IPropertyInfoService propertyInfoService;


    /*
     * @Description 查询所有抄表信息
     * @Param 抄表实体对象，页码，每页数据数
     * @return 抄表信息列表
     */
    @RequestMapping("/queryRecordsAll")
   public JsonObject queryRecordsAll(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "15") Integer limit,
                                     RecordVo recordVo){
       PageInfo <RecordVo> pageInfo=recordsService.findRecordsAll(page,limit,recordVo);
       return new JsonObject(0,"ok",pageInfo.getTotal(),pageInfo.getList());

   }


    /*
     * @Description 新增抄表信息
     * @Param 抄表实体类
     * @return 添加抄表信息成功或者失败的信息
     */
    @RequestMapping("/add")
    @Transactional(rollbackFor = {RuntimeException.class,Error.class})
    public R add(@RequestBody Records records){
        /*
           步骤：
             1、添加抄表记录信息
                 1.1 根据记录中的房子和类型查询上次抄表的度数以及相关时间信息
                     如果存在，需要获取上次的度数，上次时间
                     如果不存在 上次度数设置为0  上次时间使用本次时间
                 1.2 添加记录信息
             2、添加物业收费信息
                2.1  获取 上次到这次之间的度数信息
                2.2 根据收费类型查询收费标准
                2.3 用1的度数×2的的收费标准 获取相关费用
                2.4  并设定为未缴费状态
                2.5 添加工作
         */
        //抄表数据不得小于0
        Double num2=records.getNum2();
        if(num2<0){
            return R.fail("抄表度数不得小于0");
        }
        //根据参数房子id和类型id 获取最后一次登记信息
        Integer houId=records.getHouseId();
        Integer typeId=records.getTypeId();
        //获取最后一次记录信息
        Records rec=recordsService.queryByHouIdAndTypeId(houId,typeId);
        if(rec!=null){
            //获取上次表的度数  上次抄表时间
            records.setUpTime(rec.getOnTime());
            records.setNum(rec.getNum2());
        }else{
            records.setUpTime(records.getOnTime());
            records.setNum(0.0);
        }

          //添加记录信息到数据库
          records.setCheckTime(new Date());
          recordsService.add(records);

          //2 添加费用信息

        PropertyInfo info=new PropertyInfo();
        info.setHouseId(houId);
        info.setTypeId(typeId);
        info.setStatus(0);//未缴费
        if(rec!=null){
            info.setStartDate(rec.getUpTime());
        }else {
            info.setStartDate(records.getOnTime());
        }
        info.setEndDate(records.getOnTime());

        //根据类型的id查询类型的费用标准
        PropertyType type=propertyTypeService.findById(new Long(typeId));
        double  price=type.getPrice();//获取收费标准
        //获取度数
        double money=(records.getNum2()-records.getNum())*price;
        info.setMoney(money);
        info.setRemarks(records.getRemarks());
        //添加记录信息
        int num= propertyInfoService.add(info);

        if(num>0){
            return R.ok();
        }else{
            return R.fail("异常");
        }
      }

    /*
     * @Description 删除单条抄表信息
     * @Param 抄表信息主键
     * @return 删除抄表信息成功或失败信息
     */
      @RequestMapping("/deleteByIds")
      @Transactional(rollbackFor = {RuntimeException.class,Error.class})
      public R deleteByIds(String ids){
           //把字符串转list集合
           List<String> list=Arrays.asList(ids.split(","));
           for(String id : list){
               Long idLong=Long.parseLong(id);
               //根据id获取对应的记录信息获取登记时间以及房子id
               Records records=recordsService.findById(idLong);
               //获取房子id
               Integer houId=records.getHouseId();
               //获取时间
              // Date onTime=records.getOnTime();
               //删除登记表记录信息
               recordsService.delete(idLong);
               //物业收费信息表相关信息
               //propertyInfoService.deleteInfoByHouIdAndTime(houId ,onTime);
           }
          return R.ok();
      }

}
