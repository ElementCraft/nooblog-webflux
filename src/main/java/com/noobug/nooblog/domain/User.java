package com.noobug.nooblog.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.noobug.nooblog.tools.entity.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name="user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends BasePojo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String account;

    private String nickName;

    @JsonIgnore
    private String password;

    private String realName;

    private Integer sex;

    private String phone;

    private String email;

    //private City city;

    private String signature;

    private Integer score;

    private String iconPath;

    @Column(name="is_auth")
    private Boolean authenticated;

    @Column(name="is_ban")
    private Boolean banned;

    @Column(name="is_public")
    private Boolean isPublic;

    @Column(name="is_deleted")
    private Boolean deleted;
}
