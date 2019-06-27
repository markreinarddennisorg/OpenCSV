package com.opencsv;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CSVListIteratorTest {
	private static final List<String[]> STRING_LIST = Arrays.asList(new String[]{"test1", "test2"}, new String[]{"test3", "test4"});
	private CSVListIterator iterator;
	private CSVReader mockReader;

	private static Locale systemLocale;

	@BeforeAll
	public static void storeSystemLocale() {
		systemLocale = Locale.getDefault();
	}

	@AfterEach
	public void setSystemLocaleBackToDefault() {
		Locale.setDefault(systemLocale);
	}

	@BeforeEach
	public void setUp() throws IOException {
		Locale.setDefault(Locale.US);
		mockReader = mock(CSVReader.class);
		when(mockReader.readAll()).thenReturn(STRING_LIST);
		iterator = new CSVListIterator(mockReader.readAll());
	}

	@Test
	public void removethrowsUnsupportedOperationException() {
		String englishErrorMessage = null;
		try {
			iterator.remove();
			fail("UnsupportedOperationException should have been thrown by read-only iterator.");
		} catch (UnsupportedOperationException e) {
			englishErrorMessage = e.getLocalizedMessage();
		}

		// Now with a different locale
		iterator.setErrorLocale(Locale.GERMAN);
		try {
			iterator.remove();
			fail("UnsupportedOperationException should have been thrown by read-only iterator.");
		} catch (UnsupportedOperationException e) {
			assertNotSame(englishErrorMessage, e.getLocalizedMessage());
		}
	}

	@Test
	public void initialReadReturnsStrings() {
		assertArrayEquals(STRING_LIST.get(0), iterator.next());
		assertArrayEquals(STRING_LIST.get(1), iterator.next());
	}

	@Test
	public void hasNextWorks() throws IOException {
		when(mockReader.readAll()).thenReturn(null);
		assertTrue(iterator.hasNext()); // initial read from constructor
		iterator.next();
		assertTrue(iterator.hasNext());
		iterator.next();
		assertFalse(iterator.hasNext());
	}

}
