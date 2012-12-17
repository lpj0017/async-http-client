package com.ning.http.client.providers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import com.ning.http.client.FluentCaseInsensitiveStringsMap;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;
import com.ning.http.client.Response;
import com.ning.http.util.AsyncHttpProviderUtils;

public abstract class ResponseBase implements Response
{
    protected final static String DEFAULT_CHARSET = "ISO-8859-1";

    protected final List<HttpResponseBodyPart> bodyParts;
    protected final HttpResponseHeaders headers;
    protected final HttpResponseStatus status;

    protected ResponseBase(HttpResponseStatus status,
            HttpResponseHeaders headers,
            List<HttpResponseBodyPart> bodyParts)
    {
        this.bodyParts = bodyParts;
        this.headers = headers;
        this.status = status;
    }

    /* @Override */
    public final int getStatusCode() {
        return status.getStatusCode();
    }

    /* @Override */
    public final String getStatusText() {
        return status.getStatusText();
    }


    /* @Override */
    public final URI getUri() /*throws MalformedURLException*/ {
        return status.getUrl();
    }

    /* @Override */
    public final String getContentType() {
        return headers != null? getHeader("Content-Type"): null;
    }

    /* @Override */
    public final String getHeader(String name) {
        return headers != null? getHeaders().getFirstValue(name): null;
    }

    /* @Override */
    public final List<String> getHeaders(String name) {
        return headers != null? getHeaders().get(name): null;
    }

    /* @Override */
    public final FluentCaseInsensitiveStringsMap getHeaders() {
        return headers != null? headers.getHeaders(): new FluentCaseInsensitiveStringsMap();
    }

    /* @Override */
    public final boolean isRedirected() {
        return (status.getStatusCode() >= 300) && (status.getStatusCode() <= 399);
    }
    
    /* @Override */
    public byte[] getResponseBodyAsBytes() throws IOException {
        return AsyncHttpProviderUtils.contentToBytes(bodyParts);
    }

    /* @Override */
    public String getResponseBody() throws IOException {
        return getResponseBody(DEFAULT_CHARSET);
    }

    public String getResponseBody(String charset) throws IOException {
        String contentType = getContentType();
        if (contentType != null && charset == null) {
            charset = AsyncHttpProviderUtils.parseCharset(contentType);
        }

        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }

        return AsyncHttpProviderUtils.contentToString(bodyParts, charset);
    }

    /* @Override */
    public InputStream getResponseBodyAsStream() throws IOException {
        return AsyncHttpProviderUtils.contentAsStream(bodyParts);
    }

    protected String calculateCharset() {
        String charset = null;
        String contentType = getContentType();
        if (contentType != null) {
            charset = AsyncHttpProviderUtils.parseCharset(contentType);
        }

        if (charset == null) {
            charset = DEFAULT_CHARSET;
        }
        return charset;
    }

}
