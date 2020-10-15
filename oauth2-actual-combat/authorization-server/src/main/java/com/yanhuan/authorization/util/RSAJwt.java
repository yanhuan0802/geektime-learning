package com.yanhuan.authorization.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.IOException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * 非对称加密
 *
 * @author wang
 */
public class RSAJwt {
    public static Map<String, byte[]> map = new HashMap<String, byte[]>();

    public static void main(String[] args) throws Exception {
//        // 加密
        String token = generateToken(1000, "secert123");
        // 解密
        decode(token);
    }

    /**
     * 解密
     *
     * @param token token
     * @throws Exception 异常
     */
    public static void decode(String token) throws Exception {
        getInfoFromToken(token, map.get("pub"));
    }

    /**
     * 打印解密后的数据
     *
     * @param token  token
     * @param pubKey 公钥byte数组
     * @throws Exception 异常
     */
    public static void getInfoFromToken(String token, byte[] pubKey) throws Exception {
        Jws<Claims> claimsJws = parserToken(token, pubKey);
        Claims body = claimsJws.getBody();
        System.out.println("body:" + body.getSubject());
        System.out.println("id:" + body.get("id"));
        System.out.println("name:" + body.get("name"));
    }

    /**
     * 解析加密过的token，解析成Claims，就是自定义的playload部分
     *
     * @param token  token
     * @param pubKey 公钥byte数组
     * @return 解析结果
     * @throws Exception 异常
     */
    public static Jws<Claims> parserToken(String token, byte[] pubKey) throws Exception {
        return Jwts.parser().setSigningKey(getPublicKey(pubKey)).parseClaimsJws(token);
    }

    /***
     * 生成token
     * @param expire 过期事件
     * @param secret 盐
     */
    public static String generateToken(int expire, String secret) throws Exception {
        Map<String, byte[]> key = generateKey(secret);

        /*String compactJws = Jwts.builder()
                .setSubject("subject2")
                // 添加clain，就是自定义的playload部分
                .claim("id", "1")
                .claim("name", "zhangsan")
                .setExpiration(DateTime.now().plusSeconds(expire).toDate())
                .signWith(SignatureAlgorithm.RS256, getPrivateKey(key.get("pri")))
                .compact();*/

        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("typ", "JWT");
        headerMap.put("alg", "HS256");

        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("sub", "Tom");
        payloadMap.put("iss", "--");

        String compactJws = Jwts.builder()
                .setHeaderParams(headerMap)
                .setClaims(payloadMap)
                .signWith(SignatureAlgorithm.HS256, getPrivateKey(key.get("pri")))
                .compact();

        System.out.println(compactJws);
        return compactJws;
    }

    /**
     * 获取公钥 使用jdk 的security把byte数据解析成getPublicKey类
     *
     * @param publicKey 公钥byte数组
     * @return 公钥对象
     * @throws Exception 异常
     */
    public static PublicKey getPublicKey(byte[] publicKey) throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    /**
     * 使用jdk 的security把byte数据解析成PrivateKey类
     *
     * @param privateKey 私钥byte数组
     * @return 私钥
     * @throws Exception 异常
     */
    public static PrivateKey getPrivateKey(byte[] privateKey) throws Exception {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    /**
     * 使用jdk的security生成rsa的公钥和私钥
     *
     * @param password 密码
     * @return 秘钥map
     * @throws IOException              io异常
     * @throws NoSuchAlgorithmException 无算法异常
     */
    public static Map<String, byte[]> generateKey(String password) throws IOException, NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = new SecureRandom(password.getBytes());
        keyPairGenerator.initialize(1024, secureRandom);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        map.put("pub", publicKeyBytes);
        map.put("pri", privateKeyBytes);
        return map;
    }
}
