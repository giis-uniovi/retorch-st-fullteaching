package com.fullteaching.backend.session;

import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.course.CourseRepository;
import com.fullteaching.backend.security.AuthorizationService;
import com.fullteaching.backend.util.LogSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-sessions")
public class SessionController {

    private static final Logger log = LoggerFactory.getLogger(SessionController.class);

    private final CourseRepository courseRepository;
    private final SessionRepository sessionRepository;
    private final AuthorizationService authorizationService;

    @Autowired
    public SessionController(AuthorizationService authService,SessionRepository sessionRepo,CourseRepository courseRepo) {
        this.authorizationService = authService;
        this.sessionRepository = sessionRepo;
        this.courseRepository = courseRepo;
    }

    @PostMapping(value = "/course/{id}")
    public ResponseEntity<Object> newSession(@RequestBody SessionDto sessionDto, @PathVariable String id) {

        log.info("CRUD operation: Adding new session");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        Course course = courseRepository.findById(Long.parseLong(id)).orElse(null);
        if (course == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ResponseEntity<Object> teacherAuthorized = authorizationService.checkAuthorization(course, course.getTeacher());
        if (teacherAuthorized != null) { // If the user is not the teacher of the course
            return teacherAuthorized;
        }

        Session session = new Session(sessionDto.getTitle(), sessionDto.getDescription(), sessionDto.getDate());
        //Bi-directional saving
        session.setCourse(course);
        course.getSessions().add(session);

        //Saving the modified course: Cascade relationship between course and sessions
        //will add the new session to SessionRepository
        courseRepository.save(course);

        if (log.isInfoEnabled()) {
            log.info("New session succesfully added: {}", LogSanitizer.sanitize(session));
        }

        //Entire course is returned
        return new ResponseEntity<>(course, HttpStatus.CREATED);
    }


    @PutMapping(value = "/edit")
    public ResponseEntity<Object> modifySession(@RequestBody SessionDto sessionDto) {

        log.info("CRUD operation: Updating session");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        Session s = sessionRepository.findById(sessionDto.getId()).orElse(null);

        log.info("Updating session. Previous value: {}", s);

        assert s != null;
        ResponseEntity<Object> teacherAuthorized = authorizationService.checkAuthorization(s, s.getCourse().getTeacher());
        if (teacherAuthorized != null) { // If the user is not the teacher of the course
            return teacherAuthorized;
        } else {
            s.setTitle(sessionDto.getTitle());
            s.setDescription(sessionDto.getDescription());
            s.setDate(sessionDto.getDate());
            //Saving the modified session
            sessionRepository.save(s);

            log.info("Session succesfully updated. Modified value: {}", s);

            return new ResponseEntity<>(s, HttpStatus.OK);
        }
    }


    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Object> deleteSession(@PathVariable String id) {

        log.info("CRUD operation: Deleting session");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        Session session = sessionRepository.findById(Long.parseLong(id)).orElse(null);
        assert session != null;
        ResponseEntity<Object> teacherAuthorized = authorizationService.checkAuthorization(session, session.getCourse().getTeacher());
        if (teacherAuthorized != null) { // If the user is not the teacher of the course
            return teacherAuthorized;
        } else {

            Course course = courseRepository.findById(session.getCourse().getId()).orElse(null);
            if (course != null) {

                log.info("Deleting session: {}", session);

                course.getSessions().remove(session);
                sessionRepository.deleteById(session.getId());
                courseRepository.save(course);

                log.info("Session successfully deleted");

                return new ResponseEntity<>(session, HttpStatus.OK);
            } else {
                //The Course that owns the deleted session does not exist
                //This code is presumed to be unreachable, because of the Cascade.ALL relationship from Course to Session
                sessionRepository.delete(session);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }

}
