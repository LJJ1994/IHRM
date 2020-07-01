package com.ihrm.system.service;

import com.ihrm.domain.system.Permission;

import java.util.List;
import java.util.Map;

public interface PermissionService {
    void save(Map<String, Object> map) throws Exception;
    void update(Map<String, Object> map) throws Exception;
    void delete(String id);

    /**
     * 查询权限，根据type查询权限资源(menu, api, point), 构造成map 返回
     * @param id
     * @return
     */
    Map<String, Object> findById(String id);
    List<Permission> findAll(Map<String, Object> map);
}
