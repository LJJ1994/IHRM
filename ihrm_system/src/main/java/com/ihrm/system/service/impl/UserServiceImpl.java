package com.ihrm.system.service.impl;

import com.ihrm.common.utils.IdWorker;
import com.ihrm.domain.company.Department;
import com.ihrm.domain.system.Role;
import com.ihrm.domain.system.User;
import com.ihrm.system.client.CompanyFeignClient;
import com.ihrm.system.dao.RoleDao;
import com.ihrm.system.dao.UserDao;
import com.ihrm.system.service.UserService;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CompanyFeignClient companyFeignClient;

    @Override
    public void save(User user) {
        user.setCreateTime(new Date());
        user.setId(idWorker.nextId()+"");
        user.setEnableState(1);
        user.setPassword("123456");
        user.setLevel("user"); // 用户级别：saasAdmin，coAdmin，user
        userDao.save(user);
    }

    @Override
    public void update(User user) {
        User one = userDao.findById(user.getId()).get();
        one.setUsername(user.getUsername());
        one.setPassword(user.getPassword());
        one.setMobile(user.getMobile());
        one.setDepartmentId(user.getDepartmentId());
        one.setDepartmentName(user.getDepartmentName());
        userDao.save(one);
    }

    @Override
    public void delete(String id) {
        userDao.deleteById(id);
    }

    @Override
    public User findById(String id) {
        return userDao.findById(id).get();
    }

    @Override
    public User findByMobilAndPassword(String mobile, String password) {
        User user = userDao.findByMobile(mobile);
        if (user != null && password.equals(user.getPassword())){
            return user;
        }
        return null;
    }

    @Override
    public Page<User> findSearch(Map<String, Object> map, Integer page, Integer size) {
        if (page == null){
            page = 1;
        }
        if (size == null){
            size = 10;
        }
        return userDao.findAll(createSpecification(map), PageRequest.of(page-1, size));
    }

    @Override
    public void assignRoles(String userId, List<String> roleIds) {
        User user = userDao.findById(userId).get();
        Set<Role> roles = new HashSet<>();
        for (String id : roleIds) {
            Role role = roleDao.findById(id).get();
            roles.add(role);
        }
        user.setRoles(roles);
        userDao.save(user);
    }

    @Override
    public void changeDept(String deptId, String deptName, List<String> ids) {
        for (String id : ids) {
            User user = userDao.findById(id).get();
            user.setDepartmentId(deptId);
            user.setDepartmentName(deptName);
            userDao.save(user);
        }
    }

    @Override
    public List<String> findRoleIdsByUserId(String uid) {
        return null;
    }

    @Override
    @Transactional
    public void saveAll(List<User> userList, String companyId, String companyName) {
        for (User user : userList) {
            // 默认密码为123456， sale值为用户名, 加密3次
            user.setPassword(new Md5Hash("123456", user.getMobile(), 3).toString());
            user.setId(idWorker.nextId()+"");
            user.setCompanyId(companyId);
            user.setCompanyName(companyName);
            user.setEnableState(1);
            user.setInServiceStatus(1);
            user.setLevel("user");
            user.setCreateTime(new Date());

            //设置部门信息
            Department department = companyFeignClient.findByCode(user.getDepartmentId(), companyId);
            if (department != null){
                user.setDepartmentId(department.getId());
                user.setDepartmentName(department.getName());
            }
            userDao.save(user);
        }
    }

    /**
     * 用户头像上传
     * @param uid
     * @param file
     */
    @Override
    public String upload(String uid, MultipartFile file) {
        Optional<User> user = userDao.findById(uid);
        User one = null;
        if (user.isPresent()){
            one = user.get();
        }
        String encodeUrl;
        String dataUrl = null;
        try {
            byte[] encode = Base64.getEncoder().encode(file.getBytes());
            encodeUrl = new String(encode);
            dataUrl = "data:image/jpg;base64," + encodeUrl;

        } catch (IOException e) {
            e.printStackTrace();
        }
        assert one != null;
        one.setStaffPhoto(dataUrl);
        userDao.save(one);
        return dataUrl;
    }

    /**
     * 动态条件构建
     * @param searchMap
     * @return
     */
    private Specification<User> createSpecification(Map searchMap) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query,
                                         CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                // ID
                if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                    predicateList.add(cb.equal(root.get("id").as(String.class),
                            (String) searchMap.get("id")));
                }
                // 手机号码
                if (searchMap.get("mobile") != null &&
                        !"".equals(searchMap.get("mobile"))) {
                    predicateList.add(cb.equal(root.get("mobile").as(String.class),
                            (String) searchMap.get("mobile")));
                }
                // 用户ID
                if (searchMap.get("departmentId") != null &&
                        !"".equals(searchMap.get("departmentId"))) {
                    predicateList.add(cb.like(root.get("departmentId").as(String.class),
                            (String) searchMap.get("departmentId")));
                }
                // 标题
                if (searchMap.get("formOfEmployment") != null &&
                        !"".equals(searchMap.get("formOfEmployment"))) {
                    predicateList.add(cb.like(root.get("formOfEmployment").as(String.class),
                            (String) searchMap.get("formOfEmployment")));
                }
                if (searchMap.get("companyId") != null &&
                        !"".equals(searchMap.get("companyId"))) {
                    predicateList.add(cb.like(root.get("companyId").as(String.class),
                            (String) searchMap.get("companyId")));
                }
                if (searchMap.get("hasDept") != null &&
                        !"".equals(searchMap.get("hasDept"))) {
                    if ("0".equals((String) searchMap.get("hasDept"))) {
                        predicateList.add(cb.isNull(root.get("departmentId")));
                    } else {
                        predicateList.add(cb.isNotNull(root.get("departmentId")));
                    }
                }
                return cb.and(predicateList.toArray(new
                        Predicate[predicateList.size()]));
            }
        };
    }
}
