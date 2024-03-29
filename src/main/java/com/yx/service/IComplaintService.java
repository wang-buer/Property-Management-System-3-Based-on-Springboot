package com.yx.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.yx.model.Complaint;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author kappy
 * @since 2020-11-08
 */
public interface IComplaintService extends IService<Complaint> {


    PageInfo<Complaint> findComplaintTotal(int page, int pagesise, Complaint complaint);
    PageInfo<Complaint> findComplaintAll(int page, int pagesise, Complaint complaint);
    /**
     * 查询分页数据
     *
     * @param page      页码
     * @param pageCount 每页条数
     * @return IPage<Complaint>
     */
    IPage<Complaint> findListByPage(Integer page, Integer pageCount);


    /**
     * 添加
     *
     * @param complaint 
     * @return int
     */
    int add(Complaint complaint);

    /**
     * 删除
     *
     * @param id 主键
     * @return int
     */
    int delete(Long id);


    /**
     * 修改
     *
     * @param complaint 
     * @return int
     */
    int updateData(Complaint complaint);

    /**
     * id查询数据
     *
     * @param id id
     * @return Complaint
     */
    Complaint findById(Long id);

    int delete(QueryWrapper<Complaint> queryWrapper);
}
