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

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import org.cuckoo.ra.cci.CuckooIndexedRecord;
import org.cuckoo.ra.cci.CuckooMappedRecord;

import javax.resource.ResourceException;
import javax.resource.cci.IndexedRecord;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.Record;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JCoRecordMapper {

    private static final Logger LOG = Logger.getLogger( JCoRecordMapper.class.getName() );

    public void populateImportRecord( final JCoParameterList importList,
                                      final JCoParameterList tableList, final MappedRecord mapRecord ) {
        for ( Object o : mapRecord.entrySet() ) {
            final Map.Entry mapEntry = (Map.Entry) o;
            final Object key = mapEntry.getKey();
            final Object value = mapEntry.getValue();

            if ( key instanceof String ) {
                final String fieldName = (String) key;

                if ( value instanceof MappedRecord ) {
                    // Import parameters
                    populateStructure( importList.getStructure( fieldName ), (MappedRecord) value );
                } else if ( value instanceof IndexedRecord ) {
                    // Might be a table parameter or an input parameter with a table type.
                    JCoTable table = null;
                    // First check if a table parameter of this name exists
                    if ( tableList != null ) {
                        table = tableList.getTable( fieldName );
                    }
                    // No table parameter, look for an import parameter
                    if ( table == null ) {
                        table = importList.getTable( fieldName );
                    }
                    // No parameter of this name found at all
                    if ( table == null ) {
                        throw new IllegalArgumentException( "No table or input parameter found for field name " + fieldName );
                    }
                    // Now populate the found table
                    populateTable( table, (IndexedRecord) value );
                } else {
                    populateRecord( importList, fieldName, value );
                }
            } else if ( key instanceof Integer ) {
                final int fieldId = (Integer) key;

                if ( value instanceof MappedRecord ) {
                    populateStructure( importList.getStructure( fieldId ), (MappedRecord) value );
                } else if ( value instanceof IndexedRecord ) {
                    populateTable( importList.getTable( fieldId ), (IndexedRecord) value );
                } else {
                    populateRecord( importList, fieldId, value );
                }
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    private void populateIndexedRecord( final IndexedRecord indexedRecord, final JCoTable table ) {
        for ( int i = 0; i < table.getNumRows(); i++ ) {
            final MappedRecord mappedRecord = new CuckooMappedRecord( indexedRecord.getRecordName() + ":row:" + i );
            // Each component of the table might itself be a table, structure or simple value.
            // Luckily, we can reuse populateMappedRecord recursively - a table is also a record.
            populateMappedRecord( mappedRecord, table );

            indexedRecord.add( mappedRecord );
            table.nextRow();
        }
    }

    @SuppressWarnings( "unchecked" )
    private void populateMappedRecord( final MappedRecord mappedResultRecord, final JCoRecord record ) {
        for ( int i = 0; i < record.getFieldCount(); i++ ) {
            final JCoMetaData metaData = record.getMetaData();
            final String fieldName = metaData.getName( i );

            if ( metaData.isStructure( i ) ) {
                final MappedRecord nestedMappedRecord = new CuckooMappedRecord( fieldName );
                populateMappedRecord( nestedMappedRecord, record.getStructure( i ) );
                mappedResultRecord.put( fieldName, nestedMappedRecord );
            } else if ( metaData.isTable( i ) ) {
                final IndexedRecord nestedIndexedRecord = new CuckooIndexedRecord( fieldName );
                populateIndexedRecord( nestedIndexedRecord, record.getTable( i ) );
                mappedResultRecord.put( fieldName, nestedIndexedRecord );
            } else {
                mappedResultRecord.put( fieldName, record.getValue( i ) );
            }
        }
    }

    public void populateTable( final JCoTable table, final IndexedRecord indexRecord ) {
        for ( final Object object : indexRecord ) {
            if ( object instanceof MappedRecord ) {
                table.appendRow();
                populateTableRow( table, (MappedRecord) object );
            } else {
                throw new IllegalArgumentException(
                        "Only MappedRecords are expected to be inside the indexRecord" );
            }
        }
    }

    private void populateTableRow( final JCoTable table, final MappedRecord mapRecord ) {
        for ( final Object object : mapRecord.entrySet() ) {
            final Map.Entry mapEntry = (Map.Entry) object;
            populateRecord( table, mapEntry.getKey(), mapEntry.getValue() );
        }
    }

    @SuppressWarnings( "unchecked" )
    private void populateStructure( final JCoStructure structure, final MappedRecord mapRecord ) {
        for ( final Object object : mapRecord.entrySet() ) {
            final Map.Entry mapEntry = (Map.Entry) object;
            populateRecord( structure, mapEntry.getKey(), mapEntry.getValue() );
        }
    }

    private void populateRecord( final JCoRecord jcoRecord, final Object fieldName,
                                 final Object fieldValue ) {
        if ( fieldName instanceof String ) {
            jcoRecord.setValue( (String) fieldName, fieldValue );
        } else if ( fieldName instanceof Integer ) {
            jcoRecord.setValue( (Integer) fieldName, fieldValue );
        }
    }

    public void convertExportTable( final JCoFunction function, final Record resultRecord ) {
        final JCoParameterList exportTableList = function.getTableParameterList();

        if ( exportTableList != null ) {
            populateMappedRecord( (MappedRecord) resultRecord, exportTableList );
        }
    }

    public void convertExportParameters( final JCoFunction function, final MappedRecord resultRecord ) {
        final JCoParameterList exportList = function.getExportParameterList();

        if ( exportList != null ) {
            populateMappedRecord( resultRecord, exportList );
        }
    }

    public void checkForAbapExceptions( final JCoFunction function ) throws ResourceException {
        // TODO collect information from ABAP exceptions more intelligently
        final AbapException[] exceptions = function.getExceptionList();

        if ( exceptions != null && exceptions.length > 0 ) {
            final String message = "Error executing " + function.getName();

            for ( final AbapException exc : exceptions ) {
                LOG.log( Level.SEVERE, message, exc );
            }

            throw new ResourceException( message, exceptions[0] );
        }
    }
}
