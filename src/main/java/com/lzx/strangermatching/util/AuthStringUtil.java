package com.lzx.strangermatching.util;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: AuthStringUtil
 * @Description: 鉴权字符串生成工具
 * @Author: LZX
 * @Date: 2024/6/14 14:52
 */
public class AuthStringUtil {

    /**
     * 创建鉴权认证字符串 authorization
     * @param request
     * @param sk
     * @param ak
     * @param timestamp
     * @param expirationPeriodInSeconds
     * @return
     */
    public Request createAuthorization(Request request, String sk, String ak, String timestamp, String expirationPeriodInSeconds) throws MalformedURLException, NoSuchAlgorithmException, InvalidKeyException {
        String authStringPrefix = createAuthStringPrefix(ak, timestamp, expirationPeriodInSeconds);
        String canonicalRequest = createCanonicalRequest(request);
        String signedHeaders = createSignedHeaders(request.headers().toMultimap());
        String signingKey = createSigningKey(sk, authStringPrefix);
        String signature = createSignature(signingKey, canonicalRequest);
        String authString = authStringPrefix + "/" + signedHeaders + "/" + signature;

        List<String> headers = new ArrayList<>();
        Map<String, List<String>> map = request.headers().toMultimap();
        Map<String, String> newMap = map.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get(0)
                ));
        System.out.println(authString);

        return new Request.Builder()
                .headers(Headers.of(newMap))
                .header("Authorization", authString)
                .post(request.body() != null ? request.body() : RequestBody.create("".getBytes(StandardCharsets.UTF_8)))
                .url(request.url())
                .build();
    }

    /**
     * 创建前缀字符串(authStringPrefix)
     * https://cloud.baidu.com/doc/Reference/s/njwvz1yfu#%E4%BB%BB%E5%8A%A1%E4%B8%80%EF%BC%9A%E5%88%9B%E5%BB%BA%E5%89%8D%E7%BC%80%E5%AD%97%E7%AC%A6%E4%B8%B2authstringprefix
     * @param akId
     * @param timestamp
     * @param expirationPeriodInSeconds
     * @return
     */
    public String createAuthStringPrefix(String akId, String timestamp, String expirationPeriodInSeconds) {
        return String.format("bce-auth-v1/%s/%s/%s", akId, timestamp, expirationPeriodInSeconds);
    }

    /**
     * 创建规范请求
     * https://cloud.baidu.com/doc/Reference/s/njwvz1yfu#%E4%BB%BB%E5%8A%A1%E4%BA%8C%EF%BC%9A%E5%88%9B%E5%BB%BA%E8%A7%84%E8%8C%83%E8%AF%B7%E6%B1%82canonicalrequest%EF%BC%8C%E7%A1%AE%E5%AE%9A%E7%AD%BE%E5%90%8D%E5%A4%B4%E5%9F%9Fsignedheaders
     * @param httpRequest
     * @return
     * @throws MalformedURLException
     */
    public String createCanonicalRequest(Request httpRequest) throws MalformedURLException {
        String requestUri = String.valueOf(httpRequest.url());
        String canonicalURI = createCanonicalURI(requestUri);

        int questionMarkIndex = requestUri.indexOf("?");
        String canonicalQueryString = createCanonicalQueryString(requestUri, questionMarkIndex);

        String canonicalHeaders = createCanonicalHeaders(httpRequest.headers().toMultimap());

        return httpRequest.method() + "\n" +canonicalURI + "\n" + canonicalQueryString + "\n" + canonicalHeaders;
    }

    /**
     * 生成派生密钥(signingKey)
     * https://cloud.baidu.com/doc/Reference/s/njwvz1yfu#%E4%BB%BB%E5%8A%A1%E4%B8%89%EF%BC%9A%E7%94%9F%E6%88%90%E6%B4%BE%E7%94%9F%E5%AF%86%E9%92%A5signingkey
     * @param sk
     * @param authStringPrefix
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public String createSigningKey(String sk, String authStringPrefix) throws NoSuchAlgorithmException, InvalidKeyException {
        return calculateHMAC(authStringPrefix, sk);
    }

    /**
     * 生成签名摘要(signature)
     * @param signingKey
     * @param canonicalRequest
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public String createSignature(String signingKey, String canonicalRequest) throws NoSuchAlgorithmException, InvalidKeyException {
        return calculateHMAC(canonicalRequest, signingKey);
    }

    /**
     * 获取signedHeaders
     * @param headers
     * @return
     */
    public String createSignedHeaders(Map<String, List<String>> headers) {
        List<String> headerNames = new ArrayList<>(headers.keySet());
        List<String> signedHeaders = Arrays.asList("host", "content-length", "content-type", "content-md5");
        List<String> canonicalHeaders = new ArrayList<>();

        for (String headerName : headerNames) {
            String lowerCaseHeaderName = headerName.toLowerCase();
            if (signedHeaders.contains(lowerCaseHeaderName) || lowerCaseHeaderName.startsWith("x-bce-")) {
                if (!headers.get(headerName).isEmpty()) {
                    String headerValue = headers.get(headerName).get(0).trim();
                    if (!headerValue.isEmpty()) {
                        String encodedHeader = uriEncode(lowerCaseHeaderName, true);
                        canonicalHeaders.add(encodedHeader);
                    }
                }
            }
        }
        // 按字典序排序
        Collections.sort(canonicalHeaders);
        // 使用 ; 连接
        return String.join(";", canonicalHeaders);
    }

    /**
     * 对URL中的绝对路径进行编码
     * https://cloud.baidu.com/doc/Reference/s/njwvz1yfu#2-canonicaluri
     * @param requestUri
     * @return
     * @throws MalformedURLException
     */
    public String createCanonicalURI(String requestUri) throws MalformedURLException {
        int questionMarkIndex = requestUri.indexOf("?");
        String base = questionMarkIndex != -1 ? requestUri.substring(0, questionMarkIndex) : requestUri;

        URL baseURL = new URL(base);
        String urlPath = baseURL.getPath();
        return uriEncode(urlPath, false);
    }

    /**
     * 对于URL中的Query String（Query String即URL中“？”后面的“key1 = value1 & key2 = value2 ”字符串）进行编码
     * https://cloud.baidu.com/doc/Reference/s/njwvz1yfu#3-canonicalquerystring
     * @param requestUri
     * @param questionMarkIndex
     * @return
     */
    public String createCanonicalQueryString(String requestUri, int questionMarkIndex) {
        List<String> encodedQueries = new ArrayList<>();
        if (questionMarkIndex != -1) {
            String query = requestUri.substring(questionMarkIndex, requestUri.length() - 1);
            String[] queries = query.split("&");
            for (String queryString : queries) {
                if (queryString.startsWith("authorization=")) {
                    continue;
                }

                int equalsIndex = queryString.indexOf("=");
                if (equalsIndex != -1) {
                    String key = queryString.substring(0, equalsIndex);
                    String value = queryString.substring(equalsIndex + 1);
                    encodedQueries.add(uriEncode(key, true) + "=" + uriEncode(value, true));
                } else {
                    // 只有key
                    encodedQueries.add(uriEncode(queryString, true));
                }
            }
        }
        Collections.sort(encodedQueries);
        // 使用 & 连接
        return String.join("&", encodedQueries);
    }

    /**
     * 对HTTP请求中的Header部分进行选择性编码
     * https://cloud.baidu.com/doc/Reference/s/njwvz1yfu#4-canonicalheaders
     * @param headers
     * @return
     */
    public String createCanonicalHeaders(Map<String, List<String>> headers) {
        List<String> headerNames = new ArrayList<>(headers.keySet());
        List<String> signedHeaders = Arrays.asList("host", "content-length", "content-type", "content-md5");
        List<String> canonicalHeaders = new ArrayList<>();

        for (String headerName : headerNames) {
            String lowerCaseHeaderName = headerName.toLowerCase();
            if (signedHeaders.contains(lowerCaseHeaderName) || lowerCaseHeaderName.startsWith("x-bce-")) {
                if (!headers.get(headerName).isEmpty()) {
                    String headerValue = headers.get(headerName).get(0).trim();
                    if (!headerValue.isEmpty()) {
                        String encodedHeader = uriEncode(lowerCaseHeaderName, true) + ":" + uriEncode(headerValue, true);
                        canonicalHeaders.add(encodedHeader);
                    }
                }
            }
        }
        // 按字典序排序
        Collections.sort(canonicalHeaders);
        // 使用 \n 连接
        return String.join("\n", canonicalHeaders);
    }

    /**
     * 使用 Java 自带的 javax.crypto.Mac 类来实现 HMAC-SHA256 计算
     * @param data
     * @param key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private static String calculateHMAC(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);
        byte[] hmacBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hmacBytes);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xff & aByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // 将字符转换为其对应的 UTF-8 编码的十六进制表示
    private static String toHexUTF8(char ch) {
        byte[] bytes = String.valueOf(ch).getBytes(StandardCharsets.UTF_8);
        StringBuilder hexString = new StringBuilder();

        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static String uriEncode(CharSequence input, boolean encodeSlash) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '-' || ch == '~' || ch == '.') {
                result.append(ch);
            } else if (ch == '/') {
                result.append(encodeSlash ? "%2F" : ch);
            } else {
                result.append(toHexUTF8(ch));
            }
        }
        return result.toString();
    }


}
