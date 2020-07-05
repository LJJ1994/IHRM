package com.ihrm.audit.dao;

import com.ihrm.domain.audit.ProcTaskInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface ProcTaskInstanceDao extends JpaRepository<ProcTaskInstance,String>, JpaSpecificationExecutor<ProcTaskInstance> {


	ProcTaskInstance findByProcessIdAndTaskKey(String processId, String taskKey) ;

	/**
	 * 根据流程id查询,展示每个节点数据
	 * @param processId	流程id
	 * @return
	 */
	List<ProcTaskInstance> findByProcessId(String processId);
}
