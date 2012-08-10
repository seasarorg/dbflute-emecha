/**
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.dfassist.jdt.derived;

import java.util.HashMap;
import java.util.Map;

/**
 * 集計関数の種類
 * @author m-saito
 */
public enum DerivedType {
     MAX           ("max")
    ,MIN           ("min")
    ,SUM           ("sum")
    ,AVG           ("avg")
    ,COUNT         ("count")
    ,COUNT_DISTINCT ("countDistinct")
    ;
    private final String methodName;
    private DerivedType(String name) {
        methodName = name;
    }
    private static final Map<String, DerivedType> map;
    static {
        map = new HashMap<String, DerivedType>();
        for (DerivedType type : DerivedType.values()) {
            map.put(type.getMethodName(), type);
        }
    }
    public String getMethodName() {
        return methodName;
    }
    public static DerivedType nameOf(String name) {
        return map.get(name);
    }

}
