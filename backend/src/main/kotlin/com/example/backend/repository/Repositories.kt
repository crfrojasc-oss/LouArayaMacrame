package com.example.backend.repository

import com.example.backend.model.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, String> {
    fun findByEmailOrAlias(email: String, alias: String): UserEntity?
    fun findByEmail(email: String): UserEntity?
}

@Repository
interface ProductRepository : JpaRepository<ProductEntity, String> {
    fun findByCategory(category: String): List<ProductEntity>
}

@Repository
interface RequestRepository : JpaRepository<RequestEntity, String> {
    fun findByUserId(userId: String): List<RequestEntity>
}

@Repository
interface CampaignRepository : JpaRepository<CampaignEntity, String>

@Repository
interface EventRepository : JpaRepository<EventEntity, String>
