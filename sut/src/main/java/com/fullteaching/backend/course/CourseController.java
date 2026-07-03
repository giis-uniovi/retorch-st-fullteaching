package com.fullteaching.backend.course;

import com.fasterxml.jackson.annotation.JsonView;
import com.fullteaching.backend.course.AbstractCourseData.SimpleCourseList;
import com.fullteaching.backend.coursedetails.CourseDetails;
import com.fullteaching.backend.security.AuthorizationService;
import com.fullteaching.backend.user.User;
import com.fullteaching.backend.user.UserComponent;
import com.fullteaching.backend.user.UserRepository;
import com.fullteaching.backend.util.LogSanitizer;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api-courses")
public class CourseController {

    private static final Logger log = LoggerFactory.getLogger(CourseController.class);

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ObjectProvider<UserComponent> userProvider;
    private final AuthorizationService authorizationService;
    private final CourseAttenderService courseAttenderService;

    public CourseController(CourseRepository courseRepository, UserRepository userRepository,
                            ObjectProvider<UserComponent> userProvider,
                            AuthorizationService authorizationService,
                            CourseAttenderService courseAttenderService) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.userProvider = userProvider;
        this.authorizationService = authorizationService;
        this.courseAttenderService = courseAttenderService;
    }

    @JsonView(SimpleCourseList.class)
    @GetMapping("/user/{id}")
    public ResponseEntity<Object> getCourses(@PathVariable String id) {

        log.info("CRUD operation: Getting all user courses");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        long idI = Long.parseLong(id);
        Set<Long> s = new HashSet<>();
        s.add(idI);
        Collection<User> users = userRepository.findAllById(s);
        if (users.isEmpty()) {
            return new ResponseEntity<>(new HashSet<>(), HttpStatus.OK);
        }
        Collection<Course> courses = courseRepository.findByAttenders(users);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @GetMapping("/course/{id}")
    public ResponseEntity<Object> getCourse(@PathVariable String id) {

        log.info("CRUD operation: Getting one course");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        Course course = courseRepository.findById(Long.parseLong(id)).orElse(null);
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<Object> newCourse(@RequestBody CourseDto courseDto) {

        log.info("CRUD operation: Adding new course");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        User userLogged = userProvider.getObject().getLoggedUser();

        Course course = new Course(courseDto.getTitle(), courseDto.getImage(), userLogged);
        if (courseDto.getCourseDetails() != null) {
            CourseDetails cd = new CourseDetails();
            cd.setInfo(courseDto.getCourseDetails().getInfo() != null ? courseDto.getCourseDetails().getInfo() : "");
            if (courseDto.getCourseDetails().getForum() != null) {
                cd.getForum().setActivated(courseDto.getCourseDetails().getForum().isActivated());
            }
            course.setCourseDetails(cd);
        }

        /*Saving the new course: Course entity is the owner of the relationships
        Course-Teacher, Course-User, Course-CourseDetails. Teacher, User and CourseDetails
        tables don't need to be updated (they will automatically be)*/
        course.getAttenders().add(userLogged);
        courseRepository.save(course);
        courseRepository.flush();

        course = courseRepository.findById(course.getId()).orElse(null);

        log.info("New course succesfully added: {}", course);

        return new ResponseEntity<>(course, HttpStatus.CREATED);
    }

    @PutMapping("/edit")
    public ResponseEntity<Object> modifyCourse(@RequestBody CourseDto courseDto) {

        log.info("CRUD operation: Updating course");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        Course c = courseRepository.findById(courseDto.getId()).orElse(null);
        ResponseEntity<Object> teacherAuthorized = authorizationService.checkTeacherAuthorization(c);
        if (teacherAuthorized != null) {
            return teacherAuthorized;
        }

        log.info("Updating course. Previous value: {}", c);

        //Modifying the course attributes
        c.setImage(courseDto.getImage());
        c.setTitle(courseDto.getTitle());
        if (courseDto.getCourseDetails() != null && courseDto.getCourseDetails().getInfo() != null) {
            c.getCourseDetails().setInfo(courseDto.getCourseDetails().getInfo());
        }
        //Saving the modified course
        courseRepository.save(c);

        log.info("Course succesfully updated. Modified value: {}", c);

        return new ResponseEntity<>(c, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{courseId}")
    public ResponseEntity<Object> deleteCourse(@PathVariable String courseId) {

        log.info("CRUD operation: Deleting course");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        Course c = courseRepository.findById(Long.parseLong(courseId)).orElse(null);
        ResponseEntity<Object> teacherAuthorized = authorizationService.checkTeacherAuthorization(c);
        if (teacherAuthorized != null) {
            return teacherAuthorized;
        }

        log.info("Deleting course: {}", c);

        courseRepository.delete(c);

        log.info("Course successfully deleted");

        return new ResponseEntity<>(c, HttpStatus.OK);
    }

    @PutMapping("/edit/add-attenders/course/{courseId}")
    public ResponseEntity<Object> addAttenders(
            @RequestBody String[] attenderEmails,
            @PathVariable String courseId) {

        log.info("CRUD operation: Adding attenders to course");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        Course c = courseRepository.findById(Long.parseLong(courseId)).orElse(null);
        ResponseEntity<Object> teacherAuthorized = authorizationService.checkTeacherAuthorization(c);
        if (teacherAuthorized != null) {
            return teacherAuthorized;
        }

        if (log.isInfoEnabled()) {
            log.info("Adding attenders {} to course {}", LogSanitizer.sanitize(Arrays.toString(attenderEmails)), LogSanitizer.sanitize(c));
        }

        Set<String> attenderEmailsValid = new HashSet<>();
        Set<String> attenderEmailsInvalid = new HashSet<>();
        Set<String> attenderEmailsNotRegistered = new HashSet<>();

        EmailValidator emailValidator = EmailValidator.getInstance();
        for (String attenderEmail : attenderEmails) {
            (emailValidator.isValid(attenderEmail) ? attenderEmailsValid : attenderEmailsInvalid).add(attenderEmail);
        }

        CourseAttenderService.AttenderUpdateResult result =
                courseAttenderService.updateAttenders(c, attenderEmailsValid, attenderEmailsNotRegistered);

        AddAttendersResponse customResponse = new AddAttendersResponse(
                result.added(), result.alreadyAdded(), attenderEmailsInvalid, attenderEmailsNotRegistered);

        if (log.isInfoEnabled()) {
            log.info("Attenders added: {} | Attenders already exist: {} | Emails not valid: {} | Emails valid but no registered: {}",
                    customResponse.attendersAdded(),
                    customResponse.attendersAlreadyAdded(),
                    customResponse.emailsInvalid(),
                    customResponse.emailsValidNotRegistered());
        }

        return new ResponseEntity<>(customResponse, HttpStatus.OK);
    }

    @PutMapping("/edit/delete-attenders")
    public ResponseEntity<Object> deleteAttenders(@RequestBody CourseDto courseDto) {

        log.info("CRUD operation: Deleting attender from course");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        Course c = courseRepository.findById(courseDto.getId()).orElse(null);
        ResponseEntity<Object> teacherAuthorized = authorizationService.checkTeacherAuthorization(c);
        if (teacherAuthorized != null) {
            return teacherAuthorized;
        }

        log.info("Deleting attender from course {}", c);

        Set<Long> attenderIdsToKeep = courseDto.getAttenderIds();

        //Modifying the course attenders
        Set<User> newAttenders = new HashSet<>();
        userRepository.findAllById(attenderIdsToKeep).forEach(newAttenders::add);
        c.setAttenders(newAttenders);
        //Saving the modified course
        courseRepository.save(c);
        return new ResponseEntity<>(c.getAttenders(), HttpStatus.OK);
    }

    private record AddAttendersResponse(Collection<User> attendersAdded, Collection<User> attendersAlreadyAdded,
                                        Collection<String> emailsInvalid, Collection<String> emailsValidNotRegistered) {
    }

}
