/**
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.dfeditor;

import org.eclipse.jface.text.rules.Token;

/**
 * @author masa
 */
@Deprecated
public enum DfPropState {

	DEFAULT(null),
	COMMENT("___df_comment"),
	SEPARATER("___df_separater"),
	BRACKET("___df_bracket"),
	ELEMENT("___df_element"),
	VALIABLE("___df_valiable")
	;
	private String _partition;
	private DfPropState(String partition) {
		this._partition = partition;
	}
	/**
	 * パーティション名を取得する。
	 * @return
	 */
	public String getPartition() {
		return this._partition;
	}
	/**
	 * パーティションに対応するTokenを取得する
	 * @return
	 */
	public Token getToken() {
		return new Token(this._partition);
	}
}
