package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.KnowledgePoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 知识点Mapper接口
 */
@Mapper
public interface KnowledgePointMapper extends BaseMapper<KnowledgePoint> {

    /**
     * 根据课程ID获取知识点列表
     */
    @Select("SELECT * FROM knowledge_point WHERE course_id = #{courseId} ORDER BY sort_order")
    List<KnowledgePoint> selectByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据编码获取知识点
     */
    @Select("SELECT * FROM knowledge_point WHERE code = #{code}")
    KnowledgePoint selectByCode(@Param("code") String code);

    /**
     * 根据父级ID获取子知识点
     */
    @Select("SELECT * FROM knowledge_point WHERE parent_id = #{parentId} ORDER BY sort_order")
    List<KnowledgePoint> selectByParentId(@Param("parentId") Long parentId);
}
