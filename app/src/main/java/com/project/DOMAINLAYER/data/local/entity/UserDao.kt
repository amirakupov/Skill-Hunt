package com.project.DOMAINLAYER.data.local.entity

import android.content.Context
import androidx.privacysandbox.tools.core.generator.build

@androidx.room.Dao
interface UserDao {
    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @androidx.room.Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): Flow<UserEntity?> // Note: Flow for reactive updates

    @androidx.room.Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return androidx.room.Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "skill_hunt_domain_db"
        ).build()
    }

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao = appDatabase.userDao()
    // ... provide other DAOs
}