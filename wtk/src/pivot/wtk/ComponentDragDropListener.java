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

/**
 * Component drag/drop listener interface.
 *
 * @author gbrown
 */
public interface ComponentDragDropListener {
    /**
     * Called when a component's drag source has changed.
     *
     * @param component
     * @param previousDragSource
     */
    public void dragSourceChanged(Component component, DragSource previousDragSource);

    /**
     * Called when a component's drop target has changed.
     *
     * @param component
     * @param previousDropTarget
     */
    public void dropTargetChanged(Component component, DropTarget previousDropTarget);
}
