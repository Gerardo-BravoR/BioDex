package com.biodex.app.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class ErrorInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return try {
            val response = chain.proceed(request)

            if (!response.isSuccessful) {
                throw IOException("Error HTTP ${response.code}: ${response.message}")
            }

            response
        } catch (e: Exception) {
            throw IOException("Fallo de red en BioDex: ${e.message}", e)
        }
    }
}