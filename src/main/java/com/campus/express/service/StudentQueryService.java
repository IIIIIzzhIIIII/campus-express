package com.campus.express.service;

import cn.hutool.core.util.StrUtil;
import com.campus.express.common.BusinessException;
import com.campus.express.vo.PackageVO;
import com.campus.express.vo.SendOrderVO;
import com.campus.express.vo.StudentQueryVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生自助查询服务：合并「驿站入库快件」与「别人寄给我的寄件订单」
 * <p>按手机号或快递单号查询时，两类记录都会返回，保证收件人能查到自己名下的在途寄件。</p>
 */
@Service
public class StudentQueryService {

    @Resource
    private PackageService packageService;

    @Resource
    private SendOrderService sendOrderService;

    /**
     * 自助查询（免登录）：keyword 支持手机号 / 快递单号 / 寄件单号
     */
    public List<StudentQueryVO> query(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            throw new BusinessException("请输入手机号或快递单号");
        }
        List<StudentQueryVO> result = new ArrayList<>();
        for (PackageVO pkg : packageService.studentQuery(keyword)) {
            result.add(fromPackage(pkg));
        }
        for (SendOrderVO order : sendOrderService.queryByKeyword(keyword)) {
            result.add(fromSendOrder(order));
        }
        // 按时间倒序，时间为空排最后
        result.sort((a, b) -> {
            LocalDateTime t1 = a.getTime();
            LocalDateTime t2 = b.getTime();
            if (t1 == null) {
                return t2 == null ? 0 : 1;
            }
            if (t2 == null) {
                return -1;
            }
            return t2.compareTo(t1);
        });
        return result;
    }

    private StudentQueryVO fromPackage(PackageVO pkg) {
        StudentQueryVO vo = new StudentQueryVO();
        vo.setType(StudentQueryVO.TYPE_PACKAGE);
        vo.setId(pkg.getId());
        vo.setTrackingNo(pkg.getTrackingNo());
        vo.setCompanyId(pkg.getCompanyId());
        vo.setCompanyName(pkg.getCompanyName());
        vo.setReceiverName(pkg.getReceiverName());
        vo.setReceiverPhone(pkg.getReceiverPhone());
        vo.setPickupCode(pkg.getPickupCode());
        vo.setShelfNo(pkg.getShelfNo());
        vo.setStatus(pkg.getStatus());
        vo.setStatusDesc(pkg.getStatusDesc());
        vo.setTime(pkg.getInTime());
        vo.setPickupTime(pkg.getPickupTime());
        return vo;
    }

    private StudentQueryVO fromSendOrder(SendOrderVO order) {
        StudentQueryVO vo = new StudentQueryVO();
        vo.setType(StudentQueryVO.TYPE_SEND);
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTrackingNo(order.getTrackingNo());
        vo.setCompanyId(order.getCompanyId());
        vo.setCompanyName(order.getCompanyName());
        vo.setSenderName(order.getSenderName());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setItemName(order.getItemName());
        vo.setStatus(order.getStatus());
        vo.setStatusDesc(order.getStatusDesc());
        vo.setTime(order.getCreateTime());
        return vo;
    }
}
