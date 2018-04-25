package com.noobug.nooblog.web.router;

import com.noobug.nooblog.consts.PublicConst;
import com.noobug.nooblog.service.AdminService;
import com.noobug.nooblog.tools.utils.ConfigUtil;
import com.noobug.nooblog.tools.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * 管理员后台相关接口路由
 *
 * @author noobug.com
 */
@Slf4j
@Component
public class AdminRouter {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private SecurityUtil securityUtil;

    @Bean
    RouterFunction<?> adminRoutes() {
        return nest(path("/api/admin/"),
                route(GET("/user/logs"), this::getUserLogsPage)
        );
    }

    private Mono<ServerResponse> getUserLogsPage(ServerRequest request) {
        Integer page = Integer.valueOf(request.queryParam("page").orElse("0"));
        Integer size = Integer.valueOf(request.queryParam("size").orElse(PublicConst.PAGE_SIZE.toString()));
        return adminService.getUserLogsPage(PageRequest.of(page, size))
                .flatMap(articles -> ok().body(fromObject(articles)))
                .switchIfEmpty(ok().body(fromObject(new ArrayList<>())));
    }


}
