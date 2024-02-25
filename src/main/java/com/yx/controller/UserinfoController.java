package com.yx.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import com.yx.model.Owner;
import com.yx.model.Parking;
import com.yx.model.Userinfo;
import com.yx.service.IOwnerService;
import com.yx.service.IUserinfoService;
import com.yx.util.JsonObject;
import com.yx.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/*
 * @Author 刘东祥
 * @Date 2022/9/25
 * @Description 用户管理
 * @Since version-1.0
 */
@Api(tags = {""})
@RestController
@RequestMapping("/userinfo")
public class UserinfoController {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    private IUserinfoService userinfoService;

    @Resource
    private IOwnerService ownerService;


    /*
     * @Description 查询所有用户信息
     * @Param 用户实体对象，页码，每页数据数
     * @return 用户信息列表
     */
    @RequestMapping("/queryUserInfoAll")
    public JsonObject queryUserInfoAll(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "15") Integer limit,
                                    Userinfo userinfo){
        JsonObject object=new JsonObject();
        PageInfo<Userinfo> pageInfo= userinfoService.findUserinfoAll(page,limit,userinfo);
        object.setCode(0);
        object.setMsg("ok");
        object.setCount(pageInfo.getTotal());
        object.setData(pageInfo.getList());
        return object;
    }

    /*
     * @Description 查询所有用户信息
     * @Param 用户实体对象，页码，每页数据数
     * @return 用户信息列表
     */
    @RequestMapping("/queryAll")
    @Transactional
    public List queryAll(){
        PageInfo<Userinfo> pageInfo= userinfoService.findUserAll(1,100,null);
        System.out.println(pageInfo.getList());
        return pageInfo.getList();
    }

    /*
     * @Description 删除单条用户信息
     * @Param  用户信息主键
     * @return 删除用户信息成功或失败信息
     */
    @ApiOperation(value = "删除")
    @RequestMapping("/deleteByIds")
    public R delete(String  ids){
        List<String> list= Arrays.asList(ids.split(","));
        //遍历遍历进行删除
        for(String id:list){
            Userinfo user = userinfoService.findById(Long.parseLong(id));
            ownerService.deleteOwnerUserByUserName(user.getUsername());
            userinfoService.delete(Long.parseLong(id));
        }
        return R.ok();
    }

    /*
     * @Description 新增用户信息
     * @Param 用户实体类
     * @return 添加用户信息成功或者失败的信息
     */
    @ApiOperation(value = "新增")
    @RequestMapping("/add")
    public R add(@RequestBody Userinfo userinfo){
        String username = userinfo.getUsername();
        Userinfo user = userinfoService.findByUserName(username);
        if(user!=null){
            return R.fail(300,"用户名已存在");
        }
        userinfoService.add(userinfo);
        return R.ok();
    }

    /*
     * @Description 更新权限信息
     * @Param 用户实体类
     * @return 更新用户权限或者失败的信息
     */
    @ApiOperation(value = "更新权限")
    @RequestMapping("/editquanxian")
    public R updatequanxian(@RequestBody Userinfo userinfo ){
      userinfoService.updateData(userinfo);
       return R.ok();
    }


    /*
     * @Description 更新密码信息
     * @Param  用户实体类
     * @return 更新用户密码或者失败的信息
     */
    @ApiOperation(value = "修改密码")
    @RequestMapping("/update")
    public R update(String oldPwd, String newPwd, Integer id, HttpServletRequest request){
        //用户页面修改密码
        if(id==null){
            Userinfo user= (Userinfo) request.getSession().getAttribute("user");
            String username = user.getUsername();
            Userinfo user1 = userinfoService.findByUserName(username);
            if(oldPwd.equals(user1.getPassword())){//输入的老密码和原密码一致
                user.setPassword(newPwd);
                userinfoService.updateData(user);
                return R.ok();
            }else{
                return R.fail("两次密码不一致");
            }
        }
        //管理员页面修改密码,根据id获取当前的数据记录
        else{
            Userinfo user=userinfoService.findById(new Long(id));
            if(oldPwd.equals(user.getPassword())){//输入的老密码和原密码一致
                user.setPassword(newPwd);
                userinfoService.updateData(user);
                return R.ok();
            }else{
                return R.fail("两次密码不一致");
            }
            }
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
    public IPage<Userinfo> findListByPage(@RequestParam Integer page,
                                          @RequestParam Integer pageCount){
        return userinfoService.findListByPage(page, pageCount);
    }

    @ApiOperation(value = "id查询")
    @GetMapping("{id}")
    public Userinfo findById(@PathVariable Long id){
        return userinfoService.findById(id);
    }




}
