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

package com.ettrema.davproxy.adapter;

import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.SecurityManager;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.bradmcevoy.http.exceptions.NotFoundException;
import com.ettrema.davproxy.content.FolderHtmlContentGenerator;
import com.ettrema.httpclient.File;
import com.ettrema.httpclient.Folder;
import com.ettrema.httpclient.HttpException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author brad
 */
public class RemoteManager {

    private final com.bradmcevoy.http.SecurityManager securityManager;
    private final FolderHtmlContentGenerator contentGenerator;

    public RemoteManager(SecurityManager securityManager, FolderHtmlContentGenerator contentGenerator) {
        this.securityManager = securityManager;
        this.contentGenerator = contentGenerator;
    }

    public List<? extends com.bradmcevoy.http.Resource> getChildren(String hostName, com.ettrema.httpclient.Folder folder) throws IOException, HttpException, NotAuthorizedException, BadRequestException {

        List<com.bradmcevoy.http.Resource> list = new ArrayList<Resource>();
        for (com.ettrema.httpclient.Resource r : folder.children()) {
            list.add(adapt(hostName, r));
        }
        return list;
    }

    public com.bradmcevoy.http.Resource adapt(String hostName, com.ettrema.httpclient.Resource remote) {
        if (remote instanceof com.ettrema.httpclient.Folder) {
            com.ettrema.httpclient.Folder f = (com.ettrema.httpclient.Folder) remote;
            return new FolderResourceAdapter(f, securityManager, hostName, contentGenerator, this);
        } else {
            com.ettrema.httpclient.File f = (com.ettrema.httpclient.File) remote;
            return new FileResourceAdapter(f, securityManager, hostName, this);
        }
    }

    public void copyTo(com.ettrema.httpclient.Resource remoteResource, String destName, Folder destRemoteFolder) throws RuntimeException, ConflictException, BadRequestException, NotAuthorizedException {
        try {
            if (destName.equals(remoteResource.name)) { // this is the normal case, copy with no rename                
                remoteResource.copyTo(destRemoteFolder);
            } else {    // its possible to request a copy with a new name
                remoteResource.copyTo(destRemoteFolder, destName);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (HttpException ex) {
            throw new RuntimeException(ex);
        } catch (NotFoundException ex) {
            throw new BadRequestException("Remote resource does not exist", ex);
        }
    }

    public void moveTo(com.ettrema.httpclient.Resource remoteResource, String destName, Folder destRemoteFolder) throws NotAuthorizedException, ConflictException, BadRequestException {
        try {
            if (destName.equals(remoteResource.name)) { // this is the normal case, move with no rename                
                remoteResource.moveTo(destRemoteFolder);
            } else {    // its possible to request a copy with a new name
                remoteResource.moveTo(destRemoteFolder, destName);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (HttpException ex) {
            throw new RuntimeException(ex);
        } catch (NotFoundException ex) {
            throw new BadRequestException("Remote resource does not exist", ex);
        }
    }
}
