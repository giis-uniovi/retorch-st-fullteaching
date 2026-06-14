package com.fullteaching.backend.comment;

import com.fullteaching.backend.coursedetails.CourseDetails;
import com.fullteaching.backend.coursedetails.CourseDetailsRepository;
import com.fullteaching.backend.entry.Entry;
import com.fullteaching.backend.entry.EntryRepository;
import com.fullteaching.backend.entry.NewEntryCommentResponse;
import com.fullteaching.backend.security.AuthorizationService;
import com.fullteaching.backend.user.User;
import com.fullteaching.backend.user.UserComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-comments")
public class CommentController {

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    private final EntryRepository entryRepository;
    private final CommentRepository commentRepository;
    private final CourseDetailsRepository courseDetailsRepository;
    private final AuthorizationService authorizationService;
    private final ObjectProvider<UserComponent> userProvider;


    @Autowired
    public CommentController(AuthorizationService authService, ObjectProvider<UserComponent> userComp, CourseDetailsRepository courseDetRep, CommentRepository commentRep, EntryRepository entryRepo) {
        this.authorizationService = authService;
        this.courseDetailsRepository = courseDetRep;
        this.commentRepository = commentRep;
        this.entryRepository = entryRepo;
        this.userProvider = userComp;
    }

    @PostMapping(value = "/entry/{entryId}/forum/{courseDetailsId}")
    public ResponseEntity<Object> newComment(
            @RequestBody CommentDto commentDto,
            @PathVariable String entryId,
            @PathVariable String courseDetailsId
    ) {

        log.info("CRUD operation: Adding new comment");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }


        long idEntry = -1;
        long idCourseDetails = -1;
        try {
            idEntry = Long.parseLong(entryId);
            idCourseDetails = Long.parseLong(courseDetailsId);
        } catch (NumberFormatException e) {
            log.error("Entry ID '{}' or CourseDetails ID '{}' are not of type Long", entryId, courseDetailsId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        CourseDetails cd = courseDetailsRepository.findById(idCourseDetails).orElse(null);
        if (cd == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ResponseEntity<Object> userAuthorized = authorizationService.checkAuthorizationUsers(cd, cd.getCourse().getAttenders());
        if (userAuthorized != null) { // If the user is not an attender of the course
            return userAuthorized;
        }

        User userLogged = userProvider.getObject().getLoggedUser();

        //The comment is a root comment
        if (commentDto.getCommentParent() == null) {
            log.info("Adding new root comment");
            Entry entry = entryRepository.findById(idEntry).orElse(null);
            if (entry != null) {
                Comment comment = new Comment(commentDto.getMessage(), System.currentTimeMillis(), userLogged);
                comment.setVideourl(commentDto.getVideourl());
                comment.setAudioonly(commentDto.getAudioonly());

                comment = commentRepository.save(comment);

                entry.getComments().add(comment);
				/*Saving the modified entry: Cascade relationship between entry and comments
				  will add the new comment to CommentRepository*/
                entryRepository.save(entry);

                log.info("New comment succesfully added: {}", comment);

                return new ResponseEntity<>(new NewEntryCommentResponse(entry, comment), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        //The comment is a reply to another existing comment
        else {
            log.info("Adding new comment reply");
            Comment cParent = commentRepository.findById(commentDto.getCommentParent().getId()).orElse(null);
            if (cParent != null) {
                Comment comment = new Comment(commentDto.getMessage(), System.currentTimeMillis(), userLogged, cParent);
                comment.setVideourl(commentDto.getVideourl());
                comment.setAudioonly(commentDto.getAudioonly());

                comment = commentRepository.save(comment);

                cParent.getReplies().add(comment);
				/*Saving the modified parent comment: Cascade relationship between comment and
				 its replies will add the new comment to CommentRepository*/
                commentRepository.save(cParent);
                Entry entry = entryRepository.findById(idEntry).orElse(null);

                log.info("New comment succesfully added: {}", comment);

                return new ResponseEntity<>(new NewEntryCommentResponse(entry, comment), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }

}
