/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pivot.wtk.skin.terra;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import pivot.collections.ArrayList;
import pivot.collections.List;
import pivot.collections.Sequence;
import pivot.wtk.Checkbox;
import pivot.wtk.Component;
import pivot.wtk.Dimensions;
import pivot.wtk.Insets;
import pivot.wtk.Keyboard;
import pivot.wtk.ListView;
import pivot.wtk.ListViewItemStateListener;
import pivot.wtk.ListViewListener;
import pivot.wtk.ListViewItemListener;
import pivot.wtk.ListViewSelectionListener;
import pivot.wtk.Mouse;
import pivot.wtk.Bounds;
import pivot.wtk.Span;
import pivot.wtk.Theme;
import pivot.wtk.skin.ComponentSkin;

/**
 * List view skin.
 * <p>
 * NOTE This skin assumes a fixed renderer height.
 *
 * @author gbrown
 */
public class TerraListViewSkin extends ComponentSkin implements ListView.Skin,
    ListViewListener, ListViewItemListener, ListViewItemStateListener,
    ListViewSelectionListener {
    private Font font;
    private Color color;
    private Color disabledColor;
    private Color backgroundColor;
    private Color selectionColor;
    private Color selectionBackgroundColor;
    private Color inactiveSelectionColor;
    private Color inactiveSelectionBackgroundColor;
    private Color highlightColor;
    private Color highlightBackgroundColor;
    private boolean showHighlight;
    private Insets checkboxPadding = new Insets(2, 2, 2, 0);

    private int highlightedIndex = -1;
    private int editIndex = -1;

    private static final Checkbox CHECKBOX = new Checkbox();

    static {
        CHECKBOX.setSize(CHECKBOX.getPreferredSize());
    }

    public TerraListViewSkin() {
        TerraTheme theme = (TerraTheme)Theme.getTheme();
        font = theme.getFont();
        color = theme.getColor(1);
        disabledColor = theme.getColor(7);
        backgroundColor = theme.getColor(4);
        selectionColor = theme.getColor(4);
        selectionBackgroundColor = theme.getColor(19);
        inactiveSelectionColor = theme.getColor(1);
        inactiveSelectionBackgroundColor = theme.getColor(9);
        highlightColor = theme.getColor(1);
        highlightBackgroundColor = theme.getColor(10);
        showHighlight = true;
    }

    public void install(Component component) {
        super.install(component);

        ListView listView = (ListView)component;
        listView.getListViewListeners().add(this);
        listView.getListViewItemListeners().add(this);
        listView.getListViewItemStateListeners().add(this);
        listView.getListViewSelectionListeners().add(this);
    }

    public void uninstall() {
        ListView listView = (ListView)getComponent();
        listView.getListViewListeners().remove(this);
        listView.getListViewItemListeners().remove(this);
        listView.getListViewItemStateListeners().remove(this);
        listView.getListViewSelectionListeners().remove(this);

        super.uninstall();
    }

    @SuppressWarnings("unchecked")
    public int getPreferredWidth(int height) {
        int preferredWidth = 0;

        ListView listView = (ListView)getComponent();
        List<Object> listData = (List<Object>)listView.getListData();

        ListView.ItemRenderer renderer = listView.getItemRenderer();

        for (Object item : listData) {
            renderer.render(item, listView, false, false, false, false);
            preferredWidth = Math.max(preferredWidth, renderer.getPreferredWidth(-1));
        }

        if (listView.getCheckmarksEnabled()) {
            preferredWidth += CHECKBOX.getWidth() + (checkboxPadding.left
                + checkboxPadding.right);
        }

        return preferredWidth;
    }

    @SuppressWarnings("unchecked")
    public int getPreferredHeight(int width) {
        int preferredHeight = 0;

        ListView listView = (ListView)getComponent();
        List<Object> listData = (List<Object>)listView.getListData();
        preferredHeight = listData.getLength() * getItemHeight();

        return preferredHeight;
    }

    public Dimensions getPreferredSize() {
        return new Dimensions(getPreferredWidth(-1), getPreferredHeight(-1));
    }

    public void layout() {
        // No-op
    }

    @SuppressWarnings("unchecked")
    public void paint(Graphics2D graphics) {
        ListView listView = (ListView)getComponent();
        List<Object> listData = (List<Object>)listView.getListData();
        ListView.ItemRenderer renderer = listView.getItemRenderer();

        int width = getWidth();
        int height = getHeight();
        int itemHeight = getItemHeight();

        // Paint the background
        graphics.setPaint(backgroundColor);
        graphics.fillRect(0, 0, width, height);

        // Paint the list contents
        int itemStart = 0;
        int itemEnd = listData.getLength() - 1;

        // Ensure that we only paint items that are visible
        Rectangle clipBounds = graphics.getClipBounds();
        if (clipBounds != null) {
            itemStart = Math.max(itemStart, (int)Math.floor(clipBounds.y
                / (double)itemHeight));
            itemEnd = Math.min(itemEnd, (int)Math.ceil((clipBounds.y
                + clipBounds.height) / (double)itemHeight) - 1);
        }

        for (int itemIndex = itemStart; itemIndex <= itemEnd; itemIndex++) {
            Object item = listData.get(itemIndex);
            boolean highlighted = (itemIndex == highlightedIndex
                && listView.getSelectMode() != ListView.SelectMode.NONE);
            boolean selected = listView.isItemSelected(itemIndex);
            boolean disabled = listView.isItemDisabled(itemIndex);

            Color itemBackgroundColor = null;

            if (selected) {
                itemBackgroundColor = (listView.isFocused())
                    ? this.selectionBackgroundColor : inactiveSelectionBackgroundColor;
            } else {
                if (highlighted && showHighlight && !disabled) {
                    itemBackgroundColor = highlightBackgroundColor;
                }
            }

            if (itemBackgroundColor != null) {
                graphics.setPaint(itemBackgroundColor);
                graphics.fillRect(0, itemIndex * itemHeight, width, itemHeight);
            }

            int itemX = 0;
            int itemY = itemIndex * itemHeight;

            boolean checked = false;
            if (listView.getCheckmarksEnabled()) {
                checked = listView.isItemChecked(itemIndex);

                int checkboxY = (itemHeight - CHECKBOX.getHeight()) / 2;
                Graphics2D checkboxGraphics = (Graphics2D)graphics.create(checkboxPadding.left,
                    itemY + checkboxY, CHECKBOX.getWidth(), CHECKBOX.getHeight());

                CHECKBOX.setSelected(checked);
                CHECKBOX.paint(checkboxGraphics);
                checkboxGraphics.dispose();

                itemX = CHECKBOX.getWidth() + (checkboxPadding.left
                    + checkboxPadding.right);
            }

            // Paint the data
            Graphics2D rendererGraphics = (Graphics2D)graphics.create(itemX, itemY,
                width, itemHeight);

            renderer.render(item, listView, selected, checked, highlighted, disabled);
            renderer.setSize(width, itemHeight);
            renderer.paint(rendererGraphics);
            rendererGraphics.dispose();
        }
    }

    // List view skin methods
    @SuppressWarnings("unchecked")
    public int getItemAt(int y) {
        if (y < 0) {
            throw new IllegalArgumentException("y is negative");
        }

        ListView listView = (ListView)getComponent();

        int index = (y / getItemHeight());
        List<Object> listData = (List<Object>)listView.getListData();

        if (index >= listData.getLength()) {
            index = -1;
        }

        return index;
    }

    public Bounds getItemBounds(int index) {
        int itemHeight = getItemHeight();
        return new Bounds(0, index * itemHeight, getWidth(), itemHeight);
    }

    public int getItemIndent() {
        int itemIndent = 0;

        ListView listView = (ListView)getComponent();
        if (listView.getCheckmarksEnabled()) {
            itemIndent = CHECKBOX.getWidth() + checkboxPadding.left + checkboxPadding.right;
        }

        return itemIndent;
    }

    public int getItemHeight() {
        ListView listView = (ListView)getComponent();
        ListView.ItemRenderer renderer = listView.getItemRenderer();

        int itemHeight = renderer.getPreferredHeight(-1);
        if (listView.getCheckmarksEnabled()) {
            itemHeight = Math.max(CHECKBOX.getHeight() + (checkboxPadding.top
                + checkboxPadding.bottom), itemHeight);
        }

        return itemHeight;
    }

    @Override
    public boolean isFocusable() {
        ListView listView = (ListView)getComponent();
        return (listView.getSelectMode() != ListView.SelectMode.NONE);
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("font is null.");
        }

        this.font = font;
        invalidateComponent();
    }

    public final void setFont(String font) {
        if (font == null) {
            throw new IllegalArgumentException("font is null.");
        }

        setFont(Font.decode(font));
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color is null.");
        }

        this.color = color;
        repaintComponent();
    }

    public final void setColor(String color) {
        if (color == null) {
            throw new IllegalArgumentException("color is null.");
        }

        setColor(decodeColor(color));
    }

    public Color getDisabledColor() {
        return disabledColor;
    }

    public void setDisabledColor(Color disabledColor) {
        if (disabledColor == null) {
            throw new IllegalArgumentException("disabledColor is null.");
        }

        this.disabledColor = disabledColor;
        repaintComponent();
    }

    public final void setDisabledColor(String disabledColor) {
        if (disabledColor == null) {
            throw new IllegalArgumentException("disabledColor is null.");
        }

        setDisabledColor(decodeColor(disabledColor));
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        if (backgroundColor == null) {
            throw new IllegalArgumentException("backgroundColor is null.");
        }

        this.backgroundColor = backgroundColor;
        repaintComponent();
    }

    public final void setBackgroundColor(String backgroundColor) {
        if (backgroundColor == null) {
            throw new IllegalArgumentException("backgroundColor is null.");
        }

        setBackgroundColor(decodeColor(backgroundColor));
    }


    public Color getSelectionColor() {
        return selectionColor;
    }

    public void setSelectionColor(Color selectionColor) {
        if (selectionColor == null) {
            throw new IllegalArgumentException("selectionColor is null.");
        }

        this.selectionColor = selectionColor;
        repaintComponent();
    }

    public final void setSelectionColor(String selectionColor) {
        if (selectionColor == null) {
            throw new IllegalArgumentException("selectionColor is null.");
        }

        setSelectionColor(decodeColor(selectionColor));
    }

    public Color getSelectionBackgroundColor() {
        return selectionBackgroundColor;
    }

    public void setSelectionBackgroundColor(Color selectionBackgroundColor) {
        if (selectionBackgroundColor == null) {
            throw new IllegalArgumentException("selectionBackgroundColor is null.");
        }

        this.selectionBackgroundColor = selectionBackgroundColor;
        repaintComponent();
    }

    public final void setSelectionBackgroundColor(String selectionBackgroundColor) {
        if (selectionBackgroundColor == null) {
            throw new IllegalArgumentException("selectionBackgroundColor is null.");
        }

        setSelectionBackgroundColor(decodeColor(selectionBackgroundColor));
    }

    public Color getInactiveSelectionColor() {
        return inactiveSelectionColor;
    }

    public void setInactiveSelectionColor(Color inactiveSelectionColor) {
        if (inactiveSelectionColor == null) {
            throw new IllegalArgumentException("inactiveSelectionColor is null.");
        }

        this.inactiveSelectionColor = inactiveSelectionColor;
        repaintComponent();
    }

    public final void setInactiveSelectionColor(String inactiveSelectionColor) {
        if (inactiveSelectionColor == null) {
            throw new IllegalArgumentException("inactiveSelectionColor is null.");
        }

        setInactiveSelectionColor(decodeColor(inactiveSelectionColor));
    }

    public Color getInactiveSelectionBackgroundColor() {
        return inactiveSelectionBackgroundColor;
    }

    public void setInactiveSelectionBackgroundColor(Color inactiveSelectionBackgroundColor) {
        if (inactiveSelectionBackgroundColor == null) {
            throw new IllegalArgumentException("inactiveSelectionBackgroundColor is null.");
        }

        this.inactiveSelectionBackgroundColor = inactiveSelectionBackgroundColor;
        repaintComponent();
    }

    public final void setInactiveSelectionBackgroundColor(String inactiveSelectionBackgroundColor) {
        if (inactiveSelectionBackgroundColor == null) {
            throw new IllegalArgumentException("inactiveSelectionBackgroundColor is null.");
        }

        setInactiveSelectionBackgroundColor(decodeColor(inactiveSelectionBackgroundColor));
    }

    public Color getHighlightColor() {
        return highlightColor;
    }

    public void setHighlightColor(Color highlightColor) {
        if (highlightColor == null) {
            throw new IllegalArgumentException("highlightColor is null.");
        }

        this.highlightColor = highlightColor;
        repaintComponent();
    }

    public final void setHighlightColor(String highlightColor) {
        if (highlightColor == null) {
            throw new IllegalArgumentException("highlightColor is null.");
        }

        setHighlightColor(decodeColor(highlightColor));
    }

    public Color getHighlightBackgroundColor() {
        return highlightBackgroundColor;
    }

    public void setHighlightBackgroundColor(Color highlightBackgroundColor) {
        if (highlightBackgroundColor == null) {
            throw new IllegalArgumentException("highlightBackgroundColor is null.");
        }

        this.highlightBackgroundColor = highlightBackgroundColor;
        repaintComponent();
    }

    public final void setHighlightBackgroundColor(String highlightBackgroundColor) {
        if (highlightBackgroundColor == null) {
            throw new IllegalArgumentException("highlightBackgroundColor is null.");
        }

        setHighlightBackgroundColor(decodeColor(highlightBackgroundColor));
    }

    public boolean getShowHighlight() {
        return showHighlight;
    }

    public void setShowHighlight(boolean showHighlight) {
        this.showHighlight = showHighlight;
        repaintComponent();
    }

    @Override
    public boolean mouseMove(Component component, int x, int y) {
        boolean consumed = super.mouseMove(component, x, y);

        int previousHighlightedIndex = this.highlightedIndex;
        highlightedIndex = getItemAt(y);

        if (previousHighlightedIndex != highlightedIndex) {
            if (previousHighlightedIndex != -1) {
                repaintComponent(getItemBounds(previousHighlightedIndex));
            }

            if (highlightedIndex != -1) {
                repaintComponent(getItemBounds(highlightedIndex));
            }
        }

        return consumed;
    }

    @Override
    public void mouseOut(Component component) {
        super.mouseOut(component);

        if (highlightedIndex != -1) {
            Bounds itemBounds = getItemBounds(highlightedIndex);
            repaintComponent(itemBounds.x, itemBounds.y, itemBounds.width, itemBounds.height);
        }

        highlightedIndex = -1;
        editIndex = -1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean mouseDown(Component component, Mouse.Button button, int x, int y) {
        boolean consumed = super.mouseDown(component, button, x, y);

        ListView listView = (ListView)getComponent();
        List<Object> listData = (List<Object>)listView.getListData();

        int itemHeight = getItemHeight();
        int itemIndex = y / itemHeight;

        if (itemIndex < listData.getLength()
            && !listView.isItemDisabled(itemIndex)) {
            int itemY = itemIndex * itemHeight;

            if (!(listView.getCheckmarksEnabled()
                && x > checkboxPadding.left
                && x < checkboxPadding.left + CHECKBOX.getWidth()
                && y > itemY + checkboxPadding.top
                && y < itemY + checkboxPadding.top + CHECKBOX.getHeight())) {
                ListView.SelectMode selectMode = listView.getSelectMode();
                if (Keyboard.isPressed(Keyboard.Modifier.SHIFT)
                    && selectMode == ListView.SelectMode.MULTI) {
                    // Select the range
                    int startIndex = listView.getFirstSelectedIndex();
                    int endIndex = listView.getLastSelectedIndex();
                    Span selectedRange = (itemIndex > startIndex) ?
                        new Span(startIndex, itemIndex) : new Span(itemIndex, endIndex);

                    ArrayList<Span> selectedRanges = new ArrayList<Span>();
                    Sequence<Integer> disabledIndexes = listView.getDisabledIndexes();
                    if (disabledIndexes.getLength() == 0) {
                        selectedRanges.add(selectedRange);
                    } else {
                        // TODO Split the range by the disabled indexes; for now,
                        // just return
                        return consumed;
                    }

                    listView.setSelectedRanges(selectedRanges);
                } else if (Keyboard.isPressed(Keyboard.Modifier.CTRL)
                    && selectMode == ListView.SelectMode.MULTI) {
                    // Toggle the item's selection state
                    if (listView.isItemSelected(itemIndex)) {
                        listView.removeSelectedIndex(itemIndex);
                    } else {
                        listView.addSelectedIndex(itemIndex);
                    }
                } else {
                    if (selectMode != ListView.SelectMode.NONE) {
                        if (listView.isItemSelected(itemIndex)
                            && listView.isFocused()) {
                            // Edit the item
                            editIndex = itemIndex;
                        } else {
                            // Select the item
                            listView.setSelectedIndex(itemIndex);
                        }
                    }
                }
            }
        }

        if (listView.isFocusable()) {
            listView.requestFocus();
        }

        return consumed;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean mouseClick(Component component, Mouse.Button button, int x, int y, int count) {
        boolean consumed = super.mouseClick(component, button, x, y, count);

        ListView listView = (ListView)getComponent();

        List<Object> listData = (List<Object>)listView.getListData();

        int itemHeight = getItemHeight();
        int itemIndex = y / itemHeight;

        if (itemIndex < listData.getLength()
            && !listView.isItemDisabled(itemIndex)) {
            int itemY = itemIndex * itemHeight;

            if (listView.getCheckmarksEnabled()
                && x > checkboxPadding.left
                && x < checkboxPadding.left + CHECKBOX.getWidth()
                && y > itemY + checkboxPadding.top
                && y < itemY + checkboxPadding.top + CHECKBOX.getHeight()) {
                listView.setItemChecked(itemIndex, !listView.isItemChecked(itemIndex));
            } else {
                if (editIndex != -1
                    && count == 1) {
                    ListView.ItemEditor itemEditor = listView.getItemEditor();

                    if (itemEditor != null) {
                        itemEditor.edit(listView, editIndex);
                    }
                }

                editIndex = -1;
            }
        }

        return consumed;
    }

    @Override
    public boolean mouseWheel(Component component, Mouse.ScrollType scrollType, int scrollAmount,
        int wheelRotation, int x, int y) {
        if (highlightedIndex != -1) {
            Bounds itemBounds = getItemBounds(highlightedIndex);
            repaintComponent(itemBounds.x, itemBounds.y, itemBounds.width, itemBounds.height);
        }

        highlightedIndex = -1;

        return super.mouseWheel(component, scrollType, scrollAmount, wheelRotation, x, y);
    }

    @Override
    public boolean keyPressed(Component component, int keyCode, Keyboard.KeyLocation keyLocation) {
        boolean consumed = super.keyPressed(component, keyCode, keyLocation);

        ListView listView = (ListView)getComponent();

        switch (keyCode) {
            case Keyboard.KeyCode.UP: {
                int index = listView.getFirstSelectedIndex();

                do {
                    index--;
                } while (index >= 0
                    && listView.isItemDisabled(index));

                if (index >= 0) {
                    if (Keyboard.isPressed(Keyboard.Modifier.SHIFT)
                        && listView.getSelectMode() == ListView.SelectMode.MULTI) {
                        listView.addSelectedIndex(index);
                    } else {
                        listView.setSelectedIndex(index);
                    }

                    listView.scrollAreaToVisible(getItemBounds(index));
                }

                consumed = true;
                break;
            }

            case Keyboard.KeyCode.DOWN: {
                int index = listView.getLastSelectedIndex();
                int count = listView.getListData().getLength();

                do {
                    index++;
                } while (index < count
                    && listView.isItemDisabled(index));

                if (index < count) {
                    if (Keyboard.isPressed(Keyboard.Modifier.SHIFT)
                        && listView.getSelectMode() == ListView.SelectMode.MULTI) {
                        listView.addSelectedIndex(index);
                    } else {
                        listView.setSelectedIndex(index);
                    }

                    listView.scrollAreaToVisible(getItemBounds(index));
                }

                consumed = true;
                break;
            }
        }

        // Clear the highlight
        if (highlightedIndex != -1) {
            highlightedIndex = -1;
            repaintComponent(getItemBounds(highlightedIndex));
        }

        return consumed;
    }

    @Override
    public boolean keyReleased(Component component, int keyCode, Keyboard.KeyLocation keyLocation) {
        boolean consumed = super.keyReleased(component, keyCode, keyLocation);

        ListView listView = (ListView)getComponent();

        switch (keyCode) {
            case Keyboard.KeyCode.SPACE: {
                if (listView.getCheckmarksEnabled()
                    && listView.getSelectMode() == ListView.SelectMode.SINGLE) {
                    int selectedIndex = listView.getSelectedIndex();
                    listView.setItemChecked(selectedIndex, !listView.isItemChecked(selectedIndex));
                    consumed = true;
                }

                break;
            }
        }

        return consumed;
    }

    // Component state events
    @Override
    public void enabledChanged(Component component) {
        super.enabledChanged(component);

        repaintComponent();
    }

    @Override
    public void focusedChanged(Component component, boolean temporary) {
        super.focusedChanged(component, temporary);

        repaintComponent();
    }

    // List view events
    public void listDataChanged(ListView listView, List<?> previousListData) {
        invalidateComponent();
    }

    public void itemRendererChanged(ListView listView, ListView.ItemRenderer previousItemRenderer) {
        invalidateComponent();
    }

    public void itemEditorChanged(ListView listView, ListView.ItemEditor previousItemEditor) {
        // No-op
    }

    public void selectModeChanged(ListView listView, ListView.SelectMode previousSelectMode) {
        repaintComponent();
    }

    public void checkmarksEnabledChanged(ListView listView) {
        invalidateComponent();
    }

    public void selectedItemKeyChanged(ListView listView, String previousSelectedItemKey) {
        // No-op
    }

    public void selectedItemsKeyChanged(ListView listView, String previousSelectedItemsKey) {
        // No-op
    }

    // List view item events
    public void itemInserted(ListView listView, int index) {
        invalidateComponent();
    }

    public void itemsRemoved(ListView listView, int index, int count) {
        invalidateComponent();
    }

    public void itemUpdated(ListView listView, int index) {
        invalidateComponent();
    }

    public void itemsSorted(ListView listView) {
        repaintComponent();
    }

    // List view item state events
    public void itemDisabledChanged(ListView listView, int index) {
        repaintComponent(getItemBounds(index));
    }

    public void itemCheckedChanged(ListView listView, int index) {
        repaintComponent(getItemBounds(index));
    }

    // List view selection detail events
    public void selectedRangeAdded(ListView listView, int rangeStart, int rangeEnd) {
        // Repaint the area containing the added selection
        int itemHeight = getItemHeight();
        repaintComponent(0, rangeStart * itemHeight,
            getWidth(), (rangeEnd - rangeStart + 1) * itemHeight);
    }

    public void selectedRangeRemoved(ListView listView, int rangeStart, int rangeEnd) {
        // Repaint the area containing the removed selection
        int itemHeight = getItemHeight();
        repaintComponent(0, rangeStart * itemHeight,
            getWidth(), (rangeEnd - rangeStart + 1) * itemHeight);
    }

    public void selectedRangesChanged(ListView listView, Sequence<Span> previousSelectedRanges) {
        // TODO Repaint only the area that changed (intersection of previous
        // and new selection)
        repaintComponent();
    }
}
