package com.campus.express.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.express.common.BusinessException;
import com.campus.express.entity.ExpressCompany;
import com.campus.express.mapper.ExpressCompanyMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 快递公司服务
 */
@Service
public class CompanyService {

    @Resource
    private ExpressCompanyMapper companyMapper;

    /** 全部启用中的公司（下拉框用） */
    public List<ExpressCompany> listActive() {
        return companyMapper.selectList(new LambdaQueryWrapper<ExpressCompany>()
                .eq(ExpressCompany::getStatus, 1)
                .orderByAsc(ExpressCompany::getSort));
    }

    public IPage<ExpressCompany> page(int page, int size, String keyword) {
        LambdaQueryWrapper<ExpressCompany> wrapper = new LambdaQueryWrapper<ExpressCompany>()
                .orderByAsc(ExpressCompany::getSort).orderByAsc(ExpressCompany::getId);
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(ExpressCompany::getCompanyName, keyword)
                    .or().like(ExpressCompany::getCompanyCode, keyword));
        }
        return companyMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public void create(ExpressCompany company) {
        company.setStatus(company.getStatus() == null ? 1 : company.getStatus());
        company.setSort(company.getSort() == null ? 0 : company.getSort());
        companyMapper.insert(company);
    }

    public void update(ExpressCompany company) {
        if (company.getId() == null || companyMapper.selectById(company.getId()) == null) {
            throw new BusinessException("公司不存在");
        }
        companyMapper.updateById(company);
    }

    public void delete(Long id) {
        companyMapper.deleteById(id);
    }
}
