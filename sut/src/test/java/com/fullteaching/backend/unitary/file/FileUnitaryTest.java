package com.fullteaching.backend.unitary.file;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.fullteaching.backend.AbstractUnitTest;
import com.fullteaching.backend.file.File;

class FileUnitaryTest extends AbstractUnitTest {

	static int fileType = 0;
	static String fileName = "FileNAME.doc";
	static String fileLink = "this link";
	static int fileOrder = 1;

	@Test
	void newFileTest() {
		File f1 = new File (fileType, fileName);
		assertNotNull(f1);
        assertEquals(fileType, f1.getType());
        assertEquals(fileName, f1.getName());
		
		
		File f2 = new File (fileType, fileName, fileLink);
		assertNotNull(f2);
        assertEquals(fileType, f2.getType());
        assertEquals(fileName, f2.getName());
        assertEquals(fileLink, f2.getLink());
		
		File f3 = new File (fileType, fileName, fileLink, fileOrder);
		assertNotNull(f3);
        assertEquals(fileType, f3.getType());
        assertEquals(fileName, f3.getName());
        assertEquals(fileLink, f3.getLink());
        assertEquals(fileOrder, f3.getIndexOrder());
		
		File f4 = new File (fileType, ".doc");
		assertNotNull(f4);
        assertEquals(fileType, f4.getType());
        assertEquals(".doc", f4.getName());
		assertTrue(f4.getNameIdent().contains(".doc"));
		
	}
	

	@Test
	void setAndGetFileIdTest() {
		File f1 = new File (fileType, fileName);
		assertNotNull(f1);
        assertEquals(fileType, f1.getType());
        assertEquals(fileName, f1.getName());
		
		f1.setId(0);
        assertEquals(0, f1.getId());
	}

	@Test
	void setAndGetFileTypeTest() {
		File f1 = new File (fileType, fileName);
		assertNotNull(f1);
        assertEquals(fileType, f1.getType());
        assertEquals(fileName, f1.getName());
		
		f1.setType(1);
        assertEquals(1, f1.getType());

	}

	@Test
	void setAndGetFileNameTest() {
		File f1 = new File (fileType, fileName);
		assertNotNull(f1);
        assertEquals(fileType, f1.getType());
        assertEquals(fileName, f1.getName());
		
		f1.setName("test_name");
        assertEquals("test_name", f1.getName());
	}

	@Test
	void setAndGetFileNameIdentTest() {
		File f1 = new File (fileType, fileName);
		assertNotNull(f1);
        assertEquals(fileType, f1.getType());
        assertEquals(fileName, f1.getName());
		
		f1.setNameIdent("NAME_IDENT");
        assertEquals("NAME_IDENT", f1.getNameIdent());

	}

	@Test
	void setAndGetFileLinkTest() {
		File f1 = new File (fileType, fileName);
		assertNotNull(f1);
        assertEquals(fileType, f1.getType());
        assertEquals(fileName, f1.getName());
		
		f1.setLink(fileLink);
        assertEquals(fileLink, f1.getLink());
	}

	@Test
	void testGetIndexOrder() {
		File f1 = new File (fileType, fileName);
		assertNotNull(f1);
        assertEquals(fileType, f1.getType());
        assertEquals(fileName, f1.getName());
		
		f1.setIndexOrder(5);
        assertEquals(5, f1.getIndexOrder());
	}

	@Test
	void testEqualsObject() {
		File f1 = new File (fileType, fileName);
		f1.setId(5);
		assertNotNull(f1);
        assertEquals(fileType, f1.getType());
        assertEquals(fileName, f1.getName());
		
		File f2 = new File (fileType, fileName);
		f2.setId(2);
		assertNotNull(f2);
        assertEquals(fileType, f2.getType());
        assertEquals(fileName, f2.getName());
		
		File f3 = new File (fileType, fileName);
		f3.setId(5);
		assertNotNull(f3);
        assertEquals(fileType, f3.getType());
        assertEquals(fileName, f3.getName());

        assertEquals(f1, f3);
        assertNotEquals(null, f1);
        assertNotEquals("not a file", f1);
        assertNotEquals(f1, f2);
	}

	@Test
	void getFileExtTest() {
		File f1 = new File (fileType, fileName);
		f1.setId(5);
		assertNotNull(f1);
        assertEquals(fileType, f1.getType());
        assertEquals(fileName, f1.getName());

        assertEquals("doc", f1.getFileExtension());
	}

}
