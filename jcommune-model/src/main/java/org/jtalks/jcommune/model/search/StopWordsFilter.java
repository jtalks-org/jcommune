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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.util.Version;
import org.apache.solr.analysis.StopFilterFactory;
import org.hibernate.search.util.impl.HibernateSearchResourceLoader;

/**
 * Deletes stop words in the search text.
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class StopWordsFilter implements SearchRequestFilter {
    private List<String> stopWordsFiles;
    private boolean ignoreCase;
    
    /**
     * @param stopWordsFiles list of files that contain stop words
     * @param ignoreCase ignore case
     */
    public StopWordsFilter(List<String> stopWordsFiles, boolean ignoreCase) {
        this.stopWordsFiles = stopWordsFiles;
        this.ignoreCase = ignoreCase;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String filter(String searchText) {
        String result = searchText;
        for (String stopWordsFile : stopWordsFiles) {
            result = filter(result, stopWordsFile);
        }
        return result;
    }
    
    /**
     * This method performs a filtration of the search text.
     *  
     * @param searchText search text
     * @param stopWordsFile file that contains stop words
     * @return result of filtration
     */
    private String filter(String searchText, String stopWordsFile) {
        StopFilterFactory filterFactory = new StopFilterFactory();
        Map<String, String> arguments = new HashMap<String, String>();
        arguments.put("words", stopWordsFile);
        arguments.put("luceneMatchVersion", String.valueOf(Version.LUCENE_31));
        arguments.put("ignoreCase", String.valueOf(ignoreCase));
        filterFactory.init(arguments);
        filterFactory.inform(new HibernateSearchResourceLoader());
        
        Set<?> stopWords = filterFactory.getStopWords();
        List<String> searchTerms = splitSearchText(searchText);
        for(Object stopWord: (CharArraySet) stopWords) {
            String stopWordString = String.valueOf((char[]) stopWord).trim();
            searchTerms.remove(stopWordString);
        }
        return joinSearchTerms(searchTerms);
    }
    
    /**
     * Performs a splitting the search text.
     * 
     * @param searchText search text
     * @return list of terms
     */
    private List<String> splitSearchText(String searchText) {
        if (ignoreCase) {
            searchText = searchText.toLowerCase();
        }
        return new ArrayList<String>(
                Arrays.asList(searchText.split("\\s"))
        );
    }
    
    /**
     * Creates the single string from list of terms.
     * 
     * @param searchTerms search text
     * @return the single string from list of terms
     */
    private String joinSearchTerms(List<String> searchTerms) {
        return StringUtils.join(searchTerms, " ");
    }
}
