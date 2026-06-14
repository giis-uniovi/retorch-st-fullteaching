package com.fullteaching.backend.filereader;

import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.course.CourseRepository;
import com.fullteaching.backend.file.FileController;
import com.fullteaching.backend.file.FileOperationsService;
import com.fullteaching.backend.security.AuthorizationService;
import com.fullteaching.backend.user.User;
import com.fullteaching.backend.user.UserRepository;
import com.fullteaching.backend.util.LogSanitizer;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@RestController
@RequestMapping("/api-file-reader")
public class FileReaderController {

    private static final Logger log = LoggerFactory.getLogger(FileReaderController.class);
    private final FileReader fileReader = new FileReader();

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;
    private final FileOperationsService fileOperationsService;

    @Autowired
    public FileReaderController(CourseRepository courseRepo,UserRepository userRepo,AuthorizationService authService,FileOperationsService fileOpService){
        this.courseRepository=courseRepo;
        this.userRepository=userRepo;
        this.authorizationService=authService;
        this.fileOperationsService=fileOpService;
    }

    @PostMapping(value = "/upload/course/{courseId}")
    public ResponseEntity<Object> handleFileReaderUpload(MultipartHttpServletRequest request,
                                                         @PathVariable String courseId) throws IOException {

        log.info("Adding attenders from file");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        long idCourse = -1;
        try {
            idCourse = Long.parseLong(courseId);
        } catch (NumberFormatException e) {
            log.error("Course ID '{}' is not of type Long", courseId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Course c = courseRepository.findById(idCourse).orElse(null);
        if (c == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ResponseEntity<Object> teacherAuthorized = authorizationService.checkAuthorization(c, c.getTeacher());
        if (teacherAuthorized != null) { // If the user is not the teacher of the course
            return teacherAuthorized;
        }

        Iterator<String> i = request.getFileNames();
        while (i.hasNext()) {
            String name = i.next();
            MultipartFile file = request.getFile(name);

            assert file != null;
            if (file.isEmpty()) {
                log.error("The file is empty");
                throw new IOException("The file is empty");
            }

            if (!Files.exists(FileController.FILES_FOLDER)) {
                log.debug("Creating local directory '{}'", FileController.FILES_FOLDER);
                Files.createDirectories(FileController.FILES_FOLDER);
            }

            String fileName = sanitizeFileName(file.getOriginalFilename());
            File uploadedFile = new File(FileController.FILES_FOLDER.toFile(), fileName);

            file.transferTo(uploadedFile);

            AddAttendersByFileResponse response = null;

            try {
                if (log.isInfoEnabled()) {
                    log.info("Parsing file '{}'", LogSanitizer.sanitize(fileName));
                }
                response = this.addAttendersFromFile(c,
                        this.fileReader.parseToPlainText(uploadedFile));
            } catch (Exception e) {
                log.error("Exception while parsing the file: {}", e.getMessage());
                fileOperationsService.deleteLocalFile(uploadedFile.getName(), FileController.FILES_FOLDER);
            }

            log.info("File successfully parsed. Result: {}", response);

            fileOperationsService.deleteLocalFile(uploadedFile.getName(), FileController.FILES_FOLDER);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        log.error("Empty file request");
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // Strips any directory components from a user-supplied file name to prevent path traversal
    private String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return null;
        }
        String normalized = fileName.replace('\\', '/');
        return normalized.substring(normalized.lastIndexOf('/') + 1);
    }

    private AddAttendersByFileResponse addAttendersFromFile(Course c, String s) {

        String[] stringArray = s.split("\\s");
        List<String> stringList = new ArrayList<>(Arrays.asList(stringArray));
        stringList.removeAll(Arrays.asList("", null));

        // Strings with a valid email format
        Set<String> attenderEmailsValid = new HashSet<>();
        // Strings with a valid email format but no registered in the application
        Set<String> attenderEmailsNotRegistered = new HashSet<>();

        EmailValidator emailValidator = EmailValidator.getInstance();

        // Getting all the emails in the document and storing them in a String set
        for (String word : stringList) {
            if (emailValidator.isValid(word)) {
                attenderEmailsValid.add(word);
            }
        }

        Collection<User> newPossibleAttenders = userRepository.findByNameIn(attenderEmailsValid);
        Collection<User> newAddedAttenders = new HashSet<>();
        Collection<User> alreadyAddedAttenders = new HashSet<>();

        for (String email : attenderEmailsValid) {
            if (!this.userListContainsEmail(newPossibleAttenders, email)) {
                attenderEmailsNotRegistered.add(email);
            }
        }

        for (User attender : newPossibleAttenders) {
            boolean newAtt = true;
            if (!attender.getCourses().contains(c))
                attender.getCourses().add(c);
            else
                newAtt = false;
            if (!c.getAttenders().contains(attender))
                c.getAttenders().add(attender);
            else
                newAtt = false;
            (newAtt ? newAddedAttenders : alreadyAddedAttenders).add(attender);
        }

        // Saving the attenders (all of them, just in case a field of the bidirectional
        // relationship is missing in a Course or a User)
        userRepository.saveAll(newPossibleAttenders);
        // Saving the modified course
        courseRepository.save(c);

        AddAttendersByFileResponse customResponse = new AddAttendersByFileResponse();
        customResponse.attendersAdded = newAddedAttenders;
        customResponse.attendersAlreadyAdded = alreadyAddedAttenders;
        customResponse.emailsValidNotRegistered = attenderEmailsNotRegistered;

        return customResponse;
    }

    // Checks if a User collection contains a user with certain email
    private boolean userListContainsEmail(Collection<User> users, String email) {
        boolean isContained = false;
        for (User u : users) {
            if (u.getName().equals(email)) {
                isContained = true;
                break;
            }
        }
        return isContained;
    }

    private static class AddAttendersByFileResponse {
        private Collection<User> attendersAdded;
        private Collection<User> attendersAlreadyAdded;
        private Collection<String> emailsValidNotRegistered;

        @Override
        public String toString() {
            return "[#Attenders added: " + attendersAdded.size() + ", #Attenders already added: "
                    + attendersAlreadyAdded.size() + ", #Emails valid but not registered: "
                    + emailsValidNotRegistered.size() + "]";
        }
    }

}
