package com.ihrm.system.service;

import com.ihrm.domain.system.Role;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RoleService {
    void save(Role role);
    void update(Role role);
    void delete(String id);
    Role findById(String id);

    Page<Role> findSearch(Integer page, Integer size, String companyId);
    List<Role> findAll(String companyId);

    /**
     * 为角色分配权限
     * @param roleId
     * @param ids
     */
    void assignPerms(String roleId, List<String> ids);
}
