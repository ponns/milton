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

package com.bradmcevoy.http.values;

import com.bradmcevoy.http.webdav.PropFindResponse;
import java.util.ArrayList;

/**
 * Represents a list of responses.
 * 
 * Note you can't just have a genericed list because we need to know the type
 * of the list at runtime (for valuewriter determination) but generic information
 * is removed at compile time.
 *
 * @author bradm
 */
public class PropFindResponseList  extends ArrayList<PropFindResponse> {

    private static final long serialVersionUID = 1L;


	
}
