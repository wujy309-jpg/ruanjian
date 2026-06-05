package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.GeneratedResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI生成的个性化学习资源Mapper接口
 */
@Mapper
public interface GeneratedResourceMapper extends BaseMapper<GeneratedResource> {

    /**
     * 根据路径节点ID获取资源列表
     */
    @Select("SELECT * FROM generated_resource WHERE path_node_id = #{pathNodeId}")
    List<GeneratedResource> selectByPathNodeId(@Param("pathNodeId") Long pathNodeId);

    /**
     * 根据知识点ID获取资源列表
     */
    @Select("SELECT * FROM generated_resource WHERE knowledge_point_id = #{kpId}")
    List<GeneratedResource> selectByKnowledgePointId(@Param("kpId") Long kpId);
}
