package com.exam.service;

import com.exam.entity.UserProfile;

/**
 * 用户学习画像Service接口
 */
public interface UserProfileService {

    /**
     * 获取用户学习画像
     */
    UserProfile getUserProfile(Long userId);

    /**
     * 更新用户学习画像
     */
    void updateUserProfile(Long userId, UserProfile profile);

    /**
     * 创建用户学习画像
     */
    void createUserProfile(UserProfile profile);

    /**
     * 删除用户学习画像
     */
    void deleteUserProfile(Long userId);
}
