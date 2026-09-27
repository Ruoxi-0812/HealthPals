package cn.kmbeast.controller;

import cn.kmbeast.aop.Pager;
import cn.kmbeast.aop.Protector;
import cn.kmbeast.pojo.em.RoleEnum;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.EvaluationsQueryDto;
import cn.kmbeast.pojo.entity.Evaluations;
import cn.kmbeast.service.EvaluationsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Comment Controller
 */
@RestController
@RequestMapping("/evaluations")
public class EvaluationsController {

    @Resource
    private EvaluationsService evaluationsService;

    /**
     * Add a comment
     *
     * @return Result<String>
     */
    @Protector
    @PostMapping(value = "/insert")
    @ResponseBody
    public Result<Object> insert(@RequestBody Evaluations evaluations) {
        return evaluationsService.insert(evaluations);
    }

    /**
     * Update a comment
     *
     * @return Result<Map < String, Object>>
     */
    @Protector
    @PutMapping(value = "/update")
    @ResponseBody
    public Result<Map<String, Object>> update(@RequestBody Evaluations evaluations) {
        return evaluationsService.update(evaluations);
    }

    /**
     * Retrieve all comments for a specific content
     *
     * @return Result<String>
     */
    @Protector
    @GetMapping(value = "/list/{contentId}/{contentType}")
    @ResponseBody
    public Result<Object> list(@PathVariable Integer contentId,
                               @PathVariable String contentType) {
        return evaluationsService.list(contentId, contentType);
    }

    /**
     * Paginated query for comments
     *
     * @return Result<String>
     */
    @Pager
    @Protector(roles = RoleEnum.ADMIN)
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<Object> query(@RequestBody EvaluationsQueryDto evaluationsQueryDto) {
        return evaluationsService.query(evaluationsQueryDto);
    }

    /**
     * Batch delete comments
     *
     * @return Result<String>
     */
    @Protector(roles = RoleEnum.ADMIN)
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public Result<Object> batchDelete(@RequestBody List<Integer> ids) {
        return evaluationsService.batchDelete(ids);
    }

    /**
     * Delete a comment by ID
     *
     * @return Result<String>
     */
    @Protector
    @DeleteMapping(value = "/delete/{id}")
    @ResponseBody
    public Result<String> delete(@PathVariable Integer id) {
        return evaluationsService.delete(id);
    }

}

