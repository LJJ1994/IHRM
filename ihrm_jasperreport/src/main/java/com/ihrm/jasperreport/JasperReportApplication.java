package com.ihrm.jasperreport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "com.ihrm")
public class JasperReportApplication {
    public static void main(String[] args) {
        SpringApplication.run(JasperReportApplication.class, args);
    }
}
