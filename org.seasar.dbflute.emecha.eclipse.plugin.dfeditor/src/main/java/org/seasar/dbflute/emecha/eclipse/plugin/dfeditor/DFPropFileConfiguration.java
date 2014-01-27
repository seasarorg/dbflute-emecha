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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.action.DFPropDoubleClickStrategy;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.scanner.BsDFPropScanner;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.scanner.DFPropCommentScanner;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.scanner.DefaultTokenScanner;

/**
 * DBFlute Property File Source View Configuration.
 */
public class DFPropFileConfiguration extends TextSourceViewerConfiguration implements DFPropPartitions {

    private DFPropDoubleClickStrategy doubleClickStrategy;

    private BsDFPropScanner commentScanner;
    private BsDFPropScanner defaultScanner;
    private DfColorManager colorManager;

    public DFPropFileConfiguration(DfColorManager colorManager, IPreferenceStore preferenceStore) {
        super(preferenceStore);
        this.colorManager = colorManager;
        if (preferenceStore != null) {
            // XXX TAG WHIDTH SETTING
            preferenceStore.setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH, 4);
        }
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredContentTypes(org.eclipse.jface.text.source.ISourceViewer)
     */
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        return new String[] {
                IDocument.DEFAULT_CONTENT_TYPE,
                DFP_PARTITIONING,
                DFP_COMMENT
                };
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getDoubleClickStrategy(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
     */
    public ITextDoubleClickStrategy getDoubleClickStrategy(
            ISourceViewer sourceViewer,
            String contentType) {
        if (doubleClickStrategy == null)
            doubleClickStrategy = new DFPropDoubleClickStrategy();
        return doubleClickStrategy;
    }

    protected ITokenScanner getCommentScanner() {
        if (commentScanner == null) {
            commentScanner = new DFPropCommentScanner(colorManager, fPreferenceStore);
        }
        return commentScanner;
    }

    protected ITokenScanner getDefaultScanner() {
        if (defaultScanner == null)
            defaultScanner = new DefaultTokenScanner(colorManager, fPreferenceStore);
        return defaultScanner;
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(org.eclipse.jface.text.source.ISourceViewer)
     */
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        PresentationReconciler reconciler = new PresentationReconciler();

        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getCommentScanner());
        reconciler.setDamager(dr, DFP_COMMENT);
        reconciler.setRepairer(dr, DFP_COMMENT);

        dr = new DefaultDamagerRepairer(getDefaultScanner());
        reconciler.setDamager(dr, DFP_PARTITIONING);
        reconciler.setRepairer(dr, DFP_PARTITIONING);

        dr = new DefaultDamagerRepairer(getDefaultScanner());
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

        return reconciler;
    }

    /**
     * Extended to support the toggle comment.
     * {@inheritDoc}
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getDefaultPrefixes(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
     */
    @Override
    public String[] getDefaultPrefixes(ISourceViewer sourceViewer, String contentType) {
        return new String[] {"#"};
    }
}