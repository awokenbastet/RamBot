package moe.lukas.shiro.util

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest

/**
 * Helper to shorten urls
 */
class URLShortener {
    /**
     * Shortens a URL using bfy.tw
     * @param url
     * @return
     */
    static String shorten(String url) {
        HttpResponse<String> response = Unirest
            .post("http://tny.im/yourls-api.php")
            .field("action", "shorturl")
            .field("url", url)
            .field("format", "simple")
            .asString()

        if (response.getStatus() != 200) {
            return null
        } else {
            return response.getBody()
        }
    }
}
