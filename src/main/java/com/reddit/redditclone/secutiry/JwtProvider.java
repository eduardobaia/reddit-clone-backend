package com.reddit.redditclone.secutiry;

import com.reddit.redditclone.exceptions.SpringRedditException;
 import io.jsonwebtoken.Jwts;

import static java.util.Date.from;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.cert.CertificateException;


import org.springframework.security.core.userdetails.User;


import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

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
              .signWith(getPrivateKey())
              .compact();
    }

    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SpringRedditException("Exception occured while retrieving public key from keystore", e);
        }
    }
}