/*
 * Copyright (C) 2012 McEvoy Software Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.bradmcevoy.http;

import java.util.Set;

/**
 * Extension to PropFindableResource which allows custom
 * properties to be returned.
 *
 * See MultiNamespaceCustomPropertySource to support multiple namespaces
 *
 * @author brad
 */
public interface CustomPropertyResource extends PropFindableResource {

    /**
     * 
     * @return - a list of all the properties of this namespace which exist
     * on this resource
     */
    public Set<String> getAllPropertyNames();

    /**
     * Return an accessor for the given property if it is supported or known. Note
     * that this includes cases where the value of the property is null
     *
     * @param name
     * @return - null if the property is unknown or not supported. Otherwise an
     * accessor to the property
     */
    public CustomProperty getProperty(String name);

    /**
     * Returns a URI used as a namespace for these properties.
     * 
     * @return
     */
    public String getNameSpaceURI();



}
