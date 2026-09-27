package cn.kmbeast.controller;

import cn.kmbeast.aop.Protector;
import cn.kmbeast.pojo.em.RoleEnum;
import cn.kmbeast.aop.Pager;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.TagsQueryDto;
import cn.kmbeast.pojo.entity.Tags;
import cn.kmbeast.service.TagsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Tags Controller
 */
@RestController
@RequestMapping(value = "/tags")
public class TagsController {

    @Resource
    private TagsService tagsService;

    /**
     * Add a new tag
     */
    @Protector(roles = RoleEnum.ADMIN)
    @PostMapping(value = "/save")
    public Result<Void> save(@RequestBody Tags tags) {
        return tagsService.save(tags);
    }

    /**
     * Delete tags in batch
     */
    @Protector(roles = RoleEnum.ADMIN)
    @PostMapping(value = "/batchDelete")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        return tagsService.batchDelete(ids);
    }

    /**
     * Update a tag
     */
    @Protector(roles = RoleEnum.ADMIN)
    @PutMapping(value = "/update")
    public Result<Void> update(@RequestBody Tags tags) {
        return tagsService.update(tags);
    }

    /**
     * Query tags
     */
    @Pager
    @PostMapping(value = "/query")
    public Result<List<Tags>> query(@RequestBody TagsQueryDto tagsQueryDto) {
        return tagsService.query(tagsQueryDto);
    }

}
