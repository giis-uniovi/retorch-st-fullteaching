package com.fullteaching.backend.forum;

import com.fullteaching.backend.coursedetails.CourseDetails;
import com.fullteaching.backend.coursedetails.CourseDetailsRepository;
import com.fullteaching.backend.security.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-forum")
public class ForumController {

    private static final Logger log = LoggerFactory.getLogger(ForumController.class);

    private final AuthorizationService authorizationService;
    private final CourseDetailsRepository courseDetailsRepository;

    @Autowired
    public ForumController(AuthorizationService authService, CourseDetailsRepository courseDetailsRepo) {
        this.authorizationService = authService;
        this.courseDetailsRepository = courseDetailsRepo;
    }

    @PutMapping(value = "/edit/{courseDetailsId}")
    public ResponseEntity<Object> modifyForum(@RequestBody boolean activated, @PathVariable String courseDetailsId) {

        log.info("CRUD operation: Updating forum");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        long idI = -1;
        try {
            idI = Long.parseLong(courseDetailsId);
        } catch (NumberFormatException e) {
            log.error("CourseDetails ID '{}' is not of type Long", courseDetailsId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        CourseDetails cd = courseDetailsRepository.findById(idI).orElse(null);
        if (cd == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        log.info("Updating forum. Previous value: {}", cd.getForum());

        ResponseEntity<Object> teacherAuthorized = authorizationService.checkAuthorization(cd, cd.getCourse().getTeacher());
        if (teacherAuthorized != null) { // If the user is not the teacher of the course
            return teacherAuthorized;
        } else {

            //Modifying the forum
            cd.getForum().setActivated(activated);
            //Saving the modified course
            courseDetailsRepository.save(cd);

            log.info("Forum succesfully updated. Modified value: {}", cd.getForum());

            return new ResponseEntity<>(activated, HttpStatus.OK);
        }
    }

}
