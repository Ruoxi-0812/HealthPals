package cn.kmbeast.mapper;

import cn.kmbeast.pojo.dto.query.extend.UserQueryDto;
import cn.kmbeast.pojo.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * User Persistence Interface
 */
public interface UserMapper {

    /**
     * Insert a new user
     *
     * @param userInsert User entity
     * @return int Number of affected rows
     */
    int insert(User userInsert);

    /**
     * Query users with pagination
     *
     * @param userQueryDto Query parameters for pagination
     * @return List<User> List of users
     */
    List<User> query(UserQueryDto userQueryDto);

    /**
     * Get the total number of users matching the query
     *
     * @param userQueryDto Query parameters for pagination
     * @return int Total number of users
     */
    int queryCount(UserQueryDto userQueryDto);

    /**
     * Update user information
     *
     * @param user User entity
     * @return int Number of affected rows
     */
    int update(User user);

    int upgradePassword(@Param("id") Integer id, @Param("previous") String previous,
                        @Param("replacement") String replacement);

    /**
     * Batch delete users
     *
     * @param ids List of user IDs
     */
    void batchDelete(@Param(value = "ids") List<Integer> ids);

    /**
     * Find a user by non-null fields
     *
     * @param user Query parameters (User entity with non-null fields)
     * @return User User entity
     */
    User getByActive(User user);

}
