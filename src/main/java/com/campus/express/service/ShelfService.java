package com.campus.express.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.express.common.BusinessException;
import com.campus.express.entity.Shelf;
import com.campus.express.mapper.ShelfMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 货架库位服务
 */
@Service
public class ShelfService {

    @Resource
    private ShelfMapper shelfMapper;

    public List<Shelf> listActive() {
        return shelfMapper.selectList(new LambdaQueryWrapper<Shelf>()
                .eq(Shelf::getStatus, 1)
                .orderByAsc(Shelf::getShelfNo));
    }

    public IPage<Shelf> page(int page, int size, String keyword) {
        LambdaQueryWrapper<Shelf> wrapper = new LambdaQueryWrapper<Shelf>()
                .orderByAsc(Shelf::getShelfNo);
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Shelf::getShelfNo, keyword)
                    .or().like(Shelf::getArea, keyword));
        }
        return shelfMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public void create(Shelf shelf) {
        shelf.setStatus(shelf.getStatus() == null ? 1 : shelf.getStatus());
        shelfMapper.insert(shelf);
    }

    public void update(Shelf shelf) {
        if (shelf.getId() == null || shelfMapper.selectById(shelf.getId()) == null) {
            throw new BusinessException("货架不存在");
        }
        shelfMapper.updateById(shelf);
    }

    public void delete(Long id) {
        shelfMapper.deleteById(id);
    }
}
