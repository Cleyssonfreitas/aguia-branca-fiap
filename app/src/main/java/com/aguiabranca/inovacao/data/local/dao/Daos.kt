package com.aguiabranca.inovacao.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aguiabranca.inovacao.data.local.entity.DashboardSnapshotEntity
import com.aguiabranca.inovacao.data.local.entity.IdeaEntity
import com.aguiabranca.inovacao.data.local.entity.PendingActionEntity
import com.aguiabranca.inovacao.data.local.entity.ProjectEntity
import com.aguiabranca.inovacao.data.local.entity.StrategyEntity
import com.aguiabranca.inovacao.data.local.entity.UserProfileEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    suspend fun getAll(): List<UserProfileEntity>

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    suspend fun getById(uid: String): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: UserProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(users: List<UserProfileEntity>)
}

@Dao
interface StrategyDao {
    @Query("SELECT * FROM strategies ORDER BY priority ASC, updatedAt DESC")
    suspend fun getAll(): List<StrategyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(strategy: StrategyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(strategies: List<StrategyEntity>)

    @Query("DELETE FROM strategies WHERE remoteId = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface IdeaDao {
    @Query("SELECT * FROM ideas WHERE createdBy = :uid ORDER BY updatedAt DESC")
    suspend fun getByUser(uid: String): List<IdeaEntity>

    @Query("SELECT * FROM ideas ORDER BY updatedAt DESC")
    suspend fun getAll(): List<IdeaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(idea: IdeaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(ideas: List<IdeaEntity>)
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    suspend fun getAll(): List<ProjectEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(project: ProjectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(projects: List<ProjectEntity>)
}

@Dao
interface DashboardDao {
    @Query("SELECT * FROM dashboard_snapshots WHERE id = 'leadership' LIMIT 1")
    suspend fun getLatest(): DashboardSnapshotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(snapshot: DashboardSnapshotEntity)
}

@Dao
interface PendingActionDao {
    @Query("SELECT * FROM pending_actions ORDER BY createdAt ASC")
    suspend fun getAll(): List<PendingActionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(action: PendingActionEntity)

    @Query("DELETE FROM pending_actions WHERE id = :id")
    suspend fun deleteById(id: Long)
}

