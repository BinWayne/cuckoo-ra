/*
 * Copyright (C) 2012-2017 akquinet tech@spree GmbH
 *
 * This file is part of the Cuckoo Resource Adapter for SAP.
 *
 * Cuckoo Resource Adapter for SAP is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Cuckoo Resource Adapter for SAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with Cuckoo Resource Adapter for SAP. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cuckoo.ra.cci;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.cuckoo.ra.util.CuckooTestUtil.serializeAndReturnDeserializedObject;

@SuppressWarnings({"unchecked"})
public class CuckooIndexedRecordTest {

    @Test
    public void isSerializable() throws Exception {
        CuckooIndexedRecord record = new CuckooIndexedRecord("recordName");
        record.setRecordShortDescription("recordShortDescription");
        record.add("aString");
        CuckooIndexedRecord innerRecord = new CuckooIndexedRecord("innerRecordName");
        innerRecord.add("anotherString");
        record.add(innerRecord);

        CuckooIndexedRecord deserializedRecord = serializeAndReturnDeserializedObject(record);

        assertThat(deserializedRecord).isNotSameAs(record);

        assertThat(deserializedRecord.getRecordName()).isEqualTo("recordName");
        assertThat(deserializedRecord.getRecordShortDescription()).isEqualTo("recordShortDescription");

        assertThat(deserializedRecord).hasSize(2);

        assertThat((String) deserializedRecord.get(0)).isEqualTo("aString");
        CuckooIndexedRecord deserializedInnerRecord = (CuckooIndexedRecord) deserializedRecord.get(1);

        assertThat(deserializedInnerRecord).isNotSameAs(innerRecord);
        assertThat(deserializedInnerRecord).hasSize(1);
        assertThat(deserializedInnerRecord.getRecordName()).isEqualTo("innerRecordName");
        assertThat(deserializedInnerRecord.getRecordShortDescription()).isNull();
        assertThat((String) deserializedInnerRecord.get(0)).isEqualTo("anotherString");
    }

    @Test
    public void isCloneable() throws Exception {
        CuckooIndexedRecord record = new CuckooIndexedRecord("recordName");
        record.setRecordShortDescription("recordShortDescription");
        record.add("aString");
        CuckooIndexedRecord innerRecord = new CuckooIndexedRecord("innerRecordName");
        innerRecord.add("anotherString");
        record.add(innerRecord);

        CuckooIndexedRecord clone = record.clone();

        assertThat(clone).isNotSameAs(record);

        assertThat(clone.getRecordName()).isEqualTo("recordName");
        assertThat(clone.getRecordShortDescription()).isEqualTo("recordShortDescription");
        assertThat(clone).hasSize(2);
        assertThat((String) clone.get(0)).isEqualTo("aString");

        CuckooIndexedRecord innerRecordClone = (CuckooIndexedRecord) clone.get(1);
        assertThat(innerRecordClone).hasSize(1);
        assertThat(innerRecordClone.getRecordName()).isEqualTo("innerRecordName");
        assertThat(innerRecordClone.getRecordShortDescription()).isNull();
        assertThat((String) innerRecordClone.get(0)).isEqualTo("anotherString");
    }
}
