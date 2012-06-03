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
import com.bradmcevoy.http.MoveableResource;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import java.util.List;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Rn extends AbstractConsoleCommand {

    private static final Logger log = LoggerFactory.getLogger( Rn.class );

    public Rn( List<String> args, String host, String currentDir, ConsoleResourceFactory resourceFactory ) {
        super( args, host, currentDir, resourceFactory );
    }

    @Override
    public Result execute() {
        try {
            String srcPath = args.get( 0 );
            String destName = args.get( 1 );
            log.debug( "rename: " + srcPath + "->" + destName );

            Path pSrc = Path.path( srcPath );

            Cursor sourceCursor = cursor.find( pSrc );
            Resource target = sourceCursor.getResource();

            if( target == null ) {
                log.debug( "target not found: " + srcPath );
                return result( "target not found: " + srcPath );
            } else {
                if( target instanceof MoveableResource ) {

                    CollectionResource currentParent = (CollectionResource) sourceCursor.getParent().getResource();
                    MoveableResource mv = (MoveableResource) target;
                    try {
                        mv.moveTo( currentParent, destName );
                    } catch( NotAuthorizedException e ) {
                        return result( "not authorised" );
                    } catch( BadRequestException e ) {
                        return result( "bad request" );
                    } catch( ConflictException ex ) {
                        return result( "conflict exception" );
                    }

                    Cursor newCursor = sourceCursor.getParent().find( destName );
                    return result( "created: <a href='" + newCursor.getPath() + "'>" + destName + "</a>" );
                } else {
                    return result( "resource is not moveable" );
                }
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
