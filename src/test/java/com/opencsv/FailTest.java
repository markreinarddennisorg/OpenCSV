package com.opencsv;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.fail;

public class FailTest {

	@Test
	public void testFail() {
		fail("We fucked up");
	}
}
