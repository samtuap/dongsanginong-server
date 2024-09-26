package org.samtuap.inong.securities;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtGlobalFilter implements GlobalFilter {

    @Value("${jwt.secret_key}")
    private String secretKey;

    @Value("${jwt.token.access_expiration_time}")
    private Long accessExpirationTime;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final List<String> allowUrl = Arrays.asList("/member/sign-in", "/member/sign-up", "/seller/sign-in", "/seller/sign-up", "/seller/sign-up/verified", "/member/{id}", "/member/create-token",
                                                        "/v3/api-docs/**", "/swagger-ui/**", "/webjars/**", "/live/active", "/farm/list", "/farm/search", "/farm/detail/{farmId}", "/farm/{farm_id}/notice/list",
                                                        "/farm/{farm_id}/notice/{notice_id}", "/farm/{farm_id}/notice/{notice_id}/comment", "/reviews/{packageProductId}/list", "/reviews/detail/{reviewId}");
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String reqUri = request.getURI().getPath();
        boolean isAllowed = allowUrl.stream().anyMatch(uri -> antPathMatcher.match(uri, reqUri));

        if (isAllowed) {
            return chain.filter(exchange);
        }

        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String accessToken = bearerToken.substring(7);
            try {
                byte[] keyBytes = secretKey.getBytes();
                Claims claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(keyBytes)).build().parseSignedClaims(accessToken).getPayload();
                String memberId = claims.getSubject();
                String role = claims.get("role", String.class);
                String idName;
                if(role.equals("SELLER")){
                    idName = "sellerId";
                }
                else{
                    idName = "myId";
                }
                request = exchange.getRequest().mutate()
                        .header(idName, memberId)
                        .header("myRole", role)
                        .build();
                exchange = exchange.mutate().request(request).build();
            } catch (ExpiredJwtException e) {
//                return validateRefreshTokenAndGenerateNewAccessToken(exchange, e.getClaims().getSubject(), e.getClaims().get("role", String.class),chain);
                return onError(exchange, "Invalid token", HttpStatus.BAD_REQUEST);
            } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
                return onError(exchange, "Invalid token", HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                return onError(exchange, "Authentication failed", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return onError(exchange, "Authorization token is missing", HttpStatus.UNAUTHORIZED);
        }

        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        byte[] bytes = err.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        response.getHeaders().add("Content-Type", "application/json");
        return response.writeWith(Mono.just(buffer));
    }

    private Mono<Void> validateRefreshTokenAndGenerateNewAccessToken(ServerWebExchange exchange, String memberId, String role, GatewayFilterChain chain) {
        String refreshToken = exchange.getRequest().getHeaders().getFirst("X-Refresh-Token");
        String storedRefreshToken = redisTemplate.opsForValue().get("RT:" + memberId);

        if (refreshToken != null && refreshToken.equals(storedRefreshToken)) {
            String newAccessToken = generateNewAccessToken(memberId, role);
            // 새로운 액세스 토큰을 요청 헤더에 추가
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("Authorization", "Bearer " + newAccessToken)
                    .build();
            // 원래의 요청을 계속 처리하기 위해 exchange 객체를 수정
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } else {
            return onError(exchange, "Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }
    }

    private String generateNewAccessToken(String memberId, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessExpirationTime);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        byte[] keyBytes = secretKey.getBytes();

        // 새 액세스 토큰 생성
        return Jwts.builder()
                .claims(claims)
                .subject(memberId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(keyBytes))
                .compact();
    }
}