package com.ihrm.domain.system;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "bs_city")
@Data
public class City implements Serializable {
    @Id
    private String id;

    private String name;

    private Date createTime;
}
