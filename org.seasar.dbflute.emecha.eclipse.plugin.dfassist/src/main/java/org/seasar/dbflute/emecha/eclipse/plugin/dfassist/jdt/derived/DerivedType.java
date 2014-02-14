/*
 * Copyright 2013 the Seasar Foundation and the Others.
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
