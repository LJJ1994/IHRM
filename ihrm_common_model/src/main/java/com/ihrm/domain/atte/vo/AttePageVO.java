package com.ihrm.domain.atte.vo;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;


/**
 * 考勤列表VO
 */
@Component
@Data
public class AttePageVO implements Serializable {
    /**
     * 页尺寸
     */
    @NotNull(message = "页面条数不能为空")
    private int pagesize;
    /**
     * 页码
     */
    @NotNull(message = "参数页码不能为空")
    private int page;

    /**
     * 考勤状态
     */
    private Integer adtStatu;
    /**
     * 部门ids
     */
    private List<String> deptID;
    /**
     * /关键字
     */
    private String keyword;

    /**
     * 公司ID
     */
    private String companyId;

    /**
     * 每个月的每一天 20190901
     */
    private String day;
    private String userId;

}
