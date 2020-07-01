package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.domain.system.Role;
import com.ihrm.domain.system.response.RoleResult;
import com.ihrm.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/sys")
public class RoleController extends BaseController {
    @Autowired
    private RoleService roleService;

    //添加角色
    @RequestMapping(value = "/role", method = RequestMethod.POST)
    public Result add(@RequestBody Role role) throws Exception {
        role.setCompanyId(parseCompanyId());
        roleService.save(role);
        return Result.SUCCESS();
    }

    //更新角色
    @RequestMapping(value = "/role/{id}", method = RequestMethod.PUT)
    public Result update(@PathVariable(name = "id") String id, @RequestBody Role role)
            throws Exception {
        role.setId(id);
        roleService.update(role);
        return Result.SUCCESS();
    }
    //删除角色
    @RequestMapping(value = "/role/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable(name = "id") String id) throws Exception {
        roleService.delete(id);
        return Result.SUCCESS();
    }
    /**
     * 根据ID获取角色信息
     */
    @RequestMapping(value = "/role/{id}", method = RequestMethod.GET)
    public Result<RoleResult> findById(@PathVariable(name = "id") String id) throws Exception {
        Role role = roleService.findById(id);
        RoleResult roleResult = new RoleResult(role);
        return new Result<>(ResultCode.SUCCESS, roleResult);
    }
    /**
     * 分页查询角色
     */
    @RequestMapping(value = "/role", method = RequestMethod.GET)
    public Result findByPage(int page,int pagesize, Role role) throws Exception {
        Page<Role> searchPage = roleService.findSearch(page, pagesize, parseCompanyId());
        PageResult<Role> pr = new PageResult<>(searchPage.getTotalElements(), searchPage.getContent());
        return new Result<>(ResultCode.SUCCESS, pr);
    }

    /**
     * 查询某个公司下的所有角色
     * @return
     */
    @RequestMapping(value = "/role/list", method = RequestMethod.GET)
    public Result findAll(){
        List<Role> all = roleService.findAll(parseCompanyId());
        return new Result<>(ResultCode.SUCCESS, all);
    }

    /**
     * 为角色分配权限
     * @param map
     * @return
     */
    @RequestMapping(value = "/role/assignPrem", method = RequestMethod.PUT)
    public Result assignPerm(@RequestBody Map<String,Object> map) {
        //1.获取被分配的角色的id
        String roleId = (String) map.get("id");
        //2.获取到权限的id列表
        List<String> permIds = (List<String>) map.get("permIds");
        //3.调用service完成权限分配
        roleService.assignPerms(roleId, permIds);
        return new Result(ResultCode.SUCCESS);
    }
}
