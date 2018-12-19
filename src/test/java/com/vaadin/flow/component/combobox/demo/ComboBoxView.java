/*
 * Copyright 2000-2017 Vaadin Ltd.
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
package com.vaadin.flow.component.combobox.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javafaker.Faker;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBox.ItemFilter;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.demo.DemoView;
import com.vaadin.flow.dom.ElementConstants;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.data.provider.*;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;

/**
 * View for {@link ComboBox} demo.
 *
 * @author Vaadin Ltd
 */
@Route("vaadin-combo-box")
public class ComboBoxView extends DemoView {

    /**
     * Example object.
     */
    public static class Song {
        private String name;
        private String artist;
        private String album;

        /**
         * Default constructor.
         */
        public Song() {
        }

        /**
         * Construct a song with the given name, artist and album.
         *
         * @param name
         *            name of the song
         * @param artist
         *            name of the artist
         * @param album
         *            name of the album
         */
        public Song(String name, String artist, String album) {
            this.name = name;
            this.artist = artist;
            this.album = album;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }
    }

    @Override
    public void initView() {
        demoOverlayError();
        demoEmptyDoesntClose();
    }

    private void demoOverlayError() {
        OrderedList instructions = new OrderedList(
            new ListItem("Change the Species combo box to 'Cat'"),
            new ListItem("Open the Name combo box"),
            new ListItem("Observe the error")
        );

        // begin-source-example
        // source-example-heading: Card overlay error
        List<String> dogs = Arrays.asList("Fido", "Pluto");
        List<String> cats = Arrays.asList("Mittens");

        ComboBox<String> speciesComboBox = new ComboBox<>("Species");
        ComboBox<String> nameComboBox = new ComboBox<>("Name");
        
        speciesComboBox.setItems("Cat", "Dog");
        speciesComboBox.setRequired(true);
        speciesComboBox.setValue("Dog");
        speciesComboBox.addValueChangeListener(change -> {
            nameComboBox.setValue(null);
            nameComboBox.getDataProvider().refreshAll();
        });

        FetchCallback<String, String> fetch = query -> (speciesComboBox.getValue() == "Dog" ? dogs : cats).stream().limit(query.getLimit()).skip(query.getOffset());
        CountCallback<String, String> count = query -> (speciesComboBox.getValue() == "Dog" ? dogs : cats).size();
        nameComboBox.setDataProvider(DataProvider.fromFilteringCallbacks(fetch, count));
        // end-source-example

        addCard("Card overlay error", instructions, speciesComboBox, nameComboBox);
    }
    
    private void demoEmptyDoesntClose() {
        OrderedList instructions = new OrderedList(
            new ListItem("Open the empty combo box"),
            new ListItem("Observe the error")
        );

        // begin-source-example
        // source-example-heading: Empty list error
        ComboBox<String> comboBox = new ComboBox<>("Empty");
        List<String> items = Collections.emptyList();
        
        FetchCallback<String, String> fetch = query -> items.stream().limit(query.getLimit()).skip(query.getOffset());
        CountCallback<String, String> count = query -> 0;
        comboBox.setDataProvider(DataProvider.fromFilteringCallbacks(fetch, count));
        // end-source-example

        addCard("Empty list error", instructions, comboBox);
    }
}
