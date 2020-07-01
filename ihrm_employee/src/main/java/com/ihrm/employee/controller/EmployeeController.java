package com.ihrm.employee.controller;

import com.alibaba.fastjson.JSON;
import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.exception.CommonException;
import com.ihrm.common.poi.ExcelExportUtil;
import com.ihrm.common.utils.BeanMapUtils;
import com.ihrm.common.utils.DownloadUtils;
import com.ihrm.domain.employee.*;
import com.ihrm.domain.employee.response.EmployeeReportResult;
import com.ihrm.employee.service.*;
import io.jsonwebtoken.Claims;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JaxenXmlDataSource;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@CrossOrigin
@RequestMapping("/employees")
public class EmployeeController extends BaseController {
    @Autowired
    private UserCompanyPersonalService userCompanyPersonalService;
    @Autowired
    private UserCompanyJobsService userCompanyJobsService;
    @Autowired
    private ResignationService resignationService;
    @Autowired
    private TransferPositionService transferPositionService;
    @Autowired
    private PositiveService positiveService;
    @Autowired
    private ArchiveService archiveService;

    @RequestMapping(value = "/{id}/pdf", method = RequestMethod.GET)
    public void pdf(@PathVariable("id") String id) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/profile.jasper");
        FileInputStream fs = new FileInputStream(resource.getFile());

        UserCompanyPersonal companyPersonal = userCompanyPersonalService.findById(id);
        UserCompanyJobs companyJobs = userCompanyJobsService.findById(id);
        String staffPhoto = "http://49.232.212.161:80/upload/2020/06/IMG_20200612_091130-4a9fde8fc2f14395a00e792708eb3394.jpg";

        Map<String, Object> map = new HashMap<>();

        Map<String, Object> map1 = BeanMapUtils.beanToMap(companyPersonal);
        Map<String, Object> map2 = BeanMapUtils.beanToMap(companyJobs);
        map.putAll(map1);
        map.putAll(map2);
        map.put("staffPhoto", staffPhoto);
        ServletOutputStream outputStream = response.getOutputStream();
        try{
            JasperPrint jasperPrint = JasperFillManager.fillReport(fs, map, new JREmptyDataSource());
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
        } catch (JRException e) {
            e.printStackTrace();
        }finally {
            outputStream.flush();
        }
    }
    /**
     * 员工个人信息保存
     */
    @RequestMapping(value = "/{id}/personalInfo", method = RequestMethod.PUT)
    public Result savePersonalInfo(@PathVariable(name = "id") String uid, @RequestBody Map map) throws Exception {
        UserCompanyPersonal sourceInfo = BeanMapUtils.mapToBean(map, UserCompanyPersonal.class);
        if (sourceInfo == null) {
            sourceInfo = new UserCompanyPersonal();
        }
        sourceInfo.setUserId(uid);
        sourceInfo.setCompanyId(super.companyId);
        userCompanyPersonalService.save(sourceInfo);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 员工个人信息读取
     */
    @RequestMapping(value = "/{id}/personalInfo", method = RequestMethod.GET)
    public Result findPersonalInfo(@PathVariable(name = "id") String uid) throws Exception {
        UserCompanyPersonal info = userCompanyPersonalService.findById(uid);
        if(info == null) {
            info = new UserCompanyPersonal();
            info.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS,info);
    }

    /**
     * 员工岗位信息保存
     */
    @RequestMapping(value = "/{id}/jobs", method = RequestMethod.PUT)
    public Result saveJobsInfo(@PathVariable(name = "id") String uid, @RequestBody UserCompanyJobs sourceInfo) throws Exception {
        //更新员工岗位信息
        if (sourceInfo == null) {
            sourceInfo = new UserCompanyJobs();
            sourceInfo.setUserId(uid);
            sourceInfo.setCompanyId(super.companyId);
        }
        userCompanyJobsService.save(sourceInfo);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 员工岗位信息读取
     */
    @RequestMapping(value = "/{id}/jobs", method = RequestMethod.GET)
    public Result findJobsInfo(@PathVariable(name = "id") String uid) throws Exception {
        UserCompanyJobs info = userCompanyJobsService.findById(uid);
        if(info == null) {
            info = new UserCompanyJobs();
            info.setUserId(uid);
            info.setCompanyId(companyId);
        }
        return new Result(ResultCode.SUCCESS,info);
    }

    /**
     * 离职表单保存
     */
    @RequestMapping(value = "/{id}/leave", method = RequestMethod.PUT)
    public Result saveLeave(@PathVariable(name = "id") String uid, @RequestBody EmployeeResignation resignation) throws Exception {
        resignation.setUserId(uid);
        resignationService.save(resignation);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 离职表单读取
     */
    @RequestMapping(value = "/{id}/leave", method = RequestMethod.GET)
    public Result findLeave(@PathVariable(name = "id") String uid) throws Exception {
        EmployeeResignation resignation = resignationService.findById(uid);
        if(resignation == null) {
            resignation = new EmployeeResignation();
            resignation.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS,resignation);
    }

    /**
     * 调岗表单保存
     */
    @RequestMapping(value = "/{id}/transferPosition", method = RequestMethod.PUT)
    public Result saveTransferPosition(@PathVariable(name = "id") String uid, @RequestBody EmployeeTransferPosition transferPosition) throws Exception {
        transferPosition.setUserId(uid);
        transferPositionService.save(transferPosition);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 调岗表单读取
     */
    @RequestMapping(value = "/{id}/transferPosition", method = RequestMethod.GET)
    public Result findTransferPosition(@PathVariable(name = "id") String uid) throws Exception {
        UserCompanyJobs jobsInfo = userCompanyJobsService.findById(uid);
        if(jobsInfo == null) {
            jobsInfo = new UserCompanyJobs();
            jobsInfo.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS,jobsInfo);
    }

    /**
     * 转正表单保存
     */
    @RequestMapping(value = "/{id}/positive", method = RequestMethod.PUT)
    public Result savePositive(@PathVariable(name = "id") String uid, @RequestBody EmployeePositive positive) throws Exception {
        positiveService.save(positive);
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 转正表单读取
     */
    @RequestMapping(value = "/{id}/positive", method = RequestMethod.GET)
    public Result findPositive(@PathVariable(name = "id") String uid) throws Exception {
        EmployeePositive positive = positiveService.findById(uid);
        if(positive == null) {
            positive = new EmployeePositive();
            positive.setUserId(uid);
        }
        return new Result(ResultCode.SUCCESS,positive);
    }

    /**
     * 历史归档详情列表
     */
    @RequestMapping(value = "/archives/{month}", method = RequestMethod.GET)
    public Result archives(@PathVariable(name = "month") String month, @RequestParam(name = "type") Integer type) throws Exception {
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 归档更新
     */
    @RequestMapping(value = "/archives/{month}", method = RequestMethod.PUT)
    public Result saveArchives(@PathVariable(name = "month") String month) throws Exception {
        return new Result(ResultCode.SUCCESS);
    }

    /**
     * 历史归档列表
     */
    @RequestMapping(value = "/archives", method = RequestMethod.GET)
    public Result findArchives(@RequestParam(name = "pagesize") Integer pagesize, @RequestParam(name = "page") Integer page, @RequestParam(name = "year") String year) throws Exception {
        Map map = new HashMap();
        map.put("year",year);
        map.put("companyId",companyId);
        Page<EmployeeArchive> searchPage = archiveService.findSearch(map, page, pagesize);
        PageResult<EmployeeArchive> pr = new PageResult(searchPage.getTotalElements(),searchPage.getContent());
        return new Result(ResultCode.SUCCESS,pr);
    }

    /**
     * 人事表导出
     * @param month
     * @throws IOException
     */
    @RequestMapping(value = "/export/{month}", method = RequestMethod.GET)
    public void export(@PathVariable("month") String month) throws IOException {
        List<EmployeeReportResult> list = userCompanyPersonalService.findByReport(companyId, month);
        System.out.println("CompanyId: " + companyId);
        // 获取模板数据
        ClassPathResource resource = new ClassPathResource("excel-templates/hr-template.xlsx");
        FileInputStream fs = new FileInputStream(resource.getFile());
        XSSFWorkbook workbook = new XSSFWorkbook(fs);
        //读取工作表
        XSSFSheet sheet = workbook.getSheetAt(0);
        //获取公共样式
        XSSFRow styleRow = sheet.getRow(2);
        CellStyle[] cellStyles = new CellStyle[styleRow.getLastCellNum()];
        for (int i = 0; i < styleRow.getLastCellNum(); i++) {
            cellStyles[i] = styleRow.getCell(i).getCellStyle();
        }

        AtomicInteger dataAi = new AtomicInteger(2);
        Cell cell;
        for (int i = 0; i < 10000; i++) {
            for (EmployeeReportResult report : list) {
                Row dataRow = sheet.createRow(dataAi.getAndIncrement());
                //编号
                cell = dataRow.createCell(0);
                cell.setCellValue(report.getUserId());
                cell.setCellStyle(cellStyles[0]);
                //姓名
                cell = dataRow.createCell(1);
                cell.setCellValue(report.getUsername());
                cell.setCellStyle(cellStyles[1]);
                //手机
                cell = dataRow.createCell(2);
                cell.setCellValue(report.getMobile());
                cell.setCellStyle(cellStyles[2]);
                //最高学历
                cell = dataRow.createCell(3);
                cell.setCellValue(report.getTheHighestDegreeOfEducation());
                cell.setCellStyle(cellStyles[3]);
                //国家地区
                cell = dataRow.createCell(4);
                cell.setCellValue(report.getNationalArea());
                cell.setCellStyle(cellStyles[4]);
                //护照号
                cell = dataRow.createCell(5);
                cell.setCellValue(report.getPassportNo());
                cell.setCellStyle(cellStyles[5]);
                //籍贯
                cell = dataRow.createCell(6);
                cell.setCellValue(report.getNativePlace());
                cell.setCellStyle(cellStyles[6]);
                //生日
                cell = dataRow.createCell(7);
                cell.setCellValue(report.getBirthday());
                cell.setCellStyle(cellStyles[7]);
                //属相
                cell = dataRow.createCell(8);
                cell.setCellValue(report.getZodiac());
                cell.setCellStyle(cellStyles[8]);
                //入职时间
                cell = dataRow.createCell(9);
                cell.setCellValue(report.getTimeOfEntry());
                cell.setCellStyle(cellStyles[9]);
                //离职类型
                cell = dataRow.createCell(10);
                cell.setCellValue(report.getTypeOfTurnover());
                cell.setCellStyle(cellStyles[10]);
                //离职原因
                cell = dataRow.createCell(11);
                cell.setCellValue(report.getReasonsForLeaving());
                cell.setCellStyle(cellStyles[11]);
                //离职时间
                cell = dataRow.createCell(12);
                cell.setCellValue(report.getResignationTime());
                cell.setCellStyle(cellStyles[12]);
            }
        }
        String fileName = URLEncoder.encode(month + "人员信息.xlsx", "UTF-8");
        response.setContentType("application/octet-stream");
        response.setHeader("content-disposition", "attachment;filename=" + new
                String(fileName.getBytes("ISO8859-1")));
        response.setHeader("filename", fileName);
        workbook.write(response.getOutputStream());
    }

    /**
     * 工具类导出人事报表
     * @param month
     * @throws Exception
     */
    @RequestMapping(value = "/export01/{month}")
    public void export01(@PathVariable("month") String month) throws Exception {
        List<EmployeeReportResult> list = userCompanyPersonalService.findByReport(companyId, month);
        ClassPathResource resource = new ClassPathResource("excel-templates/hr-template.xlsx");
        FileInputStream fileInputStream = new FileInputStream(resource.getFile());

        new ExcelExportUtil(EmployeeReportResult.class, 2, 2)
                .export(response, fileInputStream, list, month + "人事报表.xlsx");
    }

    /**
     * 测试百万数据报表导出
     * @param month
     * @throws IOException
     */
    @RequestMapping(value = "/export02/{month}")
    public void export02(@PathVariable("month") String month) throws IOException {
        List<EmployeeReportResult> list = userCompanyPersonalService.findByReport(companyId, month);
        //2.创建工作簿 (百万报表导出)
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        //3.构造sheet
        String[] titles = {"编号", "姓名", "手机","最高学历", "国家地区", "护照号", "籍贯",
                "生日", "属相","入职时间","离职类型","离职原因","离职时间"};
        Sheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);
        AtomicInteger headersAi = new AtomicInteger();
        for (String title : titles) {
            Cell cell = row.createCell(headersAi.getAndIncrement());
            cell.setCellValue(title);
        }
        AtomicInteger datasAi = new AtomicInteger(1);
        Cell cell;
        for(int i=0;i<10000;i++) {
            for (EmployeeReportResult report : list) {
                Row dataRow = sheet.createRow(datasAi.getAndIncrement());
                //编号
                cell = dataRow.createCell(0);
                cell.setCellValue(report.getUserId());
                //姓名
                cell = dataRow.createCell(1);
                cell.setCellValue(report.getUsername());
                //手机
                cell = dataRow.createCell(2);
                cell.setCellValue(report.getMobile());
                //最高学历
                cell = dataRow.createCell(3);
                cell.setCellValue(report.getTheHighestDegreeOfEducation());
                //国家地区
                cell = dataRow.createCell(4);
                cell.setCellValue(report.getNationalArea());
                //护照号
                cell = dataRow.createCell(5);
                cell.setCellValue(report.getPassportNo());
                //籍贯
                cell = dataRow.createCell(6);
                cell.setCellValue(report.getNativePlace());
                //生日
                cell = dataRow.createCell(7);
                cell.setCellValue(report.getBirthday());
                //属相
                cell = dataRow.createCell(8);
                cell.setCellValue(report.getZodiac());
                //入职时间
                cell = dataRow.createCell(9);
                cell.setCellValue(report.getTimeOfEntry());
                //离职类型
                cell = dataRow.createCell(10);
                cell.setCellValue(report.getTypeOfTurnover());
                //离职原因
                cell = dataRow.createCell(11);
                cell.setCellValue(report.getReasonsForLeaving());
                //离职时间
                cell = dataRow.createCell(12);
                cell.setCellValue(report.getResignationTime());
            }
        }
        String fileName = URLEncoder.encode(month+"人员信息.xlsx", "UTF-8");
        response.setContentType("application/octet-stream");
        response.setHeader("content-disposition", "attachment;filename=" + new
                String(fileName.getBytes("ISO8859-1")));
        response.setHeader("filename", fileName);
        workbook.write(response.getOutputStream());
    }
}
