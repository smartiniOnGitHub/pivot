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
package org.apache.pivot.wtk.media.drawing;

import org.apache.pivot.collections.Sequence;

/**
 * Path listener interface.
 */
public interface PathListener {
    /**
     * Called when a path's winding rule has changed.
     *
     * @param path
     * @param previousWindingRule
     */
    public void windingRuleChanged(Path path, Path.WindingRule previousWindingRule);

    /**
     * Called when an operation has been inserted into a path.
     *
     * @param path
     * @param index
     */
    public void operationInserted(Path path, int index);

    /**
     * Called when operations have been removed from a path.
     *
     * @param path
     * @param index
     * @param removed
     */
    public void operationsRemoved(Path path, int index, Sequence<Path.Operation> removed);

    /**
     * Called when an operation has been updated.
     *
     * @param operation
     */
    public void operationUpdated(Path.Operation operation);
}
