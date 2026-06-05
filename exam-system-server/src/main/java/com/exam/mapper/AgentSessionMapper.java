package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.AgentSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Agent对话会话Mapper接口
 */
@Mapper
public interface AgentSessionMapper extends BaseMapper<AgentSession> {

    /**
     * 根据用户ID获取会话列表
     */
    @Select("SELECT * FROM agent_session WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<AgentSession> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和状态获取会话列表
     */
    @Select("SELECT * FROM agent_session WHERE user_id = #{userId} AND status = #{status} ORDER BY created_at DESC")
    List<AgentSession> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
}
