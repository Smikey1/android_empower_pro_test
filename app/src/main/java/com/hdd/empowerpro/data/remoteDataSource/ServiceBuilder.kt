package com.hdd.empowerpro.data.remoteDataSource

import android.os.Build
import com.hdd.empowerpro.data.models.User
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

object ServiceBuilder {
    // it will be changed later on
     private const val BASE_URL="http://10.0.2.2:5000/"

    // for mbl deployment
//    private const val BASE_URL="http://192.168.1.138:90/"

//    --> enable this while using unit test and disable line number 39
//    private const val BASE_URL="http://localhost:5000/"
    var token:String? = null
    var loginCode:String? = null
    var uid:String? = null
    var user: User? = null
    var jobId: String? = ""

    var followingList = mutableListOf<String>()

    // logging interceptors
    private val httpLoggingInterceptor=HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    // creating own custom header Interceptor
    private val headerInterceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()
            // let us modify our http request
            request = request.newBuilder()
                .addHeader("x-device-type", Build.DEVICE)
                .addHeader("Accept-Language", Locale.getDefault().language)
                .build()
            return chain.proceed(request)
        }
    }

    // create a OkHttp Client instance
    private val oKHttp=OkHttpClient.Builder()
        .callTimeout(20,TimeUnit.SECONDS)
            // comment header interceptor while using unit test
        .addInterceptor(headerInterceptor)
        .addInterceptor(httpLoggingInterceptor).build()

    // create a retrofit builder
    private  val retrofitBuilder = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(oKHttp)

    // retrofit instance from retrofit builder
    private val retrofit = retrofitBuilder.build()

    // create a generic function which implement a class and return retrofit instance/object of respective class
    fun <T> buildService(anyClass:Class<T>):T {
        return retrofit.create(anyClass)
    }

}