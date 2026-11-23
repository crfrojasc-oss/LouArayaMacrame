package com.example.backend.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    val id: String,
    val name: String,
    val alias: String,
    val email: String,
    val passwordHash: String,
    val phone: String,
    val role: String, // CLIENTE, ADMIN
    val avatarId: String,
    val avatarColorHex: Long
)

@Entity
@Table(name = "products")
data class ProductEntity(
    @Id
    val id: String,
    val name: String,
    val category: String,
    val price: Double,
    @Column(length = 1000)
    val description: String,
    val dimensions: String,
    val materials: String,
    val isAvailable: Boolean = true,
    val isFavorite: Boolean = false,
    val isCustomOrder: Boolean = false,
    val qrCodeId: String = "QR-MAC-01"
)

@Entity
@Table(name = "custom_requests")
data class RequestEntity(
    @Id
    val id: String,
    val folio: String,
    val userId: String,
    val userAlias: String,
    val eventType: String,
    val dimensions: String,
    val materialPreference: String,
    val colorPalette: String,
    val requiredDate: String,
    @Column(length = 1000)
    val comments: String,
    val status: String, // Pendiente, En Revisión, Aprobada, En Confección, Finalizada
    val createdAt: String,
    val estimatedPrice: Double
)

@Entity
@Table(name = "campaigns")
data class CampaignEntity(
    @Id
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val startDate: String,
    val endDate: String,
    val isActive: Boolean,
    val badgeText: String
)

@Entity
@Table(name = "events")
data class EventEntity(
    @Id
    val id: String,
    val title: String,
    val locationName: String,
    val address: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val startDate: String,
    val endDate: String,
    val schedule: String,
    val standNumber: String,
    @Column(length = 1000)
    val description: String,
    val status: String,
    val featuredCollection: String
)
