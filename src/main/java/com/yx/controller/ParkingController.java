package com.yx.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import com.yx.model.Owner;
import com.yx.model.Parking;
import com.yx.service.IParkingService;
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
import java.util.Arrays;
import java.util.List;

/*
 * @Author 徐文定
 * @Date 2022/9/21
 * @Description 车位管理
 * @Since version-1.0
 */

@Api(tags = {""})
@RestController
@RequestMapping("/parking")
public class ParkingController {

    //记录日志
    private Logger log = LoggerFactory.getLogger(getClass());
    //车位服务层接口
    @Resource
    private IParkingService parkingService;


    /*
     * @Description 管理员根据参数条件查询车位列表
     * @Param numbers:查询条件为前端搜索框中的车位号
     * @Param page,limit:分页查询条件
     * @return 车位收费列表
     */
    @RequestMapping("/queryParkAll")
    public JsonObject queryParkAll(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "15") Integer limit,
                                    String numbers){
        PageInfo<Parking> pageInfo= parkingService.findParkAll(page,limit,numbers);
        return new JsonObject(0,"ok",pageInfo.getTotal(),pageInfo.getList());

    }

    /*
     * @Description 业主根据参数条件查询车位列表
     * @Param 无
     * @return 车位列表
     */
    @RequestMapping("/queryAll")
    @Transactional
    public List queryAll(){
        PageInfo<Parking> pageInfo= parkingService.findParkAll(1,100,null);
        return pageInfo.getList();
    }

    /*
     * @Description 添加车位数据
     * @Param Parking:添加车位页面的数据以Parking实体类接收
     * @return R:返回结果类（存放返回结果的数据，返回状态和返回信息）
     */
    @ApiOperation(value = "新增")
    @RequestMapping("/add")
    public R add(@RequestBody Parking parking){
        if(parking.getOwnerId()!=null){//关联到了户主
           parking.setStatus(1);
        }else{
            parking.setStatus(0);
        }
        int num= parkingService.add(parking);
        if(num>0){
            return R.ok();
        }else{
            return R.fail("添加失败");
        }

    }

    /*
     * @Description 删除或批量删除车位
     * @Param ids:车位编号id列表
     * @return R:返回结果类（存放返回结果的数据，返回状态和返回信息）
     */
    @ApiOperation(value = "删除")
    @RequestMapping("/deleteByIds")
    public R delete(String  ids){
        List<String> list= Arrays.asList(ids.split(","));
        //遍历遍历进行删除
        for(String id:list){
            parkingService.delete(Long.parseLong(id));
        }
        return R.ok();
    }

    /*
     * @Description 修改车位数据
     * @Param Parking:修改车位页面的数据以Parking实体类接收
     * @return R:返回结果类（存放返回结果的数据，返回状态和返回信息）
     */
    @ApiOperation(value = "更新")
    @RequestMapping("/update")
    public R update(@RequestBody Parking parking){
        Parking park=new Parking();
        if(parking.getOwnerId()!=null){//关联到了户主
            park.setStatus(1);
        }else{
            park.setStatus(0);
        }
        park.setId(parking.getId());
        park.setNumbers(parking.getNumbers());
        park.setRemarks(parking.getRemarks());
        park.setOwnerId(parking.getOwnerId());
        int num= parkingService.updateData(park);
        if(num>0){
            return R.ok();
        }else{
            return R.fail("修改失败");
        }
    }




    /*
     * @Description 查询分页车位列表
     * @Param page,pageCount:分页查询条件
     * @return 返回车位列表
     */
    @ApiOperation(value = "查询分页数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "page", value = "页码"),
        @ApiImplicitParam(name = "pageCount", value = "每页条数")
    })
    @GetMapping()
    public IPage<Parking> findListByPage(@RequestParam Integer page,
                                         @RequestParam Integer pageCount){
        return parkingService.findListByPage(page, pageCount);
    }

    /*
     * @Description 通过车位编号获取修改页面需要回显的数据
     * @Param id: 车位编号
     * @return Carcharge:返回需要修改的初始的车位数据
     */
    @ApiOperation(value = "id查询")
    @GetMapping("{id}")
    public Parking findById(@PathVariable Long id){
        return parkingService.findById(id);
    }

}
