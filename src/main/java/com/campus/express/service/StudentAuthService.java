package com.campus.express.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.express.common.BusinessException;
import com.campus.express.dto.StudentLoginDTO;
import com.campus.express.dto.StudentProfileDTO;
import com.campus.express.dto.StudentRegisterDTO;
import com.campus.express.entity.StudentUser;
import com.campus.express.mapper.StudentUserMapper;
import com.campus.express.util.SecurityUtils;
import com.campus.express.vo.StudentVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 学生（用户端）认证服务
 */
@Service
public class StudentAuthService {

    @Resource
    private StudentUserMapper studentUserMapper;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 注册
     */
    public StudentVO register(StudentRegisterDTO dto) {
        String username = dto.getUsername().trim();
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        Long count = studentUserMapper.selectCount(new LambdaQueryWrapper<StudentUser>()
                .eq(StudentUser::getUsername, username));
        if (count > 0) {
            throw new BusinessException("该账号已被注册，请更换账号或直接登录");
        }

        StudentUser user = new StudentUser();
        user.setUsername(username);
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName().trim());
        user.setPhone(dto.getPhone().trim());
        user.setStatus(1);
        studentUserMapper.insert(user);
        return toVO(user);
    }

    /**
     * 账号密码登录
     */
    public StudentVO login(StudentLoginDTO dto, HttpServletRequest request) {
        StudentUser user = studentUserMapper.selectOne(new LambdaQueryWrapper<StudentUser>()
                .eq(StudentUser::getUsername, dto.getUsername().trim()));
        if (user == null || !encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系驿站管理员");
        }
        request.getSession().setAttribute(SecurityUtils.SESSION_STUDENT, user);
        return toVO(user);
    }

    /**
     * 退出登录（仅清理学生会话，不影响后台管理端登录）
     */
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(SecurityUtils.SESSION_STUDENT);
        }
    }

    /**
     * 当前登录学生（实时读取数据库，避免会话资料过期）
     */
    public StudentUser current() {
        StudentUser session = SecurityUtils.getLoginStudent();
        if (session == null) {
            throw new BusinessException(401, "请先登录");
        }
        StudentUser db = studentUserMapper.selectById(session.getId());
        if (db == null) {
            throw new BusinessException(401, "账号不存在，请重新登录");
        }
        return db;
    }

    /**
     * 个人信息（顺带刷新会话中的资料）
     */
    public StudentVO profile(HttpServletRequest request) {
        StudentUser user = current();
        request.getSession().setAttribute(SecurityUtils.SESSION_STUDENT, user);
        return toVO(user);
    }

    /**
     * 修改资料 / 修改密码
     */
    public StudentVO updateProfile(StudentProfileDTO dto, HttpServletRequest request) {
        StudentUser user = current();
        user.setRealName(dto.getRealName().trim());
        user.setPhone(dto.getPhone().trim());
        if (StrUtil.isNotBlank(dto.getNewPassword())) {
            if (StrUtil.isBlank(dto.getOldPassword()) || !encoder.matches(dto.getOldPassword(), user.getPassword())) {
                throw new BusinessException("原密码不正确");
            }
            if (dto.getNewPassword().length() < 6) {
                throw new BusinessException("新密码长度不能少于 6 位");
            }
            user.setPassword(encoder.encode(dto.getNewPassword()));
        }
        studentUserMapper.updateById(user);
        request.getSession().setAttribute(SecurityUtils.SESSION_STUDENT, user);
        return toVO(user);
    }

    private StudentVO toVO(StudentUser user) {
        StudentVO vo = new StudentVO();
        BeanUtil.copyProperties(user, vo);
        return vo;
    }
}
