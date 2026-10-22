package com.example.louarayamacrame.model

enum class UserRole(val displayName: String) {
    CLIENTE("Cliente"),
    ADMIN("Administrador")
}

data class UserProfile(
    val id: String,
    val name: String,
    val alias: String,
    val email: String,
    val passwordHash: String,
    val phone: String = "+56 9 1234 5678",
    val role: UserRole = UserRole.CLIENTE,
    val avatarId: String = "avatar_1",
    val avatarColorHex: Long = 0xFFC86D51
)

data class EventShowroom(
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
    val description: String,
    val status: String, // "En curso", "Próximo"
    val featuredCollection: String
)

data class MacrameProduct(
    val id: String,
    val name: String,
    val category: String, // Matrimonios, Festivales, Decoración
    val price: Double,
    val description: String,
    val dimensions: String,
    val materials: String,
    val isAvailable: Boolean = true,
    val isFavorite: Boolean = false,
    val isCustomOrder: Boolean = false
)

data class MacrameCollection(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val productCount: Int,
    val badgeText: String
)
