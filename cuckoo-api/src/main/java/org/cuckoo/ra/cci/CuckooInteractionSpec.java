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

import javax.resource.cci.InteractionSpec;

/**
 * A CuckooInteractionSpec holds a property for passing the function name to be called
 * when executing an Interaction with a SAP instance.
 * <p>
 * Alternatively, the function name can be provided with the input Record when calling
 * Interaction.execute(). In this case, the function name has to be provided as the
 * name of the input record and instead of an InteractionSpec object, null can be passed
 * to the execute() method.
 *
 * @see javax.resource.cci.InteractionSpec
 * @see javax.resource.cci.Interaction
 * @see javax.resource.cci.Record
 */
public interface CuckooInteractionSpec extends InteractionSpec {

    String getFunctionName();

    @SuppressWarnings("unused")
    void setFunctionName(String functionName);
}
