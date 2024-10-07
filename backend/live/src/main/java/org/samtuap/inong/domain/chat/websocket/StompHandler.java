package org.samtuap.inong.domain.chat.websocket;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import static org.samtuap.inong.common.exceptionType.ChatExceptionType.AUTHORIZATION_HEADER_MISSING;
import static org.samtuap.inong.common.exceptionType.ChatExceptionType.INVALID_JWT_TOKEN;

@Component
@RequiredArgsConstructor
// WebSocket 연결과 STOMP 메시지 인증
public class StompHandler implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() == StompCommand.CONNECT) {
            String accessToken = accessor.getFirstNativeHeader("Authorization");

            if (accessToken == null || !accessToken.startsWith("Bearer ")) {
                throw new BaseCustomException(AUTHORIZATION_HEADER_MISSING);
            }

            String token = accessToken.substring(7);
            try {
                Claims claims = jwtProvider.parseToken(token);
                String memberId = claims.getSubject();
                accessor.getSessionAttributes().put("memberId", memberId);

                String sellerId = accessor.getFirstNativeHeader("sellerId");
                if (sellerId != null) {
                    accessor.getSessionAttributes().put("sellerId", sellerId);
                }
            } catch (ExpiredJwtException | MalformedJwtException e) {
                throw new BaseCustomException(INVALID_JWT_TOKEN);
            }
        }

        return message;
    }
}
