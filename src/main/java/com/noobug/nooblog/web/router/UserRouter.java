package com.noobug.nooblog.web.router;

import com.noobug.nooblog.consts.error.PublicError;
import com.noobug.nooblog.consts.error.UserError;
import com.noobug.nooblog.service.UserService;
import com.noobug.nooblog.tools.entity.Result;
import com.noobug.nooblog.web.dto.UserLoginDTO;
import com.noobug.nooblog.web.dto.UserRegDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Slf4j
@Component
public class UserRouter {

    @Autowired
    private UserService userService;

    @Bean
    RouterFunction<?> userRoutes() {
        return nest(path("/api/user"),
                route(POST("/reg"), this::reg)
                        .andRoute(POST("/login"), this::login)
        );
    }

    /**
     * 用户登录
     *
     * @param request 请求
     * @return 响应结果
     */
    private Mono<ServerResponse> login(ServerRequest request) {
        Result err = Result.error(UserError.LOGIN_REQUIRE_IS_NULL);
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
        Result err = Result.error(UserError.REGIST_REQUIRE_IS_NULL);

        return request.bodyToMono(UserRegDTO.class)
                .filter(regDTO -> regDTO.getAccount() != null)
                .filter(regDTO -> regDTO.getPassword() != null)
                .filter(regDTO -> regDTO.getEmail() != null)
                .filter(regDTO -> regDTO.getNickName() != null)
                .flatMap(userService::reg)
                .flatMap(result -> ok().body(fromObject(result)))
                .switchIfEmpty(badRequest().body(fromObject(err)));

    }
}
