package com.exam.service.impl;

import com.exam.entity.UserProfile;
import com.exam.mapper.UserProfileMapper;
import com.exam.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户学习画像Service实现类
 */
@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Override
    public UserProfile getUserProfile(Long userId) {
        return userProfileMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public void updateUserProfile(Long userId, UserProfile profile) {
        UserProfile existing = userProfileMapper.selectByUserId(userId);
        if (existing == null) {
            throw new RuntimeException("用户画像不存在");
        }
        existing.setDimensions(profile.getDimensions());
        existing.setUpdatedBySessionId(profile.getUpdatedBySessionId());
        userProfileMapper.updateById(existing);
    }

    @Override
    @Transactional
    public void createUserProfile(UserProfile profile) {
        // 检查是否已存在
        UserProfile existing = userProfileMapper.selectByUserId(profile.getUserId());
        if (existing != null) {
            throw new RuntimeException("用户画像已存在");
        }
        userProfileMapper.insert(profile);
    }

    @Override
    @Transactional
    public void deleteUserProfile(Long userId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserProfile> wrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(UserProfile::getUserId, userId);
        userProfileMapper.delete(wrapper);
    }
}
