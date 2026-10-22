package com.example.louarayamacrame.data

import com.example.louarayamacrame.model.EventShowroom
import com.example.louarayamacrame.model.MacrameCollection
import com.example.louarayamacrame.model.MacrameProduct
import com.example.louarayamacrame.model.UserProfile
import com.example.louarayamacrame.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserRepository {
    // Cuenta administrativa ficticia precargada para demostración académica
    val adminAccount = UserProfile(
        id = "admin_01",
        name = "Lou Araya (Administración)",
        alias = "lou_admin",
        email = "admin@louaraya.cl",
        passwordHash = "admin123",
        phone = "+56 9 8765 4321",
        role = UserRole.ADMIN,
        avatarId = "avatar_admin",
        avatarColorHex = 0xFF8B5A2B
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
            avatarColorHex = 0xFFC86D51
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
            avatarColorHex = 0xFF7A8B7B
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
            avatarColorHex = 0xFFC86D51
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
        ),
        EventShowroom(
            id = "evt_05",
            title = "Encuentro Cultural & Artesanal Pucón",
            locationName = "Paseo Quilmay",
            address = "Av. Bernardo O'Higgins 480",
            city = "Pucón",
            latitude = -39.2782,
            longitude = -71.9760,
            startDate = "20 Dic",
            endDate = "24 Dic",
            schedule = "11:30 - 21:30 hrs",
            standNumber = "Espacio Central #03",
            description = "Muestra estival en la Araucanía lacustre con la colección completa de caminos de mesa, colgadores de plantas y espejos de macramé.",
            status = "Próximo",
            featuredCollection = "Colección Naturaleza & Verano"
        )
    )

    private val _events = MutableStateFlow(sampleEvents)
    val events: StateFlow<List<EventShowroom>> = _events.asStateFlow()
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
            badgeText = "Hogar & Hogar"
        )
    )

    val sampleProducts = listOf(
        MacrameProduct(
            id = "prod_01",
            name = "Arco Nupcial 'Brisa de Cobre'",
            category = "Matrimonios",
            price = 180000.0,
            description = "Estructura monumental tejida a mano en hilo de algodón de 5mm con detalles en madera nativa chilena y flecos largos.",
            dimensions = "220 cm alto x 180 cm ancho",
            materials = "Algodón 100% natural recuperado, argollas de madera de roble",
            isAvailable = true,
            isFavorite = true
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
            isFavorite = false
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
            isFavorite = true
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
            isFavorite = false
        )
    )
}
