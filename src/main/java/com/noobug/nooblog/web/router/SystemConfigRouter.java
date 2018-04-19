package com.noobug.nooblog.web.router;

import com.noobug.nooblog.consts.PublicConst;
import com.noobug.nooblog.consts.error.PublicError;
import com.noobug.nooblog.domain.SystemConfig;
import com.noobug.nooblog.service.SystemConfigService;
import com.noobug.nooblog.tools.entity.Result;
import com.noobug.nooblog.tools.utils.ConfigUtil;
import com.noobug.nooblog.tools.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
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
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

/**
 * 数据库配置项接口路由
 *
 * @author noobug.com
 */
@Slf4j
@Component
public class SystemConfigRouter {

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private SecurityUtil securityUtil;

    @Bean
    RouterFunction<?> systemConfigRoutes() {
        return nest(path("/api/system/config"),
                route(POST("/"), this::add)
                        .andRoute(PUT("/"), this::edit)
                        .andRoute(GET("/"), this::getPage)
                        .andRoute(DELETE("/{id}"), this::del)
                        .andRoute(POST("/reload"), this::reloadConfig)
        );
    }


    private Mono<ServerResponse> reloadConfig(ServerRequest request) {
        return null;
    }

    /**
     * 删除数据库配置项
     *
     * @param request 请求
     * @return 结果
     */
    private Mono<ServerResponse> del(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));

        return systemConfigService.delById(id)
                .flatMap(o -> ok().body(fromObject(o)))
                .switchIfEmpty(status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    /**
     * 编辑配置项
     *
     * @param request 请求
     * @return 响应
     */
    private Mono<ServerResponse> edit(ServerRequest request) {
        Result err = Result.error(PublicError.REQUEST_BODY_PARAM_NULL);

        return request.bodyToMono(SystemConfig.class)
                .filter(o -> o.getId() != null)
                .filter(o -> o.getData() != null)
                .flatMap(systemConfigService::edit)
                .flatMap(o -> ok().body(fromObject(o)))
                .switchIfEmpty(badRequest().body(fromObject(err)));
    }

    /**
     * 获取配置项分页
     *
     * @param request 请求
     * @return 响应
     */
    private Mono<ServerResponse> getPage(ServerRequest request) {
        Integer page = Integer.valueOf(request.queryParam("page").orElse("0"));
        Integer size = Integer.valueOf(request.queryParam("size").orElse(PublicConst.PAGE_SIZE.toString()));

        return systemConfigService.getAllByPage(PageRequest.of(page, size))
                .flatMap(systemConfigs -> ok().body(fromObject(systemConfigs)))
                .switchIfEmpty(ok().body(fromObject(new ArrayList<>())));
    }

    /**
     * 新增配置项
     *
     * @param request 请求
     * @return 响应
     */
    private Mono<ServerResponse> add(ServerRequest request) {
        Result err = Result.error(PublicError.REQUEST_BODY_PARAM_NULL);

        return request.bodyToMono(SystemConfig.class)
                .filter(o -> o.getKey() != null)
                .filter(o -> o.getData() != null)
                .flatMap(systemConfigService::add)
                .flatMap(o -> ok().body(fromObject(o)))
                .switchIfEmpty(badRequest().body(fromObject(err)));
    }
}
