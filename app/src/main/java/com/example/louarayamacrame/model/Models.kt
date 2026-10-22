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
    val avatarColorHex: Long = 0xFF7B2CBF
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
    val isCustomOrder: Boolean = false,
    val qrCodeId: String = "QR-MAC-01"
)

data class MacrameCollection(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val productCount: Int,
    val badgeText: String
)

data class CustomRequest(
    val id: String,
    val folio: String,
    val userId: String,
    val userAlias: String,
    val eventType: String, // "Matrimonio", "Festival", "Hogar / Decoración", "Otro"
    val dimensions: String,
    val materialPreference: String,
    val colorPalette: String,
    val requiredDate: String,
    val comments: String,
    val status: String = "Pendiente", // "Pendiente", "En Revisión", "Aprobada", "En Confección", "Finalizada"
    val createdAt: String,
    val estimatedPrice: Double = 0.0
)

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val date: String,
    val type: String, // "Campaña", "Solicitud", "Feria", "Novedad"
    val isRead: Boolean = false,
    val targetRoute: String = "dashboard"
)

data class MacrameCampaign(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val startDate: String,
    val endDate: String,
    val isActive: Boolean = true,
    val badgeText: String = "Promoción Especial"
)

data class QrScanHistoryItem(
    val id: String,
    val qrCode: String,
    val productId: String?,
    val productName: String,
    val scannedAt: String
)
