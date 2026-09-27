package cn.kmbeast.mapper;

import cn.kmbeast.pojo.entity.HealthSubmission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HealthSubmissionMapper {
    void claim(@Param("userId") Integer userId, @Param("key") String key,
               @Param("hash") String hash);
    HealthSubmission lock(@Param("userId") Integer userId, @Param("key") String key);
    void complete(@Param("userId") Integer userId, @Param("key") String key);
}
