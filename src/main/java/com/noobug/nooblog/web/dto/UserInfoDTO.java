package com.noobug.nooblog.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 用户个人资料DTO
 *
 * @author noobug.com
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoDTO {

    private Long id;

    private String account;

    private String nickName;

    private Integer sex;

    private String email;

    private String signature;

    private Integer score;

    private String iconPath;

    private Boolean authenticated;

    private Boolean banned;

    private Boolean isPublic;

}
