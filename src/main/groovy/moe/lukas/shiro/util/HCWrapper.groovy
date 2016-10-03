package moe.lukas.shiro.util

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonValue
import groovy.transform.CompileStatic
import org.apache.http.HttpHost
import org.apache.http.NameValuePair
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

import java.util.concurrent.TimeUnit

import static moe.lukas.shiro.util.HCWrapper.REQUEST_TYPE.*
import static moe.lukas.shiro.util.HCWrapper.REQUEST_SECURITY.*

/**
 * A wrapper around Apache's HTTP Client
 * Because Java devs wrap everything &trade;
 */
@CompileStatic
class HCWrapper {
    /**
     * Request types
     */
    static enum REQUEST_TYPE {
        POST,
        GET
    }

    /**
     * Request security types
     */
    static enum REQUEST_SECURITY {
        PLAIN,
        SSL
    }

    /**
     * Start a new request
     * @param domain
     * @param path
     * @param requestType
     * @param requestSecurity
     * @param requestBody
     * @return
     */
    static JsonValue request(
        String domain,
        String path,
        REQUEST_TYPE requestType = GET,
        REQUEST_SECURITY requestSecurity = SSL,
        HashMap<String, String> requestBody = [:]
    ) {
        CloseableHttpClient httpClient = null
        JsonValue result = null

        try {
            HttpRequestBase requestContainer = null

            HttpHost target = (requestSecurity == PLAIN) ?
                new HttpHost(domain, 80, "http") :
                new HttpHost(domain, 443, "https")

            RequestConfig config = RequestConfig.custom().build()
            HttpClientContext context = HttpClientContext.create()

            httpClient = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .setConnectionTimeToLive(5, TimeUnit.SECONDS)
                .build()

            if (requestType == GET) {
                requestContainer = new HttpGet(path)
            } else {
                HttpPost request = new HttpPost(path)
                List<NameValuePair> requestEntity = []

                requestBody.each { String k, String v ->
                    requestEntity << new BasicNameValuePair(k, v)
                }

                request.setEntity(new UrlEncodedFormEntity(requestEntity))
                requestContainer = request
            }

            CloseableHttpResponse response = httpClient.execute(target, requestContainer, context)
            try {
                if (response.statusLine.statusCode > 200) {
                    throw new Exception("HTTP_${response.statusLine.statusCode}")
                } else {
                    result = Json.parse(EntityUtils.toString(response.entity))
                    EntityUtils.consume(response.entity)
                }
            } finally {
                response.close()
            }
        } catch (Exception e) {
            throw e
        } finally {
            httpClient.close()
        }

        if (result != null) {
            return result
        } else {
            throw new Exception("Incomplete result")
        }
    }
}
