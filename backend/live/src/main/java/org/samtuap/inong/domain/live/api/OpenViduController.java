package org.samtuap.inong.domain.live.api;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.live.dto.LiveSessionRequest;
import org.samtuap.inong.domain.live.dto.LiveSessionResponse;
import org.samtuap.inong.domain.live.service.LiveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.openvidu.java.client.Connection;
import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OpenViduController {

    private final OpenVidu openvidu;
    private final LiveService liveService;

    /**
     * (1) create session : session id랑 live id랑 다름
     */
    @PostMapping("/api/sessions")
    public ResponseEntity<LiveSessionResponse> initializeSession(@RequestHeader("sellerId") Long sellerId,
                                                                 @RequestBody LiveSessionRequest request) {
        try {
            LiveSessionResponse response = liveService.createLiveSession(sellerId, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * (2) create connection
     */
    @PostMapping("/api/sessions/{sessionId}/connections")
    public ResponseEntity<String> createConnection(@PathVariable("sessionId") String sessionId,
                                                   @RequestBody(required = false) Map<String, Object> params)
            throws OpenViduJavaClientException, OpenViduHttpException {
        Session session = openvidu.getActiveSession(sessionId);
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ConnectionProperties properties = ConnectionProperties.fromJson(params).build();
        Connection connection = session.createConnection(properties);
        return new ResponseEntity<>(connection.getToken(), HttpStatus.OK);
    }

    /**
     * (3) leave session
     */
    @PatchMapping("/api/sessions/{sessionId}/leave")
    public ResponseEntity<?> leaveSession(@PathVariable("sessionId") String sessionId) {
        try {
            liveService.leaveSession(sessionId);  // 세션 종료 로직 실행
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * (4) dto 반환 (sessionId와 title을 얻기 위한)
     */
    @GetMapping("/api/sessions/{id}/sessionId")
    public ResponseEntity<?> getSessionIdByLiveId(@PathVariable("id") Long id) {
        try {
            log.info("sessionId 반환 시도 : {} liveId 잘 넘어옴", id);
            LiveSessionResponse dto = liveService.getSessionIdByLiveId(id);
            log.info("그걸로 dto 받아올게 : {}", dto);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}