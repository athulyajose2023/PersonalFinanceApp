package com.example.personalfinanceapp.di

import android.content.Context
import androidx.room.Room
import com.example.personalfinanceapp.ExpenseDao
import com.example.personalfinanceapp.ExpenseDatabase
import com.example.personalfinanceapp.ExpenseRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): ExpenseDatabase =
        Room.databaseBuilder(context, ExpenseDatabase::class.java, "item_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideItemDao(appDatabase: ExpenseDatabase): ExpenseDao = appDatabase.expenseDao()

    @Provides
    fun provideItemRepository(itemDao: ExpenseDao): ExpenseRepository = ExpenseRepository(itemDao)

}