package at.gv.egiz.pdfas.web.helper;

import at.gv.egiz.sl.util.SLMarschaller;

/**
 * Created by Andreas Fitzek on 6/23/16.
 */
public class JSONStartResponse {

    String url;
    String slRequest;
    String template;
    String locale;
    String bkuURL;

    public JSONStartResponse(String url, String slRequest, String template, String locale, String bkuURL) {
        this.url = url;
        this.slRequest = slRequest;
        this.template = template;
        this.bkuURL = bkuURL;
        this.locale = locale;
    }

    public String getBkuURL() {
        return bkuURL;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getSlRequest() {
        return slRequest;
    }

    public void setSlRequest(String slRequest) {
        this.slRequest = slRequest;
    }
}
