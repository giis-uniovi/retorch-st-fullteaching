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

        long idI = Long.parseLong(courseDetailsId);
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

        Course c = courseRepository.findById(Long.parseLong(courseId)).orElse(null);
        ResponseEntity<Object> teacherAuthorized = authorizationService.checkTeacherAuthorization(c);
        if (teacherAuthorized != null) {
            return teacherAuthorized;
        }

        FileGroup fg = fileGroupRepository.findById(fileGroupDto.getId()).orElse(null);
        if (fg != null) {
            log.info("Updating filegroup. Previous value: {}", fg);
            fg.setTitle(fileGroupDto.getTitle());
            fileGroupRepository.save(fg);
            log.info("FileGroup succesfully updated. Modified value: {}", fg);
            return new ResponseEntity<>(fg.findRoot(), HttpStatus.OK);
        }
        log.error("FileGroup with id '{}' doesn't exist", fileGroupDto.getId());
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @DeleteMapping(value = "/delete/file-group/{fileGroupId}/course/{courseId}")
    public ResponseEntity<Object> deleteFileGroup(
            @PathVariable String fileGroupId,
            @PathVariable String courseId
    ) {

        log.info("CRUD operation: Deleting filegroup");

        FileGroupAuth auth = authorizeAndFetchGroup(fileGroupId, courseId);
        if (auth.error() != null) return auth.error();
        Course c = auth.course();
        FileGroup fg = auth.fileGroup();

        if (fg != null) {

            log.info("Deleting filegroup: {}", fg);

            if (fileOperationsService.isProductionStage()) {
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

        long idFile = Long.parseLong(fileId);
        long idSource = Long.parseLong(sourceId);
        long idTarget = Long.parseLong(targetId);
        int pos = Integer.parseInt(position);
        Course c = courseRepository.findById(Long.parseLong(courseId)).orElse(null);
        ResponseEntity<Object> teacherAuthorized = authorizationService.checkTeacherAuthorization(c);
        if (teacherAuthorized != null) {
            return teacherAuthorized;
        }

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

        fileGroupRepository.saveAll(List.of(sourceFg, targetFg));

        log.info("File order succesfully updated");

        return new ResponseEntity<>(c.getCourseDetails().getFiles(), HttpStatus.OK);
    }


    @PutMapping(value = "/edit/file/file-group/{fileGroupId}/course/{courseId}")
    public ResponseEntity<Object> modifyFile(
            @RequestBody FileDto fileDto,
            @PathVariable String fileGroupId,
            @PathVariable String courseId) {

        log.info("CRUD operation: Updating file");

        FileGroupAuth auth = authorizeAndFetchGroup(fileGroupId, courseId);
        if (auth.error() != null) return auth.error();
        FileGroup fg = auth.fileGroup();
        if (fg != null) {
            for (int i = 0; i < fg.getFiles().size(); i++) {
                if (fg.getFiles().get(i).getId() == fileDto.getId()) {
                    log.info("Updating file. Previous value: {}", fg.getFiles().get(i));
                    fg.getFiles().get(i).setName(fileDto.getName());
                    fileGroupRepository.save(fg);
                    log.info("File succesfully updated. Modified value: {}", fg.getFiles().get(i));
                    return new ResponseEntity<>(fg.findRoot(), HttpStatus.OK);
                }
            }
            log.error("File not found");
        } else {
            log.error("FileGroup not found");
        }
        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
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

        long idFile = Long.parseLong(fileId);
        long idFileGroup = Long.parseLong(fileGroupId);
        Course c = courseRepository.findById(Long.parseLong(courseId)).orElse(null);
        ResponseEntity<Object> teacherAuthorized = authorizationService.checkTeacherAuthorization(c);
        if (teacherAuthorized != null) {
            return teacherAuthorized;
        }

        FileGroup fg = fileGroupRepository.findById(idFileGroup).orElse(null);

        if (fg != null) {
            File file = findFileInGroup(fg, idFile);
            if (file != null) {

                log.info("Deleting file: {}", file);

                if (fileOperationsService.isProductionStage()) {
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

    private record FileGroupAuth(ResponseEntity<Object> error, Course course, FileGroup fileGroup) {}

    /** Combines login check, teacher authorization and FileGroup fetch for course/{courseId} endpoints. */
    private FileGroupAuth authorizeAndFetchGroup(String fileGroupId, String courseId) {
        ResponseEntity<Object> logged = authorizationService.checkBackendLogged();
        if (logged != null) return new FileGroupAuth(logged, null, null);

        Course c = courseRepository.findById(Long.parseLong(courseId)).orElse(null);
        ResponseEntity<Object> authz = authorizationService.checkTeacherAuthorization(c);
        if (authz != null) return new FileGroupAuth(authz, null, null);

        FileGroup fg = fileGroupRepository.findById(Long.parseLong(fileGroupId)).orElse(null);
        return new FileGroupAuth(null, c, fg);
    }

    private File findFileInGroup(FileGroup fg, long idFile) {
        for (File f : fg.getFiles()) {
            if (f.getId() == idFile) {
                return f;
            }
        }
        return null;
    }

}