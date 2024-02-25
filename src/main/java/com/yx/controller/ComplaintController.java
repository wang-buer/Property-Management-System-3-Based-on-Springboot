package com.yx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import com.yx.model.Complaint;
import com.yx.service.IComplaintService;
import com.yx.service.IOwnerService;
import com.yx.util.JsonObject;
import com.yx.util.R;
import com.yx.model.Owner;
import com.yx.model.Userinfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/*
 * @Author 萧易
 * @Date 2022/9/29
 * @Description
 * @Since version-1.0
 */

@Api(tags = {""})
@RestController
@RequestMapping("/complaint")
public class ComplaintController {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    private IComplaintService complaintService;
    @Resource
    private IOwnerService ownerService;


    /*
     * @Description 管理员投诉信息列表接口
     * @Param  投诉实体类，页数，一页的投诉数
     * @return 管理员的投诉信息列表
     */
    @RequestMapping("/queryComplaintAll")
    public JsonObject queryComplaintAll(Complaint complaint,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "15") Integer limit){


        PageInfo<Complaint> pageInfo=complaintService.findComplaintTotal(page,limit,complaint);
        return new JsonObject(0,"ok",pageInfo.getTotal(),pageInfo.getList());

    }

    /*
     * @Description 用户投诉信息列表接口
     * @Param 投诉实体类，页数，一页的投诉数
     * @return 用户的投诉信息列表
     */
    @RequestMapping("/queryComplaintAll2")
    public JsonObject queryComplaintAll2(Complaint complaint, HttpServletRequest request,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "15") Integer limit){
        //获取当前得登录用户
        Userinfo userinfo= (Userinfo) request.getSession().getAttribute("user");
        String username=userinfo.getUsername();
        //根据username获取登录账号得业主id
        Owner owner=ownerService.queryOwnerByName(username);
        complaint.setOwnerId(owner.getId());
        PageInfo<Complaint> pageInfo=complaintService.findComplaintAll(page,limit,complaint);
        return new JsonObject(0,"ok",pageInfo.getTotal(),pageInfo.getList());

    }


    /*
     * @Description 新增投诉信息
     * @Param 投诉实体类，httpservletrequest
     * @return 添加投诉成功或者失败的信息
     */
    @ApiOperation(value = "新增")
    @RequestMapping("/add")
    public R add(@RequestBody Complaint complaint,HttpServletRequest request)
    {
        //获取当前得登录用户
        Userinfo userinfo= (Userinfo) request.getSession().getAttribute("user");
        String username=userinfo.getUsername();
        //根据username获取登录账号得业主id
        Owner owner=ownerService.queryOwnerByName(username);
        complaint.setOwnerId(owner.getId());
        complaint.setStatus(0);
        complaint.setComDate(new Date());
        int num=complaintService.add(complaint);
        if(num>0){
            return  R.ok();
        }
        return R.fail("失败啦");
    }

    /*
     * @Description 删除多条投诉信息
     * @Param  投诉信息主键
     * @return 投诉信息删除成功或者失败信息
     */
    @ApiOperation(value = "删除")
    @RequestMapping("/deleteByIds")
    public R deleteByIds(String ids){
        UpdateWrapper<Complaint> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",Long.parseLong(ids)).set("status", 2);
        complaintService.update(null, updateWrapper);
        return R.ok();
    }

    /*
     * @Description 删除单条投诉信息
     * @Param 投诉信息主键
     * @return 删除投诉成功或失败信息
     */
    @RequestMapping("/delete")
    public R delete(String ids){
       complaintService.delete(Long.parseLong(ids));
        return R.ok();
    }

    /*
     * @Description 删除多条投诉信息
     * @Param  投诉信息主键
     * @return 投诉信息删除成功或者失败信息
     */
    @ApiOperation(value = "清空已处理投诉")
    @RequestMapping("/deleteOperatedIds")
    public R deleteOperatedIds(){
        UpdateWrapper<Complaint> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("status",1).set("status",2);
        boolean num = complaintService.update(null,updateWrapper);
        if(num)
        return R.ok();
        else
            return R.fail("删除失败,无已处理投诉");
    }

    /*
     * @Description 更新投诉信息
     * @Param  投诉信息主键，httpservletrequest
     * @return 投诉信息更新成功或者失败信息
     */
    @ApiOperation(value = "更新")
    @RequestMapping("/update")
    public R update(Integer id,HttpServletRequest request){
         Complaint complaint=new Complaint();
         complaint.setId(id);
         complaint.setStatus(1);
         complaint.setHandleDate(new Date());
         HttpSession session=request.getSession();
        Userinfo user = (Userinfo) session.getAttribute("user");
        complaint.setClr( user.getId());
        int num= complaintService.updateData(complaint);
        if(num>0){
            return R.ok();
        }else{
            return R.fail("处理失败");
        }
    }

    /*
     * @Description 新增投诉信息
     * @Param 投诉实体类，httpservletrequest
     * @return 添加投诉成功或者失败的信息
     */
    @ApiOperation(value = "新增")
    @RequestMapping("/updateCom")
    public R updateCom(@RequestBody Complaint complaint,HttpServletRequest request)
    {
        //获取当前得登录用户
        System.out.println(complaint);
        Userinfo userinfo= (Userinfo) request.getSession().getAttribute("user");
        String username=userinfo.getUsername();
        //根据username获取登录账号得业主id
        Owner owner=ownerService.queryOwnerByName(username);
        complaint.setOwnerId(owner.getId());
        complaint.setStatus(0);
        complaint.setComDate(new Date());
        int num=complaintService.updateData(complaint);
        if(num>0){
            return  R.ok();
        }
        return R.fail("失败啦");
    }

    /*
     * @Description 查询某一页的数据
     * @Param 页码，每页数据数
     * @return 要查询那一页数据
     */
    @ApiOperation(value = "查询分页数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "page", value = "页码"),
        @ApiImplicitParam(name = "pageCount", value = "每页条数")
    })
    @GetMapping()
    public IPage<Complaint> findListByPage(@RequestParam Integer page,
                                           @RequestParam Integer pageCount){
        return complaintService.findListByPage(page, pageCount);
    }

    /*
     * @Description 根据id查询对的报修信息
     * @Param 报修信息id
     * @return 查询的报修信息
     */
    @ApiOperation(value = "id查询")
    @GetMapping("{id}")
    public Complaint findById(@PathVariable Long id){
        return complaintService.findById(id);
    }

}
