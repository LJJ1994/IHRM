package com.ihrm.system.service;

import com.ihrm.domain.system.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService {
    void save(User user);
    void update(User user);
    void delete(String id);
    User findById(String id);

    /**
     * 通过手机号和密码查询用户
     * @param mobile
     * @param password
     * @return
     */
    User findByMobilAndPassword(String mobile, String password);

    /**
     * 分页查询用户
     * @param map
     * @param page
     * @param size
     * @return
     */
    Page<User> findSearch(Map<String,Object> map, Integer page, Integer size);

    /**
     * 分配角色
     * @param userId
     * @param roleIds
     */
    void assignRoles(String userId, List<String> roleIds);

    /**
     * 调整部门
     * @param deptId
     * @param deptName
     * @param ids
     */
    void changeDept(String deptId, String deptName, List<String> ids);

    /**
     * 通过用户id查询所有的角色id
     * @param uid
     * @return
     */
    List<String> findRoleIdsByUserId(String uid);

    /**
     * 批量保存用户
     * @param companyId
     * @param companyName
     */
    void saveAll(List<User> userList, String companyId, String companyName);

    /**
     * 用户头像上传
     * @param id
     * @param file
     */
    String upload(String id, MultipartFile file);
}
