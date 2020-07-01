package com.ihrm.system;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JWTTest {
    @Test
    public void testCreateJwt(){
        String key = "option";
        JwtBuilder jwtBuilder = Jwts.builder().setId("110")
                .setSubject("测试")
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, key.getBytes());
        System.out.println(jwtBuilder.compact());
    }

    @Test
    public void testParseJWT(){
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMTAiLCJzdWIiOiLmtYvor5UiLCJpYXQiOjE1OTMxNjk5MDh9.m0il3DeWdg7HDTXs_vpoUomVFV7MeQxwONbdGT7Je5w";
        Claims option = Jwts.parser().setSigningKey("option".getBytes()).parseClaimsJws(token).getBody();
        System.out.println(option.getIssuedAt());
        System.out.println(option.getSubject());
        System.out.println(option.getId());
    }

    @Test
    public void testCreateCompactJWT() throws UnsupportedEncodingException {
        long now = System.currentTimeMillis();
        long exp = now + 1000*60*10;
        Map<String, Object> map = new HashMap<>();
        map.put("role", "admin");
        map.put("username", "option");
        JwtBuilder builder = Jwts.builder().setId("110")
                .setSubject("测试01")
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, "option".getBytes())
                .setExpiration(new Date(exp))
                .setClaims(map);
        System.out.println(builder.compact());
    }

    @Test
    public void testParseCompactJwt() throws UnsupportedEncodingException {
        String compact = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMjc2NTAwMjA0MzM3NjMxMjMyIiwic3ViIjoiYWRtaW4iLCJpYXQiOjE1OTMxODMzODAsImNvbXBhbnlJZCI6IjEiLCJhcGlzIjoiQVBJLVVTRVItREVMRVRFLEFQSS1VU0VSLURFTEVURSxBUEktVVNFUi1ERUxFVEUsIiwiY29tcGFueU5hbWUiOiLmsZ_oi4_kvKDmmbrmkq3lrqLmlZnogrLogqHku73mnInpmZDlhazlj7giLCJleHAiOjE1OTMxODY5ODB9.MCtFwj8Dfht-u2gdPDbLkppJdqXqfDhDIBJXtB2c4o0";
        Claims body = Jwts.parser().setSigningKey("ihrm".getBytes("UTF-8")).parseClaimsJws(compact).getBody();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        System.out.println(body.getId());
        System.out.println(body.getSubject());
        System.out.println(body.getIssuedAt());
        System.out.println(body.getExpiration());
        System.out.println(body.get("apis"));
        System.out.println(body.get("companyId"));
        System.out.println(body.get("companyName"));
//        System.out.println(simpleDateFormat.format(body.getExpiration()));
    }
}
