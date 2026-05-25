package com.aguiabranca.inovacao.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aguiabranca.inovacao.data.local.dao.DashboardDao
import com.aguiabranca.inovacao.data.local.dao.IdeaDao
import com.aguiabranca.inovacao.data.local.dao.PendingActionDao
import com.aguiabranca.inovacao.data.local.dao.ProjectDao
import com.aguiabranca.inovacao.data.local.dao.StrategyDao
import com.aguiabranca.inovacao.data.local.dao.UserDao
import com.aguiabranca.inovacao.data.local.entity.DashboardSnapshotEntity
import com.aguiabranca.inovacao.data.local.entity.IdeaEntity
import com.aguiabranca.inovacao.data.local.entity.PendingActionEntity
import com.aguiabranca.inovacao.data.local.entity.ProjectEntity
import com.aguiabranca.inovacao.data.local.entity.StrategyEntity
import com.aguiabranca.inovacao.data.local.entity.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        StrategyEntity::class,
        IdeaEntity::class,
        ProjectEntity::class,
        DashboardSnapshotEntity::class,
        PendingActionEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun strategyDao(): StrategyDao
    abstract fun ideaDao(): IdeaDao
    abstract fun projectDao(): ProjectDao
    abstract fun dashboardDao(): DashboardDao
    abstract fun pendingActionDao(): PendingActionDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aguia_branca_sprint1.db"
                ).fallbackToDestructiveMigration()
                .build().also { instance = it }
            }
        }
    }
}

