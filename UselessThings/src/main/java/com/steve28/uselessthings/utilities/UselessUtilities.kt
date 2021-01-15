package com.steve28.uselessthings.utilities

import com.fasterxml.jackson.databind.ObjectMapper
import org.jsoup.Connection
import org.jsoup.Connection.Method
import org.jsoup.Jsoup

object Json {
    fun loads(json: String) = ObjectMapper().readValue(json, HashMap::class.java) as HashMap<String, *>
    fun dumps(json: HashMap<*, *>) = ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(json)
}

object Requests {
    private fun request(
        url: String,
        method: Method,
        data: HashMap<String, String>,
        headers: HashMap<String, String>,
        cookies: HashMap<String, String>,
        body: String,
        ignoreHttp: Boolean,
        ignoreContent: Boolean
    ): Connection.Response {
        val con = Jsoup.connect(url).method(method).data(data).headers(headers).cookies(cookies).requestBody(body)
        con.ignoreHttpErrors(ignoreHttp)
        con.ignoreContentType(ignoreContent)
        return con.execute()
    }

    fun get(
        url: String,
        params: HashMap<String, String> = hashMapOf(),
        headers: HashMap<String, String> = hashMapOf(),
        cookies: HashMap<String, String> = hashMapOf(),
        body: String = "",
        ignoreHttp: Boolean = false,
        ignoreContent: Boolean = false
    ) = request(url, Method.GET, params, headers, cookies, body, ignoreHttp, ignoreContent)


    fun post(
        url: String,
        data: HashMap<String, String> = hashMapOf(),
        headers: HashMap<String, String> = hashMapOf(),
        cookies: HashMap<String, String> = hashMapOf(),
        body: String = "",
        ignoreHttp: Boolean = false,
        ignoreContent: Boolean = false
    ) = request(url, Method.POST, data, headers, cookies, body, ignoreHttp, ignoreContent)
}