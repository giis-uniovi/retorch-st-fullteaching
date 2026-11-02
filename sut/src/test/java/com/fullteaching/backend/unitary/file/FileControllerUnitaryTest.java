package com.fullteaching.backend.unitary.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

import com.fullteaching.backend.comment.CommentRepository;
import com.fullteaching.backend.course.Course;
import com.fullteaching.backend.course.CourseRepository;
import com.fullteaching.backend.file.FileController;
import com.fullteaching.backend.file.FileOperationsService;
import com.fullteaching.backend.file.FileRepository;
import com.fullteaching.backend.filegroup.FileGroupRepository;
import com.fullteaching.backend.security.AuthorizationService;
import com.fullteaching.backend.user.User;
import com.fullteaching.backend.user.UserComponent;
import com.fullteaching.backend.user.UserRepository;

class FileControllerUnitaryTest {

	private static final long COURSE_ID = 1L;
	private static final long MISSING_GROUP_ID = 999L;

	private FileGroupRepository fileGroupRepository;
	private FileController controller;

	@BeforeEach
	@SuppressWarnings("unchecked")
	void setUp() {
		AuthorizationService authorizationService = mock(AuthorizationService.class);
		CourseRepository courseRepository = mock(CourseRepository.class);
		fileGroupRepository = mock(FileGroupRepository.class);

		Course course = mock(Course.class);
		when(course.getTeacher()).thenReturn(mock(User.class));
		when(courseRepository.findById(COURSE_ID)).thenReturn(Optional.of(course));
		when(fileGroupRepository.findById(MISSING_GROUP_ID)).thenReturn(Optional.empty());

		controller = new FileController(mock(FileOperationsService.class), mock(ObjectProvider.class),
				authorizationService, mock(UserRepository.class), mock(CommentRepository.class), courseRepository,
				mock(FileRepository.class), fileGroupRepository);
	}

	@Test
	void uploadToMissingFileGroupIsRejectedAndKeepsNoFileTest() throws IOException {
		MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
		request.addFile(new MockMultipartFile("data", "missing-group.txt", "text/plain", "some content".getBytes()));
		Set<String> filesBefore = storedFiles();

		ResponseEntity<Object> response = controller.handleFileUpload(request, "" + COURSE_ID, "" + MISSING_GROUP_ID);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "failure - expected HTTP status 404");
		assertEquals(filesBefore, storedFiles(), "failure - a rejected upload must not leave a file on disk");
		verify(fileGroupRepository, never()).save(any());
	}

	private static Set<String> storedFiles() throws IOException {
		if (!Files.exists(FileController.FILES_FOLDER)) {
			return Set.of();
		}
		try (Stream<java.nio.file.Path> files = Files.list(FileController.FILES_FOLDER)) {
			return files.map(p -> p.getFileName().toString()).collect(Collectors.toSet());
		}
	}
}
