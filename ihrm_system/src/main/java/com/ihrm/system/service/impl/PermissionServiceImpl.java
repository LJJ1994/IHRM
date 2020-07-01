package com.ihrm.system.service.impl;

import com.ihrm.common.constants.PermissionConstants;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.exception.CommonException;
import com.ihrm.common.utils.BeanMapUtils;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.PermissionApi;
import com.ihrm.domain.system.PermissionMenu;
import com.ihrm.domain.system.PermissionPoint;
import com.ihrm.system.dao.PermissionApiDao;
import com.ihrm.system.dao.PermissionDao;
import com.ihrm.system.dao.PermissionMenuDao;
import com.ihrm.system.dao.PermissionPointDao;
import com.ihrm.system.service.PermissionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionDao permissionDao;
    @Autowired
    private PermissionMenuDao permissionMenuDao;
    @Autowired
    private PermissionPointDao permissionPointDao;
    @Autowired
    private PermissionApiDao permissionApiDao;
    @Autowired
    private IdWorker idWorker;

    @Override
    @Transactional
    public void save(Map<String, Object> map) throws Exception {
        Permission permission = BeanMapUtils.mapToBean(map, Permission.class);
        permission.setId(idWorker.nextId() + "");
        Integer type = permission.getType();

        // 根据类型构造不同的资源对象（菜单，按钮，api）
        switch (type){
            case PermissionConstants.PERMISSION_MENU:
                PermissionMenu permissionMenu = BeanMapUtils.mapToBean(map, PermissionMenu.class);
                permissionMenuDao.save(permissionMenu);
                break;
            case PermissionConstants.PERMISSION_POINT:
                PermissionPoint permissionPoint = BeanMapUtils.mapToBean(map, PermissionPoint.class);
                permissionPointDao.save(permissionPoint);
                break;
            case PermissionConstants.PERMISSION_API:
                PermissionApi permissionApi = BeanMapUtils.mapToBean(map, PermissionApi.class);
                permissionApiDao.save(permissionApi);
                break;
            default:
                throw new CommonException(ResultCode.FAIL);
        }
        permissionDao.save(permission);
    }

    @Override
    public void update(Map<String, Object> map) throws Exception {
        Permission perm = BeanMapUtils.mapToBean(map,Permission.class);
        //1.通过传递的权限id查询权限
        Permission permission = permissionDao.findById(perm.getId()).get();
        permission.setName(perm.getName());
        permission.setCode(perm.getCode());
        permission.setDescription(perm.getDescription());
        permission.setEnVisible(perm.getEnVisible());
        //2.根据类型构造不同的资源
        int type = perm.getType();
        switch (type) {
            case PermissionConstants.PERMISSION_MENU:
                PermissionMenu menu = BeanMapUtils.mapToBean(map,PermissionMenu.class);
                menu.setId(perm.getId());
                permissionMenuDao.save(menu);
                break;
            case PermissionConstants.PERMISSION_POINT:
                PermissionPoint point =
                        BeanMapUtils.mapToBean(map,PermissionPoint.class);
                point.setId(perm.getId());
                permissionPointDao.save(point);
                break;
            case PermissionConstants.PERMISSION_API:
                PermissionApi api = BeanMapUtils.mapToBean(map,PermissionApi.class);
                api.setId(perm.getId());
                permissionApiDao.save(api);
                break;
            default:
                throw new CommonException(ResultCode.FAIL);
        }
        //3.保存
        permissionDao.save(permission);
    }

    /**
     * 5.根据id删除
     * //1.删除权限
     * //2.删除权限对应的资源
     *
     */
    @Override
    public void delete(String id) {
        //1.通过传递的权限id查询权限
        Permission permission = permissionDao.findById(id).get();
        permissionDao.delete(permission);
        //2.根据类型构造不同的资源
        int type = permission.getType();
        switch (type) {
            case PermissionConstants.PERMISSION_MENU:
                permissionMenuDao.deleteById(id);
                break;
            case PermissionConstants.PERMISSION_POINT:
                permissionPointDao.deleteById(id);
                break;
            case PermissionConstants.PERMISSION_API:
                permissionApiDao.deleteById(id);
                break;
            default:
                throw new CommonException(ResultCode.FAIL);
        }
    }

    /**
     * 根据id查询
     * 1.查询权限
     * 2.根据权限的类型查询资源
     * 3.构造map集合
     */
    @Override
    public Map<String, Object> findById(String id) {
        Permission permission = permissionDao.findById(id).get();
        Integer type = permission.getType();
        Object object;
        if (type == PermissionConstants.PERMISSION_MENU){
            object = permissionMenuDao.findById(id);
        }else if (type == PermissionConstants.PERMISSION_POINT){
            object = permissionPointDao.findById(id);
        }else if (type == PermissionConstants.PERMISSION_API){
            object = permissionApiDao.findById(id);
        }else {
            throw new CommonException(ResultCode.FAIL);
        }

        Map<String, Object> map = BeanMapUtils.beanToMap(object);
        map.put("name", permission.getName());
        map.put("type", permission.getType());
        map.put("code", permission.getCode());
        map.put("description", permission.getDescription());
        map.put("pid", permission.getPid());
        map.put("enVisible", permission.getEnVisible());
        return map;
    }

    /**
     * 4.查询全部
     * type : 查询全部权限列表type：0：菜单 + 按钮（权限点） 1：菜单2：按钮（权限点）3：API接口
     * enVisible : 0：查询所有saas平台的最高权限，1：查询企业的权限
     * pid ：父id
     */
    @Override
    public List<Permission> findAll(Map<String, Object> map) {
        Specification<Permission> spec = new Specification<Permission>() {
            @Override
            public Predicate toPredicate(Root<Permission> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                if (!StringUtils.isEmpty((String) map.get("pid"))){
                    list.add(criteriaBuilder.equal(root.get("pid").as(String.class), (String) map.get("pid")));
                }
                if (!StringUtils.isEmpty((String) map.get("enVisible"))){
                    list.add(criteriaBuilder.equal(root.get("enVisible").as(String.class), (String) map.get("enVisible")));
                }
                if (!StringUtils.isEmpty((String) map.get("type"))){
                    String ty = (String) map.get("type");
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("type"));
                    if ("0".equals(ty)){
                        in.value(1).value(2);
                    }else{
                        in.value(Integer.parseInt(ty));
                    }
                    list.add(in);
                }
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return permissionDao.findAll(spec);
    }
}
