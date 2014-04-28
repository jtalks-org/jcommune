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
 * 
 * @author Pavel Vervenko, Mikhail Stryzhonok
 */
class ListItemsProcessor {

    private static final String LIST_TAG_OPEN = "[list";
    private static final String LIST_TAG_CLOSE = "[/list]";
    private static final String LIST_ITEM_TAG_OPEN = "[*]";
    private static final String LIST_ITEM_CLOSE = "[/*]";

    private RootElement root;

    private ListElement lastElement;

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

    StringBuilder getTextWithClosedTags() {
        root = createRootElement(initialText);
        Matcher matcher = listPattern.matcher(initialText);
        while (matcher.find()) {
            Tag tag = createTag(matcher.group(1));
            tag.processMatch(matcher);
        }
        return root.toBBString();
    }

    /**
     * Create root element of the tags tree.
     * @param str String from which root element will be created
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
     * Creates tag depending on needed tag type
     * @param tagString String tag representation
     * @return Newly created tag
     */
    private Tag createTag(String tagString) {
        if (tagString.equalsIgnoreCase(LIST_ITEM_TAG_OPEN)) {
            return new ItemElement();
        }
        if (tagString.equalsIgnoreCase(LIST_TAG_CLOSE)) {
            return new ClosingList();
        }
        if (tagString.startsWith(LIST_TAG_OPEN)) {
            return new ListElement();
        } else {
            throw new IllegalArgumentException("Unknown tag type " + tagString);
        }
    }

    private TreeElement getCurrentElement() {
        if (listStack.isEmpty()) {
            return root;
        }
        return listStack.peek().getLastChild();
    }

    /**
     * Represents one element of the tags tree.
     */
    private abstract class TreeElement {

        String text = "";
        List<TreeElement> children = new LinkedList<>();

        /**
         * @return last element child
         */
        TreeElement getLastChild() {
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
        StringBuilder toBBString()  {
            StringBuilder res = new StringBuilder(getOpenTag());
            res.append(text);
            for (TreeElement e : children) {
                res.append(e.toBBString());
            }
            return res.append(getCloseTag());
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
     * Root class of all kind of list tags. E.g. ListElement, ListItem, ClosingList
     */
    private abstract class Tag extends TreeElement {

        /**
         * Process matching tag
         * @param matcher Matcher object
         */
        public abstract void processMatch(Matcher matcher);
    }

    /**
     * List item element,[*]
     */
    private class ItemElement extends Tag {

        /**
         * We need store parent element to check if it closed
         */
        private ListElement parent;

        @Override
        protected String getOpenTag() {
            return LIST_ITEM_TAG_OPEN;
        }

        @Override
        protected String getCloseTag() {
            if (parent.closed) {
                return LIST_ITEM_CLOSE;
            } else {
                return "";
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void processMatch(Matcher matcher) {
            if (!listStack.isEmpty()) {
                this.text = matcher.group(3);
                listStack.peek().addChild(this);
                this.parent = listStack.peek();
            } else if (lastElement != null) {
                lastElement.endText += LIST_ITEM_TAG_OPEN + matcher.group(3);
            }
        }
    }

    /**
     * List tag, [list].
     */
    private class ListElement extends Tag {

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
            if (closed) {
                return LIST_TAG_CLOSE + getEndText();
            } else {
                return "";
            }
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

        /**
         * {@inheritDoc}
         */
        @Override
        public void processMatch(Matcher matcher) {
            this.params = matcher.group(2);
            this.text = matcher.group(3);
            getCurrentElement().addChild(this);
            listStack.push(this);
            lastElement = this;
        }
    }

    /**
     * Class for closing list tags. Needed for separate matching processing.
     */
    private class ClosingList extends Tag {

       /**
        * {@inheritDoc}
        */
        @Override
        public void processMatch(Matcher matcher) {
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
    }
}

