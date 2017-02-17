/*
 * Copyright (C) 2010 akquinet tech@spree GmbH
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
package org.cuckoo.ra.jco;

import java.util.Map;
import java.util.logging.Logger;

import javax.resource.cci.IndexedRecord;
import javax.resource.cci.MappedRecord;

import org.cuckoo.ra.cci.CuckooIndexedRecord;
import org.cuckoo.ra.cci.CuckooMappedRecord;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

public class JCoRecordMapper {
	private static final Logger LOG = Logger.getLogger(JCoRecordMapper.class.getName());

	public void populateInputParameters(JCoFunction function, MappedRecord inputRecord) {
		final JCoParameterList importList = function.getImportParameterList();
		final JCoParameterList changingList = function.getChangingParameterList();
		final JCoParameterList tableList = function.getTableParameterList();

		if (importList != null) {
			populateImportRecord(importList, inputRecord);
		}

		if (changingList != null) {
			populateImportRecord(changingList, inputRecord);
		}

		if (tableList != null) {
			populateImportRecord(tableList, inputRecord);
		}
	}

	private void populateImportRecord(final JCoParameterList importList, final MappedRecord mapRecord) {
		if (importList != null) {
			JCoMetaData metaData = importList.getMetaData();
			for (Object o : mapRecord.entrySet()) {
				final Map.Entry mapEntry = (Map.Entry) o;
				final Object key = mapEntry.getKey();
				final Object value = mapEntry.getValue();

				if (key instanceof String) {
					final String fieldName = (String) key;

					if (metaData.hasField(fieldName)) {
						if (value instanceof MappedRecord) {
							// Structure parameter
							populateStructure(importList.getStructure(fieldName), (MappedRecord) value);
						} else if (value instanceof IndexedRecord) {
							// Table parameter
							populateTable(importList.getTable(fieldName), (IndexedRecord) value);
						} else {
							// Simple parameter
							populateRecord(importList, fieldName, value);
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void populateIndexedRecord(final IndexedRecord indexedRecord, final JCoTable table) {
		final JCoMetaData metaData = table.getMetaData();
		for (int i = 0; i < table.getNumRows(); i++) {

			final MappedRecord mappedRecord = new CuckooMappedRecord(indexedRecord.getRecordName() + ":row:" + i);

			for (int j = 0; j < table.getNumColumns(); j++) {
				final String fieldName = metaData.getName(j);

				if (metaData.isStructure(j)) {
					final MappedRecord nestedMappedRecord = new CuckooMappedRecord(fieldName);
					populateMappedRecord(nestedMappedRecord, table.getStructure(j));
					mappedRecord.put(fieldName, nestedMappedRecord);
				} else if (metaData.isTable(j)) {
					final IndexedRecord nestedIndexedRecord = new CuckooIndexedRecord(fieldName);
					populateIndexedRecord(nestedIndexedRecord, table.getTable(j));
					mappedRecord.put(fieldName, nestedIndexedRecord);
				} else {
					mappedRecord.put(table.getMetaData().getName(j), table.getValue(j));
				}
			}

			indexedRecord.add(mappedRecord);
			table.nextRow();
		}
	}

	@SuppressWarnings("unchecked")
	private void populateMappedRecord(final MappedRecord mappedResultRecord, final JCoRecord record) {
		for (int i = 0; i < record.getFieldCount(); i++) {
			final JCoMetaData metaData = record.getMetaData();
			final String fieldName = metaData.getName(i);

			if (metaData.isStructure(i)) {
				final MappedRecord nestedMappedRecord = new CuckooMappedRecord(fieldName);
				populateMappedRecord(nestedMappedRecord, record.getStructure(i));
				mappedResultRecord.put(fieldName, nestedMappedRecord);
			} else if (metaData.isTable(i)) {
				final IndexedRecord nestedIndexedRecord = new CuckooIndexedRecord(fieldName);
				populateIndexedRecord(nestedIndexedRecord, record.getTable(i));
				mappedResultRecord.put(fieldName, nestedIndexedRecord);
			} else {
				mappedResultRecord.put(fieldName, record.getValue(i));
			}
		}
	}

	private void populateTable(final JCoTable table, final IndexedRecord indexRecord) {
		for (final Object object : indexRecord) {
			if (object instanceof MappedRecord) {
				table.appendRow();
				populateTableRow(table, (MappedRecord) object);
			} else {
				throw new IllegalArgumentException("Only MappedRecords are expected to be inside the indexRecord");
			}
		}
	}

	private void populateTableRow(final JCoTable table, final MappedRecord mapRecord) {
		for (final Object object : mapRecord.entrySet()) {
			final Map.Entry mapEntry = (Map.Entry) object;
			populateRecord(table, mapEntry.getKey(), mapEntry.getValue());
		}
	}

	private void populateStructure(final JCoStructure structure, final MappedRecord mapRecord) {
		JCoMetaData metaData = structure.getMetaData();
		for (final Object object : mapRecord.entrySet()) {
			final Map.Entry mapEntry = (Map.Entry) object;
			final Object key = mapEntry.getKey();
			final Object value = mapEntry.getValue();

			if (key instanceof String) {
				final String fieldName = (String) key;

				if (metaData.hasField(fieldName)) {
					if (value instanceof MappedRecord) {
						// Structure
						populateStructure(structure.getStructure(fieldName), (MappedRecord) value);
					} else if (value instanceof IndexedRecord) {
						// Tables
						populateTable(structure.getTable(fieldName), (IndexedRecord) value);
					} else {
						// Simple
						populateRecord(structure, key, value);
					}
				}
			}
		}
	}

	private void populateRecord(final JCoRecord jcoRecord, final Object fieldName, final Object fieldValue) {
		JCoMetaData metaData = jcoRecord.getMetaData();
		if (fieldValue != null && metaData.hasField((String) fieldName)) {
			jcoRecord.setValue((String) fieldName, fieldValue);
		}
	}

	public void convertExportParameters(final JCoFunction function, final MappedRecord resultRecord) {
		final JCoParameterList exportList = function.getExportParameterList();
		final JCoParameterList changingList = function.getChangingParameterList();
		final JCoParameterList exportTableList = function.getTableParameterList();

		if (exportList != null) {
			populateMappedRecord(resultRecord, exportList);
		}

		if (changingList != null) {
			populateMappedRecord(resultRecord, changingList);
		}

		if (exportTableList != null) {
			populateMappedRecord(resultRecord, exportTableList);
		}
	}
}