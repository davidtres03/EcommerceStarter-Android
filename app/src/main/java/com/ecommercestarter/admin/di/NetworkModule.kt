package com.ecommercestarter.admin.di

import com.ecommercestarter.admin.data.api.AccountApiService
import com.ecommercestarter.admin.data.api.AuthApiService
import com.ecommercestarter.admin.data.api.BrandingApiService
import com.ecommercestarter.admin.data.api.CustomerApiService
import com.ecommercestarter.admin.data.api.DashboardApiService
import com.ecommercestarter.admin.data.api.OrderApiService
import com.ecommercestarter.admin.data.api.ProductApiService
import com.ecommercestarter.admin.data.preferences.UserPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(userPreferences: UserPreferences): Interceptor {
        return Interceptor { chain ->
            val accessToken = runBlocking { userPreferences.authToken.first() }
            val request = chain.request().newBuilder()

            if (!accessToken.isNullOrEmpty()) {
                // Use Bearer token for JWT authentication
                request.addHeader("Authorization", "Bearer $accessToken")
            }

            chain.proceed(request.build())
        }
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        tokenAuthenticator: com.ecommercestarter.admin.data.api.TokenAuthenticator,
        userPreferences: UserPreferences
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                
                // Dynamically get the current server URL
                val baseUrl = runBlocking {
                    userPreferences.serverUrl.first() ?: "http://localhost:5000/"
                }
                
                // Parse the base URL
                val cleanUrl = baseUrl.removePrefix("http://").removePrefix("https://").removeSuffix("/")
                val urlParts = cleanUrl.split(":")
                val host = urlParts[0]
                val isHttps = baseUrl.startsWith("https")
                
                // Rebuild the URL with the current server URL
                val newUrl = originalRequest.url.newBuilder()
                    .scheme(if (isHttps) "https" else "http")
                    .host(host)
                    .apply {
                        // Only set custom port if explicitly provided in URL
                        if (urlParts.size > 1) {
                            val customPort = urlParts[1].toIntOrNull()
                            if (customPort != null) {
                                port(customPort)
                            }
                        } else {
                            // Use default ports
                            port(if (isHttps) 443 else 80)
                        }
                    }
                    .build()
                
                val newRequest = originalRequest.newBuilder()
                    .url(newUrl)
                    .build()
                
                chain.proceed(newRequest)
            }
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        // Use a dummy base URL - the actual URL will be set dynamically by the interceptor
        return Retrofit.Builder()
            .baseUrl("http://localhost:5000/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBrandingApiService(retrofit: Retrofit): BrandingApiService {
        return retrofit.create(BrandingApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideProductApiService(retrofit: Retrofit): ProductApiService {
        return retrofit.create(ProductApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCategoryApiService(retrofit: Retrofit): com.ecommercestarter.admin.data.api.CategoryApiService {
        return retrofit.create(com.ecommercestarter.admin.data.api.CategoryApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSystemMonitoringApiService(retrofit: Retrofit): com.ecommercestarter.admin.data.api.SystemMonitoringApiService {
        return retrofit.create(com.ecommercestarter.admin.data.api.SystemMonitoringApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideApiConfigApiService(retrofit: Retrofit): com.ecommercestarter.admin.data.api.ApiConfigApiService {
        return retrofit.create(com.ecommercestarter.admin.data.api.ApiConfigApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSecurityApiService(retrofit: Retrofit): com.ecommercestarter.admin.data.api.SecurityApiService {
        return retrofit.create(com.ecommercestarter.admin.data.api.SecurityApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAnalyticsApiService(retrofit: Retrofit): com.ecommercestarter.admin.data.api.AnalyticsApiService {
        return retrofit.create(com.ecommercestarter.admin.data.api.AnalyticsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCustomerApiService(retrofit: Retrofit): CustomerApiService {
        return retrofit.create(CustomerApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOrderApiService(retrofit: Retrofit): OrderApiService {
        return retrofit.create(OrderApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDashboardApiService(retrofit: Retrofit): DashboardApiService {
        return retrofit.create(DashboardApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideAccountApiService(retrofit: Retrofit): AccountApiService {
        return retrofit.create(AccountApiService::class.java)
    }
}
