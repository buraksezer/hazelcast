/*
 * Copyright (c) 2008-2021, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.sql.impl.plan.node;

import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.sql.impl.expression.Expression;
import com.hazelcast.sql.impl.extract.QueryPath;
import com.hazelcast.sql.impl.extract.QueryTargetDescriptor;
import com.hazelcast.sql.impl.type.QueryDataType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Base class to scan a map.
 */
public abstract class AbstractMapScanPlanNode extends ZeroInputPlanNode {

    protected String mapName;
    protected QueryTargetDescriptor keyDescriptor;
    protected QueryTargetDescriptor valueDescriptor;
    protected List<QueryPath> fieldPaths;
    protected List<QueryDataType> fieldTypes;
    protected List<Integer> projects;
    protected Expression<Boolean> filter;

    protected AbstractMapScanPlanNode() {
        // No-op.
    }

    protected AbstractMapScanPlanNode(
        int id,
        String mapName,
        QueryTargetDescriptor keyDescriptor,
        QueryTargetDescriptor valueDescriptor,
        List<QueryPath> fieldPaths,
        List<QueryDataType> fieldTypes,
        List<Integer> projects,
        Expression<Boolean> filter
    ) {
        super(id);

        this.mapName = mapName;
        this.keyDescriptor = keyDescriptor;
        this.valueDescriptor = valueDescriptor;
        this.fieldPaths = fieldPaths;
        this.fieldTypes = fieldTypes;
        this.projects = projects;
        this.filter = filter;
    }

    public String getMapName() {
        return mapName;
    }

    public QueryTargetDescriptor getKeyDescriptor() {
        return keyDescriptor;
    }

    public QueryTargetDescriptor getValueDescriptor() {
        return valueDescriptor;
    }

    public List<QueryPath> getFieldPaths() {
        return fieldPaths;
    }

    public List<QueryDataType> getFieldTypes() {
        return fieldTypes;
    }

    public List<Integer> getProjects() {
        return projects;
    }

    public Expression<Boolean> getFilter() {
        return filter;
    }

    @Override
    public PlanNodeSchema getSchema0() {
        List<QueryDataType> types = new ArrayList<>(projects.size());

        for (Integer project : projects) {
            types.add(fieldTypes.get(project));
        }

        return new PlanNodeSchema(types);
    }

    @Override
    protected void writeData0(ObjectDataOutput out) throws IOException {
        out.writeString(mapName);
        out.writeObject(keyDescriptor);
        out.writeObject(valueDescriptor);
        SerializationUtil.writeList(fieldPaths, out);
        SerializationUtil.writeList(fieldTypes, out);
        SerializationUtil.writeList(projects, out);
        out.writeObject(filter);
    }

    @Override
    protected void readData0(ObjectDataInput in) throws IOException {
        mapName = in.readString();
        keyDescriptor = in.readObject();
        valueDescriptor = in.readObject();
        fieldPaths = SerializationUtil.readList(in);
        fieldTypes = SerializationUtil.readList(in);
        projects = SerializationUtil.readList(in);
        filter = in.readObject();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractMapScanPlanNode that = (AbstractMapScanPlanNode) o;

        return id == that.id
            && mapName.equals(that.mapName)
            && keyDescriptor.equals(that.keyDescriptor)
            && valueDescriptor.equals(that.valueDescriptor)
            && fieldPaths.equals(that.fieldPaths)
            && fieldTypes.equals(that.fieldTypes)
            && projects.equals(that.projects)
            && Objects.equals(filter, that.filter);
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(id);
        result = 31 * result + mapName.hashCode();
        result = 31 * result + keyDescriptor.hashCode();
        result = 31 * result + valueDescriptor.hashCode();
        result = 31 * result + fieldPaths.hashCode();
        result = 31 * result + fieldTypes.hashCode();
        result = 31 * result + projects.hashCode();
        result = 31 * result + (filter != null ? filter.hashCode() : 0);
        return result;
    }
}
