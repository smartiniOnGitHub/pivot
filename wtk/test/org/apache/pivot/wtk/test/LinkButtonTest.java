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
package org.apache.pivot.wtk.test;

import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.ComponentMouseListener;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.BoxPane;
import org.apache.pivot.wtk.LinkButton;
import org.apache.pivot.wtk.VerticalAlignment;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.content.ButtonData;
import org.apache.pivot.wtk.media.Image;

public class LinkButtonTest implements Application {
    private Window window = new Window();

    public void startup(Display display, Map<String, String> properties) throws Exception {
        BoxPane boxPane = new BoxPane();
        boxPane.getStyles().put("verticalAlignment", VerticalAlignment.CENTER);
        boxPane.getStyles().put("spacing", 8);
        boxPane.getComponentMouseListeners().add(new ComponentMouseListener() {
            public boolean mouseMove(Component component, int x, int y) {
                System.out.println("BOX PANE " + x + ", " + y);
                return false;
            }

            public void mouseOver(Component component) {
            }

            public void mouseOut(Component component) {
            }
        });

        Image image = Image.load(getClass().getResource("go-home.png"));

        LinkButton linkButton = null;

        linkButton = new LinkButton("ABCDE");
        boxPane.add(linkButton);
        linkButton.getComponentMouseListeners().add(new ComponentMouseListener() {
            public boolean mouseMove(Component component, int x, int y) {
                return true;
            }

            public void mouseOver(Component component) {
            }

            public void mouseOut(Component component) {
            }
        });

        linkButton = new LinkButton(image);
        boxPane.add(linkButton);

        linkButton = new LinkButton(new ButtonData(image, "12345"));
        boxPane.add(linkButton);

        window.setContent(boxPane);
        window.open(display);
    }

    public boolean shutdown(boolean optional) {
        if (window != null) {
            window.close();
        }

        return false;
    }

    public void suspend() {
    }

    public void resume() {
    }
}
