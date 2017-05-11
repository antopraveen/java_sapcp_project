package com.sap.cloud.sample.connectivity;

import java.io.IOException;
import java.io.InputStream;

import static java.net.HttpURLConnection.HTTP_OK;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.core.connectivity.api.http.HttpDestination;
import com.sap.core.connectivity.api.DestinationFactory;

/**
 * Servlet class making http calls to specified http destinations.
 * Destinations are used in the following example connectivity scenarios:<br>
 * - Connecting to an outbound Internet resource using HTTP destinations<br>
 * - Connecting to an on-premise backend using on premise HTTP destinations,<br>
 *   where the destinations could have no authentication or BasicAuthentication.<br>
 *
 */
public class ConnectivityServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int COPY_CONTENT_BUFFER_SIZE = 1024;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectivityServlet.class);

    /** {@inheritDoc} */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpClient httpClient = null;
        String destinationName = request.getParameter("destname");
        try {
            // Get HTTP destination
            Context ctx = new InitialContext();
            HttpDestination destination = null;
            if (destinationName != null) {
                DestinationFactory destinationFactory = (DestinationFactory) ctx.lookup(DestinationFactory.JNDI_NAME);
                destination = (HttpDestination) destinationFactory.getDestination(destinationName);
            } else {
                // The default request to the Servlet will use outbound-internet-destination
                destinationName = "outbound-internet-destination";
                destination = (HttpDestination) ctx.lookup("java:comp/env/" +  destinationName);
            }
            // Create HTTP client
            httpClient = destination.createHttpClient();

            // Execute HTTP request
            HttpGet httpGet = new HttpGet();
            HttpResponse httpResponse = httpClient.execute(httpGet);

            // Check response status code
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HTTP_OK) {
                throw new ServletException("Expected response status code is 200 but it is " + statusCode + " .");
            }

            // Copy content from the incoming response to the outgoing response
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                try {
                    byte[] buffer = new byte[COPY_CONTENT_BUFFER_SIZE];
                    int len;
                    while ((len = instream.read(buffer)) != -1) {
                        response.getOutputStream().write(buffer, 0, len);
                    }
                } catch (IOException e) {
                    // In case of an IOException the connection will be released
                    // back to the connection manager automatically
                    throw e;
                } catch (RuntimeException e) {
                    // In case of an unexpected exception you may want to abort
                    // the HTTP request in order to shut down the underlying
                    // connection immediately.
                    httpGet.abort();
                    throw e;
                } finally {
                    // Closing the input stream will trigger connection release
                    try {
                        instream.close();
                    } catch (Exception e) {
                        // Ignore
                    }
                }
            }
        } catch (NamingException e) {
            // Lookup of destination failed
            String errorMessage = "Lookup of destination failed with reason: "
                    + e.getMessage()
                    + ". See "
                    + "logs for details. Hint: Make sure to have the destination "
                    + destinationName + " configured.";
            LOGGER.error("Lookup of destination failed", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    errorMessage);
        } catch (Exception e) {
            // Connectivity operation failed
            String errorMessage = "Connectivity operation failed with reason: "
                    + e.getMessage()
                    + ". See "
                    + "logs for details.";
            LOGGER.error("Connectivity operation failed", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    errorMessage);
        } finally {
            // When HttpClient instance is no longer needed, shut down the connection manager to ensure immediate
            // deallocation of all system resources
            if (httpClient != null) {
                httpClient.getConnectionManager().shutdown();
            }
        }
    }
}
