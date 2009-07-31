package org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.scanner;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.DFPropPartitions;

@Deprecated
public class FastDFPropPartitionScanner implements IPartitionTokenScanner, DFPropPartitions {

	// states
	private static final int DFPROP = 0;
	private static final int COMMENT = 1;
	private static final int CHARACTER = 2;
	private static final int STRING = 3;


	private enum BC {
		NONE(0),
		SHARP(1),
		SLASH(1),
		SLASH_STAR(2),
		CARRIAGE_RETURN(1)
		;
		private int _length;
		private BC(int length) {
			this._length = length;
		}
		public static int getLength(BC bc){
			return bc == null ? -1 : bc._length;
		}
	}

	// beginning of prefixes and postfixes
	private static final int NONE = 0;
//	private static final int BACKSLASH= 1; // postfix for STRING and CHARACTER
	private static final int SHARP = 2; // prefix for SINGLE_LINE Comment
	private static final int SLASH_STAR = 3; // prefix for SQL Comment
//	private static final int SLASH_STAR_STAR= 4; // prefix for MULTI_LINE_COMMENT or JAVADOC
//	private static final int STAR= 5; // postfix for MULTI_LINE_COMMENT or JAVADOC
	private static final int CARRIAGE_RETURN=6; // postfix for STRING, CHARACTER and SINGLE_LINE_COMMENT

	private final Token[] tokens = new Token[]{
		new Token(null),
		new Token(DFP_COMMENT),
		new Token(DFP_CHARACTER),
		new Token(DFP_STRING)
	};

	/** The scanner. */
	private BufferedDocumentScanner scanner = new BufferedDocumentScanner(100);
	/** The offset of the last returned token. */
	private int fTokenOffset;
	/** The length of the last returned token. */
	private int fTokenLength;

	/** The state of the scanner. */
	private int fState;
	/** The last significant characters read. */
	private int fLast;
	/** The amount of characters already read on first call to nextToken(). */
	private int fPrefixLength;


	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jface.text.rules.ITokenScanner#nextToken()
	 */
	public IToken nextToken() {
		// TODO 自動生成されたメソッド・スタブ
		fTokenOffset += fTokenLength;
		fTokenLength= fPrefixLength;

		IToken token = null;
		while (token == null) {
			token = serchNextToken();
		}
		return token;
	}

	protected IToken serchNextToken() {
		final int ch = scanner.read();
		switch (ch) {
			case ICharacterScanner.EOF:
				if (fTokenLength > 0) {
					fLast= NONE; // ignore last
					return preFix(fState, DFPROP, NONE, 0);
				} else {
		 			fLast= NONE;
		 			fPrefixLength= 0;
					return Token.EOF;
				}
			case '#':
				return new Token(DFP_COMMENT);
			default:
				break;
		}
		return null;
	}

	private IToken preFix(int state, int newState, int last, int prefixLength) {
		fTokenLength -= getLastLength(fLast);
		fLast= last;
		fPrefixLength= prefixLength;
		IToken token= tokens[state];
		fState= newState;
		return token;
	}

	private static final int getLastLength(int last) {
		switch (last) {
		default:
			return -1;

		case NONE:
			return 0;

		case CARRIAGE_RETURN:
		case SHARP:
			return 1;

		case SLASH_STAR:
			return 2;
		}
	}


	private int getState(String contentType) {
		if (contentType == null)
			return DFPROP;

		else if ( DFP_COMMENT.equals(contentType) )
			return COMMENT;

		else if ( DFP_CHARACTER.equals(contentType) )
			return CHARACTER;

		else if ( DFP_STRING.equals(contentType) )
			return STRING;

		else
			return DFPROP;
	}

	/**
	 * {@inheritDoc}
	 * @see IPartitionTokenScanner#setPartialRange(IDocument, int, int, String, int)
	 */
	public void setPartialRange(IDocument document, int offset, int length,
			String contentType, int partitionOffset) {
		// TODO 自動生成されたメソッド・スタブ
		scanner.setRange(document, offset, length);
		fTokenOffset= partitionOffset;
		fTokenLength= 0;
		fPrefixLength= offset - partitionOffset;
		fLast= NONE;
		if (offset == partitionOffset) {
			fState = DFPROP;
		} else {
			fState= getState(contentType);
		}

	}

	/**
	 * {@inheritDoc}
	 * @see ITokenScanner#setRange(IDocument, int, int)
	 */
	public void setRange(IDocument document, int offset, int length) {
		scanner.setRange(document, offset, length);
		fTokenOffset= offset;
		fTokenLength= 0;
		fPrefixLength= 0;
		fLast = NONE;
		fState = DFPROP;
	}

	/**
	 * {@inheritDoc}
	 * @see ITokenScanner#getTokenLength()
	 */
	public int getTokenLength() {
		return fTokenLength;
	}

	/**
	 * {@inheritDoc}
	 * @see ITokenScanner#getTokenOffset()
	 */
	public int getTokenOffset() {
		return fTokenOffset;
	}
}
