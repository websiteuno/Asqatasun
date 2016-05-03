/*
 *  Asqatasun - Automated webpage assessment
 *  Copyright (C) 2008-2015  Asqatasun.org
 * 
 *  This file is part of Asqatasun.
 * 
 *  Asqatasun is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 * 
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  Contact us by mail: asqatasun AT asqatasun DOT org
 */

package org.asqatasun.util.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author jkowalczyk
 */
public class HttpRequestHandler {
    
    private static final String ASQATASUN_USER_AGENT = "asqatasun";
    private static final Logger LOGGER  = Logger.getLogger(HttpRequestHandler.class);

    private String proxyPort;
    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }
    
    private String proxyHost;
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }
    
    private String proxyUser;
    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }
    
    private String proxyPassword;
    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }
    
    private boolean bypassCheck = false;
    public void setBypassCheck(String bypassCheck) {
        this.bypassCheck = Boolean.valueOf(bypassCheck);
    }
    
    /**
     * Multiple Url can be set through a unique String separated by ;
     */
    private final List<String> proxyExclusionUrlList = new ArrayList<>();
    public List<String> getProxyExclusionUrlList() {
        return proxyExclusionUrlList;
    }

    public void setProxyExclusionUrl(String proxyExclusionUrl) {
        if (StringUtils.isNotBlank(proxyExclusionUrl.trim())) {
            proxyExclusionUrlList.addAll(Arrays.asList(proxyExclusionUrl.split(";")));
        }
    }

    private int connectionTimeout = 3000;
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    private int socketTimeout = 3000;
    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }
    
    /**
     * The holder that handles the unique instance of HttpRequestHandler
     */
    private static class HttpRequestHandlerHolder {
        private static final HttpRequestHandler INSTANCE = new HttpRequestHandler();
    }
    
    /**
     * Private constructor
     */
    private HttpRequestHandler() {}
    
    /**
     * Singleton pattern based on the "Initialization-on-demand 
     * holder idiom". See @http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom
     * @return the unique instance of HttpRequestHandler
     */
    public static HttpRequestHandler getInstance() {
        return HttpRequestHandlerHolder.INSTANCE;
    }

    /**
     * 
     * @param url
     * @return whether the given Url is accessible or not
     */
    public boolean isUrlAccessible (String url) {
        if (bypassCheck) {
            LOGGER.debug("check on Url is bypassed by configuration");
            return true;
        }
        try {
            int statusFromHead = computeStatus(getHttpStatus(url));
            switch (statusFromHead) {
                case 1 :
                    return true;
                case 0 :
                    int statusFromGet = computeStatus(getHttpStatusFromGet(url));
                    switch (statusFromGet) {
                        case 0 :
                            return false;
                        case 1 :
                            return true;
                    }
            }
            return false;
        } catch (IOException ex) {
            LOGGER.debug(ex.getMessage());
            LOGGER.debug("IOException on " + url);
            return false;
        }
    }
    
    public int getHttpStatus (String url) throws IOException {
        String encodedUrl = getEncodedUrl(url);
        CloseableHttpClient httpClient = getHttpClient(encodedUrl);
        HttpHead head = new HttpHead(encodedUrl);
        try {
            LOGGER.debug("executing head request to retrieve page status on " + head.getURI());
            HttpResponse response = httpClient.execute(head);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("received " + response.getStatusLine().getStatusCode() + " from head request");
                for (Header h : head.getAllHeaders()) {
                    LOGGER.debug("header : " + h.getName() + " " + h.getValue());
                }
            }
            
            return response.getStatusLine().getStatusCode();
        } catch (UnknownHostException uhe) {
            LOGGER.warn("UnknownHostException on " + encodedUrl);
            return HttpStatus.SC_NOT_FOUND;
        } catch (IllegalArgumentException iae) {
            LOGGER.warn("IllegalArgumentException on " + encodedUrl);
            return HttpStatus.SC_NOT_FOUND;
        } catch (IOException ioe) {
            LOGGER.warn("IOException on " + encodedUrl);
            return HttpStatus.SC_NOT_FOUND;
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            head.releaseConnection();
            httpClient.close();
        }
    }
    
    public String getHttpContent (String url) throws URISyntaxException, UnknownHostException, IOException, IllegalCharsetNameException {
        if (StringUtils.isEmpty(url)){
            return "";
        }
        String encodedUrl = getEncodedUrl(url);
        CloseableHttpClient httpClient = getHttpClient(encodedUrl);
        HttpGet get = new HttpGet(encodedUrl);
        try {
            LOGGER.debug("executing request to retrieve content on " + get.getURI());
            HttpResponse response = httpClient.execute(get);
            LOGGER.debug("received " + response.getStatusLine().getStatusCode() + " from get request");
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                LOGGER.debug("status == HttpStatus.SC_OK " );
                return EntityUtils.toString(response.getEntity(), Charset.defaultCharset());
            } else {
                LOGGER.debug("status != HttpStatus.SC_OK " );
                return "";
            }
            
        } catch (NullPointerException ioe) {
            LOGGER.debug("NullPointerException");
            return "";
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            get.releaseConnection();
            LOGGER.debug("finally");
            httpClient.close();
        }
    }
    
    public int getHttpStatusFromGet (String url) throws IOException {
        String encodedUrl = getEncodedUrl(url);
        CloseableHttpClient httpClient = getHttpClient(encodedUrl);
        HttpGet get = new HttpGet(encodedUrl);
        try {
            LOGGER.debug("executing get request to retrieve status on " + get.getURI());
            HttpResponse status = httpClient.execute(get);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("received " + status + " from get request");
                for (Header h : get.getAllHeaders()) {
                    LOGGER.debug("header : " + h.getName() + " " +h.getValue());
                }
            }
            return status.getStatusLine().getStatusCode();
        } catch (UnknownHostException uhe) {
            LOGGER.warn("UnknownHostException on " + encodedUrl);
            return HttpStatus.SC_NOT_FOUND;
        } catch (IOException ioe) {
            LOGGER.warn("IOException on " + encodedUrl);
            return HttpStatus.SC_NOT_FOUND;
        }finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            get.releaseConnection();
            httpClient.close();
        }
    }
    
    private CloseableHttpClient getHttpClient(String url) {
        RequestConfig requestConfig = RequestConfig.custom()
			.setSocketTimeout(socketTimeout)
			.setConnectTimeout(connectionTimeout)
			.build();
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setDefaultRequestConfig(requestConfig);
        httpClientBuilder.setConnectionManager(new PoolingHttpClientConnectionManager());
        httpClientBuilder.setUserAgent(ASQATASUN_USER_AGENT);
        if (isProxySet(url)) {
            LOGGER.debug(("Set proxy with " + proxyHost + " and " + proxyPort));
            httpClientBuilder.setProxy(new HttpHost(proxyHost, Integer.valueOf(proxyPort)));
            if (isProxyCredentialSet()) {
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(
                        new AuthScope(proxyHost, Integer.valueOf(proxyPort)),
                        new UsernamePasswordCredentials(proxyUser, proxyPassword));
                httpClientBuilder.setDefaultCredentialsProvider(credsProvider);
                LOGGER.debug(("Set proxy credentials " + proxyHost + " and " + proxyPort + " and " + proxyUser + " and " + proxyPassword));
            }
        }
        return httpClientBuilder.build();
    }
    
    /**
     * 
     * @param url
     * @return 
     */
    public boolean isProxySet(String url) {
        for (String excludedUrl : proxyExclusionUrlList) {
            if (url.contains(excludedUrl) && StringUtils.isNotBlank(excludedUrl)) {
                LOGGER.debug("Proxy Not Set due to exclusion with : " + excludedUrl);
                return false;
            }
        }
        LOGGER.debug("isProxySet:  " + (StringUtils.isNotEmpty(proxyHost) && StringUtils.isNotEmpty(proxyPort)));
        return StringUtils.isNotEmpty(proxyHost) && StringUtils.isNotEmpty(proxyPort);
    }
    
    /**
     * 
     * @return
     */
    private boolean isProxyCredentialSet() {
        LOGGER.debug("isProxyCredentialSet" + (StringUtils.isNotEmpty(proxyUser) && StringUtils.isNotEmpty(proxyPassword)));
        return StringUtils.isNotEmpty(proxyUser) && StringUtils.isNotEmpty(proxyPassword);
    }
    
    private int computeStatus(int status) {
        switch (status) { 
            case HttpStatus.SC_FORBIDDEN:
            case HttpStatus.SC_METHOD_NOT_ALLOWED:
            case HttpStatus.SC_BAD_REQUEST:
            case HttpStatus.SC_UNAUTHORIZED:
            case HttpStatus.SC_PAYMENT_REQUIRED:
            case HttpStatus.SC_NOT_FOUND:
            case HttpStatus.SC_NOT_ACCEPTABLE:
            case HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED:
            case HttpStatus.SC_REQUEST_TIMEOUT:
            case HttpStatus.SC_CONFLICT:
            case HttpStatus.SC_GONE:
            case HttpStatus.SC_LENGTH_REQUIRED:
            case HttpStatus.SC_PRECONDITION_FAILED:
            case HttpStatus.SC_REQUEST_TOO_LONG:
            case HttpStatus.SC_REQUEST_URI_TOO_LONG:
            case HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE:
            case HttpStatus.SC_REQUESTED_RANGE_NOT_SATISFIABLE:
            case HttpStatus.SC_EXPECTATION_FAILED:
            case HttpStatus.SC_INSUFFICIENT_SPACE_ON_RESOURCE:
            case HttpStatus.SC_METHOD_FAILURE:
            case HttpStatus.SC_UNPROCESSABLE_ENTITY:
            case HttpStatus.SC_LOCKED:
            case HttpStatus.SC_FAILED_DEPENDENCY:
            case HttpStatus.SC_INTERNAL_SERVER_ERROR:
            case HttpStatus.SC_NOT_IMPLEMENTED:
            case HttpStatus.SC_BAD_GATEWAY:
            case HttpStatus.SC_SERVICE_UNAVAILABLE:
            case HttpStatus.SC_GATEWAY_TIMEOUT:
            case HttpStatus.SC_HTTP_VERSION_NOT_SUPPORTED:
            case HttpStatus.SC_INSUFFICIENT_STORAGE:
                return 0;
            case HttpStatus.SC_CONTINUE:
            case HttpStatus.SC_SWITCHING_PROTOCOLS:
            case HttpStatus.SC_PROCESSING:
            case HttpStatus.SC_OK:
            case HttpStatus.SC_CREATED:
            case HttpStatus.SC_ACCEPTED:
            case HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION:
            case HttpStatus.SC_NO_CONTENT:
            case HttpStatus.SC_RESET_CONTENT:
            case HttpStatus.SC_PARTIAL_CONTENT:
            case HttpStatus.SC_MULTI_STATUS:
            case HttpStatus.SC_MULTIPLE_CHOICES:
            case HttpStatus.SC_MOVED_PERMANENTLY:
            case HttpStatus.SC_MOVED_TEMPORARILY:
            case HttpStatus.SC_SEE_OTHER:
            case HttpStatus.SC_NOT_MODIFIED:
            case HttpStatus.SC_USE_PROXY:
            case HttpStatus.SC_TEMPORARY_REDIRECT:
                return 1;
            default : 
                return 1;
        }
    }
    
    private String getEncodedUrl(String url) {
        try {
            return HttpRequestHandler.encodeQuery(HttpRequestHandler.decode(url));
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("Exception on encoding " + url + " "  + e.getMessage());
            return url;
        } catch (DecoderException e) {
            LOGGER.warn("Exception on encoding " + url + " "  + e.getMessage());
            return url;
        }
    }

    /**
     * Escape and encode a string regarded as the query component of an URI with
     * the default protocol charset.
     * When a query string is not misunderstood the reserved special characters
     * ("&amp;", "=", "+", ",", and "$") within a query component, this method
     * is recommended to use in encoding the whole query.
     *
     * @param unescaped an unescaped string
     * @return the escaped string
     *
     *
     */
    private static String encodeQuery(String unescaped) throws UnsupportedEncodingException {
        byte[] rawdata = URLCodec.encodeUrl(new BitSet(256),
                HttpRequestHandler.getBytes(unescaped, Charset.forName("UTF-8").displayName()));
        return HttpRequestHandler.getAsciiString(rawdata);
    }

    /**
     * Converts the specified string to a byte array.  If the charset is not supported the
     * default system charset is used.
     *
     * @param data the string to be encoded
     * @param charset the desired character encoding
     * @return The resulting byte array.
     *
     */
    private static byte[] getBytes(final String data, String charset) {

        if (data == null) {
            throw new IllegalArgumentException("data may not be null");
        }

        if (charset == null || charset.length() == 0) {
            throw new IllegalArgumentException("charset may not be null or empty");
        }

        try {
            return data.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("Unsupported encoding: " + charset + ". System encoding used.");
            return data.getBytes();
        }
    }

    /**
     * Converts the byte array of ASCII characters to a string. This method is
     * to be used when decoding content of HTTP elements (such as response
     * headers)
     *
     * @param data the byte array to be encoded
     * @return The string representation of the byte array
     *
     * @since 3.0
     */
    private static String getAsciiString(final byte[] data) throws UnsupportedEncodingException {

        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }

        return new String(data, 0, data.length, "US-ASCII");
        }

    /**
     * Unescape and decode a given string regarded as an escaped string with the
     * default protocol charset.
     *
     * @param escaped a string
     * @return the unescaped string
     *
     *
     */
    public static String decode(String escaped) throws UnsupportedEncodingException, DecoderException {
        byte[] rawdata = URLCodec.decodeUrl(HttpRequestHandler.getAsciiBytes(escaped));
        return HttpRequestHandler.getString(rawdata, "UTF-8");
    }

    /**
     * Converts the byte array of HTTP content characters to a string. If
     * the specified charset is not supported, default system encoding
     * is used.
     *
     * @param data the byte array to be encoded
     * @param charset the desired character encoding
     * @return The result of the conversion.
     *
     * @since 3.0
     */
    private static String getString(
            final byte[] data,
            String charset
    ) {

        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }

        if (charset == null || charset.length() == 0) {
            throw new IllegalArgumentException("charset may not be null or empty");
        }

        try {
            return new String(data, 0, data.length, charset);
        } catch (UnsupportedEncodingException e) {

            LOGGER.warn("Unsupported encoding: " + charset + ". System encoding used");
            return new String(data, 0, data.length);
        }
    }

    /**
     * Converts the specified string to byte array of ASCII characters.
     *
     * @param data the string to be encoded
     * @return The string as a byte array.
     *
     * @since 3.0
     */
    private static byte[] getAsciiBytes(final String data) throws UnsupportedEncodingException {

        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }

        return data.getBytes("US-ASCII");
    }

}