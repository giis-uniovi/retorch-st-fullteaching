package com.fullteaching.backend.course;

import com.fullteaching.backend.user.User;
import com.fullteaching.backend.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class CourseAttenderService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public CourseAttenderService(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Resolves valid emails to registered users, updates the bidirectional Course-User
     * relationship, saves both sides, and returns categorized result sets.
     *
     * @param course           the course to add attenders to
     * @param validEmails      email addresses already validated as syntactically correct
     * @param notRegistered    set that will be populated with emails having no matching account
     * @return result holding newly added and already-added user collections
     */
    public AttenderUpdateResult updateAttenders(Course course, Set<String> validEmails,
                                                Set<String> notRegistered) {
        Collection<User> possibleAttenders = userRepository.findByNameIn(validEmails);
        Collection<User> added = new HashSet<>();
        Collection<User> alreadyAdded = new HashSet<>();

        for (String email : validEmails) {
            if (possibleAttenders.stream().noneMatch(u -> u.getName().equals(email))) {
                notRegistered.add(email);
            }
        }

        for (User attender : possibleAttenders) {
            (course.addAttender(attender) ? added : alreadyAdded).add(attender);
        }

        userRepository.saveAll(possibleAttenders);
        courseRepository.save(course);

        return new AttenderUpdateResult(added, alreadyAdded);
    }

    public record AttenderUpdateResult(Collection<User> added, Collection<User> alreadyAdded) {}
}
