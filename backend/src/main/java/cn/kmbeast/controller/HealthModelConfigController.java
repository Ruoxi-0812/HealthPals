package cn.kmbeast.controller;

import cn.kmbeast.aop.Pager;
import cn.kmbeast.aop.Protector;
import cn.kmbeast.pojo.em.RoleEnum;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.HealthModelConfigQueryDto;
import cn.kmbeast.pojo.entity.HealthModelConfig;
import cn.kmbeast.pojo.vo.HealthModelConfigVO;
import cn.kmbeast.service.HealthModelConfigService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Health Model Controller
 */
@RestController
@RequestMapping(value = "/health-model-config")
public class HealthModelConfigController {

    @Resource
    private HealthModelConfigService healthModelConfigService;

    /**
     * Add a health model
     *
     * @param healthModelConfig New model data
     * @return Result<Void> Generic response
     */
    @PostMapping(value = "/save")
    public Result<Void> save(@RequestBody HealthModelConfig healthModelConfig) {
        healthModelConfig.setIsGlobal(false);
        return healthModelConfigService.save(healthModelConfig);
    }

    /**
     * Add a health model (Admin only)
     *
     * @param healthModelConfig New model data
     * @return Result<Void> Generic response
     */
    @Protector(roles = RoleEnum.ADMIN)
    @PostMapping(value = "config/save")
    public Result<Void> configSave(@RequestBody HealthModelConfig healthModelConfig) {
        healthModelConfig.setIsGlobal(true);
        return healthModelConfigService.save(healthModelConfig);
    }


    /**
     * Delete health models in batch
     */
    @PostMapping(value = "/batchDelete")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        return healthModelConfigService.batchDelete(ids);
    }

    /**
     * Update a health model
     *
     * @param healthModelConfig Parameters
     * @return Result<Void> Response
     */
    @PutMapping(value = "/update")
    public Result<Void> update(@RequestBody HealthModelConfig healthModelConfig) {
        return healthModelConfigService.update(healthModelConfig);
    }

    /**
     * Retrieve both user-configured models and global models
     */
    @Pager
    @PostMapping(value = "/modelList")
    public Result<List<HealthModelConfigVO>> modelList() {
        return healthModelConfigService.modelList();
    }

    /**
     * Query health models
     */
    @Pager
    @PostMapping(value = "/query")
    public Result<List<HealthModelConfigVO>> query(@RequestBody HealthModelConfigQueryDto healthModelConfigQueryDto) {
        return healthModelConfigService.query(healthModelConfigQueryDto);
    }


}
