package org.seasar.dbflute.emecha.eclipse.plugin.dfeditor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.scanner.DFPropPartitionScanner;

public class DFPropDocumentProvider extends FileDocumentProvider implements DFPropPartitions {

	private static final String[] LEGAL_CONTENT_TYPES = new String[] {
								DFP_PARTITIONING,
								DFP_COMMENT,
							};
	public DFPropDocumentProvider() {
		super();
	}

	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					new DFPropPartitionScanner(),
					LEGAL_CONTENT_TYPES);
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
}