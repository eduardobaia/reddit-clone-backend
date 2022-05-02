package com.reddit.redditclone.secutiry;

import com.reddit.redditclone.exceptions.SpringRedditException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.cert.CertificateException;


import static io.jsonwebtoken.Jwts.parser;
import static java.util.Date.from;

import org.springframework.security.core.userdetails.User;

//import org.springframework.security.oauth2.jwt.JwtClaimsSet;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.Instant;

@Service
public class JwtProvider {

    private KeyStore keyStore;
    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;

    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
            keyStore.load(resourceAsStream, "secret".toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new SpringRedditException("Exception occurred while loading keystore", e);
        }

    }



    public String generateToken(Authentication authentication){
      User principal =   (User) authentication.getPrincipal();
      return Jwts.builder()
              .setSubject(principal.getUsername())
              .setIssuedAt(from(Instant.now()))
              .signWith(getPrivateKey())
              .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
              .compact();
    }

    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SpringRedditException("Exception occured while retrieving public key from keystore", e);
        }
    }

     /* Validate the token using the public key */
    public boolean validateToken(String jwt) throws KeyStoreException {
        parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);
        return true;

    }

    private PublicKey getPublicKey() throws KeyStoreException {
        try {
            return keyStore.getCertificate("springblog").getPublicKey();
        }catch (KeyStoreException e){
            throw new KeyStoreException("Exception occured whule retrieving public key from keystore ");
        }
    }

    public String getUsernameFromJwt(String token) throws KeyStoreException {
        Claims claims = parser()
                .setSigningKey(getPublicKey())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Long getJwtExpirationInMillis() {
        return jwtExpirationInMillis;
    }

    public void setJwtExpirationInMillis(Long jwtExpirationInMillis) {
        this.jwtExpirationInMillis = jwtExpirationInMillis;
    }

    public String generateTokenWithUserName(String username) {
//        JwtClaimsSet claims = JwtClaimsSet.builder()
//                .issuer("self")
//                .issuedAt(Instant.now())
//                .expiresAt(Instant.now().plusMillis(jwtExpirationInMillis))
//                .subject(username)
//                .claim("scope", "ROLE_USER")
//                .build();
//
//        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(from(Instant.now()))
                .signWith(getPrivateKey())
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();

    }
}
