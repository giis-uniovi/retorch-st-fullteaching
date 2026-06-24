package com.fullteaching.backend;

import com.fullteaching.backend.comment.Comment;
import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.course.CourseRepository;
import com.fullteaching.backend.coursedetails.CourseDetails;
import com.fullteaching.backend.entry.Entry;
import com.fullteaching.backend.file.File;
import com.fullteaching.backend.filegroup.FileGroup;
import com.fullteaching.backend.forum.Forum;
import com.fullteaching.backend.session.Session;
import com.fullteaching.backend.user.User;
import com.fullteaching.backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Controller
public class DatabaseInitializer implements CommandLineRunner {

    private static final Random RANDOM = new Random();

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public DatabaseInitializer(UserRepository userRepo, CourseRepository courseRepo){
        this.userRepository = userRepo;
        this.courseRepository = courseRepo;
        }
    private static void linkCommentReply(List<Comment> comments, int parentIdx, int childIdx) {
        comments.get(parentIdx).getReplies().add(comments.get(childIdx));
        comments.get(childIdx).setCommentParent(comments.get(parentIdx));
    }

    @Override
    public void run(String... args) {

        long[] dateArray = new long[3];
        dateArray[0] = 1478202152681L;
        dateArray[1] = 1477427508222L;
        dateArray[2] = 1478029352681L;

        //Sample users
        String roleStudent = "ROLE_STUDENT";
        String defPicture = "/../assets/images/default_session_image.png";
        User user1 = new User("student1@gmail.com", "pass", "Student Imprudent", defPicture, roleStudent);
        User user2 = new User("student2@gmail.com", "pass", "Student Concludent", defPicture, roleStudent);
        User user3 = new User("teacher@gmail.com", "pass", "Teacher Cheater", defPicture, "ROLE_TEACHER");
        Set<User> setUsers = new HashSet<>();
        setUsers.add(user1);
        setUsers.add(user2);
        setUsers.add(user3);

        List<User> listUsers = new LinkedList<>();
        listUsers.add(user1);
        listUsers.add(user2);
        listUsers.add(user3);

        //Saving users
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);


        User user4 = new User("student3@gmail.com", "pass", "New Student", defPicture, roleStudent);
        setUsers.add(user4);
        listUsers.add(user4);
        setUsers.add(user4);
        userRepository.save(user4);


        //Sample comments
        List<Comment> listComments = new LinkedList<>();
        for (int i = 0; i < 30; i++) {
            int userRandom = RANDOM.nextInt(3);
            listComments.add(new Comment("This is comment " + (i + 1) + ". Roses are red. Violets are blue. Comments have no color. They are just words. This does not rhyme.", dateArray[userRandom], listUsers.get(userRandom)));
        }

        //Recursiveness of comments
        //Entry 1
        linkCommentReply(listComments, 0, 1);
        linkCommentReply(listComments, 0, 2);
        linkCommentReply(listComments, 4, 5);
        linkCommentReply(listComments, 5, 6);
        //Entry 2
        linkCommentReply(listComments, 8, 9);
        //Entry 3
        linkCommentReply(listComments, 12, 13);
        linkCommentReply(listComments, 13, 14);
        //Entry 5
        linkCommentReply(listComments, 16, 17);
        linkCommentReply(listComments, 17, 18);
        linkCommentReply(listComments, 16, 19);
        //Entry 7
        linkCommentReply(listComments, 23, 24);
        //Entry 8
        linkCommentReply(listComments, 25, 26);


        //Sample entries
        List<Entry> listEntries = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            int userRandom = RANDOM.nextInt(3);
            listEntries.add(new Entry("This is entry number " + (i + 1), dateArray[userRandom], listUsers.get(userRandom)));
        }

        //Allocation of comments inside entries
        //Entry 1
        listEntries.getFirst().getComments().add(listComments.get(0));
        listEntries.get(0).getComments().add(listComments.get(3));
        listEntries.get(0).getComments().add(listComments.get(4));
        //Entry 2
        listEntries.get(1).getComments().add(listComments.get(7));
        listEntries.get(1).getComments().add(listComments.get(8));
        //Entry 3
        listEntries.get(2).getComments().add(listComments.get(10));
        listEntries.get(2).getComments().add(listComments.get(11));
        listEntries.get(2).getComments().add(listComments.get(12));
        //Entry 4
        listEntries.get(3).getComments().add(listComments.get(15));
        //Entry 5
        listEntries.get(4).getComments().add(listComments.get(16));
        listEntries.get(4).getComments().add(listComments.get(20));
        //Entry 6
        listEntries.get(5).getComments().add(listComments.get(21));
        listEntries.get(5).getComments().add(listComments.get(22));
        //Entry 7
        listEntries.get(6).getComments().add(listComments.get(23));
        //Entry 8
        listEntries.get(7).getComments().add(listComments.get(25));
        listEntries.get(7).getComments().add(listComments.get(27));
        //Entry 9
        listEntries.get(8).getComments().add(listComments.get(28));
        //Entry 10
        listEntries.get(9).getComments().add(listComments.get(29));

        //Sample files
        List<File> listFiles = new LinkedList<>();
        String[] nameArray = {"Interesting Web Link", "Cool PDF File", "This is a video"};
        for (int i = 0; i < 20; i++) {
            int randomNumber = RANDOM.nextInt(3);
            listFiles.add(new File(randomNumber, nameArray[randomNumber], "www.awesomeurl.com"));
        }

        //Sample fileGroups
        List<FileGroup> listFileGroups = new LinkedList<>();
        String[] titleArray = {"Take a look if you have plenty of time", "Optional tasks", "Lesson 1 - Important files", "Real Examples for Lesson 2", "Optional tasks"};
        for (int i = 0; i < 5; i++) {
            listFileGroups.add(new FileGroup(titleArray[i]));
        }

        //Allocation of files and fileGroups inside fileGroups
        listFileGroups.get(0).getFiles().addAll(listFiles.subList(0, 5));
        listFileGroups.get(1).getFiles().addAll(listFiles.subList(5, 7));
        listFileGroups.get(2).getFiles().addAll(listFiles.subList(7, 12));
        listFileGroups.get(3).getFiles().addAll(listFiles.subList(12, 18));
        listFileGroups.get(4).getFiles().addAll(listFiles.subList(18, 20));

        //Setting index order in each group of files
        for (FileGroup fgAux : listFileGroups) {
            fgAux.updateFileIndexOrder();
        }

        listFileGroups.get(0).setFileGroupParent(listFileGroups.get(1));
        listFileGroups.get(1).getFileGroups().add(listFileGroups.get(0));
        listFileGroups.get(4).setFileGroupParent(listFileGroups.get(3));
        listFileGroups.get(3).getFileGroups().add(listFileGroups.get(4));

        //Sample forums
        Forum f1 = new Forum(true);
        Forum f2 = new Forum(false);

        f1.setEntries(listEntries.subList(0, 6));
        f2.setEntries(listEntries.subList(6, 10));

        //Sample courseDetails
        CourseDetails cd1 = new CourseDetails();
        CourseDetails cd2 = new CourseDetails();

        cd1.setForum(f1);
        cd2.setForum(f2);
        cd1.getFiles().add(listFileGroups.get(1));
        cd1.getFiles().add(listFileGroups.get(2));
        cd2.getFiles().add(listFileGroups.get(3));

        //Sample courses
        Course c1 = new Course("Pseudoscientific course for treating the evil eye", "", user3);
        Course c2 = new Course("Don't mind. This is a real course", "", user3);

        c1.setCourseDetails(cd1);
        c2.setCourseDetails(cd2);
        c1.setAttenders(setUsers);
        c2.getAttenders().add(user1);
        c2.getAttenders().add(user3);

        long now = Instant.now().toEpochMilli();
        long milliSecondsInADay = Duration.ofDays(1).toMillis();
        String defaultDescription = "This is a nice description about this session.";
        //Sample sessions
        Session s1 = new Session("Session 1: Introduction to Web", defaultDescription, now);
        s1.setCourse(c1);
        Session s2 = new Session("Nice examples", defaultDescription, now + milliSecondsInADay);
        s2.setCourse(c1);
        Session s3 = new Session("Project analisys", defaultDescription, now + milliSecondsInADay - 8000000);
        s3.setCourse(c1);
        Session s4 = new Session("Session 3: New Web Technologies", defaultDescription, now + 4 * milliSecondsInADay);
        s4.setCourse(c2);
        Session s5 = new Session("Session 2: Databse integration", defaultDescription, now + 6 * milliSecondsInADay);
        s5.setCourse(c2);

        c1.getSessions().add(s1);
        c1.getSessions().add(s2);
        c1.getSessions().add(s3);
        c2.getSessions().add(s4);
        c2.getSessions().add(s5);

        //Saving courses
        courseRepository.save(c1);
        courseRepository.save(c2);
    }

}
