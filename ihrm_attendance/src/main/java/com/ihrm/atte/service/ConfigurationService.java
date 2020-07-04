package com.ihrm.atte.service;

import com.ihrm.atte.dao.*;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.exception.CommonException;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.domain.atte.entity.*;
import com.ihrm.domain.atte.vo.ExtDutyVO;
import com.ihrm.domain.system.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;


@Service
public class ConfigurationService{

    @Autowired
    private AttendanceConfigDao attendanceConfigDao;

    @Autowired
    private LeaveConfigDao leaveConfigDao;

    @Autowired
    private DeductionDictDao deductionDictDao;

    @Autowired
    private ExtraDutyConfigDao extraDutyConfigDao;

    @Autowired
    private ExtraDutyRuleDao extraDutyRuleDao;

    @Autowired
    private DayOffConfigDao dayOffConfigDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private UserCDao userCDao;

    /**
     * 查询考勤设置
     * @param companyId 企业id
     * @param departmentId  部门id
     * @return  企业id和部门id对应的考勤设置
     */
    public AttendanceConfig getAtteConfig(String companyId, String departmentId) {
        return attendanceConfigDao.findByCompanyIdAndDepartmentId(companyId , departmentId);
    }

    /**
     * 查询请假设置信息
     * @param companyId
     * @param departmentId
     * @return
     */
    public List<LeaveConfig> getLeaveConfig(String companyId, String departmentId){
        List<LeaveConfig> list = leaveConfigDao.findByCompanyIdAndDepartmentId(companyId, departmentId);
        return list;
    }

    /**
     * 查询扣款设置信息
     * @param companyId
     * @param departmentId
     * @return
     */
    public List<DeductionDict> getDed(String companyId, String departmentId){
        List<DeductionDict> list = deductionDictDao.findByCompanyIdAndDepartmentId(companyId, departmentId);
        return list;
    }

    /**
     * 查询加班设置信息(加班设置 + 调休设置)
     * @param companyId
     * @param departmentId
     * @return
     */
    public Map<String, Object> getExtDuty(String companyId, String departmentId){
        ExtraDutyConfig extraDutyConfig = extraDutyConfigDao.findByCompanyIdAndDepartmentId(companyId, departmentId);
        Map<String, Object> map = new HashMap<>();
        map.put("extraDutyConfig", extraDutyConfig);
        DayOffConfig dayOffConfig = dayOffConfigDao.findByCompanyIdAndDepartmentId(companyId, departmentId);
        map.put("dayOffConfigs", dayOffConfig);
        List<ExtraDutyRule> ruleList = extraDutyRuleDao.findByCompanyIdAndDepartmentId(companyId, departmentId);
        map.put("extraDutyRuleList", ruleList);
        return map;
    }

    /**
     * 添加或保存请假设置
     * @param leaveConfigList
     * @param userId
     */
    public void leaveConfigSaveOrUpdate(List<LeaveConfig> leaveConfigList, String userId, String companyId){
        Optional<User> one = userCDao.findById(userId);
        if (!one.isPresent()) {
            throw new CommonException(ResultCode.UNAUTHENCATED);
        }
        for (LeaveConfig leaveConfig : leaveConfigList) {
            leaveConfig.setCompanyId(companyId);
            List<LeaveConfig> list = leaveConfigDao.findByCompanyIdAndDepartmentId(leaveConfig.getCompanyId(), leaveConfig.getDepartmentId());
            // 更新
            if (list != null){
                for (LeaveConfig config : list) {
                    config.setUpdateBy(userId);
                    config.setUpdateDate(new Date());
                    BeanUtils.copyProperties(leaveConfig, config);
                    leaveConfigDao.save(config);
                }
            }
            //添加
            leaveConfig.setId(idWorker.nextId()+"");
            leaveConfig.setCreateBy(userId);
            leaveConfig.setCreateDate(new Date());
            leaveConfigDao.save(leaveConfig);
        }
    }

    /**
     * 保存或更新出勤设置信息
     * @param attendanceConfig
     * @param userId
     * @param companyId
     */
    public void attendanceConfigSaveOrUpdate(AttendanceConfig attendanceConfig, String userId, String companyId){
        AttendanceConfig one = attendanceConfigDao.findByCompanyIdAndDepartmentId(companyId, attendanceConfig.getDepartmentId());
        if (one != null) {//更新
            attendanceConfig.setUpdateBy(userId);
            attendanceConfig.setUpdateDate(new Date());
            attendanceConfigDao.save(attendanceConfig);
        }
        attendanceConfig.setId(idWorker.nextId()+"");
        attendanceConfig.setCompanyId(companyId);
        attendanceConfig.setCreateBy(userId);
        attendanceConfig.setCreateDate(new Date());
        attendanceConfigDao.save(attendanceConfig);
    }

    /**
     * 更新或添加扣款设置信息
     * @param deductionDictList
     * @param userId
     * @param companyId
     */
    public void deductionDictSaveOrUpdate(List<DeductionDict> deductionDictList, String userId, String companyId){
        for (DeductionDict deductionDict : deductionDictList) {
            DeductionDict one = deductionDictDao.findByCompanyIdAndDepartmentIdAndDedTypeCode(companyId, deductionDict.getDepartmentId(), deductionDict.getDedTypeCode());
            if (one != null){// 更新
                one.setUpdateBy(userId);
                one.setUpdateDate(new Date());
                BeanUtils.copyProperties(deductionDict, one);
                deductionDictDao.save(one);
            }
            //添加
            deductionDict.setId(idWorker.nextId()+"");
            deductionDict.setCompanyId(companyId);
            deductionDict.setCreateBy(userId);
            deductionDict.setCreateDate(new Date());
            deductionDictDao.save(deductionDict);
        }
    }

    /**
     * 加班信息添加或更新
     * @param extDutyVO
     * @param userId
     * @param companyId
     */
    @Transactional
    public void extDutySaveOrUpdate(ExtDutyVO extDutyVO, String userId, String companyId) {
        //设置extra_duty_config、extra_duty_rule (两个一起同时设置)
        ExtraDutyConfig extraDutyConfig = extraDutyConfigDao.findByCompanyIdAndDepartmentId(companyId, extDutyVO.getDepartmentId());
        List<ExtraDutyRule> rules = extDutyVO.getRules();
        //更新
        if (extraDutyConfig != null) {
            // 更新extra_duty_config
            ExtraDutyConfig dutyConfig = new ExtraDutyConfig();
            BeanUtils.copyProperties(extraDutyConfig, dutyConfig);
            dutyConfig.setDepartmentId(extDutyVO.getDepartmentId());
            dutyConfig.setIsClock(extDutyVO.getIsClock());
            dutyConfig.setIsCompensationint(extDutyVO.getIsCompensationint());
            dutyConfig.setUpdateBy(userId);
            dutyConfig.setUpdateDate(new Date());
            extraDutyConfigDao.save(dutyConfig);

            //更新extra_duty_rule
            List<ExtraDutyRule> ruleList = extraDutyRuleDao.findByCompanyIdAndDepartmentId(companyId, extDutyVO.getDepartmentId());
            for (ExtraDutyRule rule : rules) {
                for (ExtraDutyRule rule1 : ruleList) {
                    BeanUtils.copyProperties(rule, rule1);
                    rule1.setUpdateBy(userId);
                    rule1.setUpdateDate(new Date());
                    extraDutyRuleDao.save(rule1);
                }
            }
        }
        //添加extra_duty_config
        ExtraDutyConfig dutyConfig = new ExtraDutyConfig();
        dutyConfig.setId(idWorker.nextId()+"");
        dutyConfig.setCompanyId(companyId);
        dutyConfig.setDepartmentId(extDutyVO.getDepartmentId());
        dutyConfig.setCreateBy(userId);
        dutyConfig.setCreateDate(new Date());
        dutyConfig.setIsClock(extDutyVO.getIsClock());
        dutyConfig.setIsCompensationint(extDutyVO.getIsCompensationint());
        extraDutyConfigDao.save(dutyConfig);
        //添加extra_duty_rule
        for (ExtraDutyRule rule : rules) {
            ExtraDutyRule dutyRule = new ExtraDutyRule();
            BeanUtils.copyProperties(rule, dutyRule);
            dutyRule.setId(idWorker.nextId()+"");
            dutyRule.setExtraDutyConfigId(dutyConfig.getId());
            dutyRule.setCompanyId(companyId);
            dutyRule.setDepartmentId(extDutyVO.getDepartmentId());
            dutyRule.setCreateBy(userId);
            dutyRule.setCreateDate(new Date());
            extraDutyRuleDao.save(dutyRule);
        }

        //设置day_off_config
        DayOffConfig dayOffConfig1 = dayOffConfigDao.findByCompanyIdAndDepartmentId(companyId, extDutyVO.getDepartmentId());
        if (dayOffConfig1 != null) { //更新
            DayOffConfig dayOffConfig = new DayOffConfig();
            BeanUtils.copyProperties(dayOffConfig1, dayOffConfig);
            dayOffConfig.setLatestEffectDate(extDutyVO.getLatestEffectDate());
            dayOffConfig.setUnit(extDutyVO.getUnit());
            dayOffConfig.setUpdateBy(userId);
            dayOffConfig.setUpdateDate(new Date());
            dayOffConfigDao.save(dayOffConfig);
        }
        //添加
        DayOffConfig dayOffConfig = new DayOffConfig();
        dayOffConfig.setId(idWorker.nextId()+"");
        dayOffConfig.setLatestEffectDate(extDutyVO.getLatestEffectDate());
        dayOffConfig.setUnit(extDutyVO.getUnit());
        dayOffConfig.setCreateBy(userId);
        dayOffConfig.setCreateDate(new Date());
        dayOffConfig.setCompanyId(companyId);
        dayOffConfig.setDepartmentId(extDutyVO.getDepartmentId());
        dayOffConfigDao.save(dayOffConfig);

    }
}
