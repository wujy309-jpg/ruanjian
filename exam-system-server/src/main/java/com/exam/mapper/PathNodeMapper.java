package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.PathNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 路径节点Mapper接口
 */
@Mapper
public interface PathNodeMapper extends BaseMapper<PathNode> {

    /**
     * 根据路径ID获取节点列表
     */
    @Select("SELECT * FROM path_node WHERE path_id = #{pathId} ORDER BY node_order")
    List<PathNode> selectByPathId(@Param("pathId") Long pathId);
}
