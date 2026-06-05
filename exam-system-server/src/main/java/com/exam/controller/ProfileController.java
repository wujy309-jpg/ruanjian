package com.exam.controller;

import com.exam.common.Result;
import com.exam.entity.UserProfile;
import com.exam.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户学习画像控制器
 * 处理用户学习画像相关的HTTP请求
 */
@RestController
@RequestMapping("/api/user/profile")
@Tag(name = "用户学习画像", description = "用户学习画像相关操作，包括画像的查询和更新")
public class ProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/{userId}")
    @Operation(summary = "获取用户学习画像", description = "根据用户ID获取学习画像，A的Agent读取画像使用")
    public Result<UserProfile> getUserProfile(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        UserProfile profile = userProfileService.getUserProfile(userId);
        return Result.success(profile);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "更新用户学习画像", description = "更新用户的学习画像数据，A的Agent写入画像使用")
    public Result<Void> updateUserProfile(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @RequestBody UserProfile profile) {
        userProfileService.updateUserProfile(userId, profile);
        return Result.success(null);
    }

    @PostMapping
    @Operation(summary = "创建用户学习画像", description = "为用户创建学习画像")
    public Result<Void> createUserProfile(@RequestBody UserProfile profile) {
        userProfileService.createUserProfile(profile);
        return Result.success(null);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户学习画像", description = "删除用户的学习画像")
    public Result<Void> deleteUserProfile(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        userProfileService.deleteUserProfile(userId);
        return Result.success(null);
    }
}
