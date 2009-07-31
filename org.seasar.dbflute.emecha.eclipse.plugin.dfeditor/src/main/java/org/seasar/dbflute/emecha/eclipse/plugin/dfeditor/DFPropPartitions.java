package org.seasar.dbflute.emecha.eclipse.plugin.dfeditor;

/**
 * Definition of dfprop partitioning and its partitions.
 * @since 3.1
 */
public interface DFPropPartitions {

	/** The identifier of the single-line end comment partition content type. */
	String DFP_COMMENT = "__dfp_comment";

	/** The identifier of the dfprop partitioning. */
	String DFP_PARTITIONING = "__dfp_partitioning";

	String DFP_CHARACTER = "__dfp_character";

	String DFP_STRING = "__dfp_string";

	String DFP_TAG ="__dfp_tag";
//
//	/** The identifier of the sql comment partition content type. */
//	String DFP_SQL_COMMENT = "__dfp_sql_comment";
}
