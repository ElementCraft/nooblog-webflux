package com.noobug.nooblog.domain;

import com.noobug.nooblog.tools.entity.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "role")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BasePojo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 角色code
     */
    private String code;

    /**
     * 角色名称
     */
    private String name;

    @Column(name = "is_deleted")
    private Boolean deleted;

}
