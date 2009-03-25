/*
 * Copyright (c) 2008 VMware, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pivot.wtk;

import pivot.collections.List;

/**
 * List view listener interface.
 *
 * @author gbrown
 */
public interface ListViewListener {
    /**
     * Called when a list view's list data has changed.
     *
     * @param listView
     * @param previousListData
     */
    public void listDataChanged(ListView listView, List<?> previousListData);

    /**
     * Called when a list view's item renderer has changed.
     *
     * @param listView
     * @param previousItemRenderer
     */
    public void itemRendererChanged(ListView listView, ListView.ItemRenderer previousItemRenderer);

    /**
     * Called when a list view's item editor has changed.
     *
     * @param listView
     * @param previousItemEditor
     */
    public void itemEditorChanged(ListView listView, ListView.ItemEditor previousItemEditor);

    /**
     * Called when a list view's select mode has changed.
     *
     * @param listView
     * @param previousSelectMode
     */
    public void selectModeChanged(ListView listView, ListView.SelectMode previousSelectMode);

    /**
     * Called when a list view's checkmarks enabled flag has changed.
     *
     * @param listView
     */
    public void checkmarksEnabledChanged(ListView listView);

    /**
     * Called when a list view's selected item key has changed.
     *
     * @param listView
     * @param previousSelectedItemKey
     */
    public void selectedItemKeyChanged(ListView listView, String previousSelectedItemKey);

    /**
     * Called when a list view's selected items key has changed.
     *
     * @param listView
     * @param previousSelectedItemsKey
     */
    public void selectedItemsKeyChanged(ListView listView, String previousSelectedItemsKey);
}
