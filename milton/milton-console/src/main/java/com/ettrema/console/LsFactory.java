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

import com.bradmcevoy.http.Auth;
import com.ettrema.console.ResultFormatter;
import java.util.List;

public class LsFactory extends AbstractConsoleCommandFactory {

    private final ResultFormatter resultFormatter;

    public LsFactory() {
        this.resultFormatter = new DefaultResultFormatter();
    }

    public LsFactory( ResultFormatter resultFormatter ) {
        this.resultFormatter = resultFormatter;
    }



    @Override
    public String[] getCommandNames() {
        return new String[]{"ls"};
    }

    @Override
    public ConsoleCommand create( List<String> args, String host, String currentDir, Auth auth ) {
        return new Ls( args, host, currentDir, consoleResourceFactory, resultFormatter );
    }

    @Override
    public String getDescription() {
        return "List. List contents of the current or a specified directory";
    }
}
