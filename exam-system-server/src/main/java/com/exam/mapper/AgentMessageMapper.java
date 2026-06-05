package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.AgentMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 对话消息记录Mapper接口
 */
@Mapper
public interface AgentMessageMapper extends BaseMapper<AgentMessage> {

    /**
     * 根据会话ID获取消息列表
     */
    @Select("SELECT * FROM agent_message WHERE session_id = #{sessionId} ORDER BY created_at")
    List<AgentMessage> selectBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 根据会话ID和阶段获取消息列表
     */
    @Select("SELECT * FROM agent_message WHERE session_id = #{sessionId} AND phase = #{phase} ORDER BY created_at")
    List<AgentMessage> selectBySessionIdAndPhase(@Param("sessionId") Long sessionId, @Param("phase") String phase);
}
