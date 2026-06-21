package com.example.app.feature.article.data.di

import com.example.app.feature.article.data.api.ArticleApi
import com.example.app.feature.article.data.repository.ArticleRepositoryImpl
import com.example.app.feature.article.domain.repository.ArticleRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ArticleModule {

    @Provides
    @Singleton
    fun provideArticleApi(retrofit: Retrofit): ArticleApi = retrofit.create(ArticleApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ArticleBindingModule {

    @Binds
    abstract fun bindArticleRepository(impl: ArticleRepositoryImpl): ArticleRepository
}
