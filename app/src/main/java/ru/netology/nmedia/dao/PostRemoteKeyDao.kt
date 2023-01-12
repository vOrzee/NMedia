package ru.netology.nmedia.dao

import androidx.room.*

@Dao
interface PostRemoteKeyDao {

    @Query("SELECT max(`idPost`) FROM PostRemoteKeyEntity")
    suspend fun max(): Long?

    @Query("SELECT min(`idPost`) FROM PostRemoteKeyEntity")
    suspend fun min(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postRemoteKeyEntity: PostRemoteKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postRemoteKeyEntity: List<PostRemoteKeyEntity>)

    @Query("DELETE FROM PostRemoteKeyEntity")
    suspend fun clear()
}