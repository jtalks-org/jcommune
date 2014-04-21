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

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Close list items tags [*] with the [/*] tag.
 * Support nested lists processing.
 * Transform the incoming text to the tree-like structure and recreate the text with closed tags
 * by this tree.
 * @throws BBCodeListParsingException in the case of invalid input, for example when the close [/list] tag missed.
 * 
 * @author Pavel Vervenko
 */
class ListItemsProcessor {

    private static final String LIST_TAG_OPEN = "[list";
    private static final String LIST_TAG_CLOSE = "[/list]";
    private static final String LIST_ITEM_TAG_OPEN = "[*]";
    private static final String LIST_ITEM_CLOSE = "[/*]";

    private RootElement root;

    private ListElement lastElement;

    private enum TagType {

        LIST,
        LIST_CLOSE,
        ITEM;

        /**
         * @param tagString
         * @return appropriate tag for specified string
         */
        static ListItemsProcessor.TagType fromString(String tagString) throws BBCodeListParsingException {
            if (tagString.equalsIgnoreCase(LIST_ITEM_TAG_OPEN)) {
                return ListItemsProcessor.TagType.ITEM;
            }
            if (tagString.equalsIgnoreCase(LIST_TAG_CLOSE)) {
                return ListItemsProcessor.TagType.LIST_CLOSE;
            }
            if (tagString.startsWith(LIST_TAG_OPEN)) {
                return ListItemsProcessor.TagType.LIST;
            }
            throw new BBCodeListParsingException("unknow tag type: " + tagString);
        }
    }

    private final Pattern listPattern = Pattern.compile(LIST_REGEX, Pattern.DOTALL);

    private final Stack<ListElement> listStack = new Stack<>();

    /**
     * Matches one of the following tags: [list],[/list],[*]
     */
    private static final String TAG_PATTERN = "\\[list([^\\]\\[]+)?]|\\[\\*\\]|\\[\\/list\\]";
    /**
     * TAG + everything before next tag
     */
    private static final String LIST_REGEX = "(" + TAG_PATTERN + ")(.*?)?(?=" + TAG_PATTERN + "|$)";
    private final String initialText;

    ListItemsProcessor(String bbEncodedText) {
        this.initialText = bbEncodedText;
    }

    StringBuilder getTextWithClosedTags() throws BBCodeListParsingException {
        root = createRootElement(initialText);
        Matcher matcher = listPattern.matcher(initialText);
        while (matcher.find()) {
            processMatch(matcher);
        }
        return new StringBuilder(root.toBBString());
    }

    /**
     * Create root element of the tags tree.
     *
     * @param str
     * @return root element
     */
    private RootElement createRootElement(String str) {
        RootElement newRoot = new RootElement();
        int firstListPos = str.indexOf(LIST_TAG_OPEN);
        if (firstListPos < 0) {
            newRoot.text = str;
        } else {
            newRoot.text = str.substring(0, firstListPos);
        }
        return newRoot;
    }

    /**
     * Process matched tag.
     *
     * @param matcher
     */
    private void processMatch(Matcher matcher) throws BBCodeListParsingException {
        ListItemsProcessor.TagType tag = ListItemsProcessor.TagType.fromString(matcher.group(1));
        if (tag == ListItemsProcessor.TagType.LIST) {
            ListElement list = new ListElement();
            list.params = matcher.group(2);
            list.text = matcher.group(3);
            getCurrentElement().addChild(list);
            listStack.push(list);
            lastElement = list;
        }
        if (tag == ListItemsProcessor.TagType.LIST_CLOSE) {
            if (listStack.isEmpty()) {
                if (lastElement != null) {
                    lastElement.endText += matcher.group(3) + LIST_TAG_CLOSE;
                }
                return;
            }
            ListElement closingList = listStack.pop();
            closingList.endText = matcher.group(3);
            closingList.close();
            lastElement = closingList;
        }
        if (tag == ListItemsProcessor.TagType.ITEM) {
            if (!listStack.isEmpty()) {
                ItemElement item = new ItemElement();
                item.text = matcher.group(3);
                listStack.peek().addChild(item);
            } else if (lastElement != null) {
                lastElement.endText += LIST_ITEM_TAG_OPEN + matcher.group(3);
            }
        }
    }

    private TreeElement getCurrentElement() throws BBCodeListParsingException {
        if (listStack.isEmpty()) {
            return root;
        }
        return listStack.peek().getLastChild();
    }

    /**
     * Represents one element of the tags tree.
     */
    private class TreeElement {

        String text = "";
        List<TreeElement> children = new LinkedList<>();

        /**
         * @return last element child
         */
        TreeElement getLastChild() throws BBCodeListParsingException {
            if (children.isEmpty()) {
                return this;
            }
            return children.get(children.size() - 1);
        }

        /**
         * @param newEl new sub-element
         */
        void addChild(TreeElement newEl) {
            children.add(newEl);
        }

        /**
         * Return string representation of this element with tags and all children.
         *
         * @return BB-string
         */
        String toBBString() throws BBCodeListParsingException {
            String rs = getOpenTag() + text;
            for (TreeElement e : children) {
                rs += e.toBBString();
            }
            return rs + getCloseTag();
        }

        /**
         * @return open tag
         */
        protected String getOpenTag() {
            return "";
        }

        /**
         * @return close tag
         */
        protected String getCloseTag() {
            return "";
        }
    }

    /**
     * Root of the tags tree. Contains all lists or just text.
     */
    private class RootElement extends TreeElement {

        @Override
        TreeElement getLastChild() {
            return this;
        }
    }

    /**
     * List item element,[*]
     */
    private class ItemElement extends TreeElement {

        @Override
        protected String getOpenTag() {
            return LIST_ITEM_TAG_OPEN;
        }

        @Override
        protected String getCloseTag() {
            return LIST_ITEM_CLOSE;
        }
    }

    /**
     * List tag, [list].
     */
    private class ListElement extends TreeElement {

        /**
         * list params
         */
        String params;
        /**
         * text after list close tag
         */
        String endText;
        /**
         * true if closed tag was found
         */
        private boolean closed = false;

        @Override
        protected String getOpenTag() {
            return LIST_TAG_OPEN + getParams() + "]";
        }

        @Override
        protected String getCloseTag() {
            return LIST_TAG_CLOSE + getEndText();
        }

        public String getParams() {
            return params != null ? params : "";
        }

        public String getEndText() {
            return endText != null ? endText : "";
        }

        /**
         * Mark the list as closed (valid).
         */
        public void close() {
            closed = true;
        }

        @Override
        String toBBString() throws BBCodeListParsingException{
            if (!closed) {
                throw new BBCodeListParsingException("toBBString() invocation isn't allowed for unclosed list");
            }
            return super.toBBString();
        }
    }

}

/**
 * Thrown in the case of parsing error.
 */
class BBCodeListParsingException extends Exception {

    BBCodeListParsingException(String string) {
        super(string);
    }
}
