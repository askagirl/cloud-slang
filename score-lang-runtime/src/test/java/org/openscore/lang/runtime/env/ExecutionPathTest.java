/*******************************************************************************
* (c) Copyright 2014 Hewlett-Packard Development Company, L.P.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Apache License v2.0 which accompany this distribution.
*
* The Apache License is available at
* http://www.apache.org/licenses/LICENSE-2.0
*
*******************************************************************************/
package org.openscore.lang.runtime.env;

import org.junit.Test;
import org.openscore.lang.runtime.env.ExecutionPath;

import java.util.NoSuchElementException;

import static org.openscore.lang.runtime.env.ExecutionPath.PATH_SEPARATOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author moradi
 * @since 09/11/2014
 * @version $Id$
 */
public class ExecutionPathTest {

	/**
	 * Test method for {@link org.openscore.lang.runtime.env.ExecutionPath#getCurrentPath()}.
	 */
	@Test
	@SuppressWarnings("static-method")
	public void testCurrentPath() {
		ExecutionPath executionPath = new ExecutionPath();
		StringBuilder expectedPath = new StringBuilder("0");
		doAssert(expectedPath, executionPath);

		executionPath.down(); // 0/0
		executionPath.forward(); // 0/1
		expectedPath.append(PATH_SEPARATOR).append("1");
		doAssert(expectedPath, executionPath);

		executionPath.down(); // 0/1/0
		executionPath.forward(); // 0/1/1
		executionPath.forward(); // 0/1/2
		expectedPath.append(PATH_SEPARATOR).append("2");
		doAssert(expectedPath, executionPath);

		executionPath.up(); // 0/1
		deleteLevel(expectedPath);
		doAssert(expectedPath, executionPath);

		executionPath.forward(); // 0/2
		executionPath.forward(); // 0/3
		deleteLevel(expectedPath);
		expectedPath.append(PATH_SEPARATOR).append("3");
		doAssert(expectedPath, executionPath);

		executionPath.down(); // 0/3/0
		expectedPath.append(PATH_SEPARATOR).append("0");
		executionPath.down(); // 0/3/0/0
		executionPath.forward(); // 0/3/0/1
		expectedPath.append(PATH_SEPARATOR).append("1");
		doAssert(expectedPath, executionPath);

		executionPath.up(); // 0/3/0
		deleteLevel(expectedPath);
		executionPath.up(); // 0/3
		deleteLevel(expectedPath);
		executionPath.up(); // 0
		deleteLevel(expectedPath);
		doAssert(expectedPath, executionPath);

		try {
			executionPath.up(); // 0
		} catch(Exception ex) {
			assertTrue(ex instanceof NoSuchElementException);
		}
		doAssert(expectedPath, executionPath);
	}

	private static void doAssert(StringBuilder expectedPath, ExecutionPath executionPath) {
		assertEquals(expectedPath.toString(), executionPath.getCurrentPath());
	}

	private static void deleteLevel(StringBuilder expectedPath) {
		expectedPath.delete(expectedPath.lastIndexOf(PATH_SEPARATOR), expectedPath.length());
	}

}