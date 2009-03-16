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
package pivot.wtk.content;

import java.awt.Color;
import java.awt.Font;

import pivot.wtk.Component;
import pivot.wtk.FlowPane;
import pivot.wtk.HorizontalAlignment;
import pivot.wtk.ImageView;
import pivot.wtk.Insets;
import pivot.wtk.Label;
import pivot.wtk.TableViewHeader;
import pivot.wtk.VerticalAlignment;
import pivot.wtk.media.Image;

/**
 * Default table view header data renderer.
 *
 * @author gbrown
 */
public class TableViewHeaderDataRenderer extends FlowPane
    implements TableViewHeader.DataRenderer {
    protected ImageView imageView = new ImageView();
    protected Label label = new Label();

    public TableViewHeaderDataRenderer() {
        getStyles().put("horizontalAlignment", HorizontalAlignment.LEFT);
        getStyles().put("verticalAlignment", VerticalAlignment.CENTER);
        getStyles().put("padding", new Insets(1, 2, 1, 2));

        add(imageView);
        add(label);
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);

        // Since this component doesn't have a parent, it won't be validated
        // via layout; ensure that it is valid here
        validate();
    }

    public void render(Object data, TableViewHeader tableViewHeader, boolean highlighted) {
        Image icon = null;
        String text = null;

        if (data instanceof TableViewHeaderData) {
            TableViewHeaderData tableViewHeaderData = (TableViewHeaderData)data;
            icon = tableViewHeaderData.getIcon();
            text = tableViewHeaderData.getText();
        } else if (data instanceof Image) {
            icon = (Image)data;
        } else {
            if (data != null) {
                text = data.toString();
            }
        }

        // Left-align the content
        getStyles().put("horizontalAlignment", (text == null) ?
            HorizontalAlignment.CENTER : HorizontalAlignment.LEFT);

        // Update the icon image view
        imageView.setImage(icon);

        if (icon == null) {
            imageView.setDisplayable(false);
        } else {
            imageView.setDisplayable(true);
            imageView.getStyles().put("opacity", tableViewHeader.isEnabled() ? 1.0f : 0.5f);
        }

        // Show/hide the label
        label.setText(text);

        if (text == null) {
            label.setDisplayable(false);
        } else {
            label.setDisplayable(true);

            // Update the label styles
            Component.StyleDictionary labelStyles = label.getStyles();

            Object labelFont = tableViewHeader.getStyles().get("font");
            if (labelFont instanceof Font) {
                labelStyles.put("font", labelFont);
            }

            Object color = null;
            if (tableViewHeader.isEnabled()) {
                color = tableViewHeader.getStyles().get("color");
            } else {
                color = tableViewHeader.getStyles().get("disabledColor");
            }

            if (color instanceof Color) {
                labelStyles.put("color", color);
            }
        }
    }
}
