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
package org.apache.pivot.tutorials.lists;

import java.net.URL;

import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Dictionary;
import org.apache.pivot.util.Resources;
import org.apache.pivot.util.ThreadUtilities;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.wtk.ApplicationContext;
import org.apache.pivot.wtk.ImageView;
import org.apache.pivot.wtk.ListButton;
import org.apache.pivot.wtk.ListButtonSelectionListener;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.media.Image;

public class ListButtons extends Window implements Bindable {
    private ListButton listButton = null;
    private ImageView imageView = null;

    @Override
    public void initialize(Dictionary<String, Object> context, Resources resources) {
        listButton = (ListButton)context.get("listButton");
        imageView = (ImageView)context.get("imageView");

        listButton.getListButtonSelectionListeners().add(    new ListButtonSelectionListener() {
            @Override
            public void selectedIndexChanged(ListButton listButton, int previousIndex) {
                int index = listButton.getSelectedIndex();

                if (index != -1) {
                    String item = (String)listButton.getListData().get(index);

                    // Get the image URL for the selected item
                    ClassLoader classLoader = ThreadUtilities.getClassLoader();
                    URL imageURL = classLoader.getResource("org/apache/pivot/tutorials/" + item);

                    // If the image has not been added to the resource cache yet,
                    // add it
                    Image image = (Image)ApplicationContext.getResourceCache().get(imageURL);

                    if (image == null) {
                        try {
                            image = Image.load(imageURL);
                        } catch (TaskExecutionException exception) {
                            throw new RuntimeException(exception);
                        }

                        ApplicationContext.getResourceCache().put(imageURL, image);
                    }

                    // Update the image
                    imageView.setImage(image);
                }
            }
        });

        listButton.setSelectedIndex(0);
    }
}
