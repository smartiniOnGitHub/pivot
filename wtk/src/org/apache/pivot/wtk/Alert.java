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
package org.apache.pivot.wtk;

import java.util.Iterator;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.json.JSONSerializer;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.util.ImmutableIterator;
import org.apache.pivot.util.ListenerList;
import org.apache.pivot.util.Resources;

/**
 * Class representing an "alert", a dialog commonly used to facilitate simple
 * user interaction.
 */
public class Alert extends Dialog {
    /**
     * Option sequence implementation.
     */
    public final class OptionSequence implements Sequence<Object>, Iterable<Object> {
        private OptionSequence() {
        }

        @Override
        public int add(Object option) {
            int i = getLength();
            insert(option, i);

            return i;
        }

        @Override
        public void insert(Object option, int index) {
            if (option == null) {
                throw new IllegalArgumentException("option is null.");
            }

            options.insert(option, index);

            if (selectedOption >= index) {
                selectedOption++;
            }

            alertListeners.optionInserted(Alert.this, index);
        }

        @Override
        public Component update(int index, Object option) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int remove(Object option) {
            int index = indexOf(option);
            if (index != -1) {
                remove(index, 1);
            }

            return index;
        }

        @Override
        public Sequence<Object> remove(int index, int count) {
            Sequence<Object> removed = options.remove(index, count);

            if (removed.getLength() > 0) {
                if (selectedOption >= index) {
                    if (selectedOption < index + count) {
                        selectedOption = -1;
                    } else {
                        selectedOption -= count;
                    }
                }

                alertListeners.optionsRemoved(Alert.this, index, removed);
            }

            return removed;
        }

        @Override
        public Object get(int index) {
            return options.get(index);
        }

        @Override
        public int indexOf(Object option) {
            return options.indexOf(option);
        }

        @Override
        public int getLength() {
            return options.getLength();
        }

        @Override
        public Iterator<Object> iterator() {
            return new ImmutableIterator<Object>(options.iterator());
        }
    }

    private static class AlertListenerList extends ListenerList<AlertListener>
        implements AlertListener {
        @Override
        public void messageTypeChanged(Alert alert, MessageType previousMessageType) {
            for (AlertListener listener : this) {
                listener.messageTypeChanged(alert, previousMessageType);
            }
        }

        @Override
        public void messageChanged(Alert alert, String previousMessage) {
            for (AlertListener listener : this) {
                listener.messageChanged(alert, previousMessage);
            }
        }

        @Override
        public void bodyChanged(Alert alert, Component previousBody) {
            for (AlertListener listener : this) {
                listener.bodyChanged(alert, previousBody);
            }
        }

        @Override
        public void optionInserted(Alert alert, int index) {
            for (AlertListener listener : this) {
                listener.optionInserted(alert, index);
            }
        }

        @Override
        public void optionsRemoved(Alert alert, int index, Sequence<?> removed) {
            for (AlertListener listener : this) {
                listener.optionsRemoved(alert, index, removed);
            }
        }

        @Override
        public void selectedOptionChanged(Alert alert, int previousSelectedOption) {
            for (AlertListener listener : this) {
                listener.selectedOptionChanged(alert, previousSelectedOption);
            }
        }
    }

    private MessageType messageType = null;
    private String message = null;
    private Component body = null;

    private ArrayList<Object> options = new ArrayList<Object>();
    private OptionSequence optionSequence = new OptionSequence();
    private int selectedOption = -1;

    private AlertListenerList alertListeners = new AlertListenerList();

    private static Resources resources = null;

    static {
        try {
            resources = new Resources(Alert.class.getName());
        } catch(Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public Alert() {
        this(null, null, null);
    }

    public Alert(MessageType type, String message, Sequence<?> options) {
        this(type, message, options, true);
    }

    public Alert(MessageType type, String message, Sequence<?> options, boolean modal) {
        this(type, message, options, null, modal);
    }

    public Alert(MessageType type, String message, Sequence<?> options, Component body) {
        this(type, message, options, body, true);
    }

    public Alert(MessageType messageType, String message, Sequence<?> options, Component body, boolean modal) {
        super(modal);

        if (messageType == null) {
            messageType = MessageType.INFO;
        }

        setMessageType(messageType);
        setMessage(message);
        setBody(body);

        if (options != null
            && options.getLength() > 0) {
            for (int i = 0, n = options.getLength(); i < n; i++) {
                optionSequence.add(options.get(i));
            }

            setSelectedOption(0);
        }

        setTitle((String)resources.get("defaultTitle"));

        installThemeSkin(Alert.class);
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        if (messageType == null) {
            throw new IllegalArgumentException();
        }

        MessageType previousMessageType = this.messageType;
        if (previousMessageType != messageType) {
            this.messageType = messageType;
            alertListeners.messageTypeChanged(this, previousMessageType);
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        String previousMessage = this.message;
        if (previousMessage != message) {
            this.message = message;
            alertListeners.messageChanged(this, previousMessage);
        }
    }

    public Component getBody() {
        return body;
    }

    public void setBody(Component body) {
        Component previousBody = this.body;
        if (previousBody != body) {
            this.body = body;
            alertListeners.bodyChanged(this, previousBody);
        }
    }

    public OptionSequence getOptions() {
        return optionSequence;
    }

    public void setOptions(String options) {
        optionSequence.remove(0, optionSequence.getLength());

        try {
            for (Object option : JSONSerializer.parseList(options)) {
                optionSequence.add(option);
            }
        } catch (SerializationException exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        if (selectedOption < -1
            || selectedOption > options.getLength() - 1) {
            throw new IndexOutOfBoundsException();
        }

        int previousSelectedOption = this.selectedOption;

        if (selectedOption != previousSelectedOption) {
            this.selectedOption = selectedOption;
            alertListeners.selectedOptionChanged(this, previousSelectedOption);
        }
    }

    public ListenerList<AlertListener> getAlertListeners() {
        return alertListeners;
    }

    public static void alert(String message, Display display) {
        alert(MessageType.INFO, message, null, display, null);
    }

    public static void alert(MessageType messageType, String message, Display display) {
        alert(messageType, message, null, display, null);
    }

    public static void alert(MessageType messageType, String message, Component body, Display display) {
        alert(messageType, message, body, display, null);
    }

    public static void alert(MessageType messageType, String message, Component body, Display display,
        DialogCloseListener dialogCloseListener) {
        Alert alert = new Alert(messageType, message,
            new ArrayList<Object>(resources.get("defaultOption")), body);
        alert.setModal(false);
        alert.open(display, dialogCloseListener);
    }

    public static void alert(String message, Window owner) {
        alert(MessageType.INFO, message, null, owner, null);
    }

    public static void alert(MessageType messageType, String message, Window owner) {
        alert(messageType, message, null, owner, null);
    }

    public static void alert(MessageType messageType, String message, Window owner,
        DialogCloseListener dialogCloseListener) {
        alert(messageType, message, null, owner, dialogCloseListener);
    }

    public static void alert(MessageType messageType, String message, Component body, Window owner) {
        alert(messageType, message, body, owner, null);
    }

    public static void alert(MessageType messageType, String message, Component body, Window owner,
        DialogCloseListener dialogCloseListener) {
        Alert alert = new Alert(messageType, message,
            new ArrayList<Object>(resources.get("defaultOption")), body);
        alert.open(owner.getDisplay(), owner, dialogCloseListener);
    }
}
