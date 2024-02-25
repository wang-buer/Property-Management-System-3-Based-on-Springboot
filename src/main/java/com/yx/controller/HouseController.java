package com.yx.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import com.yx.model.House;
import com.yx.service.IHouseService;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
 * @Author 禹彦州
 * @Date 2022/9/21
 * @Description 房屋管理
 * @Since version-1.0
 */

@Api(tags = {""})
@RestController
@RequestMapping("/house")
public class HouseController {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Resource
    private IHouseService houseService;


    /*
     * @Description 查询所有房屋信息
     * @Param 房屋实体对象，页码，每页数据数
     * @return 房屋信息列表
     */
    @RequestMapping("/houseAll")
    public JsonObject queryHouseAll(House house,
                                  @RequestParam(defaultValue = "1")  Integer page,
                                    @RequestParam(defaultValue = "15")  Integer limit){
        PageInfo<House> pageInfo=houseService.findHouseAll(page,limit,house);
        System.out.println(pageInfo);
        Integer status = house.getStatus();
        return  new JsonObject(0,"ok",pageInfo.getTotal(),pageInfo.getList());
    }

    /*
     * @Description 查询所有房屋信息
     * @Param 房屋实体对象，页码，每页数据数
     * @return 房屋信息列表
     */
    @RequestMapping("/queryAll")
    public  List<House> queryAll(){
        PageInfo<House> pageInfo=houseService.findHouseAll(1,100,null);
        return pageInfo.getList();
    }


    /*
     * @Description 新增房屋信息
     * @Param 房屋实体类，httpservletrequest
     * @return 添加房屋信息成功或者失败的信息
     */
    @ApiOperation(value = "新增")
    @RequestMapping("/add")
    public R add(@RequestBody House house){
        Double area = house.getArea();
        if(area<0){
            return R.fail("输入面积不得小于0");
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyy--MM--dd");
        String time = sf.format(new Date());
        Date date=new Date();
        System.out.println(house.getIntoDate());
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date intoDate = house.getIntoDate();
        if(intoDate.getTime()<=date.getTime()){
            house.setStatus(1);
        }else{
            house.setStatus(0);
        }
        int num= houseService.add(house);
        if(num>0){
            return R.ok();
        }else{
            return R.fail("添加失败");
        }
    }

    /*
     * @Description 删除单条房屋信息
     * @Param 房屋信息主键
     * @return 删除房屋信息成功或失败信息
     */
    @ApiOperation(value = "删除")
    @RequestMapping("/deleteByIds")
    public R delete(String ids){
        //z转成集合对象
       List<String> list= Arrays.asList(ids.split(","));
       for(String id:list){
           Long idLong=Long.parseLong(id);
           houseService.delete(idLong);
       }
       return R.ok();
    }
    /*
     * @Description 更新房屋信息
     * @Param  房屋信息主键，httpservletrequest
     * @return 房屋信息更新成功或者失败信息
     */
    @ApiOperation(value = "更新")
    @RequestMapping("/update")
    public R update(@RequestBody House house){
        System.out.println(house);
        Double area = house.getArea();
        if(area<0){
            return R.fail("输入面积不得小于0");
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyy--MM--dd");
        String time = sf.format(new Date());
        Date date=new Date();
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date intoDate = house.getIntoDate();
        if(intoDate.getTime()<=date.getTime()){
            house.setStatus(1);
        }else{
            house.setStatus(0);
        }
        int num= houseService.updateData(house);
        if(num>0){
            return R.ok("修改成功");
        }else{
            return R.fail("修改失败");
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
    public IPage<House> findListByPage(@RequestParam Integer page,
                                       @RequestParam Integer pageCount){
        return houseService.findListByPage(page, pageCount);
    }

    /*
     * @Description 根据id查询对的房屋信息
     * @Param 房屋信息id
     * @return 查询的房屋信息
     */
    @ApiOperation(value = "id查询")
    @GetMapping("{id}")
    public House findById(@PathVariable Long id){
        return houseService.findById(id);
    }

}
