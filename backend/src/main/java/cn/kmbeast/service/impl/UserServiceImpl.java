package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.UserMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.PageResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.base.QueryDto;
import cn.kmbeast.pojo.dto.query.extend.UserQueryDto;
import cn.kmbeast.pojo.dto.update.GoogleLoginDTO;
import cn.kmbeast.pojo.dto.update.UserLoginDTO;
import cn.kmbeast.pojo.dto.update.UserRegisterDTO;
import cn.kmbeast.pojo.dto.update.UserUpdateDTO;
import cn.kmbeast.pojo.em.LoginStatusEnum;
import cn.kmbeast.pojo.em.RoleEnum;
import cn.kmbeast.pojo.em.WordStatusEnum;
import cn.kmbeast.pojo.entity.User;
import cn.kmbeast.pojo.vo.ChartVO;
import cn.kmbeast.pojo.vo.UserVO;
import cn.kmbeast.service.UserService;
import cn.kmbeast.utils.DateUtil;
import cn.kmbeast.utils.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User service implementation class
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    
    @Value("${google.oauth.client-id:}")
    private String googleClientId;

    /**
     * user registration
     */
    @Override
    public Result<String> register(UserRegisterDTO userRegisterDTO) {
        User user = userMapper.getByActive(
                User.builder().userName(userRegisterDTO.getUserName()).build()
        );
        if (Objects.nonNull(user)) {
            return ApiResult.error("The user name is already in use. Please change it");
        }
        User entity = userMapper.getByActive(
                User.builder().userAccount(userRegisterDTO.getUserAccount()).build()
        );
        if (Objects.nonNull(entity)) {
            return ApiResult.error("Account unavailable");
        }
        User saveEntity = User.builder()
                .userRole(RoleEnum.USER.getRole())
                .userName(userRegisterDTO.getUserName())
                .userAccount(userRegisterDTO.getUserAccount())
                .userAvatar(userRegisterDTO.getUserAvatar())
                .userPwd(userRegisterDTO.getUserPwd())
                .userEmail(userRegisterDTO.getUserEmail())
                .createTime(LocalDateTime.now())
                .isLogin(LoginStatusEnum.USE.getFlag())
                .isWord(WordStatusEnum.USE.getFlag()).build();
        userMapper.insert(saveEntity);
        return ApiResult.success("registered successfully");
    }

    /**
     * users login
     */
    @Override
    public Result<Object> login(UserLoginDTO userLoginDTO) {
        User user = userMapper.getByActive(
                User.builder().userAccount(userLoginDTO.getUserAccount()).build()
        );
        if (!Objects.nonNull(user)) {
            return ApiResult.error("Account does not exist");
        }
        if (!Objects.equals(userLoginDTO.getUserPwd(), user.getUserPwd())) {
            return ApiResult.error("wrong password");
        }
        if (user.getIsLogin()) {
            return ApiResult.error("Abnormal login status");
        }
        String token = JwtUtil.toToken(user.getId(), user.getUserRole());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("role", user.getUserRole());
        return ApiResult.success("login successfully", map);
    }
    
    @Override
    public Result<Object> googleLogin(GoogleLoginDTO googleLoginDTO) {
        if (googleLoginDTO == null || StringUtils.isBlank(googleLoginDTO.getIdToken())) {
            return ApiResult.error("Google credential is required");
        }
        if (StringUtils.isBlank(googleClientId)) {
            return ApiResult.error("Google OAuth client id is not configured on server");
        }
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance()
            ).setAudience(java.util.Collections.singletonList(googleClientId)).build();
            GoogleIdToken idToken = verifier.verify(googleLoginDTO.getIdToken());
            if (idToken == null) {
                return ApiResult.error("Invalid Google credential");
            }
            GoogleIdToken.Payload payload = idToken.getPayload();
            String sub = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");
            String account = "google_" + sub;
            User user = userMapper.getByActive(User.builder().userAccount(account).build());
            if (user == null) {
                String fallbackName = StringUtils.isNotBlank(name)
                        ? name
                        : (StringUtils.isNotBlank(email) ? email.split("@")[0] : "GoogleUser");
                User newUser = User.builder()
                        .userRole(RoleEnum.USER.getRole())
                        .userName(fallbackName)
                        .userAccount(account)
                        .userAvatar(picture)
                        .userPwd(UUID.randomUUID().toString().replace("-", ""))
                        .userEmail(email)
                        .createTime(LocalDateTime.now())
                        .isLogin(LoginStatusEnum.USE.getFlag())
                        .isWord(WordStatusEnum.USE.getFlag())
                        .build();
                userMapper.insert(newUser);
                user = userMapper.getByActive(User.builder().userAccount(account).build());
            }
            String token = JwtUtil.toToken(user.getId(), user.getUserRole());
            Map<String, Object> map = new HashMap<>();
            map.put("token", token);
            map.put("role", user.getUserRole());
            return ApiResult.success("google login successfully", map);
        } catch (Exception e) {
            log.error("Google login failed", e);
            return ApiResult.error("Google login failed");
        }
    }

    /**
     * Token verification - User information is returned after successful authentication
     */
    @Override
    public Result<UserVO> auth() {
        Integer userId = LocalThreadHolder.getUserId();
        User queryEntity = User.builder().id(userId).build();
        User user = userMapper.getByActive(queryEntity);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ApiResult.success(userVO);
    }

    /**
     * Paging query user data
     */
    @Override
    public Result<List<User>> query(UserQueryDto userQueryDto) {
        List<User> users = userMapper.query(userQueryDto);
        Integer count = userMapper.queryCount(userQueryDto);
        return PageResult.success(users, count);
    }

    /**
     * User information modification
     */
    @Override
    public Result<String> update(UserUpdateDTO userUpdateDTO) {
        User updateEntity = User.builder().id(LocalThreadHolder.getUserId()).build();
        BeanUtils.copyProperties(userUpdateDTO, updateEntity);
        userMapper.update(updateEntity);
        return ApiResult.success();
    }


    /**
     * Delete user information in batches
     */
    @Override
    public Result<String> batchDelete(List<Integer> ids) {
        userMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * User Information Change the password
     */
    @Override
    public Result<String> updatePwd(Map<String, String> map) {
        String oldPwd = map.get("oldPwd");
        String newPwd = map.get("newPwd");
        User user = userMapper.getByActive(
                User.builder().id(LocalThreadHolder.getUserId()).build()
        );
        if (!user.getUserPwd().equals(oldPwd)) {
            return ApiResult.error("The original password authentication failed");
        }
        user.setUserPwd(newPwd);
        userMapper.update(user);
        return ApiResult.success();
    }

    /**
     * Query user information by ID
     */
    @Override
    public Result<UserVO> getById(Integer id) {
        User user = userMapper.getByActive(User.builder().id(id).build());
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ApiResult.success(userVO);
    }

    /**
     * New background user
     */
    @Override
    public Result<String> insert(UserRegisterDTO userRegisterDTO) {
        return register(userRegisterDTO);
    }

    /**
     * Background user information is modified
     */
    @Override
    public Result<String> backUpdate(User user) {
        userMapper.update(user);
        return ApiResult.success();
    }

    /**
     * Collects user inventory data for a specified period of time
     */
    @Override
    public Result<List<ChartVO>> daysQuery(Integer day) {
        QueryDto queryDto = DateUtil.startAndEndTime(day);
        UserQueryDto userQueryDto = new UserQueryDto();
        userQueryDto.setStartTime(queryDto.getStartTime());
        userQueryDto.setEndTime(queryDto.getEndTime());
        List<User> userList = userMapper.query(userQueryDto);
        List<LocalDateTime> localDateTimes = userList.stream().map(User::getCreateTime).collect(Collectors.toList());
        List<ChartVO> chartVOS = DateUtil.countDatesWithinRange(day, localDateTimes);
        return ApiResult.success(chartVOS);
    }
}
