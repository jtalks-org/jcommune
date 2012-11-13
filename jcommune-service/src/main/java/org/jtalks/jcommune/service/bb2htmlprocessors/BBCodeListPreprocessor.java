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

package org.jtalks.jcommune.service.bb2htmlprocessors;

import ru.perm.kefir.bbcode.TextProcessor;

/**
 * Preprocessor for bb2html encoding which replaces all list items like [*]
 * with [*]...[/*] tags. This allows create formatted text in list items. 
 * @author Vyacheslav Mishcheryakov
 *
 */
public class BBCodeListPreprocessor implements TextProcessor {

    private static final String LIST_OPEN_TAG = "[list]";
    private static final String LIST_CLOSE_TAG = "[/list]";
    private static final String LIST_ITEM_OPEN_TAG = "[*]";
    private static final String LIST_ITEM_CLOSE_TAG = "[/*]";
    
    /**
     * Process incoming text with replacing [*] tags by [*]...[/*]
     * @param bbEncodedText BB encoded text to process
     * @return processed text 
     */
    public String process(String bbEncodedText) {
        return process(new StringBuilder(bbEncodedText)).toString();
    }
    
    /**
     * Process incoming text with replacing [*] tags by [*]...[/*]
     * @param bbEncodedText bb encoded text to process
     * @return processed text
     */
    private StringBuilder preprocessLists(String bbEncodedText) {
        StringBuilder result = new StringBuilder();
        int listStart = bbEncodedText.indexOf(LIST_OPEN_TAG);
        if (listStart == -1) {
            // if no lists found return original text;
            result.append(bbEncodedText);
        } else {
            // append text before first list
            result.append(bbEncodedText.substring(0, listStart));
            int listEnd = 0;
            int cutListEnd = 0;
            while (listStart != -1) {
                result.append(LIST_OPEN_TAG);
                listEnd = bbEncodedText.indexOf(LIST_CLOSE_TAG, listStart);
                String listItems = bbEncodedText.substring(listStart + LIST_OPEN_TAG.length(), listEnd);
                result.append(preprocessListItems(listItems));
                result.append(LIST_CLOSE_TAG);
                
                cutListEnd = bbEncodedText.indexOf(LIST_OPEN_TAG, listEnd);
                listStart = cutListEnd;
                if (cutListEnd == -1) {
                    cutListEnd = bbEncodedText.length();
                }
                result.append(bbEncodedText.substring(listEnd + LIST_CLOSE_TAG.length(), cutListEnd));
            }
        }
        return result;
     }

    /**
     * Process actual list items with replacing [*] by [*]...[/*] 
     * @param bbEncodedList text inside [list]...[/list] tags
     * @return processed text
     */
     private StringBuilder preprocessListItems(String bbEncodedList) {
          StringBuilder result = new StringBuilder();
          int itemStart = bbEncodedList.indexOf(LIST_ITEM_OPEN_TAG);
          if (itemStart == -1) {
              // if no list items retun original text
              result.append(bbEncodedList);
          } else {
              // append text before first item
              result.append(bbEncodedList.substring(0, itemStart));
          
              int itemEnd = 0;
              int cutItemEnd = 0;
              while (itemStart != -1) {
                 itemEnd = bbEncodedList.indexOf(LIST_ITEM_OPEN_TAG, itemStart + 1);
                 cutItemEnd = itemEnd != -1 ? itemEnd : bbEncodedList.length();
                 
                 result.append(bbEncodedList.substring(itemStart, cutItemEnd));
                 result.append(LIST_ITEM_CLOSE_TAG);
                 
                 itemStart = itemEnd;
              }
          }
        return result;
     }

     /**
      * Process incoming text with replacing [*] tags by [*]...[/*]
      * @param bbEncodedText bb encoded text to process
      * @return processed text
      */
    @Override
    public CharSequence process(CharSequence bbEncodedText) {
        StringBuilder result = process(new StringBuilder(bbEncodedText)); 
        return result.subSequence(0, result.length());
    }

    /**
     * Process incoming text with replacing [*] tags by [*]...[/*]
     * @param bbEncodedText bb encoded text to process
     * @return processed text
     */
    @Override
    public StringBuilder process(StringBuilder bbEncodedText) {
        return preprocessLists(bbEncodedText.toString());
    }

    /**
     * Process incoming text with replacing [*] tags by [*]...[/*]
     * @param bbEncodedText bb encoded text to process
     * @return processed text
     */
    @Override
    public StringBuffer process(StringBuffer bbEncodedText) {
        return new StringBuffer(process(bbEncodedText.toString()));
    }
    
}
