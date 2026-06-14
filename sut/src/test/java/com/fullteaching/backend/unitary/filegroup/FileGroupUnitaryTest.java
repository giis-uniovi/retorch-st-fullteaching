package com.fullteaching.backend.unitary.filegroup;


import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.fullteaching.backend.AbstractUnitTest;
import com.fullteaching.backend.file.File;
import com.fullteaching.backend.filegroup.FileGroup;

class FileGroupUnitaryTest extends AbstractUnitTest {

	private static final String FILE_GROUP ="FileGroup";
	static int fileType = 0;
	static String fileName = "FileNAME.doc";
	
	@BeforeEach
	void setUp() throws Exception {
		// Not implemented in those tests, add by convention
	}

	@Test
	void testFileGroup() {
		FileGroup fg1 = new FileGroup();
		assertNotNull(fg1);
		
		FileGroup fg2 = new FileGroup(FILE_GROUP);
		assertNotNull(fg2);
        assertEquals(FILE_GROUP, fg2.getTitle());
		
		FileGroup fg3 = new FileGroup(FILE_GROUP,fg2);
		assertNotNull(fg3);
        assertEquals(FILE_GROUP, fg3.getTitle());
		assertNotNull(fg3.getFileGroupParent());
        assertEquals(fg2, fg3.getFileGroupParent());

	}

	@Test
	void setAndGetFileGroupIdTest() {
		FileGroup fg1 = new FileGroup();
		assertNotNull(fg1);
		
		fg1.setId(1);
        assertEquals(1, fg1.getId());
	}

	@Test
	void setAndGetTitleTest() {
		FileGroup fg1 = new FileGroup();
		assertNotNull(fg1);
		
		fg1.setTitle(FILE_GROUP);
        assertEquals(FILE_GROUP, fg1.getTitle());
	}

	@Test
	void setAndGetFilesTest() {
		FileGroup fg1 = new FileGroup();
		assertNotNull(fg1);
		
		List<File> files = new ArrayList<File>();
		files.add( new File (fileType, fileName));
		fg1.setFiles(files);

        assertEquals(1, fg1.getFiles().size());
		
	}

	@Test
	void setAndGetFileGroupsTest() {
		List<FileGroup> groups = new ArrayList<FileGroup>();
		groups.add(new FileGroup(FILE_GROUP +"2"));
		groups.add(new FileGroup(FILE_GROUP +"3"));
		
		FileGroup fg3 = new FileGroup();
		assertNotNull(fg3);
		
		fg3.setFileGroups(groups);

        assertEquals(2, fg3.getFileGroups().size());
	}

	@Test
	void setAndGetFileGroupParentTest() {
		FileGroup fg1 = new FileGroup();
		assertNotNull(fg1);
		
		FileGroup fg2 = new FileGroup(FILE_GROUP);
		assertNotNull(fg2);
        assertEquals(FILE_GROUP, fg2.getTitle());
		
		fg1.setFileGroupParent(fg2);
		assertNotNull(fg1.getFileGroupParent());
        assertEquals(fg2, fg1.getFileGroupParent());
	}

	@Test
	void fileGroupEqualTest() {
		FileGroup fg1 = new FileGroup();
		assertNotNull(fg1);
		fg1.setId(1);
		FileGroup fg2 = new FileGroup(FILE_GROUP);
		assertNotNull(fg2);
		fg2.setId(2);
		FileGroup fg3 = new FileGroup(FILE_GROUP);
		assertNotNull(fg3);
		fg3.setId(1);

        assertNotEquals(null, fg1);
        assertNotEquals("not a group", fg1);
        assertNotEquals(fg1, fg2);
        assertEquals(fg1, fg3);
		
	}

	@Test
	void updateFileIndexOrderTest() {
		FileGroup fg1 = new FileGroup();
		assertNotNull(fg1);
		
		List<File> files = new ArrayList<File>();
		files.add( new File (fileType, fileName));
		files.add( new File (fileType, fileName +"2"));
		fg1.setFiles(files);
		
		fg1.updateFileIndexOrder();
		
		List<File> list = fg1.getFiles();
        assertEquals(0, list.get(0).getIndexOrder());
        assertEquals(1, list.get(1).getIndexOrder());
		
	}

}
