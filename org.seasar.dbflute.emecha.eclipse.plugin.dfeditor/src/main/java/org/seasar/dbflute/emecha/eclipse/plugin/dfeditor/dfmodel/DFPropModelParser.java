/*
 * Copyright 2014 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel;

/**
 *
 */
public class DFPropModelParser {

    private enum State {
        DEFAULT
        ,BLOCK_START
        ,INDENT
        ,COMMENT
        ,SINGLE_QUATE
        ,DUBLE_QUATE
    }
    public DFPropFileModel parse(String source) {
        DFPropFileModel model = new DFPropFileModel();
        if (source == null || source.trim().length() == 0) {
            return model;
        }

        AbstractModel current = model;
        int offset = 0;
        int indentOffset = 0;
        State beforeState = State.INDENT;
        StringBuilder buffer = new StringBuilder();
        State state = State.INDENT;
        int length = source.length();
        for (int index = 0; index < length; index++) {
            char c = source.charAt(index);
            switch (c) {
            case '\r':
            {
                char next = source.charAt((index + 1));
                if (next == '\n') {
                    break;
                }
                // not break
            }
            case '\n':
            {
                if (State.INDENT.equals(state)) {
                    if (current instanceof MultiLineCommentModel) {
                        ((MultiLineCommentModel) current).setLength(offset - current.getOffset());
                        current = (AbstractModel) current.getParent();
                    }
                    offset = index + 1;
                } else if (State.COMMENT.equals(state)) {
                    parseBuffer(current, buffer, offset, index);
                    beforeState = state;
                    state = State.INDENT;
                    buffer = new StringBuilder();
                    offset = index + 1;
                } else if (buffer.toString().trim().length() == 0) {
                    beforeState = state;
                    state = State.INDENT;
                    buffer = new StringBuilder();
                    offset = index + 1;
                }
                AbstractModel prevModel = current;
                while(true) {
                    if (prevModel == null) {
                        break;
                    }
                    if (prevModel instanceof BlockModel) {
                        ((BlockModel) prevModel).addLineCount();
                    }
                    prevModel = (AbstractModel) prevModel.getParent();
                }
                indentOffset = 0;
                break;
            }
            case '#' :
            {
                if (State.INDENT.equals(state) ) {
                    if (!(current instanceof MultiLineCommentModel)) {
                        MultiLineCommentModel multiLineComment = new MultiLineCommentModel();
                        multiLineComment.setOffset(offset);
                        current.addChild(multiLineComment);
                        current = multiLineComment;
                    }
                    buffer = new StringBuilder();
                    offset = index;
                    beforeState = state;
                    state = State.COMMENT;
                }
                if (State.DEFAULT.equals(state) || State.BLOCK_START.equals(state)) {
                    parseBuffer(current, buffer, offset, index);

                    buffer = new StringBuilder();
                    offset = index;
                    beforeState = state;
                    state = State.COMMENT;
                }
                buffer.append(c);
                break;
            }
            case '{' :
                buffer.append(c);
                if (State.DEFAULT.equals(state)) {
                    BlockModel blockModel = null;
                    if (MapModel.START_BRACE.equals(buffer.toString())) {
                        blockModel = new MapModel();
                    } else if (ListModel.START_BRACE.equals(buffer.toString())) {
                        blockModel = new ListModel();
                    }
                    if (blockModel != null) {
                        if (State.INDENT.equals(beforeState)) {
                            blockModel.setOffset(offset - indentOffset);
                        } else {
                            blockModel.setOffset(offset);
                        }
                        current.addChild(blockModel);
                        current = blockModel;
                        buffer = new StringBuilder();
                        offset = index;
                        beforeState = state;
                        state = State.BLOCK_START;
                    }
                }
                break;
            case '}' :
                if (State.DEFAULT.equals(state) || State.INDENT.equals(state) || State.BLOCK_START.equals(state)) {
                    parseBuffer(current, buffer, offset, index);
                    AbstractModel prevModel = current;
                    while(true) {
                        if (prevModel == null) {
                            break;
                        }
                        if (prevModel instanceof BlockModel) {
                            int prevOffset = prevModel.getOffset();
                            int appendLength = 1;
                            for (int search = index + 1; search < length; search++) {
                                char next = source.charAt(search);
                                switch (next) {
                                case ' ':
                                case '\t':
                                    appendLength++;
                                    continue;
                                case '\r' :
                                    char crNext = source.charAt(search + 1);
                                    if (crNext == '\n') {
                                        appendLength++;
                                        continue;
                                    }
                                case '\n' :
                                    appendLength++;
                                    break;
                                default:
                                    break;
                                }
                                break;
                            }
                            ((BlockModel) prevModel).setLength(index - prevOffset + appendLength); // 長さに判定対象文字を含める
                            current = (AbstractModel) prevModel.getParent();
                            break;
                        }
                        if (prevModel instanceof MultiLineCommentModel) {
                            ((MultiLineCommentModel) prevModel).setLength(offset - prevModel.getOffset());
                        }
                        if (prevModel instanceof MapEntryModel) {
                            ((MapEntryModel) prevModel).setLength(index - prevModel.getOffset());
                        }
                        prevModel = (AbstractModel) prevModel.getParent();
                    }
                    buffer = new StringBuilder();
                    offset = index;
                    beforeState = state;
                    state = State.BLOCK_START;
                } else {
                    buffer.append(c);
                }
                break;
            case ';' :
                boolean skip = false;
                if (current instanceof MapEntryModel && "url".equals(((MapEntryModel) current).getNameText()) ) {
                    char before = source.charAt(index - 1);
                    if (before != ' ' && before != '\t' && before != '\r' && before != '\n') {
                        skip = true;
                    }
                }
                if (!skip && (State.DEFAULT.equals(state) || State.INDENT.equals(state) || State.BLOCK_START.equals(state))) {
                    parseBuffer(current, buffer, offset, index);
                    AbstractModel prevModel = current;
                    while(true) {
                        if (prevModel == null) {
                            break;
                        }
                        if (prevModel instanceof BlockModel) {
                            current = prevModel;
                            break;
                        }
                        if (prevModel instanceof MultiLineCommentModel) {
                            ((MultiLineCommentModel) prevModel).setLength(offset - prevModel.getOffset());
                        }
                        if (prevModel instanceof MapEntryModel) {
                            ((MapEntryModel) prevModel).setLength(index - prevModel.getOffset());
                        }
                        prevModel = (AbstractModel) prevModel.getParent();
                    }
                    buffer = new StringBuilder();
                    offset = index;
                    beforeState = state;
                    state = State.BLOCK_START;
                } else {
                    buffer.append(c);
                }
                break;
            case '=' :
                if ((State.DEFAULT.equals(state)) && current instanceof MapModel) {
                    MapEntryModel entry = new MapEntryModel();
                    entry.setOffset(offset);
                    entry.setNameText(buffer.toString().trim());
                    current.addChild(entry);
                    current = entry;
                    buffer = new StringBuilder();
                    offset = index;
                    beforeState = state;
                    state = State.BLOCK_START;
                } else {
                    buffer.append(c);
                }
                break;
            case '\\':
            {
                if (State.INDENT.equals(state)) {
                    if (buffer.length() > 0) {
                        buffer = new StringBuilder();
                        offset = index;
                    }
                    beforeState = state;
                    state = State.DEFAULT;
                }
                char next = source.charAt((index + 1));
                switch (next) {
                case '{':
                case '}':
                case ';':
                case '=':
                    buffer.append(c);
                    buffer.append(next);
                    index++;
                    break;
                default:
                    buffer.append(c);
                    break;
                }
                break;
            }
            case ' ' : // not break;
            case '\t':
                buffer.append(c);
                if (State.INDENT.equals(state)) {
                    indentOffset++;
                }
                break;
            default:
                if (State.INDENT.equals(state) || State.BLOCK_START.equals(state)) {
                    if (current instanceof MultiLineCommentModel) {
                        ((MultiLineCommentModel) current).setLength(offset - current.getOffset());
                        current = (AbstractModel) current.getParent();
                    }
                    buffer = new StringBuilder();
                    offset = index;
                    beforeState = state;
                    state = State.DEFAULT;
                }
                buffer.append(c);
                break;
            }
        }
        if (buffer.length() > 0) {
            parseBuffer(current, buffer, offset, length - 1);
        }
        if (current instanceof MultiLineCommentModel) {
            ((MultiLineCommentModel) current).setLength(length - current.getOffset());
            current = (AbstractModel) current.getParent();
        }

        return model;
    }

    private void parseBuffer(AbstractModel current, StringBuilder buffer, int offset, int index) {
        if (buffer.length() > 0 && buffer.charAt(0) == '#') {
            CommentModel comment = new CommentModel();
            comment.setOffset(offset);
            comment.setInput(buffer.toString());
            current.addChild(comment);
            return;
        }
        if (buffer.toString().trim().length() > 0) {
            LiteralValueModel value = new LiteralValueModel();
            value.setOffset(offset);
            value.setInput(buffer.toString().trim());
            current.addChild(value);
        }
        if (current instanceof MapEntryModel) {
            ((MapEntryModel) current).setLength(index - current.getOffset());
        }
    }


}
