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
package org.apache.pivot.json.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.pivot.collections.List;
import org.apache.pivot.json.JSON;
import org.apache.pivot.json.JSONSerializer;
import org.apache.pivot.serialization.SerializationException;
import org.junit.Test;

public class JSONSerializerTest {
    @Test
    public void testCarriageReturns() {
        List<?> emptyList;
        try {
            emptyList = JSONSerializer.parseList("[\n]");
        } catch(SerializationException exception) {
            throw new RuntimeException(exception);
        }

        assertEquals(0, emptyList.getLength());
    }

    @Test(expected=RuntimeException.class)
    public void testInvalidNumbers() {
        JSONSerializer.toString(Float.NaN);
        JSONSerializer.toString(Float.NEGATIVE_INFINITY);
        JSONSerializer.toString(Float.POSITIVE_INFINITY);
        JSONSerializer.toString(Double.NaN);
        JSONSerializer.toString(Double.NEGATIVE_INFINITY);
        JSONSerializer.toString(Double.POSITIVE_INFINITY);
    }

    @Test
    public void testE() throws SerializationException {
        assertEquals(5000000, JSONSerializer.parseDouble("5.0E6"), 0);
        assertEquals(0.000005, JSONSerializer.parseDouble("5.0E-6"), 0);
    }

    @Test
    public void testEquals() throws IOException, SerializationException {
        JSONSerializer jsonSerializer = new JSONSerializer();
        Object o1 = jsonSerializer.readObject(getClass().getResourceAsStream("sample.json"));
        Object o2 = jsonSerializer.readObject(getClass().getResourceAsStream("sample.json"));

        assertTrue(o1.equals(o2));

        List<?> d = JSON.getList(o1, "d");
        d.remove(0, 1);

        assertFalse(o1.equals(o2));
    }
}
