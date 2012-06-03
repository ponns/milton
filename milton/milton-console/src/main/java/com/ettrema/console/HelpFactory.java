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
import java.util.List;

public class HelpFactory extends AbstractConsoleCommandFactory {

    @Override
    public ConsoleCommand create(List<String> args, String host, String currentDir, Auth auth) {
        return new Help(args, host, currentDir, consoleResourceFactory);
    }

    @Override
    public String[] getCommandNames() {
        return new String[]{"help"};
    }

    @Override
    public String getDescription() {
        return "Help. Display all commands";
    }

    

}
