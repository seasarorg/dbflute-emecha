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
package org.seasar.dbflute.emecha.eclipse.plugin.dfeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public enum DfColor {

    DEFAULT(new RGB(0, 0, 0)),
    LINE_COMMENT(new RGB(0, 128, 0)),
    LIKE_SEARCH_MARK(new RGB(0, 0, 255)),
    VALIABLE(new RGB(100, 100, 255)),
    //FixedLiteralMark
    FIXED_LITERAL_MARK(new RGB(128, 0, 128), SWT.BOLD),
    SQL(new RGB(0, 128, 128)),
    ALIAS_MARK(new RGB(0, 0, 255));

    private RGB _foreground;
    private RGB _background = null;
    private int _style = SWT.NORMAL;
    private DfColor(RGB foreground) {
        this._foreground = foreground;
    }
    private DfColor(RGB foreground, int style) {
        this._foreground = foreground;
        this._style = style;
    }
    public RGB getForeground() {
        return _foreground;
    }
    public void setForeground(RGB _foreground) {
        this._foreground = _foreground;
    }
    public RGB getBackground() {
        return _background;
    }
    public void setBackground(RGB _background) {
        this._background = _background;
    }
    public int getStyle() {
        return _style;
    }
    public void setStyle(int _style) {
        this._style = _style;
    }
}
