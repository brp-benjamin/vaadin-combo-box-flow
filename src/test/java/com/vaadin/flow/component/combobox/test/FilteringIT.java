/*
 * Copyright 2000-2018 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.component.combobox.test;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.flow.component.combobox.testbench.ComboBoxElement;
import com.vaadin.flow.testutil.TestPath;

@TestPath("filtering")
public class FilteringIT extends AbstractComboBoxIT {

    private ComboBoxElement box;

    @Before
    public void init() {
        open();
        waitUntil(driver -> findElements(By.tagName("vaadin-combo-box"))
                .size() > 0);
        box = $(ComboBoxElement.class).first();
    }

    @Test
    public void itemsLessThanPageSize_clientSideFiltering() {
        box.openPopup();
        assertClientSideFilter(true);
    }

    @Test
    public void clientSideFiltering_lowerCaseContains() {
        box.openPopup();
        List<String> filteredItems = setFilterAndGetImmediateResults("item 20");
        Assert.assertEquals(
                "Expected one item to match the client-side filtering 'item 20'.",
                1, filteredItems.size());
        Assert.assertEquals("Unexpected item to match the filter.", "Item 20",
                filteredItems.get(0));

        filteredItems = setFilterAndGetImmediateResults("M 10");
        Assert.assertEquals(
                "Expected one item to match the client-side filtering 'M 10'.",
                1, filteredItems.size());
        Assert.assertEquals("Unexpected item to match the filter.", "Item 10",
                filteredItems.get(0));
    }

    @Test
    public void itemsMoreThanPageSize_serverSideFiltering() {
        clickButton("add-items");
        box.openPopup();
        assertClientSideFilter(false);
    }

    @Test
    public void loadItems_addItemsToCrossPageSize_switchToServerSideFiltering() {
        box.openPopup();
        clickButton("add-items");
        box.openPopup();
        assertRendered("Item 8");
        assertClientSideFilter(false);
    }

    @Test
    public void removeItemsToCrossPageSize_switchToClientSideFiltering() {
        clickButton("add-items");
        box.openPopup();
        clickButton("remove-items");
        box.openPopup();
        assertClientSideFilter(true);
    }

    @Test
    public void useItemFilterWithLessThanPageSize_serverSideFiltering() {
        clickButton("item-filter");
        box.openPopup();
        assertClientSideFilter(false);

        Assert.assertEquals("No item should match the ItemFilter (startsWith).",
                0, getNonEmptyOverlayContents().size());

        setFilterAndGetImmediateResults("Ite");
        Assert.assertEquals(
                "All the items should match the ItemFilter (startsWith).", 40,
                getNonEmptyOverlayContents().size());
    }

    private void assertClientSideFilter(boolean clientSide) {

        List<String> items = setFilterAndGetImmediateResults("3");

        if (clientSide) {
            Assert.assertEquals("Unexpected amount of filtered items. "
                    + "Expected the items to be already filtered synchronously in client-side.",
                    13, items.size());
            items.forEach(item -> {
                Assert.assertThat(
                        "Found an item which doesn't match the filter. "
                                + "Expected the items to be already filtered synchronously in client-side.",
                        item, CoreMatchers.containsString("3"));
            });
        } else {
            Assert.assertEquals("Expected server-side filtering, so there "
                    + "should be no filtered items until server has responded.",
                    0, items.size());

            waitUntil(driver -> getNonEmptyOverlayContents().size() > 10);
            getNonEmptyOverlayContents().forEach(rendered -> {
                Assert.assertThat(
                        "Item which doesn't match the filter was found after server-side filtering.",
                        rendered, CoreMatchers.containsString("3"));
            });
        }
    }

    private List<String> setFilterAndGetImmediateResults(String filter) {
        /*
         * If combo box is doing client-side filtering, filteredItems is changed
         * synchronously after changing the filter.
         */
        String script = String.format("const box = arguments[0];" //
                + "box.filter = '%s';" //
                + "return box.filteredItems.map(item => item.label);", filter);
        return (List<String>) executeScript(script, box);
    }
}