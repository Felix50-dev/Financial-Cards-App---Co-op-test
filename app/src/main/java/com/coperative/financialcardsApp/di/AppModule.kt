package com.coperative.financialcardsApp.di

import android.content.Context
import androidx.room.Room
import com.coperative.financialcardsApp.data.local.database.CardDatabase
import com.coperative.financialcardsApp.data.local.dao.CardDao
import com.coperative.financialcardsApp.data.remote.MockApi
import com.coperative.financialcardsApp.domain.repositories.cardRepositories.CardRepositoryImpl
import com.coperative.financialcardsApp.domain.repositories.cardRepositories.CardRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideFinancialApi(
        moshi: Moshi,
        client: OkHttpClient
    ): MockApi {
        return Retrofit.Builder()
            .baseUrl("https://card-services.free.beeceptor.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(MockApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): CardDatabase {
        return Room.databaseBuilder(
            context,
            CardDatabase::class.java,
            "financial_cards_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideCardDao(db: CardDatabase): CardDao = db.cardDao()

    @Provides
    @Singleton
    fun provideCardRepository(
        api: MockApi,
        dao: CardDao
    ): CardRepository {
        return CardRepositoryImpl(api, dao)
    }
}
