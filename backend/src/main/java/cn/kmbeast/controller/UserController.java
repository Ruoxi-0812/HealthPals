package cn.kmbeast.controller;

import cn.kmbeast.aop.Pager;
import cn.kmbeast.aop.Protector;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.UserQueryDto;
import cn.kmbeast.pojo.dto.update.GoogleLoginDTO;
import cn.kmbeast.pojo.dto.update.UserLoginDTO;
import cn.kmbeast.pojo.dto.update.UserRegisterDTO;
import cn.kmbeast.pojo.dto.update.UserUpdateDTO;
import cn.kmbeast.pojo.entity.User;
import cn.kmbeast.pojo.vo.ChartVO;
import cn.kmbeast.pojo.vo.UserVO;
import cn.kmbeast.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * User login
     */
    @PostMapping(value = "/login")
    @ResponseBody
    public Result<Object> login(@RequestBody UserLoginDTO userLoginDTO) {
        return userService.login(userLoginDTO);
    }
    
    /**
     * Google sign-in / sign-up
     */
    @PostMapping(value = "/google-login")
    @ResponseBody
    public Result<Object> googleLogin(@RequestBody GoogleLoginDTO googleLoginDTO) {
        return userService.googleLogin(googleLoginDTO);
    }

    /**
     * Token verification
     */
    @Protector
    @GetMapping(value = "/auth")
    @ResponseBody
    public Result<UserVO> auth() {
        return userService.auth();
    }


    /**
     * Retrieve user information by ID
     */
    @Protector
    @GetMapping(value = "/getById/{id}")
    @ResponseBody
    public Result<UserVO> getById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    /**
     * User registration
     */
    @PostMapping(value = "/register")
    @ResponseBody
    public Result<String> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        return userService.register(userRegisterDTO);
    }

    /**
     * Add new user (Admin only)
     */
    @Protector(role = "admin")
    @PostMapping(value = "/insert")
    @ResponseBody
    public Result<String> insert(@RequestBody UserRegisterDTO userRegisterDTO) {
        return userService.insert(userRegisterDTO);
    }

    /**
     * Update user information
     */
    @Protector
    @PutMapping(value = "/update")
    @ResponseBody
    public Result<String> update(@RequestBody UserUpdateDTO userUpdateDTO) {
        return userService.update(userUpdateDTO);
    }

    /**
     * Update user information (Admin only)
     */
    @Protector(role = "admin")
    @PutMapping(value = "/backUpdate")
    @ResponseBody
    public Result<String> backUpdate(@RequestBody User user) {
        return userService.backUpdate(user);
    }

    /**
     * Change user password
     */
    @PutMapping(value = "/updatePwd")
    @ResponseBody
    public Result<String> updatePwd(@RequestBody Map<String, String> map) {
        return userService.updatePwd(map);
    }

    /**
     * Batch delete users (Admin only)
     */
    @Protector(role = "admin")
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public Result<String> batchDelete(@RequestBody List<Integer> ids) {
        return userService.batchDelete(ids);
    }

    /**
     * Query user data (Admin only)
     */
    @Pager
    @Protector(role = "admin")
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<List<User>> query(@RequestBody UserQueryDto userQueryDto) {
        return userService.query(userQueryDto);
    }

    /**
     * Retrieve user statistics for a specific number of days
     */
    @GetMapping(value = "/daysQuery/{day}")
    @ResponseBody
    public Result<List<ChartVO>> query(@PathVariable Integer day) {
        return userService.daysQuery(day);
    }

}

