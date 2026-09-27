package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.HealthModelConfigMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.PageResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.HealthModelConfigQueryDto;
import cn.kmbeast.pojo.entity.HealthModelConfig;
import cn.kmbeast.pojo.vo.HealthModelConfigVO;
import cn.kmbeast.service.HealthModelConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.kmbeast.security.AccessPolicy;
import cn.kmbeast.security.OwnershipGuard;
import cn.kmbeast.security.OwnershipGuard.ResourceType;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Health model business logic implementation
 */
@Service
public class HealthModelConfigServiceImpl implements HealthModelConfigService {

    @Resource
    private OwnershipGuard ownershipGuard;

    @Resource
    private HealthModelConfigMapper healthModelConfigMapper;

    /**
     * Health model addition
     */
    @Override
    public Result<Void> save(HealthModelConfig healthModelConfig) {
        if (!AccessPolicy.isAdmin()) healthModelConfig.setIsGlobal(false);
        healthModelConfig.setUserId(LocalThreadHolder.getUserId());
        healthModelConfigMapper.save(healthModelConfig);
        return ApiResult.success();
    }

    /**
     * Health model delete
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> batchDelete(List<Long> ids) {
        ownershipGuard.requireOwned(ResourceType.MODEL, ids);
        healthModelConfigMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * Health model update
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> update(HealthModelConfig healthModelConfig) {
        ownershipGuard.requireOwned(ResourceType.MODEL, java.util.Collections.singletonList(healthModelConfig.getId()));
        healthModelConfigMapper.update(healthModelConfig);
        return ApiResult.success();
    }

    /**
     * Query the model and global model configured by the user
     */
    @Override
    public Result<List<HealthModelConfigVO>> modelList() {
        HealthModelConfigQueryDto healthModelConfigQueryDto = new HealthModelConfigQueryDto();
        if (!AccessPolicy.isAdmin()) healthModelConfigQueryDto.setVisibleTo(AccessPolicy.userId());
        healthModelConfigQueryDto.setUserId(LocalThreadHolder.getUserId());
        List<HealthModelConfigVO> modelConfigs = healthModelConfigMapper.query(healthModelConfigQueryDto);
        healthModelConfigQueryDto.setUserId(null);
        healthModelConfigQueryDto.setIsGlobal(true);
        List<HealthModelConfigVO> modelConfigsGlobal = healthModelConfigMapper.query(healthModelConfigQueryDto);
        List<HealthModelConfigVO> modelAll = new ArrayList<>();
        modelAll.addAll(modelConfigs);
        modelAll.addAll(modelConfigsGlobal);
        return ApiResult.success(modelAll);
    }

    /**
     * Health model query
     */
    @Override
    public Result<List<HealthModelConfigVO>> query(HealthModelConfigQueryDto healthModelConfigQueryDto) {
        healthModelConfigQueryDto.setVisibleTo(AccessPolicy.isAdmin() ? null : AccessPolicy.userId());
        List<HealthModelConfigVO> modelConfigs = healthModelConfigMapper.query(healthModelConfigQueryDto);
        Integer totalCount = healthModelConfigMapper.queryCount(healthModelConfigQueryDto);
        return PageResult.success(modelConfigs, totalCount);
    }

}
