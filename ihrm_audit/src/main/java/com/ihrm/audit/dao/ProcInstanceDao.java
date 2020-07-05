package com.ihrm.audit.dao;

import com.ihrm.domain.audit.ProcInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProcInstanceDao extends JpaRepository<ProcInstance,String>,
		JpaSpecificationExecutor<ProcInstance> {
}
