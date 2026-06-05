package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.KnowledgeEdge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 知识点前置依赖关系Mapper接口
 */
@Mapper
public interface KnowledgeEdgeMapper extends BaseMapper<KnowledgeEdge> {

    /**
     * 根据课程ID获取所有依赖关系
     */
    @Select("SELECT ke.* FROM knowledge_edge ke " +
            "INNER JOIN knowledge_point kp ON ke.from_kp_id = kp.id " +
            "WHERE kp.course_id = #{courseId}")
    List<KnowledgeEdge> selectByCourseId(@Param("courseId") Long courseId);

    /**
     * 获取某个知识点的所有前置依赖
     */
    @Select("SELECT * FROM knowledge_edge WHERE to_kp_id = #{kpId}")
    List<KnowledgeEdge> selectByToKpId(@Param("kpId") Long kpId);

    /**
     * 获取某个知识点的所有后置依赖
     */
    @Select("SELECT * FROM knowledge_edge WHERE from_kp_id = #{kpId}")
    List<KnowledgeEdge> selectByFromKpId(@Param("kpId") Long kpId);
}
