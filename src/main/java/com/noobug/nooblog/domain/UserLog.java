package com.noobug.nooblog.domain;

import com.noobug.nooblog.tools.entity.BasePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "user_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLog extends BasePojo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String logIp;

}
