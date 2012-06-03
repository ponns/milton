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
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cd extends AbstractConsoleCommand{

    private static final Logger log = LoggerFactory.getLogger(Cd.class);
    
    Cd(List<String> args, String host, String currentDir, ConsoleResourceFactory resourceFactory) {
        super(args, host, currentDir, resourceFactory);
    }


    @Override
    public Result execute() {
        try {
            log.debug("execute");
            String sPath = args.get(0);
            Path path = Path.path(sPath);
            log.debug("cd path: " + path.toString());
            Resource r;
            Cursor c = cursor.find( path );
            if( !c.exists() ) {
                return result("not found: " + path);
            } else if( !c.isFolder()) {
                return result("not a folder: " + path);
            } else {
                return new Result(c.getPath().toString(),"");
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
