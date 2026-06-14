package com.fullteaching.backend.security;

import com.fullteaching.backend.user.User;
import com.fullteaching.backend.user.UserComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

public class AuthorizationService {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationService.class);

    private final ObjectProvider<UserComponent> userProvider;

    public AuthorizationService(ObjectProvider<UserComponent> userProvider) {
        this.userProvider = userProvider;
    }

    // Checks if user logged
    public ResponseEntity<Object> checkBackendLogged() {
        if (!userProvider.getObject().isLoggedUser()) {
            log.error("Not user logged");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return null;
    }

    // Checks authorization of teacher
    public ResponseEntity<Object> checkAuthorization(Object o, User u) {
        if (o == null) {
            // The object does not exist
            log.error("Element not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!this.userProvider.getObject().getLoggedUser().equals(u)) {
            // The user does not match the logged one
            log.warn("The user is not authorized");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return null;
    }

    // 	Checks authorization of participant
    public ResponseEntity<Object> checkAuthorizationUsers(Object o, Collection<User> users) {
        if (o == null) {
            //The object does not exist
            log.error("Element not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!users.contains(this.userProvider.getObject().getLoggedUser())) {
            //The user is not authorized to edit if it is not an attender of the Course
            log.error("User not authorized. Must be a participant of the course");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return null;
    }

}
