package com.fullteaching.backend.unitary.forum;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.fullteaching.backend.AbstractUnitTest;
import com.fullteaching.backend.entry.Entry;
import com.fullteaching.backend.forum.Forum;

class ForumUnitaryTest extends AbstractUnitTest {

	
	@Test
	void newForumTest() {
		Forum f = new Forum();
		assertNotNull(f);
		
		Forum f2 = new Forum(true);
		assertNotNull(f2);
		assertTrue(f2.isActivated());
		
		Forum f3 = new Forum(false);
		assertNotNull(f3);
        assertFalse(f3.isActivated());
	}

	
	@Test
	void activateAndDeactivateTest() {
		Forum f = new Forum();
		f.setActivated(true);
		assertTrue(f.isActivated());
		
		f.setActivated(false);
        assertFalse(f.isActivated());
		
	}

	@Test
	void testGetEntries() {
		Forum f = new Forum();
		List<Entry> entries = new ArrayList<Entry>();
		
		f.setEntries(entries);
		
		assertNotNull(f);
        assertEquals(f.getEntries(), entries);
	}

}
