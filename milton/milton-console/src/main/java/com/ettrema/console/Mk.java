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

import com.bradmcevoy.common.Path;
import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.PutableResource;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import org.slf4j.LoggerFactory;

public class Mk extends AbstractConsoleCommand {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger( Mk.class );
    
    public Mk(List<String> args, String host, String currentDir, ConsoleResourceFactory resourceFactory) {
        super(args, host, currentDir, resourceFactory);
    }

    @Override
    public Result execute() {
        try {
            String newName = args.get(0);
            if( newName == null || newName.length() == 0 ) return result("Please enter a new file name");
            String content = "";
            if( args.size() > 1 ) content = args.get(1);
            ByteArrayInputStream inputStream = new ByteArrayInputStream( content.getBytes());

            if( !cursor.isFolder() ) {
                return result("Couldnt find current folder");
            }
            CollectionResource cur = (CollectionResource) cursor.getResource();
            if( cur.child(newName) != null ) return result("File already exists: " + newName);

            if( cur instanceof PutableResource ) {
                PutableResource putable = (PutableResource) cur;
                try {
                    putable.createNew( newName, inputStream, (long) content.length(), newName );
                    Path newPath = cursor.getPath().child( newName );
                    return result( "created <a href='" + newPath + "'>" + newName + "</a>");
                } catch(BadRequestException e) {
                    return result("bad request exception");
                } catch(NotAuthorizedException ex) {
                    return result("not authorised");
                } catch( ConflictException ex ) {
                    return result("ConflictException writing content");
                } catch( IOException ex ) {
                    return result("IOException writing content");
                }
            } else {
                return result("the folder doesnt support creating new items");
            }
        } catch (NotAuthorizedException ex) {
            log.error("not authorised", ex);
            return result(ex.getLocalizedMessage());
        } catch (BadRequestException ex) {
            log.error("bad req", ex);
            return result(ex.getLocalizedMessage());
        }
    }

}
