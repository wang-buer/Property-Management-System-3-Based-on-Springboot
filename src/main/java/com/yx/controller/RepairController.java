package com.yx.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageInfo;
import com.yx.model.*;
import com.yx.service.IOwnerService;
import com.yx.service.IRepairService;
import com.yx.service.IRepairtypeService;
import com.yx.util.JsonObject;
import com.yx.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/*
 * @Description 
 * @Param 萧易
 * @return 
 */
@Api(tags = {""})
@RestController
@RequestMapping("/repair")
public class RepairController {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    private IRepairService repairService;
    @Resource
    private IRepairtypeService repairtypeService;

    @Resource
    private IOwnerService ownerService;

    /*
     * @Description 管理员报修信息列表接口
     * @Param  报修实体类，页数，一页的投诉数
     * @return 管理员的投诉信息列表
     */
    @RequestMapping("/queryRepairAll")
    public JsonObject queryRepairAll(Repair repair,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "15") Integer limit){


        PageInfo<Repair> pageInfo=repairService.findRepairAll(page,limit,repair);
        System.out.println(pageInfo);
        return new JsonObject(0,"ok",pageInfo.getTotal(),pageInfo.getList());

    }

    /*
     * @Description 用户报修信息列表接口
     * @Param  报修实体类，页数，一页的投诉数
     * @return 用户的投诉信息列表
     */
    @RequestMapping("/queryRepairAll2")
    public JsonObject queryRepairAll2(Repair repair, HttpServletRequest request,
                                     @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "15") Integer limit){


        //获取当前得登录用户
        Userinfo userinfo= (Userinfo) request.getSession().getAttribute("user");
        String username=userinfo.getUsername();
        //根据username获取登录账号得业主id
        Owner owner=ownerService.queryOwnerByName(username);
        repair.setOwnerId(owner.getId());
        PageInfo<Repair> pageInfo=repairService.findRepairAll2(page,limit,repair);
        System.out.println(pageInfo);
        return new JsonObject(0,"ok",pageInfo.getTotal(),pageInfo.getList());

    }

    /*
     * @Description 更新投诉信息
     * @Param  报修信息主键，httpservletrequest
     * @return 报修信息更新成功或者失败信息
     */
    @ApiOperation(value = "更改")
    @RequestMapping("/updateRep")
    public R updateCom(@RequestBody Repair repair,HttpServletRequest request)
    {
        //获取当前得登录用户
        System.out.println(repair);
        Userinfo userinfo= (Userinfo) request.getSession().getAttribute("user");
        String username=userinfo.getUsername();
        //根据username获取登录账号得业主id
        Owner owner=ownerService.queryOwnerByName(username);
        repair.setOwnerId(owner.getId());
        repair.setStatus(0);
        repair.setComDate(new Date());
        int num=repairService.updateData(repair);
        if(num>0){
            return  R.ok();
        }
        return R.fail("失败啦");
    }

    /*
     * @Description 管理员查询报修列表接口
     * @Param
     * @return 报修列表
     */
    @RequestMapping("/queryAll")
    public List<Repairtype> queryAll(){
        System.out.println(repairtypeService.findList());
        return repairtypeService.findList();
    }

    /*
     * @Description 删除多条报修信息
     * @Param  报修信息主键
     * @return 报修信息删除成功或者失败信息
     */
    @ApiOperation(value = "删除")
    @RequestMapping("/delete")
    public R deleteByIds(String ids){
        UpdateWrapper<Repair> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",Long.parseLong(ids)).set("status", 2);
        repairService.update(null, updateWrapper);
        return R.ok();
    }
    /*
     * @Description 批量删除报修信息
     * @Param  报修id
     * @return 删除报修信息是否成功或者失败
     */
    @RequestMapping("/deleteByIds")
    public R delete(String ids){
           repairService.delete(Long.parseLong(ids));
       return R.ok();
    }

    /*
     * @Description 清空所有已处理报修
     * @Param
     * @return 返回删除成功或者失败提示信息
     */
    @ApiOperation(value = "清空已处理报修")
    @RequestMapping("/deleteAll")
    public R deleteOperatedIds(){
        UpdateWrapper<Repair> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("status",1).set("status",2);
        boolean num = repairService.update(null,updateWrapper);
        if(num)
            return R.ok();
        else
            return R.fail("删除失败,无已处理报修信息");
    }

    /*
     * @Description 新增报修信息
     * @Param 报修实体类，httpservletrequest
     * @return 添加报修成功或者失败的信息
     */
    @ApiOperation(value = "新增")
    @RequestMapping("/add")
    public R add(@RequestBody Repair repair,HttpServletRequest request)
    {
        System.out.println("-------111--------");
        System.out.println(repair);
        System.out.println("---------------");
        //获取当前得登录用户
        Userinfo userinfo= (Userinfo) request.getSession().getAttribute("user");
        String username=userinfo.getUsername();

        //根据username获取登录账号得业主id
        Owner owner=ownerService.queryOwnerByName(username);
        repair.setOwnerId(owner.getId());
        repair.setStatus(0);
        repair.setComDate(new Date());
        System.out.println("-------222--------");
        System.out.println(repair);
        System.out.println("---------------");
        int num=repairService.add(repair);
        if(num>0){
            return  R.ok();
        }
        return R.fail("新增失败");
    }

    /*
     * @Description 更新报修信息
     * @Param  报修信息主键
     * @return 报修信息更新成功或者失败信息
     */
    @ApiOperation(value = "更新")
    @RequestMapping("/update")
    public R update(Integer id){
         Repair repair=new Repair();
         repair.setId(id);
         repair.setStatus(1);
         repair.setHandleDate(new Date());
         int num=repairService.updateData(repair);
         return R.ok();
    }

    /**
     * 统计分析
     */
    @RequestMapping("/queryTongJi")
    public List<Tongji> queryTongji(){
        return repairService.queryTongji();
    }



}
