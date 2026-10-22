package com.campus.express.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.express.common.BusinessException;
import com.campus.express.common.RoleCode;
import com.campus.express.dto.UserDTO;
import com.campus.express.entity.SysUser;
import com.campus.express.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户管理服务
 */
@Service
public class UserService {

    @Resource
    private SysUserMapper sysUserMapper;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public IPage<SysUser> page(int page, int size, String keyword) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .orderByDesc(SysUser::getCreateTime);
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getRealName, keyword)
                    .or().like(SysUser::getPhone, keyword));
        }
        IPage<SysUser> result = sysUserMapper.selectPage(new Page<>(page, size), wrapper);
        result.getRecords().forEach(u -> u.setPassword(null));
        return result;
    }

    public void create(UserDTO dto) {
        Long count = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("账号已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(StrUtil.isBlank(dto.getPassword()) ? "123456" : dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setRoleCode(dto.getRoleCode());
        user.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        user.setRemark(dto.getRemark());
        sysUserMapper.insert(user);
    }

    public void update(UserDTO dto) {
        SysUser user = sysUserMapper.selectById(dto.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (dto.getId() == 1L && !RoleCode.SUPER_ADMIN.equals(dto.getRoleCode())) {
            throw new BusinessException("不能修改内置超级管理员的角色");
        }
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setRoleCode(dto.getRoleCode());
        user.setStatus(dto.getStatus());
        user.setRemark(dto.getRemark());
        if (StrUtil.isNotBlank(dto.getPassword())) {
            user.setPassword(encoder.encode(dto.getPassword()));
        }
        sysUserMapper.updateById(user);
    }

    public void resetPassword(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(encoder.encode("123456"));
        sysUserMapper.updateById(user);
    }

    public void delete(Long id) {
        if (id == 1L) {
            throw new BusinessException("不能删除内置超级管理员");
        }
        sysUserMapper.deleteById(id);
    }
}
