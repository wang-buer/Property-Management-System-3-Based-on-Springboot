package com.yx.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import com.yx.model.Building;
import com.yx.service.IBuildingService;
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
import java.util.Arrays;
import java.util.List;

/*
 * @Author 禹彦州
 * @Date 2022/9/23
 * @Description
 * @Since version-1.0
 */
@Api(tags = {""})
@RestController
@RequestMapping("/building")
public class BuildingController {

    //记录日志
    private Logger log = LoggerFactory.getLogger(getClass());

    //楼层服务层接口
    @Resource
    private IBuildingService buildingService;


    /*
     * @Description 查询所有楼层信息
     * @Param 楼层实体对象，页码，每页数据数
     * @return 楼层信息列表
     */
    @RequestMapping("/queryBuildAll")
    public JsonObject queryBuildAll(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "15") Integer limit,
                                    String numbers){
        JsonObject object=new JsonObject();
        PageInfo<Building> pageInfo= buildingService.findBuildAll(page,limit,numbers);
        object.setCode(0);
        object.setMsg("ok");
        object.setCount(pageInfo.getTotal());
        object.setData(pageInfo.getList());
        return object;
    }

    /*
     * @Description 查询所有楼层信息
     * @Param 楼层实体对象，页码，每页数据数
     * @return 楼层信息列表
     */
    @RequestMapping("/queryBuild")
    public List<Building> queryBuild(){
        PageInfo<Building> pageInfo= buildingService.findBuildAll(1,100,null);
        return pageInfo.getList();
    }


    /*
     * @Description 新增楼层信息
     * @Param 楼层实体类
     * @return 添加楼层信息成功或者失败的信息
     */
    @ApiOperation(value = "新增")
    @RequestMapping("/add")
    public R add(@RequestBody Building building){
        int num= buildingService.add(building);
        if(num>0){
            return R.ok();
        }else{
            return R.fail("添加失败");
        }

    }

    /*
     * @Description 删除单条楼层信息
     * @Param 楼层信息主键
     * @return 删除楼层信息成功或失败信息
     */
    @ApiOperation(value = "删除")
    @RequestMapping("/deleteByIds")
    public R delete(String  ids){
        List<String> list= Arrays.asList(ids.split(","));
        //遍历遍历进行删除
        for(String id:list){
            buildingService.delete(Long.parseLong(id));
        }
        return R.ok();
    }

    /*
     * @Description 更新楼层信息
     * @Param  楼层信息主键
     * @return 楼层信息更新成功或者失败信息
     */
    @ApiOperation(value = "更新")
    @RequestMapping("/update")
    public R update(@RequestBody Building building){
        int num= buildingService.updateData(building);
        if(num>0){
            return R.ok();
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
    public IPage<Building> findListByPage(@RequestParam Integer page,
                                          @RequestParam Integer pageCount){
        return buildingService.findListByPage(page, pageCount);
    }
    /*
     * @Description 根据id查询对的楼层信息
     * @Param 楼层信息id
     * @return 查询的楼层信息
     */
    @ApiOperation(value = "id查询")
    @GetMapping("{id}")
    public Building findById(@PathVariable Long id){
        return buildingService.findById(id);
    }

}
