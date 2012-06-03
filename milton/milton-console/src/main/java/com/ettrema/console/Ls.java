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

import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ls extends AbstractConsoleCommand{

    private static final Logger log = LoggerFactory.getLogger(Ls.class);

    private final ResultFormatter resultFormatter;

    Ls(List<String> args, String host, String currentDir, ConsoleResourceFactory resourceFactory, ResultFormatter resultFormatter) {
        super(args, host, currentDir, resourceFactory);
        this.resultFormatter = resultFormatter;
    }

    
    
    @Override
    public Result execute() {
        try {
            Resource cur = currentResource();
            if( cur == null ) {
                return result("current dir not found: " + cursor.getPath().toString());
            }
            CollectionResource target;
            Cursor newCursor;
            if( args.size() > 0 ) {
                String dir = args.get(0);
                log.debug( "dir: " + dir);
                newCursor = cursor.find( dir );

                if( !newCursor.exists() ) {
                    return result("not found: " + dir);
                } else if( !newCursor.isFolder() ) {
                    return result("not a folder: " + dir);
                }
                target = (CollectionResource) newCursor.getResource();
            } else {
                newCursor = cursor;
                target = currentResource();
            }
            StringBuilder sb = new StringBuilder();
            List<? extends Resource> children = target.getChildren();
            sb.append( resultFormatter.begin( children));
            for( Resource r1 : target.getChildren() ) {
                String href = newCursor.getPath().child(r1.getName()).toString();
                sb.append(resultFormatter.format( href, r1 ));
            }
            sb.append( resultFormatter.end());
            return result(sb.toString());
        } catch (NotAuthorizedException ex) {
            log.error("not authorised", ex);
            return result(ex.getLocalizedMessage());
        } catch (BadRequestException ex) {
            log.error("bad req", ex);
            return result(ex.getLocalizedMessage());
        }
    }
}
