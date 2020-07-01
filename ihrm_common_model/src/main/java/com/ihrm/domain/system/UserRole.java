package com.ihrm.domain.system;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "pe_user_role")
@Getter
@Setter
public class UserRole {
    private static final long serialVersionUID = 4297464181093070302L;

    @Id
    private String id;

    @Column(name = "role_id")
    private String roleId;

    @Column(name = "user_id")
    private String userId;
}
