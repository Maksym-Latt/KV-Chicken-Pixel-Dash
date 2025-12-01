package com.chicken.pixeldash.di

import com.chicken.pixeldash.data.player.DataStorePlayerRepository
import com.chicken.pixeldash.data.player.PlayerRepository
import com.chicken.pixeldash.data.settings.DataStoreSettingsRepository
import com.chicken.pixeldash.data.settings.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: DataStoreSettingsRepository): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindPlayerRepository(impl: DataStorePlayerRepository): PlayerRepository

}
