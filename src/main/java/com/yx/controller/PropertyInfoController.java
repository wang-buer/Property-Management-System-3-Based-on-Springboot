package com.yx.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import com.yx.model.*;
import com.yx.service.IHouseService;
import com.yx.service.IOwnerService;
import com.yx.service.IPropertyInfoService;
import com.yx.service.IPropertyTypeService;
import com.yx.util.JsonObject;
import com.yx.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
 * @Author 龚安兴
 * @Date 2022/9/23
 * @Description 物业收费管理
 * @Since version-1.0
 */
@Api(tags = {""})
@RestController
@RequestMapping("/propertyinfo")
public class PropertyInfoController {
    //记录日志
    private Logger log = LoggerFactory.getLogger(getClass());
    //物业收费信息服务接口
    @Resource
    private IPropertyInfoService propertyInfoService;
    //房屋信息服务接口
    @Resource
    private IHouseService houseService;
    //业主信息服务接口
    @Resource
    private IOwnerService ownerService;
    //物业类型服务接口
    @Resource
    private IPropertyTypeService propertyTypeService;


    /*
     * @Description 管理员根据参数条件查询物业收费列表
     * @Param Property:查询条件为前端搜索框中的缴费状态以Property实体类接收
     * @Param numbers:查询条件为前端搜索框中的房间号
     * @Param page,limit:分页查询条件
     * @return 物业收费列表
     */
    @RequestMapping("/queryPropertyAll")
    public JsonObject queryPropertyAll(PropertyInfo propertyInfo, String numbers,
                                       @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "15") Integer limit){
        if(numbers!=null){
            House house=new House();
            house.setNumbers(numbers);
            propertyInfo.setHouse(house);
        }

        PageInfo<PropertyInfo> pageInfo=propertyInfoService.findPropertyInfoAll(page,limit,propertyInfo);
        return new JsonObject(0,"ok",pageInfo.getTotal(),pageInfo.getList());

    }

    /*
     * @Description 业主根据参数条件查询物业收费列表
     * @Param ProperInfo:查询条件为前端搜索框中的缴费状态以ProperInfo实体类接收
     * @Param page,limit:分页查询条件
     * @return 物业收费列表
     */
    @RequestMapping("/queryPropertyAll2")
    public JsonObject queryPropertyAll2(PropertyInfo propertyInfo, HttpServletRequest request,
                                       @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "15") Integer limit){
        Userinfo userinfo= (Userinfo) request.getSession().getAttribute("user");
        String username=userinfo.getUsername();
        //根据username获取登录账号得业主id
        Owner owner=ownerService.queryOwnerByName(username);
        Integer houId= owner.getHouseId();
        propertyInfo.setHouseId(houId);
        PageInfo<PropertyInfo> pageInfo=propertyInfoService.findPropertyInfoAll(page,limit,
                propertyInfo);
        return new JsonObject(0,"ok",pageInfo.getTotal(),pageInfo.getList());

    }


    /*
     * @Description 添加物业收费数据
     * @Param ProperInfo:添加物业收费页面的数据以ProperInfo实体类接收
     * @return R:返回结果类（存放返回结果的数据，返回状态和返回信息）
     */
    @ApiOperation(value = "添加物业收费数据")
    @RequestMapping("/addData")
    public R AddData(@RequestBody PropertyInfo propertyInfo){
        Date startDate = propertyInfo.getStartDate();
        Date endDate = propertyInfo.getEndDate();
        //缴费起始时间不能大于截止时间
        if(startDate.getTime()>endDate.getTime()){
            System.out.println("时间设置错误");
            return R.fail("添加失败，缴费起始时间不能大于截止时间");
        }
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        long start1 = start.getTimeInMillis();
        long end1 = end.getTimeInMillis();
        long diffdate=(end1-start1)/(24*60*60*1000);
        if(diffdate>30){
            System.out.println("时间设置错误");
            return R.fail("添加失败，缴费截止时间比缴费开始时间不能多于30天");
        }
        //输入金额不能小于等于0大于10000
        if(propertyInfo.getMoney()<=0){
            return R.fail(300,"输入金额不能小于等于0");
        }else if(propertyInfo.getMoney()>=10000){
            return R.fail(300,"物业费不得大于10000");
        }
        propertyInfo.setStatus(0);
        int num = propertyInfoService.add(propertyInfo);
        if(num>0){
            return R.ok("添加成功");}
        else return R.fail("添加失败");
    }

    /*
     * @Description 删除或批量删除物业收费
     * @Param ids:物业收费编号id列表
     * @return R:返回结果类（存放返回结果的数据，返回状态和返回信息）
     */
    @ApiOperation(value = "删除")
    @RequestMapping("/deleteByIds")
    public R delete(String ids){
        List<String> list= Arrays.asList(ids.split(","));
        for(String id:list){
            Long idLong=new Long(id);
            propertyInfoService.delete(idLong);
        }
        return R.ok();
    }

    /*
     * @Description 物业缴费（修改缴费状态）
     * @Param ids:物业收费编号id
     * @return R:返回结果类（存放返回结果的数据，返回状态和返回信息）
     */
    @ApiOperation(value = "缴费")
    @RequestMapping("/pay")
    public R update(Integer id){
        PropertyInfo propertyInfo =new PropertyInfo();
        propertyInfo.setId(id);
        propertyInfo.setStatus(1);
        int num=propertyInfoService.updateData(propertyInfo);
        if(num>0){
            return R.ok();
        }
        return R.fail("失败");
    }
    /*
     * @Description 修改物业收费数据
     * @Param PropertyInfo:添加物业收费页面的数据以PropertyInfo实体类接收
     * @return R:返回结果类（存放返回结果的数据，返回状态和返回信息）
     */
    @ApiOperation(value = "修改")
    @RequestMapping("/editData")
    public R update(@RequestBody PropertyInfo propertyInfo){
        Date startDate = propertyInfo.getStartDate();
        Date endDate = propertyInfo.getEndDate();
        //缴费起始时间不能大于截止时间
        if(startDate.getTime()>endDate.getTime()){
            System.out.println("时间设置错误");
            return R.fail("修改失败，缴费起始时间不能大于截止时间");
        }
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        long start1 = start.getTimeInMillis();
        long end1 = end.getTimeInMillis();
        long diffdate=(end1-start1)/(24*60*60*1000);
       if(diffdate>30){
           System.out.println("时间设置错误");
            return R.fail("添加失败，缴费截止时间比缴费开始时间不能多于30天");
       }
        //输入金额不能小于等于0大于10000
        if(propertyInfo.getMoney()<=0){
            return R.fail(300,"输入金额不能小于等于0");
        }else if(propertyInfo.getMoney()>10000){
            return R.fail(300,"物业费每月不得大于10000");
        }
        int num=propertyInfoService.updateData(propertyInfo);
        if(num>0){
            return R.ok("修改成功");
        }
        return R.fail("修改失败");
    }


    /*
     * @Description 查询分页物业收费列表
     * @Param page,pageCount:分页查询条件
     * @return 返回物业收费列表
     */
    @ApiOperation(value = "查询分页数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "page", value = "页码"),
        @ApiImplicitParam(name = "pageCount", value = "每页条数")
    })
    @GetMapping()
    public IPage<PropertyInfo> findListByPage(@RequestParam Integer page,
                                              @RequestParam Integer pageCount){
        return propertyInfoService.findListByPage(page, pageCount);
    }

    /*
     * @Description 通过物业收费编号获取修改页面需要回显的数据
     * @Param id: 物业收费编号
     * @return Carcharge:返回需要修改的初始的物业收费数据
     */
    @ApiOperation(value = "id查询")
    @GetMapping("{id}")
    public PropertyInfo findById(@PathVariable Long id){
        return propertyInfoService.findById(id);
    }

}
