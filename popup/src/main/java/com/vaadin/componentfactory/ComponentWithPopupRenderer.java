/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.componentfactory;

import java.util.concurrent.atomic.AtomicLong;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableFunction;

/**
 * A renderer that renders a provided component along with a Popup generated
 * by a {@link PopupGenerator} in a wrapper container.
 *
 * @param <ITEM> the type of the input object that can be used by the rendered
 *               component
 * @author Vaadin Ltd
 */
public class ComponentWithPopupRenderer<ITEM> extends ComponentRenderer<Component, ITEM> {

    protected PopupGenerator<ITEM> itemPopupGenerator;

    protected SerializableFunction<ITEM, ? extends Component> componentFunction;

    private static final AtomicLong idCounter = new AtomicLong(0);


    /**
     * Creates a new renderer instance using the provided
     * {@code componentFunction} and {@code itemPopupGenerator}.
     *
     * @param itemPopupGenerator the item popup generator
     * @param componentFunction  function which returns the component that
     *                           will be rendered in the grid. A popup will appear
     *                           when this component is clicked.
     */
    public ComponentWithPopupRenderer(SerializableFunction<ITEM, ? extends Component> componentFunction,
                                      PopupGenerator<ITEM> itemPopupGenerator) {
        this.itemPopupGenerator = itemPopupGenerator;
        this.componentFunction = componentFunction;
    }

    @Override
    public Component createComponent(ITEM item) {
        HasComponents container = createWrappingContainer();

        final Component component = addComponentToContainer(item, container);
        addPopupToContainer(item, container, component);

        return (Component) container;
    }

    protected void addPopupToContainer(ITEM item, HasComponents container, Component component) {
        Popup popup = itemPopupGenerator.apply(item);
        popup.setFor(component.getId().orElse(null));
        container.add(popup);
    }

    protected Component addComponentToContainer(ITEM item, HasComponents container) {
        final Component component = componentFunction.apply(item);
        component.setId(createUniqueId());
        container.add(component);
        return component;
    }

    protected HasComponents createWrappingContainer() {
        return new Div();
    }

    protected String createUniqueId() {
        return "item-with-popup-" + idCounter.incrementAndGet();
    }
}
