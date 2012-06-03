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

import com.bradmcevoy.http.webdav.PropPatchHandler.Fields;

/**
 *
 *
 */
public interface PropPatchableResource extends Resource {
    /**
     *
     * @param fields
     * @deprecated - you should leave this method empty and implement CustomPropertyResource.
     * Starting with 1.5.0 you must configure a PropPatchableSetter onto the PropPatchHandler
     * for this method to be called.
     */
    public void setProperties(Fields fields);
}
