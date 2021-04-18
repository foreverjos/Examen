package com.example.examen.conection

import android.os.StrictMode
import cz.msebera.android.httpclient.HttpResponse
import cz.msebera.android.httpclient.NameValuePair
import cz.msebera.android.httpclient.client.ClientProtocolException
import cz.msebera.android.httpclient.client.HttpClient
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity
import cz.msebera.android.httpclient.client.methods.HttpGet
import cz.msebera.android.httpclient.client.methods.HttpPost
import cz.msebera.android.httpclient.client.methods.HttpUriRequest
import cz.msebera.android.httpclient.entity.StringEntity
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder
import cz.msebera.android.httpclient.message.BasicNameValuePair
import cz.msebera.android.httpclient.protocol.HTTP
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.SocketTimeoutException
import java.net.URLEncoder
import java.util.*

public class HttpClass(url_paramp: String) {


    private var params  = ArrayList<NameValuePair>()
    private var headers = ArrayList<NameValuePair>()
    private var responseCode = 0
    private var message: String = ""
    private var response: String = ""
    private var http_res: HttpResponse? = null
    private var iscontent = false
    private var content = ""
    private var url=url_paramp

    enum class RequestMethod {
        GET, POST
    }

    fun getResponse(): String {
        return response
    }

    fun getErrorMessage(): String {
        return message
    }

    fun getResponseCode(): Int {
        return responseCode
    }

    fun http_resp(): HttpResponse? {
        return http_res
    }

    fun is_contect(iscontent: Boolean) {
        this.iscontent = iscontent
    }

    fun add_contect(content: String) {
        this.content = content
    }


    fun AddParam(name: String, value: String) {
        params!!.add(BasicNameValuePair(name, value))
    }

    fun AddHeader(name: String, value: String) {
        headers!!.add(BasicNameValuePair(name, value))
    }

    @Throws(Exception::class)
    fun Execute(method: RequestMethod?) {
        message = ""
        response = ""
        when (method) {
            RequestMethod.GET -> {

                //add parameters
                var combinedParams = ""
                if (!params!!.isEmpty()) {
                    combinedParams += "?"
                    for (p in params!!) {
                        val paramString = p.name + "=" + URLEncoder.encode(p.value, "UTF-8")
                        combinedParams += if (combinedParams.length > 1) {
                            "&$paramString"
                        } else {
                            paramString
                        }
                    }
                }
                val request = HttpGet(url + combinedParams)
                //add headers
                for (h in headers!!) {
                    request.addHeader(h.name, h.value)
                }
                executeRequest(request, url)
            }
            RequestMethod.POST -> {
                val request = HttpPost(url)
                //add headers
                for (h in headers!!) {
                    request.addHeader(h.name, h.value)
                }
                if (!params!!.isEmpty()) {
                    request.entity = UrlEncodedFormEntity(params, HTTP.UTF_8)
                } else {
                    if (iscontent) {
                        if (content.trim { it <= ' ' } != "") {
                            val stringEntity = StringEntity(content)
                            request.entity = stringEntity
                        }
                    }
                }
                executeRequest(request, url)
            }
        }
    }

    private fun executeRequest(request: HttpUriRequest, url: String) {

        //permite ejecutar httpclient en android 3.0 en adelante sin necesidad de otro hilo
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        //HttpClient client = new DefaultHttpClient(httpParameters);
        val client: HttpClient = HttpClientBuilder.create().build()
        val httpResponse: HttpResponse
        try {
            httpResponse = client.execute(request)
            responseCode = httpResponse.statusLine.statusCode
            message = httpResponse.statusLine.reasonPhrase
            val entity = httpResponse.entity
            if (entity != null) {
                val instream = entity.content
                response = convertStreamToString(instream)
                http_res = httpResponse
                // Closing the input stream will trigger connection release
                instream.close()
            } else {
                response = ""
            }
        } catch (e: ClientProtocolException) {
            message = "ClientProtocolException $e"
            response = ""
        } catch (e: SocketTimeoutException) {
            message = "SocketTimeoutException $e"
            response = ""
        } catch (e: IOException) {
            message = "IOException$e"
            response = ""
        }
    }

    private fun convertStreamToString(`is`: InputStream): String {
        val reader = BufferedReader(InputStreamReader(`is`))
        val sb = StringBuilder()
        var line: String? = null
        try {
            while (reader.readLine().also { line = it } != null) {
                sb.append(
                    """
                    $line
                    
                    """.trimIndent()
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
            message = e.message.toString()
        } finally {
            try {
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
                message = e.toString()
            }
        }
        return sb.toString()
    }
}