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
package org.apache.pivot.tests.text;

import org.apache.pivot.wtk.text.TextNode;
import org.apache.pivot.wtk.text.TextNodeListener;

public class TextNodeAdapter extends NodeAdapter {
    private TextNodeListener textNodeListener = new TextNodeListener() {
        @Override
        public void charactersInserted(TextNode textNode, int index, int count) {
            update(textNode);
        }

        @Override
        public void charactersRemoved(TextNode textNode, int index, String characters) {
            update(textNode);
        }
    };

    private String text;

    public TextNodeAdapter(TextNode textNode) {
        super(textNode);

        update(textNode);
    }

    @Override
    protected void setParent(ElementAdapter parent) {
        super.setParent(parent);

        TextNode textNode = (TextNode)getNode();

        if (parent == null) {
            textNode.getTextNodeListeners().remove(textNodeListener);
        } else {
            textNode.getTextNodeListeners().add(textNodeListener);
        }
    }

    @Override
    public String getText() {
        return text;
    }

    private void update(TextNode textNode) {
        text = "\"" + textNode.getText() + "\"";

        ElementAdapter parent = getParent();
        if (parent != null) {
            parent.update(parent.indexOf(TextNodeAdapter.this));
        }
    }
}
