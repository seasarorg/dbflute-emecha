package org.seaser.dbflute.emecha.eclipse.plugin.dfeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public enum DfColor {

	DEFAULT(new RGB(0, 0, 0)),
	COMMENT(new RGB(0, 128, 0)),
	ELEMENT(new RGB(255, 64, 64)),
	VALIABLE(new RGB(100, 100, 255)),
	MAP(new RGB(128, 0, 128), SWT.BOLD),
	LIST(new RGB(128, 0, 128), SWT.BOLD),
	SQL(new RGB(0, 128, 128)),
	BIND(new RGB(0, 0, 255));

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