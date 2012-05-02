package com.ettrema.http.caldav.demo.client;

import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.bradmcevoy.http.exceptions.NotFoundException;
import com.ettrema.http.caldav.demo.TResourceFactory;
import com.ettrema.httpclient.Folder;
import com.ettrema.httpclient.Host;
import com.ettrema.httpclient.HttpException;
import com.ettrema.httpclient.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * This class is a demonstration of using milton-client with calendars
 * 
 * It should be run as an executable java class
 *
 * @author brad
 */
public class QueryAndPutExample {
    public static void main(String[] args) throws Exception{
        String server = getArg(args, 0, "localhost");
        int port = Integer.parseInt(getArg(args, 1, "8080"));
        String rootPath = getArg(args, 2, "/p/userA");
        String userName = getArg(args, 3, "userA");
        String password = getArg(args, 4, "password");
        String path = getArg(args, 5, "/calendars/cal1");
        
        QueryAndPutExample example = new QueryAndPutExample(server, port, rootPath, userName, password);
        example.cd(path);
        example.query();
        example.putEvent();
        example.query();
    }

    private static String getArg(String[] args, int i, String sDefault) {
        if( i < args.length) {
            return args[i];
        } else {
            return sDefault;
        }
    }
    
    private String server;
    private int port;
    private String rootPath;
    private String userName;
    private String password;

    private Host host;
    private Folder folder;
    
    public QueryAndPutExample(String server, int port, String rootPath, String userName, String password) {
        this.server = server;
        this.port = port;
        this.rootPath = rootPath;
        host = new Host(server, rootPath, port, userName, password, null, null);
    }
    
    private void cd(String path) throws IOException, HttpException, NotAuthorizedException, BadRequestException {
        folder = host.getFolder(path);
    }

    private void query() throws IOException, HttpException, NotAuthorizedException, BadRequestException {                
        List<? extends Resource> children = folder.children();
        System.out.println("------ Query: " + folder.href() + " --------");
        for( Resource r : children ) {
            System.out.println("     Resource: " + r.name + " " + r.getModifiedDate() + " - " + r.getClass());
        }
    }
    
    private void putEvent() throws IOException, HttpException, NotAuthorizedException, ConflictException, BadRequestException, NotFoundException {        
        String icalData = TResourceFactory.createICalData();
        byte[] arr = icalData.getBytes();
        ByteArrayInputStream bin = new ByteArrayInputStream(arr);
        String newName = UUID.randomUUID().toString() + ".ics"; // just any unique name
        System.out.println("------ PUT Event to folder: " + folder.href() + " --------");
        folder.upload(newName, bin, arr.length, null);        
    }
    
    
}
