package com.noobug.nooblog.security;

public interface JwtConst {

    String SECRET = "nooblog!Daedalus@130#noobug.com";
    String BEARER = "Bearer ";
    String SECRET_KEY = "jwt.secret";
    String EXPIRED_KEY = "jwt.token.expired";
    Long EXPIRED = 24L * 60L * 60L * 1000L;
}
