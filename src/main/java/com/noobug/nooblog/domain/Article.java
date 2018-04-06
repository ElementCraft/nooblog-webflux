package com.noobug.nooblog.domain;

import com.noobug.nooblog.tools.entity.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "article")
public class Article extends BasePojo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "column_id")
    private UserColumn userColumn;

    private String title;

    private Integer typeFlag;

    private String reprintUrl;

    private String translateUrl;

    private String label;

    private Boolean isPrivate;

    private Boolean isRecommend;

    private Boolean isTop;

    private Integer status;

    private Long clickNumber;

    private Integer goodNumber;

    private Integer badNumber;

    private Integer favoriteNumber;

    private Integer commentNumber;

    private Boolean isMarkdown;

    private String content;
}
