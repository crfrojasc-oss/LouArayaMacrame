package com.example.louarayamacrame.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.louarayamacrame.data.CatalogRepository
import com.example.louarayamacrame.data.EventRepository
import com.example.louarayamacrame.data.UserRepository
import com.example.louarayamacrame.model.EventShowroom
import com.example.louarayamacrame.model.MacrameCollection
import com.example.louarayamacrame.model.MacrameProduct
import com.example.louarayamacrame.model.UserProfile
import com.example.louarayamacrame.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
    val products: List<MacrameProduct> = CatalogRepository.sampleProducts

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
}
