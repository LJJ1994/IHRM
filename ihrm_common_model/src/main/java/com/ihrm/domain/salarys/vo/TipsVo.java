package com.ihrm.domain.salarys.vo;

import lombok.Data;

@Data
public class TipsVo {
    private String dateRange; //月份
    private int worksCount; //入职
    private int leavesCount; //离职
    private int adjustCount; // 调薪
    private int unGradingCount; //需定薪
}
