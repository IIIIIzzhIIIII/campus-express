package com.campus.express.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.express.common.BusinessException;
import com.campus.express.common.DepositStatus;
import com.campus.express.dto.DepositAcceptDTO;
import com.campus.express.dto.DepositOrderDTO;
import com.campus.express.entity.DepositOrder;
import com.campus.express.entity.Shelf;
import com.campus.express.entity.StudentUser;
import com.campus.express.entity.SysUser;
import com.campus.express.mapper.DepositOrderMapper;
import com.campus.express.mapper.ShelfMapper;
import com.campus.express.mapper.StudentUserMapper;
import com.campus.express.mapper.SysUserMapper;
import com.campus.express.util.DateParseUtil;
import com.campus.express.util.OrderNoUtil;
import com.campus.express.util.PickupCodeUtil;
import com.campus.express.util.SecurityUtils;
import com.campus.express.vo.DepositOrderVO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 存件（寄存）订单服务
 */
@Service
public class DepositOrderService {

    @Resource
    private DepositOrderMapper depositOrderMapper;

    @Resource
    private StudentUserMapper studentUserMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private ShelfMapper shelfMapper;

    @Value("${campus.pickup-code-length:6}")
    private int codeLength;

    /**
     * 学生提交存件申请
     */
    public DepositOrderVO create(DepositOrderDTO dto) {
        StudentUser student = requireStudent();

        DepositOrder order = new DepositOrder();
        order.setOrderNo(OrderNoUtil.generate("D"));
        order.setStudentId(student.getId());
        order.setItemName(dto.getItemName().trim());
        order.setQuantity(dto.getQuantity() == null || dto.getQuantity() < 1 ? 1 : dto.getQuantity());
        order.setDepositCode(generateUniqueCode());
        order.setDepositDays(dto.getDepositDays() == null || dto.getDepositDays() < 1 ? 3 : dto.getDepositDays());
        order.setExpectTime(DateParseUtil.parse(dto.getExpectTime()));
        order.setRemark(dto.getRemark());
        order.setStatus(DepositStatus.WAIT.getCode());
        depositOrderMapper.insert(order);
        return toVO(order);
    }

    /**
     * 学生「我的存件」分页
     */
    public IPage<DepositOrderVO> pageByStudent(int page, int size, Integer status) {
        StudentUser student = requireStudent();
        LambdaQueryWrapper<DepositOrder> wrapper = new LambdaQueryWrapper<DepositOrder>()
                .eq(DepositOrder::getStudentId, student.getId())
                .orderByDesc(DepositOrder::getId);
        if (status != null) {
            wrapper.eq(DepositOrder::getStatus, status);
        }
        return depositOrderMapper.selectPage(new Page<>(page, size), wrapper).convert(this::toVO);
    }

    /**
     * 学生查看自己的存件详情
     */
    public DepositOrderVO detailForStudent(Long id) {
        return toVO(findOwnOrder(id));
    }

    /**
     * 学生取消存件申请（仅待到站可取消）
     */
    public DepositOrderVO cancelByStudent(Long id) {
        DepositOrder order = findOwnOrder(id);
        if (!DepositStatus.WAIT.getCode().equals(order.getStatus())) {
            throw new BusinessException("物品已入站，无法取消，请联系驿站处理");
        }
        order.setStatus(DepositStatus.CANCELED.getCode());
        depositOrderMapper.updateById(order);
        return toVO(order);
    }

    /**
     * 后台分页查询
     */
    public IPage<DepositOrderVO> page(int page, int size, String keyword, Integer status) {
        LambdaQueryWrapper<DepositOrder> wrapper = new LambdaQueryWrapper<DepositOrder>()
                .orderByDesc(DepositOrder::getId);
        if (StrUtil.isNotBlank(keyword)) {
            String k = keyword.trim();
            wrapper.and(w -> w.like(DepositOrder::getOrderNo, k)
                    .or().like(DepositOrder::getDepositCode, k)
                    .or().like(DepositOrder::getItemName, k)
                    .or().like(DepositOrder::getShelfNo, k));
        }
        if (status != null) {
            wrapper.eq(DepositOrder::getStatus, status);
        }
        return depositOrderMapper.selectPage(new Page<>(page, size), wrapper).convert(this::toVO);
    }

    /**
     * 后台查看存件详情
     */
    public DepositOrderVO detail(Long id) {
        DepositOrder order = depositOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("存件单不存在");
        }
        return toVO(order);
    }

    /**
     * 驿站接收存件并上架（在库）
     */
    public DepositOrderVO accept(DepositAcceptDTO dto) {
        DepositOrder order = depositOrderMapper.selectById(dto.getId());
        if (order == null) {
            throw new BusinessException("存件单不存在");
        }
        if (!DepositStatus.WAIT.getCode().equals(order.getStatus())) {
            throw new BusinessException("该存件单已处理，请勿重复操作");
        }
        String shelfNo = StrUtil.isBlank(dto.getShelfNo()) ? autoAssignShelf() : dto.getShelfNo().trim();
        order.setShelfNo(shelfNo);
        order.setStatus(DepositStatus.STORED.getCode());
        order.setInTime(LocalDateTime.now());
        order.setOperatorId(SecurityUtils.getLoginUserId());
        depositOrderMapper.updateById(order);
        return toVO(order);
    }

    /**
     * 驿站办理取出
     */
    public DepositOrderVO takeOut(Long id) {
        DepositOrder order = depositOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("存件单不存在");
        }
        if (!DepositStatus.STORED.getCode().equals(order.getStatus())) {
            throw new BusinessException("仅「在库」中的存件可以办理取出");
        }
        order.setStatus(DepositStatus.TAKEN.getCode());
        order.setOutTime(LocalDateTime.now());
        order.setOperatorId(SecurityUtils.getLoginUserId());
        depositOrderMapper.updateById(order);
        return toVO(order);
    }

    private DepositOrder findOwnOrder(Long id) {
        StudentUser student = requireStudent();
        DepositOrder order = depositOrderMapper.selectById(id);
        if (order == null || !student.getId().equals(order.getStudentId())) {
            throw new BusinessException("存件单不存在");
        }
        return order;
    }

    private StudentUser requireStudent() {
        StudentUser session = SecurityUtils.getLoginStudent();
        if (session == null) {
            throw new BusinessException(401, "请先登录学生账号");
        }
        StudentUser student = studentUserMapper.selectById(session.getId());
        if (student == null) {
            throw new BusinessException(401, "账号不存在，请重新登录");
        }
        return student;
    }

    private String generateUniqueCode() {
        for (int i = 0; i < 20; i++) {
            String code = PickupCodeUtil.generate(codeLength);
            Long count = depositOrderMapper.selectCount(new LambdaQueryWrapper<DepositOrder>()
                    .eq(DepositOrder::getDepositCode, code));
            if (count == 0) {
                return code;
            }
        }
        return String.valueOf(System.currentTimeMillis()).substring(6);
    }

    private String autoAssignShelf() {
        List<Shelf> shelves = shelfMapper.selectList(new LambdaQueryWrapper<Shelf>()
                .eq(Shelf::getStatus, 1)
                .orderByAsc(Shelf::getShelfNo)
                .last("limit 1"));
        return shelves.isEmpty() ? "未分配" : shelves.get(0).getShelfNo();
    }

    private DepositOrderVO toVO(DepositOrder order) {
        DepositOrderVO vo = new DepositOrderVO();
        BeanUtil.copyProperties(order, vo);
        vo.setStatusDesc(DepositStatus.descOf(order.getStatus()));
        if (order.getStudentId() != null) {
            StudentUser student = studentUserMapper.selectById(order.getStudentId());
            if (student != null) {
                vo.setStudentName(student.getRealName());
                vo.setStudentPhone(student.getPhone());
            }
        }
        if (order.getOperatorId() != null) {
            SysUser user = sysUserMapper.selectById(order.getOperatorId());
            vo.setOperatorName(user == null ? null : user.getRealName());
        }
        return vo;
    }
}
