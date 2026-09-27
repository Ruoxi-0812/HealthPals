package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.NewsSaveMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.PageResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.NewsSaveQueryDto;
import cn.kmbeast.pojo.entity.NewsSave;
import cn.kmbeast.pojo.vo.NewsSaveVO;
import cn.kmbeast.service.NewsSaveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.kmbeast.security.AccessPolicy;
import cn.kmbeast.security.OwnershipGuard;
import cn.kmbeast.security.OwnershipGuard.ResourceType;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Health information collection business logic implementation
 */
@Service
public class NewsSaveServiceImpl implements NewsSaveService {

    @Resource
    private OwnershipGuard ownershipGuard;

    @Resource
    private NewsSaveMapper newsSaveMapper;

    /**
     * Health information collection added
     */
    @Override
    public Result<Void> save(NewsSave newsSave) {
        newsSave.setUserId(AccessPolicy.userId());
        newsSave.setCreateTime(LocalDateTime.now());
        newsSaveMapper.save(newsSave);
        return ApiResult.success();
    }

    /**
     * Health information collection deleted
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> batchDelete(List<Long> ids) {
        ownershipGuard.requireOwned(ResourceType.BOOKMARK, ids);
        newsSaveMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * Health information collection inquiry
     */
    @Override
    public Result<List<NewsSaveVO>> query(NewsSaveQueryDto newsSaveQueryDto) {
        if (!AccessPolicy.isAdmin()) newsSaveQueryDto.setUserId(AccessPolicy.userId());
        List<NewsSaveVO> tagsList = newsSaveMapper.query(newsSaveQueryDto);
        Integer totalCount = newsSaveMapper.queryCount(newsSaveQueryDto);
        return PageResult.success(tagsList, totalCount);
    }

    /**
     * Bookmark or unBookmark information
     */
    @Override
    public Result<Void> operation(NewsSave newsSave) {
        newsSave.setUserId(LocalThreadHolder.getUserId());
        NewsSaveQueryDto newsSaveQueryDto = new NewsSaveQueryDto();
        newsSaveQueryDto.setUserId(LocalThreadHolder.getUserId());
        newsSaveQueryDto.setNewsId(newsSave.getNewsId());
        List<NewsSaveVO> newsSaveVOS = newsSaveMapper.query(newsSaveQueryDto);
        if (newsSaveVOS.isEmpty()) {
            newsSave.setCreateTime(LocalDateTime.now());
            newsSaveMapper.save(newsSave);
        } else {
            List<Long> ids = Collections.singletonList(Long.valueOf(newsSaveVOS.get(0).getId()));
            newsSaveMapper.batchDelete(ids);
        }
        return ApiResult.success();
    }
}
