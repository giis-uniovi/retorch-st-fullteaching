package com.fullteaching.backend.user;

import com.fullteaching.backend.security.AuthorizationService;
import org.apache.commons.validator.routines.EmailValidator;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api-users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final ObjectProvider<UserComponent> userProvider;
    private final AuthorizationService authorizationService;

    @Autowired
    public UserController ( UserRepository  userRepo, ObjectProvider<UserComponent> userComp, AuthorizationService authService) {
        this.userRepository=userRepo;
        this.userProvider=userComp;
        this.authorizationService=authService;
    }

    @Value("${recaptcha.private.key}")
    private String recaptchaPrivateKey;

    @Value("${profile.stage:dev}")
    private String profileStage;

    //Between 8-20 characters long, at least one uppercase, one lowercase and one number
    private static final String PASS_REGEX = "^((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20})$";

    //userData: [name, pass, nickName, captchaToken]
    @PostMapping(value = "/new")
    public ResponseEntity<User> newUser(@RequestBody String[] userData) throws IOException, ParseException {

        log.info("Signing up a user...");

        if (this.validateGoogleCaptcha(userData[3])) {

            //If the email is not already in use
            if (userRepository.findByName(userData[0]) == null) {

                //If the password has a valid format (at least 8 characters long and contains one uppercase, one lowercase and a number)
                if (userData[1].matches(UserController.PASS_REGEX)) {

                    //If the email has a valid format
                    if (EmailValidator.getInstance().isValid(userData[0])) {
                        log.info("Email, password and captcha are valid");
                        User newUser = new User(userData[0], userData[1], userData[2], "", "ROLE_STUDENT");
                        userRepository.save(newUser);
                        log.info("User successfully signed up");

                        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
                    } else {
                        log.error("Email NOT valid");
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    }
                } else {
                    log.error("Password NOT valid");
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

            } else {
                log.error("Email already in use");
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        } else {
            log.error("Captcha not validated");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    //userData: [oldPassword, newPassword]
    @PutMapping(value = "/changePassword")
    public ResponseEntity<Object> changePassword(@RequestBody String[] userData) {

        log.info("Updating password...");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        //If the stored current password and the given current password match
        if (encoder.matches(userData[0], userProvider.getObject().getLoggedUser().getPasswordHash())) {

            //If the password has a valid format (at least 8 characters long and contains one uppercase, one lowercase and a number)
            if (userData[1].matches(PASS_REGEX)) {
                User modifiedUser = userRepository.findByName(userProvider.getObject().getLoggedUser().getName());
                modifiedUser.setPasswordHash(encoder.encode(userData[1]));
                userRepository.save(modifiedUser);

                log.info("Password successfully updated");

                return new ResponseEntity<>(true, HttpStatus.OK);
            } else {
                log.error("New password NOT valid");
                return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
            }
        } else {
            log.error("Invalid current password");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    private boolean validateGoogleCaptcha(String token) throws IOException, ParseException {

        log.info("Validating Google Captcha");

        if ("dev".equals(profileStage)) {
            log.info("Dev profile: skipping captcha validation");
            return true;
        }

        BufferedReader in = getBufferedReader(token);
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //Print result
        log.debug("Response: {}", response);

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response.toString());

        boolean successCaptchaResponse = (boolean) json.get("success");

        if (successCaptchaResponse) {
            log.info("Captcha response successful");
        } else {
            log.error("Captcha invalid");
        }

        return successCaptchaResponse;
    }

    private BufferedReader getBufferedReader(String token) throws IOException {
        String url = "https://www.google.com/recaptcha/api/siteverify";
        String requestBody = "secret=" + this.recaptchaPrivateKey + "&response=" + token;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            // Read the full body as a String so the client can be closed before returning
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return new BufferedReader(new java.io.StringReader(response.body()));
        } catch (InterruptedException e) {
            // Restore the interrupted status and wrap in an IOException to maintain your original method signature
            Thread.currentThread().interrupt();
            throw new IOException("HTTP request was interrupted", e);
        }
    }

}
