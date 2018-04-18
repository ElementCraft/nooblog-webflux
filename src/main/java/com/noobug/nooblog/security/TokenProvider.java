package com.noobug.nooblog.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.noobug.nooblog.tools.utils.ConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author 小王子
 */
@Slf4j
@Component
public class TokenProvider {

    @Autowired
    private ConfigUtil configUtil;

    public String generateToken(String subject, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        Long expired = configUtil.getLong(JwtConst.EXPIRED_KEY, JwtConst.EXPIRED);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer("noobug.com")
                .expirationTime(new Date(System.currentTimeMillis() + 60 * 1000))
                .claim(JwtConst.AUTHORITIES_CLAIM_KEY, authorities.parallelStream()
                        .map(auth -> (GrantedAuthority) auth)
                        .map(a -> a.getAuthority())
                        .collect(Collectors.joining(JwtConst.AUTHORITIES_CLAIM_DELIMITER)))
                .claim(JwtConst.PASSWORD_CLAIM_KEY, credentials)
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        String secret = configUtil.get(JwtConst.SECRET_KEY, JwtConst.SECRET);

        try {
            signedJWT.sign(new MACSigner(secret));
        } catch (JOSEException e) {
            log.error("[生成JWTToken] 失败", e);
        }

        return signedJWT.serialize();
    }
}
