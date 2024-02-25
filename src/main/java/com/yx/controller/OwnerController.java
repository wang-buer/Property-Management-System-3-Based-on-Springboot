package com.yx.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import com.yx.model.Owner;
import com.yx.model.Userinfo;
import com.yx.service.IOwnerService;
import com.yx.service.IUserinfoService;
import com.yx.util.JsonObject;
import com.yx.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/*
 * @Author 徐文定
 * @Date 2022/9/21
 * @Description 业主管理
 * @Since version-1.0
 */

@Api(tags = {""})
@RestController
@RequestMapping("/owner")
public class OwnerController {

    //记录日志
    private Logger log = LoggerFactory.getLogger(getClass());
    //业主服务层接口
    @Resource
    private IOwnerService ownerService;
    //用户服务层接口
    @Resource
    private IUserinfoService userinfoService;

    /*
     * @Description 管理员根据参数条件查询业主列表
     * @Param Owner:查询条件以Owner实体类接收
     * @Param page,limit:分页查询条件
     * @return 业主列表
     */
    @RequestMapping("/queryOwnerAll")
    public JsonObject queryOwnerAll(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "15") Integer limit,
                                    Owner owner){
        PageInfo<Owner> pageInfo= ownerService.findOwnerAll(page,limit,owner);
        return new JsonObject(0,"ok",pageInfo.getTotal(),pageInfo.getList());

    }

    /*
     * @Description 查询业主列表
     * @Param 无
     * @return 业主列表
     */
    @RequestMapping("/queryAll")
    @Transactional
    public List queryAll(){
        PageInfo<Owner> pageInfo= ownerService.findOwnerAll(1,100,null);
        return pageInfo.getList();
    }

    /*
     * @Description 添加业主数据
     * @Param Owner:添加业主页面的数据以Owner实体类接收
     * @return R:返回结果类（存放返回结果的数据，返回状态和返回信息）
     */
    @ApiOperation(value = "新增")
    @RequestMapping("/add")
    public R add(@RequestBody Owner owner){
        //md5加密
       //String md5Password = DigestUtils.md5DigestAsHex("123456".getBytes());
        String username = owner.getUsername();
        Owner owner1 = ownerService.findByUserName(username);
        if(owner1!=null){
            return R.fail(300,"业主已存在");
        }
        int num= ownerService.add(owner);

        /*//同步添加到用户信息
        Userinfo user=new Userinfo();
        user.setPassword("123456");//默认密码
        user.setRemarks(owner.getRemarks());
        user.setType(0);
        user.setUsername(owner.getUsername());
        userinfoService.add(user);*/


        if(num>0){
            return R.ok();
        }else{
            return R.fail("添加失败");
        }

    }

    /*
     * @Description 删除或批量删除业主
     * @Param ids:业主编号id列表
     * @return R:返回结果类（存放返回结果的数据，返回状态和返回信息）
     */
    @ApiOperation(value = "删除")
    @RequestMapping("/deleteByIds")
    public R delete(String  ids){
        List<String> list= Arrays.asList(ids.split(","));
        //遍历遍历进行删除
        for(String id:list){
            Owner owner = ownerService.queryOwnerById(Long.parseLong(id));
            userinfoService.deleteUserByUsername(owner.getUsername());
            ownerService.delete(Long.parseLong(id));
        }
        return R.ok();
    }


    /*
     * @Description 修改业主数据
     * @Param Owner:修改业主页面的数据以Owner实体类接收
     * @return R:返回结果类（存放返回结果的数据，返回状态和返回信息）
     */
    @ApiOperation(value = "更新")
    @RequestMapping("/update")
    public R update(@RequestBody Owner owner){
        int num= ownerService.updateData(owner);
        if(num>0){
            return R.ok();
        }else{
            return R.fail("修改失败");
        }
    }

    /*@ApiOperation(value = "更新信息")
    @RequestMapping("/editData")
    public R editData(@RequestBody Owner owner){
        int num= ownerService.updateData(owner);
        if(num>0){
            return R.ok();
        }else{
            return R.fail("修改失败");
        }
    }
*/

    /*
     * @Description 查询分页业主列表
     * @Param page,pageCount:分页查询条件
     * @return 返回业主列表
     */
    @GetMapping()
    public IPage<Owner> findListByPage(@RequestParam Integer page,
                                       @RequestParam Integer pageCount){
        return ownerService.findListByPage(page, pageCount);
    }

    /*
     * @Description 通过业主编号获取修改页面需要回显的数据
     * @Param id: 业主编号
     * @return Carcharge:返回需要修改的初始的业主数据
     */
    @ApiOperation(value = "id查询")
    @GetMapping("{id}")
    public Owner findById(@PathVariable Long id){
        return ownerService.findById(id);
    }

}
