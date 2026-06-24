package com.fullteaching.backend.file;

import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.course.CourseRepository;
import com.fullteaching.backend.security.AuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/assets/videos/{courseId}")
public class VideosHttpHandler {

    private static final Logger log = LoggerFactory.getLogger(VideosHttpHandler.class);


    private final AuthorizationService authorizationService;
    private final CourseRepository courseRepository;

    @Autowired
    public VideosHttpHandler(AuthorizationService authService, CourseRepository courseDetailsRepo) {
        this.authorizationService = authService;
        this.courseRepository = courseDetailsRepo;
    }

    @GetMapping(value = "{filename:.+}")
    public ResponseEntity<Object> getEpisodeFile(@PathVariable String courseId, @PathVariable String filename,
                                                 HttpServletRequest request, HttpServletResponse response) throws IOException {

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();

        if (authorized != null) {
            return authorized;
        } else {
            long idCourse = -1;
            try {
                idCourse = Long.parseLong(courseId);
            } catch (NumberFormatException e) {
                log.error("Course ID '{}' is not of type Long", courseId);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Course c = courseRepository.findById(idCourse).orElse(null);

            assert c != null;
            ResponseEntity<Object> userAuthorized = authorizationService.checkAuthorizationUsers(c, c.getAttenders());
            if (userAuthorized != null) { // If the user is not an attender of the course
                return userAuthorized;
            } else {

                MultipartFileSender.fromPath(new File(FileController.VIDEOS_FOLDER.toFile(), filename).toPath())
                        .with(request).with(response).serveResource();
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
