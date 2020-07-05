package com.ihrm.audit.dao;

import com.ihrm.domain.audit.ProcUserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface ProcUserGroupDao extends JpaRepository<ProcUserGroup,String>,
		JpaSpecificationExecutor<ProcUserGroup> {
}
