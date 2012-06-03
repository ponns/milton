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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Help extends AbstractConsoleCommand {

    final List<ConsoleCommandFactory> factories;

    Help(List<String> args, String host, String currentDir, ConsoleResourceFactory consoleResourceFactory) {
        super(args, host, currentDir, consoleResourceFactory);
        this.factories = consoleResourceFactory.factories;
    }
    
    @Override
    public Result execute() {
        StringBuilder sb = new StringBuilder();
        List<ConsoleCommandFactory> list = new ArrayList<ConsoleCommandFactory>();
        list.addAll(factories );
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                ConsoleCommandFactory f1 = (ConsoleCommandFactory)o1;
                ConsoleCommandFactory f2 = (ConsoleCommandFactory)o2;
                return f1.getCommandNames()[0].compareTo(f2.getCommandNames()[0]);
            }
        });
        for( ConsoleCommandFactory f : list ) {
            sb.append("<b>");
            for( String s : f.getCommandNames() ) {
                sb.append(s).append(" ");
            }
            sb.append("</b>");
            sb.append("<br/>").append("\n");
            sb.append("<br/>").append(f.getDescription());
            sb.append("<br/>").append("\n");
            sb.append("<br/>").append("\n");
        }
        return new Result(this.cursor.getPath().toString(), sb.toString());
    }

}
