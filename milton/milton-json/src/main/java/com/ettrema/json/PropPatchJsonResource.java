package com.ettrema.json;

import com.bradmcevoy.http.FileItem;
import com.bradmcevoy.http.PostableResource;
import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.Response.Status;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.bradmcevoy.http.webdav.PropFindResponse;
import com.bradmcevoy.http.webdav.PropFindResponse.NameAndError;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

public class PropPatchJsonResource extends JsonResource implements PostableResource {
    
    private final Resource wrappedResource;
    private final JsonPropPatchHandler patchHandler;
    private final String encodedUrl;
    private PropFindResponse resp;

    public PropPatchJsonResource( Resource wrappedResource, JsonPropPatchHandler patchHandler, String encodedUrl ) {
        super(wrappedResource, Request.Method.PROPPATCH.code);
        this.wrappedResource = wrappedResource;
        this.encodedUrl = encodedUrl;
        this.patchHandler = patchHandler;
    }

    public void sendContent( OutputStream out, Range range, Map<String, String> params, String contentType ) throws IOException, NotAuthorizedException {
        JsonConfig cfg = new JsonConfig();
        cfg.setIgnoreTransientFields(true);
        cfg.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);

        List<FieldError> errors = new ArrayList<FieldError>();
        for( Status stat : resp.getErrorProperties().keySet()) {
            List<NameAndError> props = resp.getErrorProperties().get(stat);
            for( NameAndError ne : props ) {
                errors.add(new FieldError(ne.getName().getLocalPart(), ne.getError(), stat.code));
            }
        }

        FieldError[] arr = new FieldError[errors.size()];
        arr = errors.toArray(arr);
        Writer writer = new PrintWriter(out);
        JSON json = JSONSerializer.toJSON(arr, cfg);
        json.write(writer);
        writer.flush();   
    }

    public String processForm(Map<String, String> parameters, Map<String, FileItem> files) throws BadRequestException, NotAuthorizedException {
        resp = patchHandler.process(wrappedResource, encodedUrl, parameters);
        return null;
    }

    public class FieldError {
        private String name;
        private String description;
        private int code;

        public FieldError(String name, String description, int code) {
            this.name = name;
            this.description = description;
            this.code = code;
        }



        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }


    }
}