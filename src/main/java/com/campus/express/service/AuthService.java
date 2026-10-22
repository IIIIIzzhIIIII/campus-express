package com.campus.express.service;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.express.common.BusinessException;
import com.campus.express.dto.LoginDTO;
import com.campus.express.entity.SysLoginLog;
import com.campus.express.entity.SysUser;
import com.campus.express.mapper.SysLoginLogMapper;
import com.campus.express.mapper.SysUserMapper;
import com.campus.express.util.IpUtil;
import com.campus.express.util.SecurityUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 */
@Service
public class AuthService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysLoginLogMapper sysLoginLogMapper;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public SysUser login(LoginDTO dto, HttpServletRequest request) {
        String ip = IpUtil.getClientIp(request);
        String ua = request.getHeader("User-Agent");
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));

        if (user == null || !encoder.matches(dto.getPassword(), user.getPassword())) {
            saveLoginLog(dto.getUsername(), ip, ua, 0, "用户名或密码错误");
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            saveLoginLog(dto.getUsername(), ip, ua, 0, "账号已被禁用");
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        request.getSession().setAttribute(SecurityUtils.SESSION_USER, user);
        saveLoginLog(dto.getUsername(), ip, ua, 1, "登录成功");
        user.setPassword(null);
        return user;
    }

    public void logout(HttpServletRequest request) {
        request.getSession().invalidate();
    }

    private void saveLoginLog(String username, String ip, String ua, int status, String msg) {
        SysLoginLog log = new SysLoginLog();
        log.setUsername(username);
        log.setIp(ip);
        if (ua != null) {
            try {
                UserAgent parsed = UserAgentUtil.parse(ua);
                log.setBrowser(parsed.getBrowser() != null ? parsed.getBrowser().getName() : ua);
                log.setOs(parsed.getOs() != null ? parsed.getOs().getName() : "");
            } catch (Exception e) {
                log.setBrowser(ua);
            }
        }
        log.setStatus(status);
        log.setMsg(msg);
        sysLoginLogMapper.insert(log);
    }
}
