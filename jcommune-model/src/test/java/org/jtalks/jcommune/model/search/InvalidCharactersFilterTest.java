/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.jcommune.model.search;

import org.jtalks.jcommune.model.search.InvalidCharactersFilter;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class InvalidCharactersFilterTest {
	private InvalidCharactersFilter filter = new InvalidCharactersFilter();
	
	@Test
	public void testRemovePunctuationMarks() {
		String searchText = "φ@#nice-book.!Ω";
		String expectedResult = "nice book";
		
		String filterResult = filter.filter(searchText);
		
		Assert.assertEquals(filterResult, expectedResult, "Punctuation marks aren't removed.");
	}
	
	@Test
	public void testFilterCorrectSearchText() {
		String searchText = "nice book";
		
		String filterResult = filter.filter(searchText);
		
		Assert.assertEquals(filterResult, filterResult, "The search text should remain the same.");
	}
}
