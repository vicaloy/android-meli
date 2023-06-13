package com.valoy.meli

import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.valoy.meli.infraestructure.client.ArticleClient
import com.valoy.meli.infraestructure.repository.ArticleRemoteRepository
import com.valoy.meli.domain.repository.ArticleRepository
import com.valoy.meli.ui.viewmodel.ViewModelFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Injection {

    private fun provideArticleRepository(): ArticleRepository = ArticleRemoteRepository(
        provideProductClient(providerOkHttpClient())
    )

    fun provideViewModelFactory(owner: SavedStateRegistryOwner): ViewModelProvider.Factory {
        return ViewModelFactory(owner,  provideArticleRepository())
    }

    private fun providerOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    private fun provideProductClient(
        okHttpClient: OkHttpClient
    ): ArticleClient {
        return Retrofit.Builder()
            .baseUrl("https://api.mercadolibre.com/")
            .addConverterFactory(
                MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ArticleClient::class.java)
    }
}
