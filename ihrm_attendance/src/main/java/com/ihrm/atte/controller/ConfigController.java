package com.ihrm.atte.controller;

import com.ihrm.atte.service.ConfigurationService;
import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.domain.atte.entity.AttendanceConfig;
import com.ihrm.domain.atte.entity.DeductionDict;
import com.ihrm.domain.atte.entity.LeaveConfig;
import com.ihrm.domain.atte.vo.ExtDutyVO;
import com.ihrm.domain.system.response.ProfileResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 设置考勤设置的controller
 * @author: hyl
 * @date: 2020/03/16
 **/
@RestController
@RequestMapping("/cfg")
public class ConfigController extends BaseController {

    @Autowired
    private ConfigurationService configurationService;

    /**
     * 获取考勤设置
     */
    @RequestMapping(value = "/atte/item" , method = RequestMethod.POST)
    public Result atteConfig(String departmentId){
        AttendanceConfig ac = configurationService.getAtteConfig(companyId , departmentId);
        return new Result<>(ResultCode.SUCCESS , ac);
    }

    /**
     * 请假设置信息查询
     * @param departmentId
     * @return
     */
    @RequestMapping(value = "/leave/list", method = RequestMethod.POST)
    public Result getLeave(@RequestParam("departmentId") String departmentId){
        List<LeaveConfig> list = configurationService.getLeaveConfig(companyId, departmentId);
        return new Result<>(ResultCode.SUCCESS, list);
    }

    /**
     * 查询扣款设置信息
     * @param departmentId
     * @return
     */
    @RequestMapping(value = "/ded/list", method = RequestMethod.POST)
    public Result getDed(@RequestParam("departmentId") String departmentId){
        List<DeductionDict> list = configurationService.getDed(companyId, departmentId);
        return new Result<>(ResultCode.SUCCESS, list);
    }

    /**
     * 查询加班设置(加班 + 调休信息)
     * @param departmentId
     * @return
     */
    @RequestMapping(value = "/extDuty/item", method = RequestMethod.POST)
    public Result getExtDuty(@RequestParam("departmentId") String departmentId){
        Map<String, Object> map = configurationService.getExtDuty(companyId, departmentId);
        return new Result<>(ResultCode.SUCCESS, map);
    }

    /**
     * 请假设置信息更新或添加
     * @param
     * @return
     */
    @RequestMapping(value = "/leave", method = RequestMethod.PUT)
    public Result saveLeave(@RequestBody List<LeaveConfig> leaveConfigList){
        configurationService.leaveConfigSaveOrUpdate(leaveConfigList, getUserId(), companyId);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 出勤设置信息更新或添加
     * @param attendanceConfig
     * @return
     */
    @RequestMapping(value = "/atte", method = RequestMethod.PUT)
    public Result saveAtte(@RequestBody AttendanceConfig attendanceConfig){
        configurationService.attendanceConfigSaveOrUpdate(attendanceConfig, getUserId(), companyId);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 更新或添加扣款设置信息
     * @param deductionDictList
     * @return
     */
    @RequestMapping(value = "/deduction", method = RequestMethod.PUT)
    public Result saveDeduction(@RequestBody List<DeductionDict> deductionDictList){
        configurationService.deductionDictSaveOrUpdate(deductionDictList, getUserId(), companyId);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 更新或添加加班设置信息
     * @param extDutyVO
     * @return
     */
    @RequestMapping(value = "/extDuty", method = RequestMethod.PUT)
    public Result saveExtDuty(@RequestBody ExtDutyVO extDutyVO){
        configurationService.extDutySaveOrUpdate(extDutyVO, getUserId(), companyId);
        return new Result(ResultCode.SUCCESS);
    }

    private String getUserId(){
        Subject subject = SecurityUtils.getSubject();
        PrincipalCollection principals = subject.getPrincipals();
        ProfileResult profileResult = (ProfileResult) principals.getPrimaryPrincipal();
        return profileResult.getUserId();
    }
}
