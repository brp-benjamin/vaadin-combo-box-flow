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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.flow.component.combobox.ComboBoxElementUpdated;
import com.vaadin.flow.component.combobox.testbench.ComboBoxElement;
import com.vaadin.flow.testutil.AbstractComponentIT;
import com.vaadin.flow.testutil.TestPath;

@TestPath("data-provider")
public class DataProviderIT extends AbstractComponentIT {
    @Before
    public void init() {
        open();
        waitUntil(driver -> findElements(By.tagName("vaadin-combo-box"))
                .size() > 0);
    }

    @Test
    public void setValue_ProviderHasGetIdAndValueIdExists_selectionTextShouldBeSet() {
        open();
        ComboBoxElement comboBox = $(ComboBoxElementUpdated.class)
                .id(DataProviderPage.COMBO_BOX_WITH_GET_ID_ID);

        // The item must have been communicated before it can be found based on
        // its id
        comboBox.openPopup();

        findElement(By.id(DataProviderPage.SET_VALUE_USING_GET_ID_BUTTON_ID))
                .click();

        Assert.assertEquals(
                "ComboBox::setValue must use DataProvider::getId to find the item.",
                "b, Second", comboBox.getSelectedText());
    }

    @Test
    public void setValue_ValueReferenceExists_selectionTextShouldBeSet() {
        open();
        findElement(By.id(DataProviderPage.SET_VALUE_USING_REFERENCE_BUTTON_ID))
                .click();
        ComboBoxElement comboBox = $(ComboBoxElementUpdated.class)
                .id(DataProviderPage.COMBO_BOX_WITHOUT_GET_ID_ID);
        Assert.assertEquals(
                "ComboBox::setValue must use object reference to find the item.",
                "b, Second", comboBox.getSelectedText());
    }

    @Test
    public void setValue_ValueEqualToAnExistingItem_selectionTextShouldBeSet() {
        open();
        ComboBoxElement comboBox = $(ComboBoxElementUpdated.class)
                .id(DataProviderPage.COMBO_BOX_WITHOUT_GET_ID_ID);

        // The item must have been communicated before it can be found based on
        // equals
        comboBox.openPopup();

        findElement(By.id(DataProviderPage.SET_VALUE_USING_EQUALS_BUTTON_ID))
                .click();
        Assert.assertEquals(
                "ComboBox::setValue must use Object::equals to find the item.",
                "c, Third", comboBox.getSelectedText());
    }
}
