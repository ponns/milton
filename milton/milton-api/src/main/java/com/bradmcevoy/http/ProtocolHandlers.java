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

package com.bradmcevoy.http;

import com.bradmcevoy.http.http11.Http11Protocol;
import com.bradmcevoy.http.quota.DefaultStorageChecker;
import com.bradmcevoy.http.quota.StorageChecker;
import com.bradmcevoy.http.webdav.DefaultWebDavResponseHandler;
import com.bradmcevoy.http.webdav.WebDavProtocol;
import com.bradmcevoy.http.webdav.WebDavResponseHandler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author brad
 */
public class ProtocolHandlers implements Iterable<HttpExtension> {

	private final List<HttpExtension> handlers;
	private final HandlerHelper handlerHelper;

	public ProtocolHandlers(List<HttpExtension> handlers) {
		this.handlers = handlers;
		this.handlerHelper = null;
	}

	public ProtocolHandlers(WebDavResponseHandler responseHandler, AuthenticationService authenticationService, List<HttpExtension> protocolList) {
		this.handlers = new ArrayList<HttpExtension>();
		List<StorageChecker> quotaCheckers = new ArrayList<StorageChecker>();
		quotaCheckers.add(new DefaultStorageChecker());
		this.handlerHelper = new HandlerHelper(authenticationService, quotaCheckers);
		for (HttpExtension protocol : protocolList) {
			this.handlers.add(protocol);
		}
	}

	public ProtocolHandlers(WebDavResponseHandler responseHandler, AuthenticationService authenticationService) {
		this.handlers = new ArrayList<HttpExtension>();
		List<StorageChecker> quotaCheckers = new ArrayList<StorageChecker>();
		quotaCheckers.add(new DefaultStorageChecker());
		this.handlerHelper = new HandlerHelper(authenticationService, quotaCheckers);
		this.handlers.add(new Http11Protocol(responseHandler, handlerHelper));
		this.handlers.add(new WebDavProtocol(responseHandler, handlerHelper));
	}

	public ProtocolHandlers(WebDavResponseHandler responseHandler, HandlerHelper handlerHelper) {
		this.handlerHelper = handlerHelper;
		this.handlers = new ArrayList<HttpExtension>();
		this.handlers.add(new Http11Protocol(responseHandler, handlerHelper));
		this.handlers.add(new WebDavProtocol(responseHandler, handlerHelper));
	}

	public ProtocolHandlers() {
		this.handlers = new ArrayList<HttpExtension>();
		AuthenticationService authenticationService = new AuthenticationService();
		WebDavResponseHandler responseHandler = new DefaultWebDavResponseHandler(authenticationService);
		this.handlerHelper = new HandlerHelper(authenticationService, new ArrayList<StorageChecker>());
		this.handlers.add(new Http11Protocol(responseHandler, handlerHelper));
		this.handlers.add(new WebDavProtocol(responseHandler, handlerHelper));
	}

	public Iterator<HttpExtension> iterator() {
		return handlers.iterator();
	}

	public boolean isEnableExpectContinue() {
		if (handlerHelper == null) {
			throw new RuntimeException("handlerHelper is not set. Read the appropriate property directly on injected HttpExtension implementations");
		}
		return handlerHelper.isEnableExpectContinue();
	}

	public void setEnableExpectContinue(boolean enableExpectContinue) {
		if (handlerHelper == null) {
			throw new RuntimeException("handlerHelper is not set. Set the appropriate property directly on injected HttpExtension implementations");
		}
		handlerHelper.setEnableExpectContinue(enableExpectContinue);
	}
}
