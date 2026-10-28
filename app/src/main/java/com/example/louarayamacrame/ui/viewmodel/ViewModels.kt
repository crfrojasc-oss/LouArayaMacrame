package com.example.louarayamacrame.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.louarayamacrame.data.CampaignRepository
import com.example.louarayamacrame.data.CatalogRepository
import com.example.louarayamacrame.data.EventRepository
import com.example.louarayamacrame.data.NotificationRepository
import com.example.louarayamacrame.data.QrRepository
import com.example.louarayamacrame.data.RequestRepository
import com.example.louarayamacrame.data.UserRepository
import com.example.louarayamacrame.model.AppNotification
import com.example.louarayamacrame.model.CustomRequest
import com.example.louarayamacrame.model.EventShowroom
import com.example.louarayamacrame.model.MacrameCampaign
import com.example.louarayamacrame.model.MacrameCollection
import com.example.louarayamacrame.model.MacrameProduct
import com.example.louarayamacrame.model.QrScanHistoryItem
import com.example.louarayamacrame.model.UserProfile
import com.example.louarayamacrame.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AuthViewModel : ViewModel() {
    val activeUser: StateFlow<UserProfile?> = UserRepository.activeUser
    val registeredUsers: StateFlow<List<UserProfile>> = UserRepository.users

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _registerError = MutableStateFlow<String?>(null)
    val registerError: StateFlow<String?> = _registerError.asStateFlow()

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess.asStateFlow()

    fun login(emailOrAlias: String, password: String): Boolean {
        _loginError.value = null
        if (emailOrAlias.isBlank() || password.isBlank()) {
            _loginError.value = "Por favor ingresa correo/alias y contraseña."
            return false
        }

        val result = UserRepository.login(emailOrAlias, password)
        return if (result.isSuccess) {
            true
        } else {
            _loginError.value = result.exceptionOrNull()?.message ?: "Error al iniciar sesión."
            false
        }
    }

    fun loginAsAdminDemo() {
        _loginError.value = null
        UserRepository.login("admin@louaraya.cl", "admin123")
    }

    fun register(
        name: String,
        alias: String,
        email: String,
        password: String,
        confirmPassword: String,
        phone: String,
        avatarId: String
    ): Boolean {
        _registerError.value = null
        _registerSuccess.value = false

        if (name.isBlank() || alias.isBlank() || email.isBlank() || password.isBlank()) {
            _registerError.value = "Todos los campos marcados con (*) son obligatorios."
            return false
        }
        if (!email.contains("@") || !email.contains(".")) {
            _registerError.value = "Ingresa un correo electrónico válido."
            return false
        }
        if (password.length < 6) {
            _registerError.value = "La contraseña debe tener al menos 6 caracteres."
            return false
        }
        if (password != confirmPassword) {
            _registerError.value = "Las contraseñas no coinciden."
            return false
        }

        val result = UserRepository.registerUser(name, alias, email, password, phone, avatarId)
        return if (result.isSuccess) {
            _registerSuccess.value = true
            true
        } else {
            _registerError.value = result.exceptionOrNull()?.message ?: "Error al registrar cliente."
            false
        }
    }

    fun updateProfile(name: String, alias: String, phone: String, avatarId: String) {
        UserRepository.updateProfile(name, alias, phone, avatarId)
    }

    fun switchUser(user: UserProfile) {
        UserRepository.switchActiveUser(user)
    }

    fun logout() {
        UserRepository.logout()
    }

    fun clearErrors() {
        _loginError.value = null
        _registerError.value = null
        _registerSuccess.value = false
    }
}

class DashboardViewModel : ViewModel() {
    val activeUser: StateFlow<UserProfile?> = UserRepository.activeUser
    val collections: List<MacrameCollection> = CatalogRepository.sampleCollections
    val products: StateFlow<List<MacrameProduct>> = CatalogRepository.products
    val notifications: StateFlow<List<AppNotification>> = NotificationRepository.notifications

    val unreadCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun toggleRoleDemo() {
        val current = UserRepository.activeUser.value
        if (current == null) {
            UserRepository.login("admin@louaraya.cl", "admin123")
        } else if (current.role == UserRole.ADMIN) {
            val firstClient = UserRepository.users.value.firstOrNull { it.role == UserRole.CLIENTE }
            if (firstClient != null) {
                UserRepository.switchActiveUser(firstClient)
            }
        } else {
            UserRepository.switchActiveUser(UserRepository.adminAccount)
        }
    }
}

class MapViewModel : ViewModel() {
    val allEvents: StateFlow<List<EventShowroom>> = EventRepository.events

    private val _selectedCity = MutableStateFlow("Todas")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    private val _selectedStatus = MutableStateFlow("Todos")
    val selectedStatus: StateFlow<String> = _selectedStatus.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedEvent = MutableStateFlow<EventShowroom?>(EventRepository.events.value.firstOrNull())
    val selectedEvent: StateFlow<EventShowroom?> = _selectedEvent.asStateFlow()

    val filteredEvents: StateFlow<List<EventShowroom>> = combine(
        allEvents,
        _selectedCity,
        _selectedStatus,
        _searchQuery
    ) { eventsList, city, status, query ->
        eventsList.filter { event ->
            val matchesCity = city == "Todas" || event.city.equals(city, ignoreCase = true)
            val matchesStatus = status == "Todos" || event.status.equals(status, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    event.title.contains(query, ignoreCase = true) ||
                    event.locationName.contains(query, ignoreCase = true) ||
                    event.city.contains(query, ignoreCase = true)
            matchesCity && matchesStatus && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, EventRepository.events.value)

    fun setCity(city: String) {
        _selectedCity.value = city
    }

    fun setStatus(status: String) {
        _selectedStatus.value = status
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectEvent(event: EventShowroom?) {
        _selectedEvent.value = event
    }

    fun addEvent(
        title: String,
        locationName: String,
        address: String,
        city: String,
        schedule: String,
        standNumber: String,
        description: String
    ) {
        val newEvent = EventShowroom(
            id = "evt_${System.currentTimeMillis()}",
            title = title,
            locationName = locationName,
            address = address,
            city = city,
            latitude = -33.4000,
            longitude = -70.5700,
            startDate = "Hoy",
            endDate = "Fin de semana",
            schedule = schedule,
            standNumber = standNumber,
            description = description,
            status = "Próximo",
            featuredCollection = "Colección General"
        )
        EventRepository.addEvent(newEvent)
        _selectedEvent.value = newEvent
    }
}

class CatalogViewModel : ViewModel() {
    val products: StateFlow<List<MacrameProduct>> = CatalogRepository.products

    private val _selectedCategory = MutableStateFlow("Todas")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredProducts: StateFlow<List<MacrameProduct>> = combine(
        products,
        _selectedCategory,
        _searchQuery
    ) { list, cat, query ->
        list.filter { p ->
            val matchesCat = cat == "Todas" || p.category.equals(cat, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    p.name.contains(query, ignoreCase = true) ||
                    p.materials.contains(query, ignoreCase = true) ||
                    p.category.contains(query, ignoreCase = true)
            matchesCat && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val favoriteProducts: StateFlow<List<MacrameProduct>> = products.map { list ->
        list.filter { it.isFavorite }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(productId: String) {
        CatalogRepository.toggleFavorite(productId)
    }

    fun getProductById(id: String): MacrameProduct? {
        return CatalogRepository.getProductById(id)
    }

    fun getProductByQrCode(code: String): MacrameProduct? {
        return CatalogRepository.getProductByQrCode(code)
    }
}

class RequestViewModel : ViewModel() {
    val allRequests: StateFlow<List<CustomRequest>> = RequestRepository.requests

    fun getRequestsForUser(userId: String): List<CustomRequest> {
        return allRequests.value.filter { it.userId == userId }
    }

    fun createRequest(
        user: UserProfile,
        eventType: String,
        dimensions: String,
        materialPreference: String,
        colorPalette: String,
        requiredDate: String,
        comments: String
    ): CustomRequest {
        return RequestRepository.createRequest(
            user = user,
            eventType = eventType,
            dimensions = dimensions,
            materialPreference = materialPreference,
            colorPalette = colorPalette,
            requiredDate = requiredDate,
            comments = comments
        )
    }

    fun updateStatus(requestId: String, newStatus: String) {
        RequestRepository.updateStatus(requestId, newStatus)
    }
}

class NotificationViewModel : ViewModel() {
    val notifications: StateFlow<List<AppNotification>> = NotificationRepository.notifications

    val unreadCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun markAsRead(id: String) {
        NotificationRepository.markAsRead(id)
    }

    fun markAllAsRead() {
        NotificationRepository.markAllAsRead()
    }
}

class AdminViewModel : ViewModel() {
    val campaigns: StateFlow<List<MacrameCampaign>> = CampaignRepository.campaigns
    val scanHistory: StateFlow<List<QrScanHistoryItem>> = QrRepository.scanHistory

    fun saveProduct(
        id: String?,
        name: String,
        category: String,
        price: Double,
        description: String,
        dimensions: String,
        materials: String,
        isAvailable: Boolean,
        isCustomOrder: Boolean,
        qrCodeId: String
    ) {
        val prodId = id ?: "prod_${System.currentTimeMillis()}"
        val product = MacrameProduct(
            id = prodId,
            name = name,
            category = category,
            price = price,
            description = description,
            dimensions = dimensions,
            materials = materials,
            isAvailable = isAvailable,
            isFavorite = false,
            isCustomOrder = isCustomOrder,
            qrCodeId = qrCodeId.ifBlank { "QR-$prodId" }
        )
        CatalogRepository.saveProduct(product)
    }

    fun deleteProduct(productId: String) {
        CatalogRepository.deleteProduct(productId)
    }

    fun createCampaign(
        title: String,
        subtitle: String,
        category: String,
        startDate: String,
        endDate: String,
        badgeText: String,
        sendPush: Boolean
    ) {
        val campaign = MacrameCampaign(
            id = "camp_${System.currentTimeMillis()}",
            title = title,
            subtitle = subtitle,
            category = category,
            startDate = startDate,
            endDate = endDate,
            isActive = true,
            badgeText = badgeText
        )
        CampaignRepository.addCampaign(campaign)

        if (sendPush) {
            NotificationRepository.addNotification(
                title = "🎉 $title",
                message = subtitle,
                type = "Campaña",
                targetRoute = "catalog"
            )
        }
    }

    fun recordQrScan(qrCode: String): QrScanHistoryItem {
        val product = CatalogRepository.getProductByQrCode(qrCode)
        return QrRepository.recordScan(qrCode, product)
    }
}
