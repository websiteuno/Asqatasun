package org.asqatasun.rest.request;

/**
 * Created by jkowalczyk on 10/27/16.
 */
public class PageAuditRequest {

    public String referential;
    public String level;
    public String url;

    public PageAuditRequest(){};
    public PageAuditRequest(String referential, String level,  String url) {
        this.level = level;
        this.referential = referential;
        this.url = url;
    }
}
