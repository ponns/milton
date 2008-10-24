package com.bradmcevoy.http;

import java.util.Date;


/** Copyright 2008 Brad Mcevoy Licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable 
 * law or agreed to in writing, software distributed under the License is 
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY 
 * KIND, either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
 * 
 * 
 * Implementations should implemenet compareTo as an alphabetic comparison 
 *  on the name property
 * 
 * @author Alienware1
 */
public interface Resource extends Comparable {

    /**
     * 
     * @return - a string which uniquely identifies this resource. This will be
     * used in the ETag header field, and affects caching of resources. 
     * 
     * Returning a null value is allowed, and disables the ETag field
     */
    String getUniqueId();
    
    /**
     * 
     * @return - the name of this resource. Ie just the local name, within its folder
     */
    String getName();    
    
    
    /**
     * Check the given credentials, and return a relevant object if accepted.
     * 
     * @param user - the username provided by the user's agent
     * @param password - the password provided by the user's agent
     * @return - if credentials are accepted, some object to attach to the Auth object. otherwise null
     */
    Object authenticate(String user, String password);

    /** Return true if the current user is permitted to access this resource using
     *  the specified method.
     *
     *  Note that the current user may be determined by the Auth associated with
     *  the request, or by a seperate, application specific, login mechanism such
     *  as a session variable or cookie based system. This method should correctly
     *  interpret all such mechanisms
     *
     *  The auth given as a parameter will be null if authentication failed. The
     *  auth associated with the request will still exist
     */
    boolean authorise(Request request, Request.Method method,Auth auth);

    /** Return the security realm for this resource. Just any string identifier
     */
    String getRealm();

    /** The date and time that this resource, or any part of this resource, was last
     *  modified. For dynamic rendered resources this should consider everything
     *  which will influence its output.
     *
     *  Resources for which no such date can be calculated should return null  
     *
     */
    Date getModifiedDate();

    /** If unknown return nnull
     */
    Long getContentLength();

    /** The MIME type. Eg text/html, image/jpeg, etc
     *
     *  Must be IANA registered
     *
     *  accepts is the accepts header. Eg: Accept: text/*, text/html, text/html;level=1
     *
     *  See - http://www.iana.org/assignments/media-types/
     */
    String getContentType(String accepts);

    
    /** Determine if a redirect is required for this request, and if so return
     *  the URL to redirect to. May be absolute or relative.
     *  
     *  Called after authorization check but before any method specific processing
     *
     *  Return null for no redirect
     */
    abstract String checkRedirect(Request request);
    
}
