package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.PathNodeKp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 路径节点知识点关联Mapper接口
 */
@Mapper
public interface PathNodeKpMapper extends BaseMapper<PathNodeKp> {

    /**
     * 根据路径节点ID获取关联的知识点ID列表
     */
    @Select("SELECT knowledge_point_id FROM path_node_kp WHERE path_node_id = #{pathNodeId}")
    List<Long> selectKpIdsByPathNodeId(@Param("pathNodeId") Long pathNodeId);

    /**
     * 根据知识点ID获取关联的路径节点ID列表
     */
    @Select("SELECT path_node_id FROM path_node_kp WHERE knowledge_point_id = #{kpId}")
    List<Long> selectPathNodeIdsByKpId(@Param("kpId") Long kpId);
}
