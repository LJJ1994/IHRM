package com.ihrm.domain.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "bs_user")
@Getter
@Setter
@NoArgsConstructor
public class User {
    private static final long serialVersionUID = 4297464181093070302L;

    /**
     * ID
     */
    @Id
    private String id;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 用户名称
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 启用状态 0为禁用 1为启用
     */
    private Integer enableState;
    /**
     * 创建时间
     */
    private Date createTime;
    private String companyId;
    private String companyName;
    /**
     * 部门ID
     */
    private String departmentId;
    /**
     * 入职时间
     */
    private Date timeOfEntry;
    /**
     * 聘用形式
     */
    private Integer formOfEmployment;
    /**
     * 工号
     */
    private String workNumber;
    /**
     * 管理形式
     */
    private String formOfManagement;
    /**
     * 工作城市
     */
    private String workingCity;
    /**
     * 转正时间
     */
    private Date correctionTime;
    /**
     * 在职状态 1.在职 2.离职
     */
    private Integer inServiceStatus;
    private String departmentName;
    private String level;

    private String staffPhoto;

    public User(Object [] values) {
        //用户名	手机号	工号	聘用 形式	入职 时间	部门编码
        this.username = values[1].toString();
        this.mobile = values[2].toString();
//        this.workNumber = new DecimalFormat("#").format(values[3]);
        this.workNumber = values[3].toString();
        this.formOfEmployment = Integer.valueOf(((String) values[4]));
        try {
            this.timeOfEntry = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse((String) values[5]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.departmentId = values[6].toString(); //部门编码 != 部门id
    }

    /**
     *  JsonIgnore
     *     : 忽略json转化
     */
    @JsonIgnore
    @ManyToMany
    @JoinTable(name="pe_user_role",joinColumns={@JoinColumn(name="user_id",referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="role_id",referencedColumnName="id")}
    )
    private Set<Role> roles = new HashSet<Role>();//用户与角色   多对多
}
