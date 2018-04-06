package com.noobug.nooblog.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "system_config")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 配置项key
     */
    private String key;

    /**
     * 配置项值
     */
    private String data;

    @Column(name = "is_deleted")
    private Boolean deleted;
}
