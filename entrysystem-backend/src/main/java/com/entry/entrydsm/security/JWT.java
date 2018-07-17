package com.entry.entrydsm.security;

import com.auth0.jwt.Algorithm;
import com.auth0.jwt.internal.org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.InitializingBean;
import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWT implements InitializingBean {

    @Value("${issuer}")
    private String issuer;
    @Value("${clientId}")
    private String clientId;
    @Value("${exp}")
    private int exp;
    @Value("${refreshExp}")
    private int refreshExp;

    private JWTSigner signer;
    private JWTVerifier jwtVerifier;

    @Override
    public void afterPropertiesSet() throws Exception {
        signer = new JWTSigner(Base64.decodeBase64("들어갈 Key"));
        jwtVerifier = new JWTVerifier(Base64.decodeBase64("들어갈 Key"), clientId, issuer);
    }

    private JWTSigner.Options initSeting(String type) {
        JWTSigner.Options options = new JWTSigner.Options();
        options.setAlgorithm(Algorithm.HS512);
        options.setJwtId(true);
        options.setIssuedAt(true);
        if (type.equals("refresh")) {
            options.setExpirySeconds(refreshExp);
        } else {
            options.setExpirySeconds(exp);
        }
        return options;
    }

    public String createToken(ApiUser user, Collection<? extends GrantedAuthority> roles) {
        Map<String, Object> map = new HashMap<>();
        map.put("iss", issuer);
        map.put("email", user.getCredentials());
        map.put("roles", roles);
        return signer.sign(map, initSeting("access"));
    }

    public String createRefreshToken(String token) throws Exception {
        Map<String, Object> claims = authToken(token);
        claims.remove("exp");
        claims.remove("iat");
        claims.remove("jti");
        return signer.sign(claims, initSeting("refresh"));
    }

    public Map<String, Object> authToken(String token) throws Exception {
        return jwtVerifier.verify(token);
    }
}