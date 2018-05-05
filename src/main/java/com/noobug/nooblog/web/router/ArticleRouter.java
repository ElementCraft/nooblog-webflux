package com.noobug.nooblog.web.router;

import com.noobug.nooblog.consts.PublicConst;
import com.noobug.nooblog.consts.error.PublicError;
import com.noobug.nooblog.consts.error.UserError;
import com.noobug.nooblog.service.ArticleService;
import com.noobug.nooblog.tools.entity.Result;
import com.noobug.nooblog.tools.utils.SecurityUtil;
import com.noobug.nooblog.web.dto.AddNewArticleDTO;
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
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Slf4j
@Component
public class ArticleRouter {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private SecurityUtil securityUtil;

    @Bean
    RouterFunction<?> articleRoutes() {
        return nest(path("/api/article"),
                route(DELETE("/{id}"), this::del)
                        .andRoute(GET("/content/{id}"), this::getContent)
                        .andRoute(POST("/"), this::add)
                        .andRoute(PUT("/"), this::edit)
                        .andRoute(GET("/all"), this::getPage)
                        .andRoute(GET("/info/{id}"), this::getInfoById)
                        .andRoute(GET("/hot"), this::getHotByPage)
                        .andRoute(PUT("/ban/{id}"), this::ban)
                        .andRoute(PUT("/unban/{id}"), this::unban)
        );

    }

    private Mono<ServerResponse> getInfoById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));

        return articleService.getById(id)
                .map(o -> ok().body(fromObject(o)))
                .orElse(status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }


    private Mono<ServerResponse> getContent(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));

        return articleService.getContentById(id)
                .flatMap(o -> ok().body(fromObject(o)))
                .switchIfEmpty(status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    private Mono<ServerResponse> edit(ServerRequest request) {
        return null;
    }

    /**
     * 新增文章
     *
     * @param request 请求
     * @return 响应
     */
    private Mono<ServerResponse> add(ServerRequest request) {
        Result err = Result.error(UserError.Login.REQUIRE_IS_NULL);

        return securityUtil.getCurrentUser()
                .flatMap(authentication -> request.bodyToMono(AddNewArticleDTO.class)
                        .filter(articleDTO -> articleDTO.getTitle() != null)
                        .filter(articleDTO -> articleDTO.getTypeFlag() != null)
                        .flatMap(articleDTO -> articleService.addNew(authentication.getPrincipal().toString(), articleDTO))
                        .flatMap(result -> ok().body(fromObject(result)))
                        .switchIfEmpty(badRequest().body(fromObject(err)))
                )
                .switchIfEmpty(badRequest().body(fromObject(Result.error(PublicError.SESSION_NO_USER))));

    }

    /**
     * 解封文章
     *
     * @param request 请求
     * @return 响应
     */
    private Mono<ServerResponse> unban(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));

        return articleService.unbanById(id)
                .flatMap(o -> ok().body(fromObject(o)))
                .switchIfEmpty(status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    /**
     * 封禁文章
     *
     * @param request 请求
     * @return 响应
     */
    private Mono<ServerResponse> ban(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));

        return articleService.banById(id)
                .flatMap(o -> ok().body(fromObject(o)))
                .switchIfEmpty(status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    /**
     * 获取文章分页
     *
     * @param request 请求
     * @return 响应
     */
    private Mono<ServerResponse> getPage(ServerRequest request) {
        Integer page = Integer.valueOf(request.queryParam("page").orElse("0"));
        Integer size = Integer.valueOf(request.queryParam("size").orElse(PublicConst.PAGE_SIZE.toString()));

        return articleService.getAllByPage(PageRequest.of(page, size))
                .flatMap(articles -> ok().body(fromObject(articles)))
                .switchIfEmpty(ok().body(fromObject(new ArrayList<>())));
    }


    private Mono<ServerResponse> getHotByPage(ServerRequest request) {
        Integer page = Integer.valueOf(request.queryParam("page").orElse("0"));
        Integer size = Integer.valueOf(request.queryParam("size").orElse(PublicConst.PAGE_SIZE.toString()));

        return articleService.getAllHotByPage(PageRequest.of(page, size))
                .flatMap(articles -> ok().body(fromObject(articles)))
                .switchIfEmpty(ok().body(fromObject(new ArrayList<>())));
    }

    /**
     * 删除文章
     *
     * @param request 请求
     * @return 响应
     */
    private Mono<ServerResponse> del(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));

        return articleService.delById(id)
                .flatMap(o -> ok().body(fromObject(o)))
                .switchIfEmpty(status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

}
