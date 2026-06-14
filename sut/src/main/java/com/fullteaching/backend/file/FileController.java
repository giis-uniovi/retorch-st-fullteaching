package com.fullteaching.backend.file;

import com.fullteaching.backend.comment.Comment;
import com.fullteaching.backend.comment.CommentRepository;
import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.course.CourseRepository;
import com.fullteaching.backend.filegroup.FileGroup;
import com.fullteaching.backend.filegroup.FileGroupRepository;
import com.fullteaching.backend.security.AuthorizationService;
import com.fullteaching.backend.user.User;
import com.fullteaching.backend.user.UserComponent;
import com.fullteaching.backend.user.UserRepository;
import com.fullteaching.backend.util.LogSanitizer;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

@RestController
@RequestMapping("/api-load-files")
public class FileController {
    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private static final String USER_DIR = System.getProperty("user.dir");
    public static final Path FILES_FOLDER = Paths.get(USER_DIR, "/assets/files");
    public static final Path VIDEOS_FOLDER = Paths.get(USER_DIR, "/assets/videos");
    public static final Path PICTURES_FOLDER = Paths.get(USER_DIR, "/assets/pictures");
    private static final String LOG_FILE_NAME = "File name: '{}'";
    private static final String LOG_CREATING_FOLDER = "Creating folder '{}'";
    private static final String LOG_THREAD_INTERRUPTED = "Thread interrupted while saving file";
    private final FileGroupRepository fileGroupRepository;
    private final FileRepository fileRepository;
    private final CourseRepository courseRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ObjectProvider<UserComponent> userProvider;
    private final AuthorizationService authorizationService;
    private final FileOperationsService fileOperationsService;
    private static final String HTTPS_PROTOCOL = "https://";
    @Value("${profile.stage}")
    private String profileStage;

    @Autowired
    public FileController(FileOperationsService fileOpServ, ObjectProvider<UserComponent> userComp, AuthorizationService authServ, UserRepository userRep, CommentRepository commentRepo, CourseRepository courseRepo, FileRepository fileRepo, FileGroupRepository fileGroupRepo) {
        this.fileOperationsService = fileOpServ;
        this.userProvider = userComp;
        this.authorizationService = authServ;
        this.userRepository = userRep;
        this.commentRepository = commentRepo;
        this.courseRepository = courseRepo;
        this.fileRepository = fileRepo;
        this.fileGroupRepository = fileGroupRepo;
    }

    @PostMapping(value = "/upload/course/{courseId}/file-group/{fileGroupId}")
    public ResponseEntity<Object> handleFileUpload(MultipartHttpServletRequest request,
                                                   @PathVariable String courseId, @PathVariable String fileGroupId)
            throws IOException {

        log.info("Uploading file...");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        long idCourse;
        long idFileGroup;
        try {
            idCourse = Long.parseLong(courseId);
            idFileGroup = Long.parseLong(fileGroupId);
        } catch (NumberFormatException e) {
            log.error("Course ID '{}' or FileGroup ID '{}' are not of type Long", courseId, fileGroupId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Course c = courseRepository.findById(idCourse).orElse(null);
        if (c == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ResponseEntity<Object> teacherAuthorized = authorizationService.checkAuthorization(c, c.getTeacher());
        if (teacherAuthorized != null) {
            return teacherAuthorized;
        }

        FileGroup fg = null;
        Iterator<String> i = request.getFileNames();
        while (i.hasNext()) {
            String name = i.next();
            log.info(LOG_FILE_NAME, name);
            MultipartFile file = request.getFile(name);
            if (file == null) continue;
            fg = processUploadedFile(file, idFileGroup);
        }

        if (fg == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        fileGroupRepository.save(fg);
        return new ResponseEntity<>(this.getRootFileGroup(fg), HttpStatus.CREATED);
    }

    private FileGroup processUploadedFile(MultipartFile file, long idFileGroup) throws IOException {
        String fileName = file.getOriginalFilename();
        if (log.isInfoEnabled()) {
            log.info("File full name: '{}'", LogSanitizer.sanitize(fileName));
        }

        if (file.isEmpty()) {
            log.error("The file is empty");
            throw new IllegalArgumentException("The file is empty");
        }

        if (!Files.exists(FILES_FOLDER)) {
            log.debug(LOG_CREATING_FOLDER, FILES_FOLDER);
            Files.createDirectories(FILES_FOLDER);
        }

        com.fullteaching.backend.file.File customFile = new com.fullteaching.backend.file.File(1, fileName);
        File uploadedFile = new File(FILES_FOLDER.toFile(), customFile.getNameIdent());
        file.transferTo(uploadedFile);

        if (this.isProductionStage()) {
            saveFileProduction(customFile, uploadedFile);
        } else {
            customFile.setLink(uploadedFile.getPath());
        }

        FileGroup fg = fileGroupRepository.findById(idFileGroup).orElse(null);
        assert fg != null;
        fg.getFiles().add(customFile);
        fg.updateFileIndexOrder();
        if (log.isInfoEnabled()) {
            log.info("File succesfully uploaded to path '{}'", LogSanitizer.sanitize(uploadedFile.getPath()));
        }
        return fg;
    }

    private void saveFileProduction(com.fullteaching.backend.file.File customFile, File uploadedFile) {
        try {
            fileOperationsService.productionFileSaver(customFile.getNameIdent(), "files", uploadedFile);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(LOG_THREAD_INTERRUPTED, e);
            fileOperationsService.deleteLocalFile(uploadedFile.getName(), FILES_FOLDER);
        }
        customFile.setLink(HTTPS_PROTOCOL + fileOperationsService.getBucketAWS() + ".s3.amazonaws.com/files/"
                + customFile.getNameIdent());
        fileOperationsService.deleteLocalFile(uploadedFile.getName(), FILES_FOLDER);
    }

    @RequestMapping("/course/{courseId}/download/{fileId}")
    public void handleFileDownload(@PathVariable String fileId, @PathVariable String courseId,
                                   HttpServletResponse response) throws IOException {

        log.info("Downloading file...");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            response.sendError(401, "Not logged");
            return;
        }

        long idCourse;
        long idFile;
        try {
            idCourse = Long.parseLong(courseId);
            idFile = Long.parseLong(fileId);
        } catch (NumberFormatException e) {
            log.error("Course ID '{}' or File ID '{}' are not of type Long", courseId, fileId);
            response.sendError(422, "Invalid format");
            return;
        }

        Course c = courseRepository.findById(idCourse).orElse(null);
        if (c == null) {
            response.sendError(404, "Course not found");
            return;
        }

        ResponseEntity<Object> userAuthorized = authorizationService.checkAuthorizationUsers(c, c.getAttenders());
        if (userAuthorized != null) {
            response.sendError(401, "Unauthorized");
            return;
        }

        com.fullteaching.backend.file.File f = fileRepository.findById(idFile).orElse(null);
        if (f == null) {
            return;
        }

        log.info(LOG_FILE_NAME, f.getName());

        if (this.isProductionStage()) {
            fileOperationsService.productionFileDownloader(f.getNameIdent(), response);
        } else {
            downloadFileDev(f, response);
        }
    }

    private void downloadFileDev(com.fullteaching.backend.file.File f, HttpServletResponse response) throws IOException {
        Path file = FILES_FOLDER.resolve(f.getNameIdent());
        if (Files.exists(file)) {
            try (InputStream is = new FileInputStream(file.toString())) {
                response.setContentType(MimeTypes.getMimeType(f.getFileExtension()));
                StreamUtils.copy(is, response.getOutputStream());
                response.flushBuffer();
                log.info("File '{}' succesfully downloaded", f.getName());
            } catch (IOException ex) {
                throw new IOException("IOError writing file to output stream");
            }
        } else {
            log.error("File '{}' does not exist and cannot be downloaded", f.getName());
            response.sendError(404, "File" + f.getNameIdent() + "(" + file.toAbsolutePath() + ") does not exist");
        }
    }

    @PostMapping(value = "/upload/picture/{userId}")
    public ResponseEntity<Object> handlePictureUpload(MultipartHttpServletRequest request,
                                                      @PathVariable String userId) throws IOException {

        log.info("Uploading picture...");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        long idUser;
        try {
            idUser = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            log.error("User ID '{}' is not of type Long", userId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User u = userRepository.findById(idUser).orElse(null);

        ResponseEntity<Object> userAuthorized = authorizationService.checkAuthorization(u, this.userProvider.getObject().getLoggedUser());
        if (userAuthorized != null) {
            return userAuthorized;
        }

        Iterator<String> i = request.getFileNames();
        while (i.hasNext()) {
            String name = i.next();
            log.info(LOG_FILE_NAME, name);
            MultipartFile file = request.getFile(name);
            assert file != null;
            processPictureFile(file, u);
        }

        assert u != null;
        return new ResponseEntity<>(u.getPicture(), HttpStatus.CREATED);
    }

    private void processPictureFile(MultipartFile file, User u) throws IOException {
        if (log.isInfoEnabled()) {
            log.info("File original name: '{}'", LogSanitizer.sanitize(file.getOriginalFilename()));
        }

        if (file.isEmpty()) {
            log.error("File is empty");
            throw new IOException("The picture is empty");
        }

        if (!Files.exists(PICTURES_FOLDER)) {
            log.debug(LOG_CREATING_FOLDER, PICTURES_FOLDER);
            Files.createDirectories(PICTURES_FOLDER);
        }

        String encodedName = fileOperationsService.getEncodedPictureName(file.getOriginalFilename());
        File uploadedPicture = new File(PICTURES_FOLDER.toFile(), encodedName);
        file.transferTo(uploadedPicture);

        if (this.isProductionStage()) {
            savePictureProduction(encodedName, uploadedPicture, u);
        } else {
            savePictureDevelopment(uploadedPicture, u);
        }

        userRepository.save(u);
        this.userProvider.getObject().getLoggedUser().setPicture(u.getPicture());
    }

    private void savePictureProduction(String encodedName, File uploadedPicture, User u) {
        try {
            fileOperationsService.productionFileSaver(encodedName, "pictures", uploadedPicture);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(LOG_THREAD_INTERRUPTED, e);
            fileOperationsService.deleteLocalFile(uploadedPicture.getName(), PICTURES_FOLDER);
        }
        fileOperationsService.deleteLocalFile(uploadedPicture.getName(), PICTURES_FOLDER);
        fileOperationsService.productionFileDeletion(
                fileOperationsService.getFileNameFromURL(u.getPicture()), "/pictures");
        u.setPicture(HTTPS_PROTOCOL + fileOperationsService.getBucketAWS() + ".s3.amazonaws.com/pictures/" + encodedName);
    }

    private void savePictureDevelopment(File uploadedPicture, User u) {
        fileOperationsService.deleteLocalFile(fileOperationsService.getFileNameFromURL(u.getPicture()),
                PICTURES_FOLDER);
        u.setPicture("/assets/pictures/" + uploadedPicture.getName());
        if (log.isInfoEnabled()) {
            log.info("Picture succesfully uploaded to path '{}'", LogSanitizer.sanitize(uploadedPicture.getPath()));
        }
    }

    @PostMapping(value = "/upload/course/{courseId}/comment/{commentId}")
    public ResponseEntity<Object> handleVideoMessageUpload(MultipartHttpServletRequest request,
                                                           @PathVariable String courseId, @PathVariable String commentId)
            throws IOException {

        log.info("Uploading video message...");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        long idCourse;
        long idComment;
        try {
            idCourse = Long.parseLong(courseId);
            idComment = Long.parseLong(commentId);
        } catch (NumberFormatException e) {
            log.error("Course ID '{}' or Comment ID '{}' are not of type Long", courseId, commentId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Course c = courseRepository.findById(idCourse).orElse(null);
        if (c == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Comment comment = commentRepository.findById(idComment).orElse(null);

        ResponseEntity<Object> userAuthorized = authorizationService.checkAuthorizationUsers(c, c.getAttenders());
        if (userAuthorized != null) {
            return userAuthorized;
        }

        if (comment == null) {
            log.error("Comment with id '{}' doesn't exist", idComment);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        userAuthorized = authorizationService.checkAuthorization(comment, comment.getUser());
        if (userAuthorized != null) {
            return userAuthorized;
        }

        Iterator<String> i = request.getFileNames();
        while (i.hasNext()) {
            String name = i.next();
            log.info("Video file name: '{}'", name);
            MultipartFile file = request.getFile(name);
            assert file != null;
            processVideoFile(file, comment, idComment, courseId);
        }

        commentRepository.save(comment);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    private void processVideoFile(MultipartFile file, Comment comment, long idComment, String courseId) throws IOException {
        if (log.isInfoEnabled()) {
            log.info("Video file full name: '{}'", LogSanitizer.sanitize(file.getOriginalFilename()));
        }

        if (file.isEmpty()) {
            log.error("The video file is empty");
            throw new IOException("The video file is empty");
        }

        if (!Files.exists(VIDEOS_FOLDER)) {
            log.debug(LOG_CREATING_FOLDER, VIDEOS_FOLDER);
            Files.createDirectories(VIDEOS_FOLDER);
        }

        String finalName = "video-comment-" + idComment + ".webm";
        log.info("Video file final name: '{}'", finalName);
        File uploadedFile = new File(VIDEOS_FOLDER.toFile(), finalName);
        file.transferTo(uploadedFile);

        if (this.isProductionStage()) {
            saveVideoProduction(finalName, uploadedFile, comment);
        } else {
            saveVideoDevelopment(finalName, courseId, comment);
        }
        log.info("File succesfully uploaded to path '{}'", uploadedFile.getPath());
    }

    private void saveVideoProduction(String finalName, File uploadedFile, Comment comment) {
        try {
            fileOperationsService.productionFileSaver(finalName, "videos", uploadedFile);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(LOG_THREAD_INTERRUPTED, e);
            fileOperationsService.deleteLocalFile(uploadedFile.getName(), VIDEOS_FOLDER);
        }
        comment.setVideourl(HTTPS_PROTOCOL + fileOperationsService.getBucketAWS() + ".s3.amazonaws.com/videos/" + finalName);
        fileOperationsService.deleteLocalFile(uploadedFile.getName(), VIDEOS_FOLDER);
    }

    private void saveVideoDevelopment(String finalName, String courseId, Comment comment) {
        comment.setVideourl("/assets/videos/" + courseId + "/" + finalName);
    }

    // Method to get the root FileGroup of a FileGroup tree structure, given a FileGroup
    private FileGroup getRootFileGroup(FileGroup fg) {
        while (fg.getFileGroupParent() != null) {
            fg = fg.getFileGroupParent();
        }
        return fg;
    }

    private boolean isProductionStage() {
        return this.profileStage.equals("prod");
    }

}