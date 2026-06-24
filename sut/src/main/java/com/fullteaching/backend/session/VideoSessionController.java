package com.fullteaching.backend.session;

import com.fullteaching.backend.security.AuthorizationService;
import com.fullteaching.backend.user.UserComponent;
import com.fullteaching.backend.util.LogSanitizer;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.TokenOptions;
import jakarta.annotation.PostConstruct;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api-video-sessions")
public class VideoSessionController {

    private static final Logger log = LoggerFactory.getLogger(VideoSessionController.class);
    String secret;
    String url;

    private final SessionRepository sessionRepository;
    private final AuthorizationService authorizationService;
    private final ObjectProvider<UserComponent> userProvider;

    @Autowired
    public VideoSessionController(SessionRepository sessionRepo,AuthorizationService authService,ObjectProvider<UserComponent> userComp) {
        this.sessionRepository = sessionRepo;
        this.authorizationService = authService;
        this.userProvider = userComp;
    }


    @Value("${openvidu.url}")
    private String openviduUrl;
    @Value("${openvidu.secret}")
    private String openviduSecret;
    private final Map<Long, io.openvidu.java.client.Session> lessonIdSession = new ConcurrentHashMap<>();
    private final Map<String, Map<Long, String>> sessionIdUserIdToken = new ConcurrentHashMap<>();
    private final Map<String, Integer> sessionIdindexColor = new ConcurrentHashMap<>();
    private final String [] colors = {"#2C3539", "#7D0552", "#2B1B17", "#25383C", "#CD7F32", "#151B54", "#625D5D", "#DAB51B",
            "#3CB4C7", "#461B7E", "#C12267", "#438D80", "#657383", "#E56717", "#667C26", "#E42217", "#FFA62F",
            "#254117", "#321640", "#321640", "#173180", "#8C001A", "#4863A0"};
    private OpenVidu openVidu;



    @PostConstruct
    public void initIt() {
        this.secret = openviduSecret;
        this.url = openviduUrl;
        log.info("OPENVIDU_URL: {}", this.url);
        this.openVidu = new OpenVidu(this.url, this.secret);
    }

    @GetMapping(value = "/get-sessionid-token/{id}")
    public ResponseEntity<Object> getSessionIdAndToken(@PathVariable String id) {

        if (log.isInfoEnabled()) {
            log.info("Getting OpenVidu sessionId and token for session with id '{}'", LogSanitizer.sanitize(id));
        }

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        Session session = sessionRepository.findById(Long.parseLong(id)).orElse(null);
        if (session == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        JSONObject responseJson = new JSONObject();
        ResponseEntity<Object> teacherAuthorized = authorizationService.checkAuthorization(session,
                session.getCourse().getTeacher());

        long sessionId = session.getId();
        if (this.lessonIdSession.get(sessionId) == null) { // First user connecting to the session (only the teacher can)
            if (teacherAuthorized != null) {
                log.error("Error geting OpenVidu sessionId and token: First user must be the teacher of the course");
                return teacherAuthorized;
            }
            return handleFirstConnection(sessionId, responseJson);
        } else { // The video session is already created
            ResponseEntity<Object> userAuthorized = authorizationService.checkAuthorizationUsers(session,
                    session.getCourse().getAttenders());
            if (userAuthorized != null) {
                log.error("Error geting OpenVidu token: user must be a student of the course");
                return userAuthorized;
            }
            return handleSubsequentConnection(sessionId, teacherAuthorized, responseJson);
        }
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Object> handleFirstConnection(long idI, JSONObject responseJson) {
        try {
            io.openvidu.java.client.Session s = this.openVidu.createSession();
            String sessionId = s.getSessionId();
            log.info("OpenVidu sessionId '{}' succesfully retrieved from OpenVidu Server", sessionId);

            String tokenHash = s.generateToken(new TokenOptions.Builder()
                    .data("{\"name\": \"" + this.userProvider.getObject().getLoggedUser().getNickName()
                            + "\", \"isTeacher\": true, \"color\": \"" + colors[0] + "\"}")
                    .build());
            String token = buildFullToken(sessionId, tokenHash);
            log.info("OpenVidu token '{}' (for session '{}') succesfully retrieved from OpenVidu Server",
                    token, sessionId);

            responseJson.put(0, sessionId);
            responseJson.put(1, token);

            this.lessonIdSession.put(idI, s);
            this.sessionIdUserIdToken.put(s.getSessionId(), new ConcurrentHashMap<>());
            this.sessionIdUserIdToken.get(s.getSessionId()).put(this.userProvider.getObject().getLoggedUser().getId(), token);
            this.sessionIdindexColor.put(s.getSessionId(), 1);

            log.info("sessionId '{}' successfully associated to lesson '{}'", sessionId, idI);
            log.info("Sending back to client sessionId '{}' and token '{}'", sessionId, token);

            return new ResponseEntity<>(responseJson, HttpStatus.OK);

        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Object> handleSubsequentConnection(long idI, ResponseEntity<Object> teacherAuthorized,
                                                              JSONObject responseJson) {
        io.openvidu.java.client.Session s = this.lessonIdSession.get(idI);
        String sessionId = s.getSessionId();
        log.info("OpenVidu sessionId '{}' already exists for lesson '{}'", sessionId, idI);

        try {
            String tokenHash = s.generateToken(new TokenOptions.Builder()
                    .data("{\"name\": \"" + this.userProvider.getObject().getLoggedUser().getNickName() + "\", \"isTeacher\": "
                            + ((teacherAuthorized == null) ? "true" : "false") + ", \"color\": \""
                            + colors[this.sessionIdindexColor.get(s.getSessionId())] + "\"}")
                    .build());
            String token = buildFullToken(sessionId, tokenHash);
            log.info("OpenVidu token '{}' (for session '{}') succesfully retrieved from OpenVidu Server",
                    token, sessionId);

            responseJson.put(0, sessionId);
            responseJson.put(1, token);

            this.sessionIdUserIdToken.get(s.getSessionId()).put(this.userProvider.getObject().getLoggedUser().getId(), token);
            this.sessionIdindexColor.put(s.getSessionId(), this.sessionIdindexColor.get(s.getSessionId()) + 1);

            log.info("Sending back to client sessionId '{}' and token '{}'", sessionId, token);

            return new ResponseEntity<>(responseJson, HttpStatus.OK);

        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String buildFullToken(String sessionId, String tokenHash) {
        if (tokenHash.startsWith("wss://") || tokenHash.startsWith("ws://")) {
            // openvidu-server 2.3.0+: generateToken() already returns the full WSS URL
            return tokenHash;
        }
        // openvidu-server <2.3.0: sessionId = wss://server:port/sessionHash, tokenHash = hash only
        // openvidu-browser 2.3.0 needs: wss://server:port?sessionId=hash&token=hash
        String sessionHash = sessionId.substring(sessionId.lastIndexOf('/') + 1);
        String serverBaseUrl = sessionId.substring(0, sessionId.lastIndexOf('/'));
        return serverBaseUrl + "?sessionId=" + sessionHash + "&token=" + tokenHash;
    }

    @PostMapping(value = "/remove-user")
    public ResponseEntity<Object> removeUser(@RequestBody String sessionName) throws ParseException {

        if (log.isInfoEnabled()) {
            log.info("Removing user '{}' from videosession '{}'",
                    LogSanitizer.sanitize(this.userProvider.getObject().getLoggedUser().getNickName()),
                    LogSanitizer.sanitize(sessionName));
        }

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        JSONObject sessionNameTokenJSON = (JSONObject) new JSONParser().parse(sessionName);
        Long lessonId = (Long) sessionNameTokenJSON.get("lessonId");

        io.openvidu.java.client.Session session = this.lessonIdSession.get(lessonId);
        if (session == null) {
            log.error("There was no OpenVidu session for lesson with id '{}'", lessonId);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return removeUserFromSession(session.getSessionId(), lessonId, sessionName);
    }

    private ResponseEntity<Object> removeUserFromSession(String sessionId, Long lessonId, String sessionName) {
        if (!this.sessionIdUserIdToken.containsKey(sessionId)) {
            log.error("There was no OpenVidu SESSIONID associated with lesson '{}'", lessonId);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (this.sessionIdUserIdToken.get(sessionId).remove(this.userProvider.getObject().getLoggedUser().getId()) == null) {
            log.error("OpenVidu TOKEN asssociated to user '{}' wasn't valid",
                    this.userProvider.getObject().getLoggedUser().getNickName());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // User left the session
        if (log.isInfoEnabled()) {
            log.info("User '{}' removed", LogSanitizer.sanitize(this.userProvider.getObject().getLoggedUser().getNickName()));
        }
        if (this.sessionIdUserIdToken.get(sessionId).isEmpty()) {
            // Last user left the session
            if (log.isInfoEnabled()) {
                log.info("Last user removed from session. Session '{}' empty and removed", LogSanitizer.sanitize(sessionName));
            }
            this.lessonIdSession.remove(lessonId);
            this.sessionIdindexColor.remove(sessionId);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}