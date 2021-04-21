package com.shangma.cn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shangma.cn.common.page.PageResult;
import com.shangma.cn.domin.criteria.AdminCriteria;
import com.shangma.cn.domin.entity.Admin;
import com.shangma.cn.domin.entity.AdminRole;
import com.shangma.cn.domin.entity.Category;
import com.shangma.cn.domin.vo.AdminVo;
import com.shangma.cn.mapper.AdminMapper;
import com.shangma.cn.mapper.AdminRoleMapper;
import com.shangma.cn.service.AdminService;
import com.shangma.cn.service.CategoryService;
import com.shangma.cn.service.base.impl.BaseServiceImpl;
import com.shangma.cn.transfer.AdminTransfer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 开发者：辉哥
 * 特点： 辉哥很帅
 * 开发时间：2021/4/14 16:54
 * 文件说明：
 */
@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl extends BaseServiceImpl<Admin> implements AdminService {
    /**
     * 分页条件查询
     *
     * @param adminCriteria
     * @return
     */


    private final AdminMapper adminMapper;
    private final AdminTransfer adminTransfer;
    private final AdminRoleMapper adminRoleMapper;


    @Override
    public PageResult<AdminVo> searchPage(AdminCriteria adminCriteria) {
        PageHelper.startPage(adminCriteria.getCurrentPage(), adminCriteria.getPageSize());
        LambdaQueryWrapper<Admin> lambda = new QueryWrapper<Admin>().lambda();

        if (!StringUtils.isEmpty(adminCriteria.getAdminName())) {
            lambda.like(Admin::getAdminName, adminCriteria.getAdminName());
        }
        if (!StringUtils.isEmpty(adminCriteria.getAdminPhone())) {
            lambda.eq(Admin::getAdminPhone, adminCriteria.getAdminPhone());
        }

        if (adminCriteria.getDeptId() != null && adminCriteria.getDeptId() != 0) {
            lambda.eq(Admin::getDeptId, adminCriteria.getDeptId());
        }
        if (!StringUtils.isEmpty(adminCriteria.getStartTime())) {
            lambda.between(Admin::getCreateTime, adminCriteria.getStartTime(), adminCriteria.getEndTime());
        }
        List<Admin> admins = adminMapper.selectList(lambda);
        PageInfo<Admin> pageInfo = new PageInfo<>(admins);
        List<AdminVo> adminVos = adminTransfer.setSex(admins);
        return new PageResult<AdminVo>(pageInfo.getTotal(), adminVos);
    }

    /**
     * 保存员工和员工的角色
     *
     * @param admin
     * @return
     */
    @Override
    public int saveAdminAndRoles(Admin admin) {
        int save = this.save(admin);
        Set<Long> roleIds = admin.getRoleIds();
        roleIds.forEach(roleId -> {
            adminRoleMapper.insert(new AdminRole(admin.getId(), roleId));
        });
        return save;
    }

    @Override
    public Admin getAdminAndRoleIdsById(Long id) {
        Admin byId = this.getById(id);
        List<AdminRole> adminRoles = adminRoleMapper.selectList(new QueryWrapper<AdminRole>().lambda().eq(AdminRole::getAdminId, id));
        byId.setRoleIds(adminRoles.stream().map(AdminRole::getRoleId).collect(Collectors.toSet()));
        return byId;
    }

    /**
     * 更新用户和用户的角色
     *
     * @param admin
     * @return
     */
    @Override
    public int updateAdminAndRoles(Admin admin) {
        adminRoleMapper.delete(new UpdateWrapper<AdminRole>().lambda().eq(AdminRole::getAdminId, admin.getId()));
        admin.getRoleIds().forEach(roleId -> adminRoleMapper.insert(new AdminRole(admin.getId(), roleId)));
        return this.update(admin);
    }
}
