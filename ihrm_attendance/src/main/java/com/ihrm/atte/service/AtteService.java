package com.ihrm.atte.service;

import com.ihrm.atte.dao.*;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.utils.DateUtil;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.domain.atte.bo.AtteItemBO;
import com.ihrm.domain.atte.entity.ArchiveMonthlyInfo;
import com.ihrm.domain.atte.entity.Attendance;
import com.ihrm.domain.atte.vo.AttePageVO;
import com.ihrm.domain.social_security.CompanySettings;
import com.ihrm.domain.system.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AtteService  {

	@Autowired
	private IdWorker idWorker;

    @Autowired
    private AttendanceDao attendanceDao;

    @Autowired
    private DeductionDictDao deductionDictDao;

    @Autowired
    private UserCDao userDao;

    @Autowired
    private AttendanceConfigDao attendanceConfigDao;

    @Resource(name = "ihrm_attendance")
    private CompanySettingsDao companySettingsDao;

    /**
     * 获取用户的考勤数据
     * @param companyId 公司id
     * @param page  页码
     * @param pageSize  每页大小
     * @return  获取对应公司的对应月份的考勤数据
     */
    public Map getAtteDate(String companyId, int page, int pageSize) throws ParseException {
        //考勤月
        CompanySettings css = companySettingsDao.findById(companyId).get();
        String dataMonth = css.getDataMonth();
        //分页查询用户
        Page<User> users = userDao.findPage(companyId, new PageRequest(page - 1, pageSize));
        List<AtteItemBO> list = new ArrayList<>();
        //获取当前月所有的天数
        String[] days = DateUtil.getDaysByYearMonth(dataMonth);
        //循环所有的用户,获取每个用户每天的考勤情况
        for (User user : users.getContent()) {
            AtteItemBO bo = new AtteItemBO();
            BeanUtils.copyProperties(user, bo);
            List<Attendance> attendanceRecord = new ArrayList<>();
            //循环每天查询考勤记录
            for (String day : days) {
                Attendance atte = attendanceDao.findByUserIdAndDay(user.getId(), day);
                if (atte == null){
                    atte = new Attendance();
                    atte.setAdtStatu(2);    //旷工
                    atte.setId(user.getId());
                    atte.setDay(day);
                }
                attendanceRecord.add(atte);
            }
            //封装到attendanceRecord
            bo.setAttendanceRecord(attendanceRecord);
            list.add(bo);
        }
        Map map = new HashMap();

        //分页对象数据
        PageResult<AtteItemBO> pr = new PageResult<>(users.getTotalElements(), list);
        //待处理的考勤数据
        map.put("data" , pr);
        //待处理的考勤数量
        map.put("tobeTaskCount" , 0);
        //当前的考勤月份
        int month = Integer.parseInt(dataMonth.substring(4));
        map.put("monthOfReport" , month);
        return map;
    }

    /**
     * 动态查询用户考勤数据
     * @param attePageVO
     * @return
     * @throws ParseException
     */
    public Map findList(AttePageVO attePageVO) throws ParseException {
        //考勤月
        CompanySettings css = companySettingsDao.findById(attePageVO.getCompanyId()).get();
        String dataMonth = css.getDataMonth();
        //分页查询用户
        int page = attePageVO.getPage();
        int pageSize = attePageVO.getPagesize();
        if (page < 1){
            page = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
        Page<User> users = userDao.findPage(attePageVO.getCompanyId(), new PageRequest(page - 1, pageSize));
        List<AtteItemBO> list = new ArrayList<>();
        //获取当前月所有的天数
        String[] days = DateUtil.getDaysByYearMonth(dataMonth);
        //循环所有的用户,获取每个用户每天的考勤情况
        for (User user : users.getContent()) {
            AtteItemBO bo = new AtteItemBO();
            BeanUtils.copyProperties(user, bo);
            List<Attendance> attendanceRecord = new ArrayList<>();
            //循环每天查询考勤记录
            for (String day : days) {
//                Attendance atte = attendanceDao.findByUserIdAndDay(user.getId(), day);
                attePageVO.setUserId(user.getId());
                attePageVO.setDay(day);
                Page<Attendance> attendances = attendanceDao.findAll(createSpecification(attePageVO), PageRequest.of(page-1, pageSize));
                for (Attendance attendance : attendances) {
                    if (attendance == null){
                        attendance = new Attendance();
                        attendance.setAdtStatu(2);    //旷工
                        attendance.setId(user.getId());
                        attendance.setDay(day);
                        attendanceRecord.add(attendance);
                    }
                    attendanceRecord.add(attendance);
                }
            }
            //封装到attendanceRecord
            bo.setAttendanceRecord(attendanceRecord);
            list.add(bo);
        }
        Map map = new HashMap();

        //分页对象数据
        PageResult<AtteItemBO> pr = new PageResult<>(users.getTotalElements(), list);
        //待处理的考勤数据
        map.put("data" , pr);
        //待处理的考勤数量
        map.put("tobeTaskCount" , 0);
        //当前的考勤月份
        int month = Integer.parseInt(dataMonth.substring(4));
        map.put("monthOfReport" , month);
        return map;
    }

    /**
     * 编辑考勤
     * @param attendance 考勤数据
     */
    public void ehitAtte(Attendance attendance) {
        //查询考勤记录是否存在
        Attendance ac = attendanceDao.findByUserIdAndDay(attendance.getUserId(), attendance.getDay());
        if (ac == null){
            //如果不存在,需要设置id
            attendance.setId(idWorker.nextId() + "");
        }else{
            attendance.setId(ac.getId());
        }
        attendanceDao.save(attendance);
    }

    /**
     * 查询归档数据
     * @param atteDate  日期
     * @param companyId 企业id
     * @return  对应日期和对应企业id的归档数据
     */
    public List<ArchiveMonthlyInfo> getReports(String atteDate, String companyId) {
        //查询所有企业用户
        List<User> users = userDao.findByCompanyId(companyId);
        //循环遍历用户列表,统计每个用户的当月考勤记录
        List<ArchiveMonthlyInfo> list = new ArrayList<>();
        for (User user : users) {
            ArchiveMonthlyInfo info = new ArchiveMonthlyInfo(user);
            //统计每个用户的考勤数量
            Map map = attendanceDao.statisByUser(user.getId() , atteDate+"%");
            info.setStatisData(map);
            list.add(info);
        }
        return list;
    }

    /**
     * 新建报表,将atte_company_settings中的月份修改为指定的数据
     * @param atteDate 年月
     * @param companyId 企业id
     */
    public void newReports(String atteDate, String companyId) {
        CompanySettings companySettings = companySettingsDao.findById(companyId).get();
        companySettings.setDataMonth(atteDate);
        companySettingsDao.save(companySettings);
    }

    /**
     * 动态条件构建
     * @param
     * @return
     */
    private Specification<Attendance> createSpecification(AttePageVO attePageVO) {
        return new Specification<Attendance>() {
            @Override
            public Predicate toPredicate(Root<Attendance> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                //用户id
                if (attePageVO.getUserId()!=null && !"".equals(attePageVO.getUserId())){
                    predicateList.add(cb.equal(root.get("userId").as(String.class), attePageVO.getUserId()));
                }
                //当天day
                if (attePageVO.getDay() != null && !"".equals(attePageVO.getDay())) {
                    predicateList.add(cb.equal(root.get("day").as(String.class), attePageVO.getDay()));
                }
                // 考勤状态
                if (attePageVO.getAdtStatu() != null){
                    predicateList.add(cb.equal(root.get("adtStatu").as(Integer.class), attePageVO.getAdtStatu()));
                }
                //部门状态ids in查询
                if (attePageVO.getDeptID() != null && attePageVO.getDeptID().size() > 0){
                    Expression<String> expression = root.get("departmentId");
                    predicateList.add(expression.in(attePageVO.getDeptID()));
                }
                return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
            }
        };
    }
}
