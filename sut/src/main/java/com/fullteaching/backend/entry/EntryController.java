package com.fullteaching.backend.entry;

import com.fullteaching.backend.comment.Comment;
import com.fullteaching.backend.comment.CommentRepository;
import com.fullteaching.backend.coursedetails.CourseDetails;
import com.fullteaching.backend.coursedetails.CourseDetailsRepository;
import com.fullteaching.backend.forum.Forum;
import com.fullteaching.backend.forum.ForumRepository;
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
@RequestMapping("/api-entries")
public class EntryController {

    private static final Logger log = LoggerFactory.getLogger(EntryController.class);

    private final ForumRepository forumRepository;
    private final EntryRepository entryRepository;
    private final CommentRepository commentRepository;
    private final CourseDetailsRepository courseDetailsRepository;
    private final ObjectProvider<UserComponent> userProvider;
    private final AuthorizationService authorizationService;

    @Autowired
    public EntryController(AuthorizationService authorizationServ, ObjectProvider<UserComponent> userComp, CourseDetailsRepository courseDetailsRepo, CommentRepository commentRepo, EntryRepository entryRepo, ForumRepository forumRepo) {
        this.authorizationService = authorizationServ;
        this.userProvider = userComp;
        this.courseDetailsRepository = courseDetailsRepo;
        this.commentRepository = commentRepo;
        this.entryRepository = entryRepo;
        this.forumRepository = forumRepo;
    }

    @PostMapping(value = "/forum/{id}")
    public ResponseEntity<Object> newEntry(@RequestBody EntryDto entryDto, @PathVariable(value = "id") String forumId) {

        log.info("CRUD operation: Adding new entry");

        ResponseEntity<Object> authorized = authorizationService.checkBackendLogged();
        if (authorized != null) {
            return authorized;
        }

        long idI = -1;
        try {
            idI = Long.parseLong(forumId);
        } catch (NumberFormatException e) {
            log.error("Forum ID '{}' is not of type Long", forumId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Forum forum = forumRepository.findById(idI).orElse(null);
        if (forum == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        CourseDetails cd = courseDetailsRepository.findByForum(forum);
        if (cd == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ResponseEntity<Object> userAuthorized = authorizationService.checkAuthorizationUsers(cd, cd.getCourse().getAttenders());
        if (userAuthorized != null) { // If the user is not an attender of the course
            return userAuthorized;
        }

        // Construct entities from the DTO
        Comment comment = new Comment(entryDto.getComments().getFirst().getMessage(), 0, null);
        Entry entry = new Entry(entryDto.getTitle(), 0, null);
        entry.getComments().add(comment);

        //Setting the author of the entry
        User userLogged = userProvider.getObject().getLoggedUser();
        entry.setUser(userLogged);

        //Setting the author and date of its first comment
        comment.setUser(userLogged);
        comment.setDate(System.currentTimeMillis());

        //Setting the date of the entry
        entry.setDate(System.currentTimeMillis());

        comment = commentRepository.save(comment);
        entry = entryRepository.save(entry);

        forum.getEntries().add(entry);
        forumRepository.save(forum);

        log.info("New entry succesfully added: {}", entry);

        /*Entire forum is returned in order to have the new entry ID available just
        in case the author wants to add to it a new comment without refreshing the page*/
        return new ResponseEntity<>(new NewEntryCommentResponse(entry, comment), HttpStatus.CREATED);
    }

}
