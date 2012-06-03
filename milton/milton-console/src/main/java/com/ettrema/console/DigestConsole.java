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

import com.bradmcevoy.http.DigestResource;
import com.bradmcevoy.http.ResourceFactory;
import com.bradmcevoy.http.http11.auth.DigestResponse;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author brad
 */
public class DigestConsole extends Console implements DigestResource{

    final DigestResource digestResource;

    public DigestConsole(String host, final ResourceFactory wrappedFactory, String name, DigestResource secureResource, Date modDate, Map<String,ConsoleCommandFactory> mapOfFactories) {
        super(host, wrappedFactory, name, secureResource, modDate, mapOfFactories );
        this.digestResource = secureResource;
    }

    public Object authenticate( DigestResponse digestRequest ) {
        return digestResource.authenticate( digestRequest );
    }

    public boolean isDigestAllowed() {
        return digestResource.isDigestAllowed();
    }



}
