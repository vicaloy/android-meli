package com.valoy.meli

import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.valoy.meli.infraestructure.client.ArticleClient
import com.valoy.meli.infraestructure.repository.ArticleRemoteRepository
import com.valoy.meli.domain.repository.ArticleRepository
import com.valoy.meli.ui.detail.ArticleDetailViewModelFactory
import com.valoy.meli.ui.search.ArticleSearchViewModelFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Injection {

    private fun provideArticleRepository(): ArticleRepository = ArticleRemoteRepository(
        provideProductClient(providerOkHttpClient())
    )

    fun provideArticleSearchViewModelFactory(owner: SavedStateRegistryOwner): ViewModelProvider.Factory {
        return ArticleSearchViewModelFactory(owner,  provideArticleRepository())
    }

    fun provideArticleDetailViewModelFactory(owner: SavedStateRegistryOwner): ViewModelProvider.Factory {
        return ArticleDetailViewModelFactory(owner,  provideArticleRepository())
    }

    private fun providerOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
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
