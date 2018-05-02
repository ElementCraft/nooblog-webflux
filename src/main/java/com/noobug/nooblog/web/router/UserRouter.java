package com.noobug.nooblog.web.router;

import com.noobug.nooblog.consts.PublicConst;
import com.noobug.nooblog.consts.error.PublicError;
import com.noobug.nooblog.consts.error.UserError;
import com.noobug.nooblog.service.ArticleService;
import com.noobug.nooblog.service.UserService;
import com.noobug.nooblog.tools.entity.Result;
import com.noobug.nooblog.tools.utils.SecurityUtil;
import com.noobug.nooblog.web.dto.AddUserColumnDTO;
import com.noobug.nooblog.web.dto.UserFixInfoDTO;
import com.noobug.nooblog.web.dto.UserLoginDTO;
import com.noobug.nooblog.web.dto.UserRegDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static org.springframework.web.reactive.function.BodyExtractors.toMultipartData;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Slf4j
@Component
public class UserRouter {

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private SecurityUtil securityUtil;

    @Bean
    RouterFunction<?> userRoutes() {
        return nest(path("/api/user"),
                route(POST("/reg"), this::reg)
                        .andRoute(POST("/login"), this::login)
                        .andRoute(POST("/icon/upload").and(accept(MediaType.MULTIPART_FORM_DATA)), this::uploadIcon)
                        .andRoute(POST("/auth/info/{id}"), this::addAuthInfo)
                        .andRoute(GET("/info"), this::getUserInfo)
                        .andRoute(POST("/info"), this::fixUserInfo)
                        .andRoute(GET("/logs"), this::logs)
                        .andRoute(POST("/article/unlike"), this::unlikeArticle)
                        .andRoute(POST("/article/like"), this::likeArticle)
                        .andRoute(POST("/column"), this::addColumn)
                        .andRoute(GET("/col1"), this::col1)
                        .andRoute(GET("/col2"), this::col2)
                        .andRoute(GET("/article"), this::getArticles)
        );
    }

    private Mono<ServerResponse> getArticles(ServerRequest request) {
        Integer page = Integer.valueOf(request.queryParam("page").orElse("1"));
        Integer size = Integer.valueOf(request.queryParam("size").orElse(PublicConst.PAGE_SIZE.toString()));

        return securityUtil.getCurrentUser()
                .flatMap(authentication -> userService.getArticlesPage(authentication.getPrincipal().toString(), PageRequest.of(page - 1, size))
                        .flatMap(o -> ok().body(fromObject(o)))
                        .switchIfEmpty(ok().body(fromObject(new ArrayList<>())))
                )
                .switchIfEmpty(status(HttpStatus.UNAUTHORIZED).build());
    }

    /**
     * 获取全部一级栏目
     *
     * @param request
     * @return
     */
    private Mono<ServerResponse> col1(ServerRequest request) {
        return securityUtil.getCurrentUser()
                .flatMap(authentication -> userService.col1(authentication.getPrincipal().toString())
                        .flatMap(o -> ok().body(fromObject(o)))
                        .switchIfEmpty(ok().body(fromObject(new ArrayList<>())))
                )
                .switchIfEmpty(status(HttpStatus.UNAUTHORIZED).build());
    }

    /**
     * 获取指定一级栏目下的二级栏目
     *
     * @param request
     * @return
     */
    private Mono<ServerResponse> col2(ServerRequest request) {
        Result err = Result.error(PublicError.REQUEST_PARAM_ERROR);

        return request.queryParam("id")
                .map(parentId -> securityUtil.getCurrentUser()
                        .flatMap(authentication -> userService.col2(authentication.getPrincipal().toString(), Long.valueOf(parentId))
                                .flatMap(o -> ok().body(fromObject(o)))
                                .switchIfEmpty(ok().body(fromObject(new ArrayList<>())))
                        )
                        .switchIfEmpty(status(HttpStatus.UNAUTHORIZED).build()))
                .orElse(badRequest().body(fromObject(err)));


    }

    /**
     * 添加栏目
     *
     * @param request 请求
     * @return 响应
     */
    private Mono<ServerResponse> addColumn(ServerRequest request) {
        Result err = Result.error(PublicError.REQUEST_BODY_PARAM_NULL);

        return securityUtil.getCurrentUser()
                .flatMap(authentication -> request.bodyToMono(AddUserColumnDTO.class)
                        .filter(dto -> dto.getTitle() != null)
                        .flatMap(addUserColumnDTO -> userService.addColumn(authentication.getPrincipal().toString(), addUserColumnDTO))
                        .flatMap(result -> ok().body(fromObject(result)))
                        .switchIfEmpty(badRequest().body(fromObject(err)))
                )
                .switchIfEmpty(status(HttpStatus.UNAUTHORIZED).build());


    }

    /**
     * 用户对文章进行差评
     *
     * @param request 请求
     * @return 响应
     */
    private Mono<ServerResponse> unlikeArticle(ServerRequest request) {

        // 取URL参数，有进行处理 无返回400
        return request.queryParam("id")
                .map(id -> {
                    Long articleId = Long.valueOf(id);

                    return securityUtil.getCurrentUser()
                            .flatMap(authentication -> {
                                return userService.unlikeArticle(authentication.getPrincipal().toString(), articleId)
                                        .flatMap(result -> ok().body(fromObject(result)))
                                        .switchIfEmpty(status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                            })
                            .switchIfEmpty(status(HttpStatus.UNAUTHORIZED).build());
                })
                .orElse(badRequest().build());
    }

    /**
     * 用户对文章进行点赞
     *
     * @param request 请求
     * @return 响应
     */
    private Mono<ServerResponse> likeArticle(ServerRequest request) {

        // 取URL参数，有进行处理 无返回400
        return request.queryParam("id")
                .map(id -> {
                    Long articleId = Long.valueOf(id);

                    return securityUtil.getCurrentUser()
                            .flatMap(authentication -> {
                                return userService.likeArticle(authentication.getPrincipal().toString(), articleId)
                                        .flatMap(result -> ok().body(fromObject(result)))
                                        .switchIfEmpty(status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                            })
                            .switchIfEmpty(status(HttpStatus.UNAUTHORIZED).build());
                })
                .orElse(badRequest().build());
    }


    /**
     * FIXME 获取全部用户登录日志分页
     *
     * @param request
     * @return
     */
    private Mono<ServerResponse> logs(ServerRequest request) {
        Integer page = Integer.valueOf(request.queryParam("page").orElse("0"));
        Integer size = Integer.valueOf(request.queryParam("size").orElse(PublicConst.PAGE_SIZE.toString()));
        return userService.getLogPage(PageRequest.of(page, size))
                .flatMap(articles -> ok().body(fromObject(articles)))
                .switchIfEmpty(ok().body(fromObject(new ArrayList<>())));
    }

    /**
     * 修改资料
     *
     * @param request 请求
     * @return 结果
     */
    private Mono<ServerResponse> fixUserInfo(ServerRequest request) {
        Result err = Result.error(PublicError.REQUEST_BODY_PARAM_NULL);

        return securityUtil.getCurrentUser()
                .flatMap(authentication -> request.bodyToMono(UserFixInfoDTO.class)
                        .filter(dto -> dto.getSex() != null)
                        .filter(dto -> dto.getIsPublic() != null)
                        .filter(dto -> dto.getNickName() != null)
                        .flatMap(dto -> userService.fixUserInfo(authentication.getPrincipal().toString(), dto))
                        .flatMap(result -> ok().body(fromObject(result)))
                        .switchIfEmpty(badRequest().body(fromObject(err)))
                ).switchIfEmpty(status(HttpStatus.UNAUTHORIZED).build());

    }

    /**
     * 获取用户信息
     *
     * @param request 请求
     * @return 结果
     */
    private Mono<ServerResponse> getUserInfo(ServerRequest request) {

        return securityUtil.getCurrentUser()
                .flatMap(authentication -> userService.getUserInfoByAccount(authentication.getPrincipal().toString())
                        .flatMap(o -> ok().body(fromObject(o)))
                        .switchIfEmpty(status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                )
                .switchIfEmpty(status(HttpStatus.UNAUTHORIZED).build());
    }

    /**
     * 添加实名认证信息
     *
     * @param request 请求
     * @return 结果
     */
    private Mono<ServerResponse> addAuthInfo(ServerRequest request) {
        return null;
    }

    /**
     * 用户登录
     *
     * @param request 请求
     * @return 响应结果
     */
    private Mono<ServerResponse> login(ServerRequest request) {
        Result err = Result.error(UserError.Login.REQUIRE_IS_NULL);
        String ip = request.headers().host().getHostString();

        return request.bodyToMono(UserLoginDTO.class)
                .filter(loginDTO -> loginDTO.getAccount() != null)
                .filter(loginDTO -> loginDTO.getPassword() != null)
                .flatMap(loginDTO -> userService.login(loginDTO, ip))
                .flatMap(result -> ok().body(fromObject(result)))
                .switchIfEmpty(badRequest().body(fromObject(err)));
    }

    /**
     * 用户注册
     *
     * @param request 请求
     * @return 响应结果
     */
    private Mono<ServerResponse> reg(ServerRequest request) {
        Result err = Result.error(UserError.Reg.REQUIRE_IS_NULL);

        return  request.bodyToMono(UserRegDTO.class)
                    .filter(regDTO -> regDTO.getAccount() != null)
                    .filter(regDTO -> regDTO.getPassword() != null)
                    .filter(regDTO -> regDTO.getEmail() != null)
                    .filter(regDTO -> regDTO.getNickName() != null)
                    .flatMap(userService::reg)
                    .flatMap(result -> ok().body(fromObject(result)))
                    .switchIfEmpty(badRequest().body(fromObject(err)));
    }

    /**
     * 上传头像
     *
     * @param request 请求
     * @return 响应结果
     */
    private Mono<ServerResponse> uploadIcon(ServerRequest request) {
        return request.body(toMultipartData())
                .filter(data -> !data.isEmpty())
                .flatMap(userService::uploadIcon)
                .flatMap(o -> ok().body(fromObject(o)))
                .switchIfEmpty(badRequest().build());
    }
}
