package com.ihrm.jasperreport.controller;

import net.sf.jasperreports.engine.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/jasper")
public class JasperController {

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void export(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/parameterTest.jasper");
        FileInputStream isRef = new FileInputStream(resource.getFile());
        ServletOutputStream sosRef = response.getOutputStream();
        try{
            Map<String, Object> map = new HashMap<>();
            map.put("username", "哈哈");
//            map.put("mobile", "13200000001");
//            map.put("company", "科技公司");
//            map.put("department", "研发部");
            map.put("title", "人事档案表");
            JasperPrint jasperPrint = JasperFillManager.fillReport(isRef, map, new JREmptyDataSource());
            JasperExportManager.exportReportToPdfStream(jasperPrint, sosRef);
            response.setContentType("application/pdf");
        } catch (JRException e) {
            e.printStackTrace();
        }finally {
            sosRef.flush();
            sosRef.close();
        }
    }
}
