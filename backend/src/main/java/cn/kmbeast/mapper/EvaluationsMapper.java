package cn.kmbeast.mapper;

import cn.kmbeast.pojo.dto.query.extend.EvaluationsQueryDto;
import cn.kmbeast.pojo.entity.Evaluations;
import cn.kmbeast.pojo.vo.CommentChildVO;
import cn.kmbeast.pojo.vo.CommentParentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Comment Persistence Interface (Mapper)
 */
public interface EvaluationsMapper {

    /**
     * Retrieve all top-level (parent) comments under a specific content
     */
    List<CommentParentVO> getParentComments(@Param(value = "contentId") Integer contentId,
                                            @Param(value = "contentType") String contentType);

    /**
     * Paginated query for comments
     */
    List<CommentChildVO> query(EvaluationsQueryDto evaluationsQueryDto);

    /**
     * Get the total number of comments in a paginated query
     */
    Integer queryCount(EvaluationsQueryDto evaluationsQueryDto);

    /**
     * Retrieve all secondary (child) comments
     */
    List<Integer> selectChildComments(@Param(value = "ids") List<Integer> ids);

    /**
     * Batch delete comments
     */
    void batchDelete(@Param(value = "ids") List<Integer> ids);

    /**
     * Get the total number of comments for a specific content
     */
    Integer totalCount(Integer contentId, String contentType);

    /**
     * Add a new comment
     */
    void save(Evaluations evaluations);

    /**
     * Update an existing comment
     */
    void update(Evaluations evaluations);

    /**
     * Distinct commenter user IDs on a piece of content (for inbox notifications).
     */
    List<Integer> listCommenterIdsByContent(@Param("contentId") Integer contentId,
                                            @Param("contentType") String contentType,
                                            @Param("excludeUserId") Integer excludeUserId);

}
