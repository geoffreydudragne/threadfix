////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2015 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.importer.impl.upload.fortify;

import org.junit.Test;

import java.util.Map;

import static com.denimgroup.threadfix.CollectionUtils.map;

/**
 * Created by mcollins on 3/5/15.
 */
public class FortifyFilterTests {

    @Test
    public void testCategoryFilterNull() {
        String test = getFilterResult("test");

        assert test == null : "Got non-null result for dummy category.";
    }

    @Test
    public void testCategoryFilterNotNull() {
        String result = getFilterResult("use after free");

        assert "Critical".equals(result) : "Got " + result + " instead of Critical.";
    }

    @Test
    public void testCategoryFilterUpperCase() {
        String result = getFilterResult("Use After Free");

        assert "Critical".equals(result) : "Got " + result + " instead of Critical.";
    }

    private String getFilterResult(String value) {
        FortifyFilter filter = getFortifyFilter();

        return filter.getFinalSeverity(value);
    }

    private FortifyFilter getFortifyFilter() {
        Map<Key, String> filterMap = map(
                Key.SEVERITY, "Critical",
                Key.QUERY, "category:\"use after free\""
        );

        return new FortifyFilter(filterMap);
    }

}
