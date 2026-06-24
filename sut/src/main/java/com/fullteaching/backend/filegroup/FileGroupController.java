package com.fullteaching.backend.filegroup;

import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.course.CourseRepository;
import com.fullteaching.backend.coursedetails.CourseDetails;
import com.fullteaching.backend.coursedetails.CourseDetailsRepository;
import com.fullteaching.backend.file.File;
import com.fullteaching.backend.file.FileController;
import com.fullteaching.backend.file.FileDto;
import com.fullteaching.backend.file.FileOperationsService;
import com.fullteaching.backend.file.FileRepository;
import com.fullteaching.backend.security.AuthorizationService;
import com.fullteaching.backend.util.LogSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api-files")
public class FileGroupController {

    private static final Logger log = LoggerFactory.getLogger(FileGroupController.class);

    private final FileGroupRepository fileGroupRepository;
    private final FileRepository fileRepository;
    private final CourseRepository courseRepository;
    private final CourseDetailsRepository courseDetailsRepository;
    private final AuthorizationService authorizationService;
    private final FileOperationsService fileOperationsService;

    @Value("${profile.stage}")
    private String profileStage;

    @Autowired
    public FileGroupController(FileOperationsService fileOperationsServ, AuthorizationService authServ, CourseDetailsRepository courseDetRepo, CourseRepository courseRepo, FileRepository fileRepo, FileGroupRepository fileGroupRepo) {
        this.fileOperationsService = fileOperationsServ;
        this.authorizationService = authServ;
        this.courseDetailsRepository = courseDetRepo;
        this.courseRepository = courseRepo;
        this.fileRepository = fileRepo;
        this.fileGroupRepository = fileGroupRepo;
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<Object> newFileGroup(@RequestBody FileGroupDto fileGroupDto, @PathVariable(value = "id") String courseDetailsId) {

        log.info("CRUD operation: Adding new file group");

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

        assert cd != null;
        ResponseEntity<Object> teacherAuthorized = authorizationService.checkAuthorization(cd, cd.getCourse().getTeacher());
        if (teacherAuthorized != null) { // If the user is not the teacher of the course
            return teacherAuthorized;
        }

        //fileGroup is a root FileGroup
        if (fileGroupDto.getFileGroupParentId() == null) {
            return addRootFileGroup(fileGroupDto, cd);
        }

        //fileGroup is a child of an existing FileGroup
        return addChildFileGroup(fileGroupDto, idI);
    }

    private ResponseEntity<Object> addRootFileGroup(FileGroupDto fileGroupDto, CourseDetails cd) {
        FileGroup fileGroup = new FileGroup(fileGroupDto.getTitle());
        cd.getFiles().add(fileGroup);
		/*Saving the modified courseDetails: Cascade relationship between courseDetails
		  and fileGroups will add the new fileGroup to FileGroupRepository*/
        courseDetailsRepository.save(cd);

        if (log.isInfoEnabled()) {
            log.info("New root file group succesfully added: {}", LogSanitizer.sanitize(fileGroup));
        }

        /*Entire courseDetails is returned*/
        return new ResponseEntity<>(cd, HttpStatus.CREATED);
    }

    private ResponseEntity<Object> addChildFileGroup(FileGroupDto fileGroupDto, long courseDetailsId) {
        FileGroup fParent = fileGroupRepository.findById(fileGroupDto.getFileGroupParentId()).orElse(null);
        if (fParent == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        FileGroup fileGroup = new FileGroup(fileGroupDto.getTitle(), fParent);
        fParent.getFileGroups().add(fileGroup);
		/*Saving the modified parent FileGroup: Cascade relationship between FileGroup and
		 its FileGroup children will add the new fileGroup to FileGroupRepository*/
        fileGroupRepository.save(fParent);
        CourseDetails cd2 = courseDetailsRepository.findById(courseDetailsId).orElse(null);

        if (log.isInfoEnabled()) {
            log.info("New file sub-group succesfully added: {}", LogSanitizer.sanitize(fileGroup));
        }

        /*Entire courseDetails is returned*/
        return new ResponseEntity<>(cd2, HttpStatus.CREATED);
    }


    @PutMapping(value = "/edit/file-group/course/{courseId}")
    public ResponseEntity<Object> modifyFileGroup(@RequestBody FileGroupDto fileGroupDto, @PathVariable String courseId) {

        log.info("CRUD operation: Updating filegroup");

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

        assert c != null;
        ResponseEntity<Object> teacherAuthorized = authorizationService.checkAuthorization(c, c.getTeacher());
        if (teacherAuthorized != null) { // If the user is not the teacher of the course
            return teacherAuthorized;
        } else {

            FileGroup fg = fileGroupRepository.findById(fileGroupDto.getId()).orElse(null);

            if (fg != null) {

                log.info("Updating filegroup. Previous value: {}", fg);

                fg.setTitle(fileGroupDto.getTitle());
                fileGroupRepository.save(fg);

                log.info("FileGroup succesfully updated. Modified value: {}", fg);

                //Returning the root FileGroup of the added file
                return new ResponseEntity<>(this.getRootFileGroup(fg), HttpStatus.OK);
            } else {
                log.error("FileGroup with id '{}' doesn't exist", fileGroupDto.getId());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
    }


    @DeleteMapping(value = "/delete/file-group/{fileGroupId}/course/{courseId}")
    public ResponseEntity<Object> deleteFileGroup(
            @PathVariable String fileGroupId,
            @PathVariable String courseId
    ) {

        log.info("CRUD operation: Deleting filegroup");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        long idFileGroup = -1;
        long idCourse = -1;
        try {
            idFileGroup = Long.parseLong(fileGroupId);
            idCourse = Long.parseLong(courseId);
        } catch (NumberFormatException e) {
            log.error("Course ID '{}' or FileGroup ID '{}' are not of type Long", courseId, fileGroupId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        Course c = courseRepository.findById(idCourse).orElse(null);

        assert c != null;
        ResponseEntity<Object> teacherAuthorized = authorizationService.checkAuthorization(c, c.getTeacher());
        if (teacherAuthorized != null) { // If the user is not the teacher of the course
            return teacherAuthorized;
        }

        FileGroup fg = fileGroupRepository.findById(idFileGroup).orElse(null);

        if (fg != null) {

            log.info("Deleting filegroup: {}", fg);

            if (this.isProductionStage()) {
                //Removing all the S3 stored files of the tree structure...
                for (File f : fg.getFiles()) {
                    fileOperationsService.deleteRemoteFile(f.getNameIdent(), "/files");
                }
                fileOperationsService.recursiveS3StoredFileDeletion(fg.getFileGroups());
            } else {
                //Removing all the locally stored files of the tree structure...
                for (File f : fg.getFiles()) {
                    fileOperationsService.deleteLocalFile(f.getNameIdent(), FileController.FILES_FOLDER);
                }
                fileOperationsService.recursiveLocallyStoredFileDeletion(fg.getFileGroups());
            }

            //It is necessary to remove the FileGroup from the CourseDetails that owns it
            CourseDetails cd = c.getCourseDetails();
            cd.getFiles().remove(fg);
            courseDetailsRepository.save(cd);
            fileGroupRepository.delete(fg);

            log.info("Filegroup successfully deleted");

            return new ResponseEntity<>(fg, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping(value = "/edit/file-order/course/{courseId}/file/{fileId}/from/{sourceID}/to/{targetId}/pos/{position}")
    public ResponseEntity<Object> editFileOrder(
            @PathVariable String courseId,
            @PathVariable String fileId,
            @PathVariable(value = "sourceID") String sourceId,
            @PathVariable String targetId,
            @PathVariable String position
    ) {

        log.info("CRUD operation: Editing file order in filegroup");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        long idCourse = -1;
        long idFile = -1;
        long idSource = -1;
        long idTarget = -1;
        int pos = 0;
        try {
            idCourse = Long.parseLong(courseId);
            idFile = Long.parseLong(fileId);
            idSource = Long.parseLong(sourceId);
            idTarget = Long.parseLong(targetId);
            pos = Integer.parseInt(position);
        } catch (NumberFormatException e) {
            log.error("Course ID '{}' or File ID '{}' or source ID '{}' or target ID '{}' are not of type Long; or position {} is not of type Integer",
                    courseId, fileId, sourceId, targetId, pos);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Course c = courseRepository.findById(idCourse).orElse(null);

        assert c != null;
        ResponseEntity<Object> teacherAuthorized = authorizationService.checkAuthorization(c, c.getTeacher());
        if (teacherAuthorized != null) { // If the user is not the teacher of the course
            return teacherAuthorized;
        } else {

            FileGroup sourceFg = fileGroupRepository.findById(idSource).orElse(null);
            FileGroup targetFg = fileGroupRepository.findById(idTarget).orElse(null);
            File fileMoved = fileRepository.findById(idFile).orElse(null);

            log.info("Moving file {} from filegroup {} to filegroup {} into position {}", fileMoved, sourceFg, targetFg, pos);

            assert sourceFg != null;
            assert targetFg != null;
            sourceFg.getFiles().remove(fileMoved);
            targetFg.getFiles().add(pos, fileMoved);

            sourceFg.updateFileIndexOrder();
            targetFg.updateFileIndexOrder();

            List<FileGroup> l = new ArrayList<>();
            l.add(sourceFg);
            l.add(targetFg);
            fileGroupRepository.saveAll(l);

            log.info("File order succesfully updated");

            //Returning the FileGroups of the course
            return new ResponseEntity<>(c.getCourseDetails().getFiles(), HttpStatus.OK);
        }
    }


    @PutMapping(value = "/edit/file/file-group/{fileGroupId}/course/{courseId}")
    public ResponseEntity<Object> modifyFile(
            @RequestBody FileDto fileDto,
            @PathVariable String fileGroupId,
            @PathVariable String courseId) {

        log.info("CRUD operation: Updating file");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        long idFileGroup = -1;
        long idCourse = -1;
        try {
            idFileGroup = Long.parseLong(fileGroupId);
            idCourse = Long.parseLong(courseId);
        } catch (NumberFormatException e) {
            log.error("Course ID '{}' or FileGroup ID '{}' are not of type Long", courseId, fileGroupId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Course c = courseRepository.findById(idCourse).orElse(null);

        assert c != null;
        ResponseEntity<Object> teacherAuthorized = authorizationService.checkAuthorization(c, c.getTeacher());
        if (teacherAuthorized != null) { // If the user is not the teacher of the course
            return teacherAuthorized;
        } else {

            FileGroup fg = fileGroupRepository.findById(idFileGroup).orElse(null);

            if (fg != null) {
                for (int i = 0; i < fg.getFiles().size(); i++) {
                    if (fg.getFiles().get(i).getId() == fileDto.getId()) {

                        log.info("Updating file. Previous value: {}", fg.getFiles().get(i));

                        fg.getFiles().get(i).setName(fileDto.getName());
                        fileGroupRepository.save(fg);

                        log.info("File succesfully updated. Modified value: {}", fg.getFiles().get(i));

                        //Returning the root FileGroup of the added file
                        return new ResponseEntity<>(this.getRootFileGroup(fg), HttpStatus.OK);
                    }
                }

                log.error("File not found");
            } else {
                log.error("FileGroup not found");
            }
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
    }


    @DeleteMapping(value = "/delete/file/{fileId}/file-group/{fileGroupId}/course/{courseId}")
    public ResponseEntity<Object> deleteFile(
            @PathVariable String fileId,
            @PathVariable String fileGroupId,
            @PathVariable String courseId
    ) {

        log.info("CRUD operation: Deleting file");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        long idFile = -1;
        long idFileGroup = -1;
        long idCourse = -1;
        try {
            idFile = Long.parseLong(fileId);
            idFileGroup = Long.parseLong(fileGroupId);
            idCourse = Long.parseLong(courseId);
        } catch (NumberFormatException e) {
            log.error("Course ID '{}' or FileGroup ID '{}' or File ID '{}' are not of type Long", courseId, fileGroupId, fileId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Course c = courseRepository.findById(idCourse).orElse(null);

        assert c != null;
        ResponseEntity<Object> teacherAuthorized = authorizationService.checkAuthorization(c, c.getTeacher());
        if (teacherAuthorized != null) { // If the user is not the teacher of the course
            return teacherAuthorized;
        }

        FileGroup fg = fileGroupRepository.findById(idFileGroup).orElse(null);

        if (fg != null) {
            File file = findFileInGroup(fg, idFile);
            if (file != null) {

                log.info("Deleting file: {}", file);

                if (this.isProductionStage()) {
                    //ONLY ON PRODUCTION
                    //Deleting S3 stored file...
                    fileOperationsService.deleteRemoteFile(file.getNameIdent(), "/files");
                    //ONLY ON PRODUCTION
                } else {
                    //ONLY ON DEVELOPMENT
                    //Deleting locally stored file...
                    fileOperationsService.deleteLocalFile(file.getNameIdent(), FileController.FILES_FOLDER);
                    //ONLY ON DEVELOPMENT
                }

                fg.getFiles().remove(file);
                fg.updateFileIndexOrder();

                fileGroupRepository.save(fg);

                log.info("File successfully deleted");

                return new ResponseEntity<>(file, HttpStatus.OK);

            } else {
                //The file to delete does not exist or does not have a fileGroup parent
                fileRepository.deleteById(idFile);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            //The fileGroup parent does not exist
            fileRepository.deleteById(idFile);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private File findFileInGroup(FileGroup fg, long idFile) {
        for (File f : fg.getFiles()) {
            if (f.getId() == idFile) {
                return f;
            }
        }
        return null;
    }

    //Method to get the root FileGroup of a FileGroup tree structure, given a FileGroup
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