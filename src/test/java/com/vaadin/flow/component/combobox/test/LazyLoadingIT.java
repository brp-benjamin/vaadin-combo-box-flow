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
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.flow.component.combobox.testbench.ComboBoxElement;
import com.vaadin.flow.testutil.AbstractComponentIT;
import com.vaadin.flow.testutil.TestPath;
import com.vaadin.testbench.TestBenchElement;

import elemental.json.JsonObject;

@TestPath("lazy-loading")
public class LazyLoadingIT extends AbstractComponentIT {

    private ComboBoxElement stringBox;
    private ComboBoxElement pagesizeBox;
    private ComboBoxElement beanBox;

    @Before
    public void init() {
        open();
        waitUntil(driver -> findElements(By.tagName("vaadin-combo-box"))
                .size() > 0);
        stringBox = $(ComboBoxElement.class).id("lazy-strings");
        pagesizeBox = $(ComboBoxElement.class).id("pagesize");
        beanBox = $(ComboBoxElement.class).id("lazy-beans");
    }

    @Test
    public void initiallyEmpty_openPopup_firstPageLoaded() {
        assertLoadedItemsCount("Lazy loading ComboBox should not have items "
                + "before opening the dropdown.", 0, stringBox);
    }

    @Test
    public void openPopup_firstPageLoaded() {
        stringBox.openPopup();
        assertLoadedItemsCount(
                "After opening the ComboBox, the first 50 items should be loaded",
                50, stringBox);
        assertRendered("Item 10");
    }

    @Test
    public void scrollOverlay_morePagesLoaded() {
        stringBox.openPopup();
        scrollToItem(stringBox, 50);

        assertLoadedItemsCount(
                "There should be 100 items after loading two pages", 100,
                stringBox);
        assertRendered("Item 52");

        scrollToItem(stringBox, 110);

        assertLoadedItemsCount(
                "There should be 150 items after loading three pages", 150,
                stringBox);
        assertRendered("Item 115");
    }

    @Test
    public void clickItem_valueChanged() {
        stringBox.openPopup();
        getItemElements().get(2).click();
        assertMessage("Item 2");
    }

    @Test
    @Ignore
    public void openPopup_setValue_valueChanged_valueShown() {
        stringBox.openPopup();
        clickButton("set-value");
        assertMessage("Item 10");
        Assert.assertEquals(
                "The selected value should be displayed in the ComboBox's TextField",
                "Item 10", getTextFieldValue(stringBox));
        stringBox.openPopup();
        assertItemSelected("Item 10");
    }

    @Test
    public void setValueBeforeLoading_valueChanged_valueShown() {
        $("button").id("set-value").click();
        assertMessage("Item 10");
        Assert.assertEquals(
                "The selected value should be displayed in the ComboBox's TextField",
                "Item 10", getTextFieldValue(stringBox));
        stringBox.openPopup();
        assertItemSelected("Item 10");
    }

    @Test
    public void customPageSize_correctAmountOfItemsRequested() {
        pagesizeBox.openPopup();
        assertLoadedItemsCount(
                "After opening the ComboBox, the first 'pageSize' amount "
                        + "of items should be loaded.",
                180, pagesizeBox);

        scrollToItem(pagesizeBox, 200);

        assertLoadedItemsCount("Expected two pages to be loaded.", 360,
                pagesizeBox);
        assertRendered("Item 200");
    }

    @Test
    @Ignore
    public void loadItems_changeItemLabelGenerator() {
        beanBox.openPopup();
        clickButton("item-label-generator");
        beanBox.openPopup();
        assertRendered("Born 3");

        getItemElements().get(5).click();
        Assert.assertEquals("Born 5", getTextFieldValue(beanBox));

        assertLoadedItemsCount("Only the first page should be loaded.", 50,
                beanBox);
    }

    @Test
    @Ignore
    public void loadItems_changeRenderer() {
        beanBox.openPopup();
        clickButton("component-renderer");
        beanBox.openPopup();
        assertRendered("<flow-component-renderer appid=\"ROOT\">"
                + "<h4>Person 4</h4></flow-component-renderer>");
        assertLoadedItemsCount("Only the first page should be loaded.", 50,
                beanBox);
    }

    @Test
    @Ignore
    public void loadItems_changeDataProvider() {
        beanBox.openPopup();
        clickButton("data-provider");
        beanBox.openPopup();

        assertRendered("Changed 6");
        assertLoadedItemsCount("Only the first page should be loaded.", 50,
                beanBox);
    }

    @Test
    public void setItemLabelGenerator_setComponentRenderer_labelGeneratorUsedForTextField() {
        clickButton("item-label-generator");
        clickButton("component-renderer");
        beanBox.openPopup();
        assertRendered("<flow-component-renderer appid=\"ROOT\">"
                + "<h4>Person 4</h4></flow-component-renderer>");
        getItemElements().get(7).click();
        Assert.assertEquals("Born 7", getTextFieldValue(beanBox));

    }

    private void assertItemSelected(String label) {
        Optional<TestBenchElement> itemElement = getItemElements().stream()
                .filter(element -> getItemLabel(element).equals(label))
                .findFirst();
        Assert.assertTrue(
                "Could not find the item with label '" + label
                        + "' which was expected to be selected.",
                itemElement.isPresent());
        Assert.assertEquals(
                "Expected item element with label '" + label
                        + "' to have 'selected' attribute.",
                true, itemElement.get().getProperty("selected"));
    }

    private String getTextFieldValue(ComboBoxElement comboBox) {
        return (String) executeScript("return arguments[0].inputElement.value",
                comboBox);
    }

    private void assertMessage(String expectedMessage) {
        Assert.assertEquals(expectedMessage, $("div").id("message").getText());
    }

    private void assertLoadedItemsCount(String message, int expectedCount,
            ComboBoxElement comboBox) {
        Assert.assertEquals(message, expectedCount,
                getLoadedItems(comboBox).size());
    }

    // Gets all the loaded json items, but they are not necessarily rendered
    private List<JsonObject> getLoadedItems(ComboBoxElement comboBox) {
        List<JsonObject> list = (List<JsonObject>) executeScript(
                "return arguments[0].filteredItems.filter("
                        + "item => !(item instanceof Vaadin.ComboBoxPlaceholder));",
                comboBox);
        return list;
    }

    private void assertRendered(String innerHTML) {
        List<String> overlayContents = getOverlayContents();
        Optional<String> matchingItem = overlayContents.stream()
                .filter(s -> s.equals(innerHTML)).findFirst();
        Assert.assertTrue(
                "Expected to find an item with rendered innerHTML: " + innerHTML
                        + "\nRendered items: "
                        + overlayContents.stream().reduce("",
                                (result, next) -> String.format("%s\n- %s",
                                        result, next)),
                matchingItem.isPresent());
    }

    // Gets the innerHTML of all the actually rendered item elements.
    // There's more items loaded though.
    private List<String> getOverlayContents() {
        return getItemElements().stream().map(this::getItemLabel)
                .collect(Collectors.toList());
    }

    private String getItemLabel(TestBenchElement itemElement) {
        return itemElement.$("div").id("content")
                .getPropertyString("innerHTML");
    }

    private List<TestBenchElement> getItemElements() {
        return getOverlay().$("div").id("content").$("vaadin-combo-box-item")
                .all();
    }

    private void scrollToItem(ComboBoxElement comboBox, int index) {
        executeScript("arguments[0].$.overlay._scrollIntoView(arguments[1])",
                comboBox, index);
    }

    private TestBenchElement getOverlay() {
        return $("vaadin-combo-box-overlay").first();
    }

    private void clickButton(String id) {
        $("button").id(id).click();
    }

}
