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

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class StopWordsFilterTest {
	private static final String TEST_STOP_WORDS_FILE = "org/jtalks/jcommune/lucene/test_stopwords.txt";
	private StopWordsFilter filter;
	
	@BeforeMethod
	public void init() {
		filter = new StopWordsFilter(Arrays.asList(TEST_STOP_WORDS_FILE), true);
	}
	
	@Test
	public void testExcludeStopWord() {
		String stopWord = "the";
		String searchText = "The book";
		
		searchText = filter.filter(searchText);

		Assert.assertEquals(isAllWordsExcluded(searchText, stopWord), true, "The word has not been excluded.");
	}

    @Test
    public void testExcludeMoreThanOneStopWord() {
        String stopWord = "the";
        String searchText = "The text the word";

        searchText = filter.filter(searchText);

        Assert.assertEquals(isAllWordsExcluded(searchText, stopWord), true, "The word has not been excluded.");
    }


	@Test
	public void testCorrectSearchText() {
		String searchText = "nice book";

		String filterResult = filter.filter(searchText);

		Assert.assertEquals(searchText, filterResult, "The search text should remain the same.");
	}

    private boolean isAllWordsExcluded(String searchText, String stopWord) {
        boolean excluded = true;
        for (String piece : searchText.split(" ")) {
            if (stopWord.equalsIgnoreCase(piece)) {
                excluded = false;
            }
        }
        return  excluded;
    }
	
}
