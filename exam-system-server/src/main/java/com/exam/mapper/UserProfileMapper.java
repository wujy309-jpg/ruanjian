package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.exam.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

/**
 * 用户学习画像Mapper接口
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {

    /**
     * 根据用户ID获取画像
     */
    @Select("SELECT * FROM user_profile WHERE user_id = #{userId}")
    @Results(id = "userProfileMap", value = {
        @Result(property = "dimensions", column = "dimensions", typeHandler = JacksonTypeHandler.class)
    })
    UserProfile selectByUserId(@Param("userId") Long userId);
}
