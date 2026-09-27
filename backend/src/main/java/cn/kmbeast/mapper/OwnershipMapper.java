package cn.kmbeast.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OwnershipMapper {
    List<Integer> lockOwners(@Param("resource") String resource, @Param("ids") List<Long> ids);
}
