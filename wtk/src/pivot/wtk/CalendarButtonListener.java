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

import java.util.Locale;

/**
 * Calendar button listener interface.
 *
 * @author tvolkert
 * @author gbrown
 */
public interface CalendarButtonListener {
    /**
     * Called when a calendar button's selected date key has changed.
     *
     * @param calendarButton
     * @param previousSelectedDateKey
     */
    public void selectedDateKeyChanged(CalendarButton calendarButton,
        String previousSelectedDateKey);

    /**
     * Called when a calendar button's locale has changed.
     *
     * @param calendarButton
     * @param previousLocale
     */
    public void localeChanged(CalendarButton calendarButton, Locale previousLocale);
}
