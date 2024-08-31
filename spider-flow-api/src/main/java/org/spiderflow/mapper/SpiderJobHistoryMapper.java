package org.spiderflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.spiderflow.core.dto.SpiderJobHistoryDTO;
import org.spiderflow.core.model.SpiderJobHistory;

import java.util.Date;
import java.util.List;

/**
 * @author yida
 * @package org.spiderflow.mapper
 * @date 2024-08-28 20:14
 * @description Type your description over here.
 */
public interface SpiderJobHistoryMapper extends BaseMapper<SpiderJobHistory> {
	@Select({
			"<script>",
			"select sjh.id, sf.name as spider_flow_name, sjh.flow_id, sjh.start_execution_time, sjh.end_execution_time, sjh.execution_status",
			"from sp_job_history sjh",
			"left join sp_flow sf on sf.id=sjh.flow_id",
			"where 1=1",
			"<if test=\"flowId != null and flowId != ''\">",
			"	and sjh.flow_id = #{flowId}",
			"</if>",
			"<if test=\"spiderName != null and spiderName != ''\">",
			"	and sf.name like concat('%',#{spiderName},'%')",
			"</if>",
			"<if test=\"executionStatus != null\">",
			"	and sjh.execution_status = #{executionStatus}",
			"</if>",
			"<if test=\"startExecutionTime != null\">",
			"	and sjh.start_execution_time &lt;= #{startExecutionTime}",
			"</if>",
			"<if test=\"endExecutionTime != null\">",
			"	and sjh.end_execution_time &gt;= #{endExecutionTime}",
			"</if>",
			"order by sjh.start_execution_time desc",
			"</script>"
	})
	IPage<SpiderJobHistoryDTO> selectPage(Page<SpiderJobHistoryDTO> page, @Param("flowId") String flowId,
										  @Param("spiderName") String spiderName,
										  @Param("executionStatus") Integer executionStatus,
										  @Param("startExecutionTime") Date startExecutionTime,
										  @Param("endExecutionTime") Date endExecutionTime);

	@Insert("insert into sp_job_history(id,flow_id,start_execution_time,end_execution_time,execution_status) " +
			"values(#{spiderJobHistory.id},#{spiderJobHistory.flowId},#{spiderJobHistory.startExecutionTime}," +
			"#{spiderJobHistory.endExecutionTime},#{spiderJobHistory.executionStatus})")
	int insertSpiderJobHistory(@Param("spiderJobHistory") SpiderJobHistory spiderJobHistory);

	@Update("update sp_job_history set flow_id=#{spiderJobHistory.flowId}," +
			"start_execution_time=#{spiderJobHistory.startExecutionTime}," +
			"end_execution_time=#{spiderJobHistory.endExecutionTime}," +
			"execution_status=#{spiderJobHistory.executionStatus} where id=#{spiderJobHistory.id}")
	int updateSpiderJobHistory(@Param("spiderJobHistory") SpiderJobHistory spiderJobHistory);

	@Update("update sp_job_history set execution_status=#{executionStatus} where id=#{id}")
	int updateExecutionStatus(@Param("id") String id, @Param("executionStatus") Integer executionStatus);

	@Update("update sp_job_history set start_execution_time=NOW() where id=#{id}")
	int updateStartExecutionTime(@Param("id") String id);

	@Update("update sp_job_history set end_execution_time=NOW() where id=#{id}")
	int updateEndExecutionTime(@Param("id") String id);

	@Select("select id,flow_id,start_execution_time,end_execution_time,execution_status from sp_job_history where id = #{id}")
	SpiderJobHistory queryById(@Param("id") String id);

	@Select("select id,flow_id,start_execution_time,end_execution_time,execution_status from sp_job_history where flow_id = #{flowId}")
	List<SpiderJobHistory> queryByFlowId(@Param("flowId") String flowId);

	@Select("select id,flow_id,start_execution_time,end_execution_time,execution_status from sp_job_history " +
			"where flow_id = #{flowId} order by start_execution_time desc limit 0,1")
	SpiderJobHistory queryLastJobHistory(@Param("flowId") String flowId);

	@Delete("delete from sp_job_history where id=#{id}")
	int deleteById(@Param("id") String id);

	@Delete("delete from sp_job_history where flowId=#{flowId}")
	int deleteByFlowId(@Param("flowId") String flowId);

	@Delete({
			"<script>",
			"delete from sp_job_history where id in ",
			"    <foreach collection='idList' item='id' separator=',' open='(' close=')'> ",
			"        #{id}",
			"    </foreach>",
			"</script>"
	})
	int batchDeleteByIds(@Param("idList") List<String> idList);
}
