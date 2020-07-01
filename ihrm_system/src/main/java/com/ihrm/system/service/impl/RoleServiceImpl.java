package com.ihrm.system.service.impl;

import com.ihrm.common.constants.PermissionConstants;
import com.ihrm.common.service.BaseService;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.Role;
import com.ihrm.system.dao.PermissionDao;
import com.ihrm.system.dao.RoleDao;
import com.ihrm.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleServiceImpl extends BaseService implements RoleService {
    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PermissionDao permissionDao;

    @Override
    public void save(Role role) {
        role.setId(idWorker.nextId()+"");
        roleDao.save(role);
    }

    @Override
    public void update(Role role) {
        Role one = roleDao.getOne(role.getId());
        one.setDescription(role.getDescription());
        one.setName(role.getName());
        roleDao.save(one);
    }

    @Override
    public void delete(String id) {
        roleDao.deleteById(id);
    }

    @Override
    public Role findById(String id) {
        return roleDao.findById(id).get();
    }

    @Override
    public Page<Role> findSearch(Integer page, Integer size, String companyId) {
        Specification<Role> specification = new Specification<Role>() {
            @Override
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("companyId").as(String.class), companyId);
            }
        };
        return roleDao.findAll(specification, PageRequest.of(page-1, size));
    }

    /**
     * 查询某公司下的所有角色
     * @param companyId
     * @return
     */
    @Override
    public List<Role> findAll(String companyId) {
        return roleDao.findAll(getSpecification(companyId));
    }

    /**
     * 为角色分配权限
     * @param roleId
     * @param ids
     */
    @Override
    public void assignPerms(String roleId, List<String> ids) {
        Role role = roleDao.findById(roleId).get();
        Set<Permission> permissions = new HashSet<>();
        for (String id : ids) {
            Permission permission = permissionDao.findById(id).get();
            //需要根据父id和类型查询API权限列表
            List<Permission> permissionList = permissionDao.findByTypeAndPid(PermissionConstants.PERMISSION_API, permission.getId());
            //自定义赋予API权限
            permissions.addAll(permissionList);
            //当前菜单或按钮的权限
            permissions.add(permission);
        }
        role.setPermissions(permissions);
        roleDao.save(role);
    }
}
