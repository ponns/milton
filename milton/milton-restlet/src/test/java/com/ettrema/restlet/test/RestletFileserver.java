package com.ettrema.restlet.test;

import com.ettrema.http.fs.FileSystemResourceFactory;
import com.ettrema.http.fs.FsMemoryLockManager;
import com.ettrema.http.fs.NullSecurityManager;
import com.ettrema.http.fs.SimpleFileContentService;
import com.ettrema.restlet.WebDavRestlet;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;

import java.io.File;

public class RestletFileserver extends Component {

    public static void main(String[] args) throws Exception {
        Component component = new Component();

        component.getServers().add(Protocol.HTTP, 8080);

        // Restlet logs requests by default
        // component.getLogService().setEnabled(false);

        component.getDefaultHost().attach(
                new Application() {
                    @Override
                    public Restlet createInboundRoot() {

                        FileSystemResourceFactory factory = new FileSystemResourceFactory(
                                new File(System.getProperty("user.home")),
                                new NullSecurityManager()
                        );

                        factory.setContentService(new SimpleFileContentService());
                        factory.setLockManager(new FsMemoryLockManager());

                        return new WebDavRestlet(factory);
                    }
                }

        );

        component.start();

        System.out.println(
                "Restlet demo fileserver started, open http://localhost:8080 in your browser or WebDAV client..."
        );
    }


}
