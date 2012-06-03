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

package com.ettrema.console;

import com.bradmcevoy.http.Resource;
import java.util.List;

public class DefaultResultFormatter implements ResultFormatter {

    public String format( String href, Resource r1 ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "<a href=\'" ).append( href ).append( "\'>" ).append( r1.getName() ).append( "</a>" ).append( "<br/>" );
        return sb.toString();
    }

    public String begin( List<? extends Resource> list ) {
        return "";
    }

    public String end() {
        return "";
    }
}
