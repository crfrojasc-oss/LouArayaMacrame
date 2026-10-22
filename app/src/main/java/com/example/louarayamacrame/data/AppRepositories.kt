package com.example.louarayamacrame.data

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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserRepository {
    val adminAccount = UserProfile(
        id = "admin_01",
        name = "Lou Araya (Administración)",
        alias = "lou_admin",
        email = "admin@louaraya.cl",
        passwordHash = "admin123",
        phone = "+56 9 8765 4321",
        role = UserRole.ADMIN,
        avatarId = "avatar_admin",
        avatarColorHex = 0xFF5A189A
    )

    private val initialUsers = listOf(
        adminAccount,
        UserProfile(
            id = "user_01",
            name = "Camila Silva",
            alias = "cami_silva",
            email = "camila@mail.com",
            passwordHash = "123456",
            phone = "+56 9 5555 1234",
            role = UserRole.CLIENTE,
            avatarId = "avatar_1",
            avatarColorHex = 0xFF7B2CBF
        ),
        UserProfile(
            id = "user_02",
            name = "Matías Morales",
            alias = "mati_boho",
            email = "matias@mail.com",
            passwordHash = "123456",
            phone = "+56 9 4444 8888",
            role = UserRole.CLIENTE,
            avatarId = "avatar_2",
            avatarColorHex = 0xFF6B8A7A
        )
    )

    private val _users = MutableStateFlow(initialUsers)
    val users: StateFlow<List<UserProfile>> = _users.asStateFlow()

    private val _activeUser = MutableStateFlow<UserProfile?>(null)
    val activeUser: StateFlow<UserProfile?> = _activeUser.asStateFlow()

    fun registerUser(
        name: String,
        alias: String,
        email: String,
        password: String,
        phone: String,
        avatarId: String
    ): Result<UserProfile> {
        val currentList = _users.value
        if (currentList.any { it.email.equals(email, ignoreCase = true) }) {
            return Result.failure(Exception("El correo ya se encuentra registrado."))
        }
        if (currentList.any { it.alias.equals(alias, ignoreCase = true) }) {
            return Result.failure(Exception("El alias ya está en uso por otro usuario."))
        }

        val newUser = UserProfile(
            id = "user_${System.currentTimeMillis()}",
            name = name.trim(),
            alias = alias.trim(),
            email = email.trim(),
            passwordHash = password,
            phone = phone.ifBlank { "+56 9 1234 5678" },
            role = UserRole.CLIENTE,
            avatarId = avatarId,
            avatarColorHex = 0xFF7B2CBF
        )

        _users.value = _users.value + newUser
        _activeUser.value = newUser
        return Result.success(newUser)
    }

    fun login(emailOrAlias: String, password: String): Result<UserProfile> {
        val user = _users.value.find {
            (it.email.equals(emailOrAlias, ignoreCase = true) || it.alias.equals(emailOrAlias, ignoreCase = true)) &&
                    it.passwordHash == password
        }

        return if (user != null) {
            _activeUser.value = user
            Result.success(user)
        } else {
            Result.failure(Exception("Credenciales incorrectas. Revisa correo/alias y contraseña."))
        }
    }

    fun switchActiveUser(user: UserProfile) {
        _activeUser.value = user
    }

    fun updateProfile(name: String, alias: String, phone: String, avatarId: String) {
        val current = _activeUser.value ?: return
        val updated = current.copy(
            name = name.ifBlank { current.name },
            alias = alias.ifBlank { current.alias },
            phone = phone.ifBlank { current.phone },
            avatarId = avatarId
        )
        _users.value = _users.value.map { if (it.id == current.id) updated else it }
        _activeUser.value = updated
    }

    fun logout() {
        _activeUser.value = null
    }
}

object EventRepository {
    private val sampleEvents = listOf(
        EventShowroom(
            id = "evt_01",
            title = "Feria Artesanal Parque Araucano",
            locationName = "Parque Araucano",
            address = "Av. Presidente Riesco 5330, Las Condes",
            city = "Santiago",
            latitude = -33.4025,
            longitude = -70.5750,
            startDate = "15 Nov",
            endDate = "18 Nov",
            schedule = "11:00 - 20:30 hrs",
            standNumber = "Stand #24 (Zona Textil & Deco)",
            description = "Exhibición presencial con catálogo completo, muestras táctiles de algodón 100% natural y demostraciones de tejido de macramé en vivo.",
            status = "En curso",
            featuredCollection = "Colección Decoración Boho & Hogar"
        ),
        EventShowroom(
            id = "evt_02",
            title = "Showroom Nupcial Casona Las Condes",
            locationName = "Casona Las Condes",
            address = "Camino El Alba 12500, Las Condes",
            city = "Santiago",
            latitude = -33.3850,
            longitude = -70.5120,
            startDate = "28 Nov",
            endDate = "30 Nov",
            schedule = "10:00 - 19:00 hrs",
            standNumber = "Módulo A-08 (Alta Costura & Alta Decoración)",
            description = "Espacio dedicado exclusivamente a arcos matrimoniales de macramé, caminos de mesa bordados a mano y recuerdos personalizados para eventos de bodas.",
            status = "Próximo",
            featuredCollection = "Colección Nupcial & Bodas Premium"
        ),
        EventShowroom(
            id = "evt_03",
            title = "Expo Bazar Diseño Valparaíso",
            locationName = "Muelle Barón / Paseo 21 de Mayo",
            address = "Paseo 21 de Mayo, Cerros de Valparaíso",
            city = "Valparaíso",
            latitude = -33.0350,
            longitude = -71.6280,
            startDate = "05 Dic",
            endDate = "08 Dic",
            schedule = "12:00 - 21:00 hrs",
            standNumber = "Stand #12 (Muelle Costero)",
            description = "Encuentro de artesanía y diseño sustentable de la Quinta Región. Venta directa y toma de pedidos para la temporada estival.",
            status = "Próximo",
            featuredCollection = "Colección Festivales & Accesorios"
        ),
        EventShowroom(
            id = "evt_04",
            title = "Feria de Artesanías de Viña del Mar",
            locationName = "Plaza Vergara",
            address = "Plaza Vergara S/N, Centro Viña",
            city = "Viña del Mar",
            latitude = -33.0245,
            longitude = -71.5518,
            startDate = "12 Dic",
            endDate = "15 Dic",
            schedule = "11:00 - 20:00 hrs",
            standNumber = "Stand #45",
            description = "Edición especial navideña con promociones en lámparas colgantes, telares de pared y tapices artesanales.",
            status = "Próximo",
            featuredCollection = "Colección Lámparas & Tapices"
        )
    )

    private val _events = MutableStateFlow(sampleEvents)
    val events: StateFlow<List<EventShowroom>> = _events.asStateFlow()

    fun addEvent(event: EventShowroom) {
        _events.value = listOf(event) + _events.value
    }
}

object CatalogRepository {
    val sampleCollections = listOf(
        MacrameCollection(
            id = "col_01",
            title = "Matrimonios & Bodas",
            subtitle = "Arcos ceremoniales, al altar y recuerdos tejidos",
            category = "Matrimonios",
            productCount = 12,
            badgeText = "Nupcial Premium"
        ),
        MacrameCollection(
            id = "col_02",
            title = "Festivales & Eventos",
            subtitle = "Backdrops gigantes, iluminación y pérgolas",
            category = "Festivales",
            productCount = 18,
            badgeText = "Alta Escena"
        ),
        MacrameCollection(
            id = "col_03",
            title = "Decoración de Interiores",
            subtitle = "Tapices de pared, lámparas y porta maceteros",
            category = "Interiores",
            productCount = 24,
            badgeText = "Hogar & Decoración"
        )
    )

    private val initialProducts = listOf(
        MacrameProduct(
            id = "prod_01",
            name = "Arco Nupcial 'Brisa de Cobre'",
            category = "Matrimonios",
            price = 180000.0,
            description = "Estructura monumental tejida a mano en hilo de algodón de 5mm con detalles en madera nativa chilena y flecos largos.",
            dimensions = "220 cm alto x 180 cm ancho",
            materials = "Algodón 100% natural recuperado, argollas de madera de roble",
            isAvailable = true,
            isFavorite = true,
            isCustomOrder = false,
            qrCodeId = "QR-MAC-01"
        ),
        MacrameProduct(
            id = "prod_02",
            name = "Tapiz de Pared 'Bosque Nativo'",
            category = "Interiores",
            price = 65000.0,
            description = "Tapiz artesanal con diseño geométrico y tonos tierra, suspendido sobre una rama natural seleccionada de la cordillera.",
            dimensions = "110 cm alto x 80 cm ancho",
            materials = "Cordoncillo de algodón 4mm, varilla de avellano",
            isAvailable = true,
            isFavorite = false,
            isCustomOrder = false,
            qrCodeId = "QR-MAC-02"
        ),
        MacrameProduct(
            id = "prod_03",
            name = "Lámpara Colgante 'Sol del Sur'",
            category = "Festivales",
            price = 48000.0,
            description = "Lámpara de macramé con ondas envolventes que filtran la luz cálida proyectando patrones geométricos en las paredes.",
            dimensions = "70 cm largo x 40 cm diámetro",
            materials = "Estructura metálica inoxidable, cordel de algodón ecocertificado",
            isAvailable = true,
            isFavorite = true,
            isCustomOrder = false,
            qrCodeId = "QR-MAC-03"
        ),
        MacrameProduct(
            id = "prod_04",
            name = "Camino de Mesa 'Fiesta Silvestre'",
            category = "Matrimonios",
            price = 35000.0,
            description = "Camino de mesa calado ideal para banquetes y cenas rústicas chic, lavable y de alta firmeza.",
            dimensions = "200 cm largo x 35 cm ancho",
            materials = "Hilo de lino y algodón beige",
            isAvailable = true,
            isFavorite = false,
            isCustomOrder = false,
            qrCodeId = "QR-MAC-04"
        ),
        MacrameProduct(
            id = "prod_05",
            name = "Porta Macetero 'Triada Andina'",
            category = "Interiores",
            price = 22000.0,
            description = "Set triple de colgadores para plantas con cuentas de madera teñidas en tonos morados y terracota.",
            dimensions = "120 cm largo",
            materials = "Hilo de algodón 3mm, argolla de madera",
            isAvailable = true,
            isFavorite = true,
            isCustomOrder = false,
            qrCodeId = "QR-MAC-05"
        )
    )

    private val _products = MutableStateFlow(initialProducts)
    val products: StateFlow<List<MacrameProduct>> = _products.asStateFlow()

    fun getProductById(id: String): MacrameProduct? {
        return _products.value.find { it.id == id }
    }

    fun getProductByQrCode(qrCode: String): MacrameProduct? {
        return _products.value.find { it.qrCodeId.equals(qrCode, ignoreCase = true) || it.id.equals(qrCode, ignoreCase = true) }
    }

    fun toggleFavorite(productId: String) {
        _products.value = _products.value.map {
            if (it.id == productId) it.copy(isFavorite = !it.isFavorite) else it
        }
    }

    fun saveProduct(product: MacrameProduct) {
        val existingIndex = _products.value.indexOfFirst { it.id == product.id }
        if (existingIndex >= 0) {
            _products.value = _products.value.toMutableList().apply { set(existingIndex, product) }
        } else {
            _products.value = listOf(product) + _products.value
        }
    }
}

object RequestRepository {
    private val initialRequests = listOf(
        CustomRequest(
            id = "req_101",
            folio = "COT-2024-001",
            userId = "user_01",
            userAlias = "cami_silva",
            eventType = "Matrimonio",
            dimensions = "240 cm alto x 200 cm ancho",
            materialPreference = "Algodón 100% natural 5mm e hilo dorado",
            colorPalette = "Crema, Terracota y Morado Suave",
            requiredDate = "15 Diciembre 2024",
            comments = "Para altar principal al aire libre en casona de eventos.",
            status = "En Revisión",
            createdAt = "10 Nov 2024",
            estimatedPrice = 195000.0
        ),
        CustomRequest(
            id = "req_102",
            folio = "COT-2024-002",
            userId = "user_02",
            userAlias = "mati_boho",
            eventType = "Festival",
            dimensions = "300 cm x 150 cm",
            materialPreference = "Cordoncillo grueso de algodón ecológico",
            colorPalette = "Natural y detalles violeta",
            requiredDate = "20 Enero 2025",
            comments = "Backdrop para escenario secundario de música.",
            status = "Pendiente",
            createdAt = "12 Nov 2024",
            estimatedPrice = 250000.0
        )
    )

    private val _requests = MutableStateFlow(initialRequests)
    val requests: StateFlow<List<CustomRequest>> = _requests.asStateFlow()

    fun createRequest(
        user: UserProfile,
        eventType: String,
        dimensions: String,
        materialPreference: String,
        colorPalette: String,
        requiredDate: String,
        comments: String
    ): CustomRequest {
        val folio = "COT-2024-${String.format("%03d", _requests.value.size + 1)}"
        val newReq = CustomRequest(
            id = "req_${System.currentTimeMillis()}",
            folio = folio,
            userId = user.id,
            userAlias = user.alias,
            eventType = eventType,
            dimensions = dimensions,
            materialPreference = materialPreference,
            colorPalette = colorPalette,
            requiredDate = requiredDate,
            comments = comments,
            status = "Pendiente",
            createdAt = "Hoy",
            estimatedPrice = 0.0
        )
        _requests.value = listOf(newReq) + _requests.value
        NotificationRepository.addNotification(
            title = "Nueva Cotización Recibida",
            message = "Solicitud ${newReq.folio} ingresada por @${user.alias} para ${newReq.eventType}.",
            type = "Solicitud",
            targetRoute = "request_history"
        )
        return newReq
    }

    fun updateStatus(requestId: String, newStatus: String) {
        _requests.value = _requests.value.map {
            if (it.id == requestId) it.copy(status = newStatus) else it
        }
    }
}

object NotificationRepository {
    private val initialNotifications = listOf(
        AppNotification(
            id = "notif_01",
            title = "¡Nueva Colección Nupcial Morado & Cobre!",
            message = "Descubre los nuevos arcos y caminos de mesa para bodas con detalles morados artesanales.",
            date = "Hace 2 horas",
            type = "Campaña",
            isRead = false,
            targetRoute = "catalog"
        ),
        AppNotification(
            id = "notif_02",
            title = "Feria Artesanal Parque Araucano",
            message = "Visítanos en el Stand #24 este fin de semana en Santiago.",
            date = "Ayer",
            type = "Feria",
            isRead = true,
            targetRoute = "map"
        )
    )

    private val _notifications = MutableStateFlow(initialNotifications)
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    fun addNotification(title: String, message: String, type: String, targetRoute: String) {
        val newNotif = AppNotification(
            id = "notif_${System.currentTimeMillis()}",
            title = title,
            message = message,
            date = "Ahora",
            type = type,
            isRead = false,
            targetRoute = targetRoute
        )
        _notifications.value = listOf(newNotif) + _notifications.value
    }

    fun markAllAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun markAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }
}

object CampaignRepository {
    private val initialCampaigns = listOf(
        MacrameCampaign(
            id = "camp_01",
            title = "Especial Bodas de Verano 2025",
            subtitle = "15% de descuento en arcos matrimoniales a medida con paletas moradas y terracota.",
            category = "Matrimonios",
            startDate = "01 Nov",
            endDate = "15 Dic",
            isActive = true,
            badgeText = "Promoción Nupcial"
        ),
        MacrameCampaign(
            id = "camp_02",
            title = "Temporada Festivales & Escenarios",
            subtitle = "Arriendo y confección de backdrops gigantes para eventos y conciertos.",
            category = "Festivales",
            startDate = "10 Nov",
            endDate = "30 Enero",
            isActive = true,
            badgeText = "Alta Escena"
        )
    )

    private val _campaigns = MutableStateFlow(initialCampaigns)
    val campaigns: StateFlow<List<MacrameCampaign>> = _campaigns.asStateFlow()

    fun addCampaign(campaign: MacrameCampaign) {
        _campaigns.value = listOf(campaign) + _campaigns.value
    }
}

object QrRepository {
    private val _scanHistory = MutableStateFlow<List<QrScanHistoryItem>>(emptyList())
    val scanHistory: StateFlow<List<QrScanHistoryItem>> = _scanHistory.asStateFlow()

    fun recordScan(qrCode: String, product: MacrameProduct?): QrScanHistoryItem {
        val item = QrScanHistoryItem(
            id = "scan_${System.currentTimeMillis()}",
            qrCode = qrCode,
            productId = product?.id,
            productName = product?.name ?: "Producto no catalogado ($qrCode)",
            scannedAt = "Hoy"
        )
        _scanHistory.value = listOf(item) + _scanHistory.value
        return item
    }
}
