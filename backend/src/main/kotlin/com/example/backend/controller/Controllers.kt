package com.example.backend.controller

import com.example.backend.model.*
import com.example.backend.repository.*
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userRepository: UserRepository) {

    @GetMapping
    fun getAllUsers(): List<UserEntity> = userRepository.findAll()

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<UserEntity> {
        val user = userRepository.findByEmailOrAlias(request.emailOrAlias, request.emailOrAlias)
        if (user != null && user.passwordHash == request.password) {
            return ResponseEntity.ok(user)
        }
        return ResponseEntity.status(401).build()
    }

    @PostMapping("/register")
    fun register(@RequestBody user: UserEntity): ResponseEntity<Any> {
        if (userRepository.findByEmail(user.email) != null) {
            return ResponseEntity.badRequest().body(mapOf("error" to "El correo ya está registrado."))
        }
        val saved = userRepository.save(user)
        return ResponseEntity.ok(saved)
    }
}

data class LoginRequest(val emailOrAlias: String, val password: String)

@RestController
@RequestMapping("/api/products")
class ProductController(private val productRepository: ProductRepository) {

    @GetMapping
    fun getAllProducts(): List<ProductEntity> = productRepository.findAll()

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: String): ResponseEntity<ProductEntity> {
        val product = productRepository.findById(id)
        return if (product.isPresent) ResponseEntity.ok(product.get()) else ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createProduct(@RequestBody product: ProductEntity): ProductEntity = productRepository.save(product)

    @PutMapping("/{id}")
    fun updateProduct(@PathVariable id: String, @RequestBody product: ProductEntity): ResponseEntity<ProductEntity> {
        return if (productRepository.existsById(id)) {
            ResponseEntity.ok(productRepository.save(product.copy(id = id)))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: String): ResponseEntity<Void> {
        return if (productRepository.existsById(id)) {
            productRepository.deleteById(id)
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}

@RestController
@RequestMapping("/api/requests")
class RequestController(private val requestRepository: RequestRepository) {

    @GetMapping
    fun getAllRequests(): List<RequestEntity> = requestRepository.findAll()

    @GetMapping("/user/{userId}")
    fun getRequestsByUser(@PathVariable userId: String): List<RequestEntity> = requestRepository.findByUserId(userId)

    @PostMapping
    fun createRequest(@RequestBody req: RequestEntity): RequestEntity = requestRepository.save(req)

    @PutMapping("/{id}/status")
    fun updateStatus(@PathVariable id: String, @RequestParam status: String): ResponseEntity<RequestEntity> {
        val reqOpt = requestRepository.findById(id)
        return if (reqOpt.isPresent) {
            val updated = reqOpt.get().copy(status = status)
            ResponseEntity.ok(requestRepository.save(updated))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}

@RestController
@RequestMapping("/api/campaigns")
class CampaignController(private val campaignRepository: CampaignRepository) {
    @GetMapping
    fun getAllCampaigns(): List<CampaignEntity> = campaignRepository.findAll()
}

@RestController
@RequestMapping("/api/events")
class EventController(private val eventRepository: EventRepository) {
    @GetMapping
    fun getAllEvents(): List<EventEntity> = eventRepository.findAll()
}

@Configuration
class DataSeeder(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val campaignRepository: CampaignRepository,
    private val eventRepository: EventRepository
) {
    @Bean
    fun initDatabase() = CommandLineRunner {
        if (userRepository.count() == 0L) {
            userRepository.save(
                UserEntity(
                    id = "admin_01",
                    name = "Lou Araya (Administración)",
                    alias = "lou_admin",
                    email = "admin@louaraya.cl",
                    passwordHash = "admin123",
                    phone = "+56 9 8765 4321",
                    role = "ADMIN",
                    avatarId = "avatar_admin",
                    avatarColorHex = 0xFF5A189A
                )
            )
            userRepository.save(
                UserEntity(
                    id = "user_01",
                    name = "Camila Silva",
                    alias = "cami_silva",
                    email = "camila@mail.com",
                    passwordHash = "123456",
                    phone = "+56 9 5555 1234",
                    role = "CLIENTE",
                    avatarId = "avatar_1",
                    avatarColorHex = 0xFF7B2CBF
                )
            )
        }

        if (productRepository.count() == 0L) {
            productRepository.save(
                ProductEntity(
                    id = "prod_1",
                    name = "Arcoiris Boho Mural",
                    category = "Decoración",
                    price = 45990.0,
                    description = "Mural tejido a mano en tonos terracota y crudo, ideal para cabeceras de cama o salas de estar.",
                    dimensions = "80x70 cm",
                    materials = "Algodón 100% natural, madera nativa",
                    isAvailable = true,
                    qrCodeId = "QR-MAC-01"
                )
            )
            productRepository.save(
                ProductEntity(
                    id = "prod_2",
                    name = "Set de Posavasos Geométricos",
                    category = "Decoración",
                    price = 14990.0,
                    description = "Set de 4 posavasos de macramé tejido con diseño geométrico escandinavo.",
                    dimensions = "12x12 cm c/u",
                    materials = "Cordón de algodón peinado",
                    isAvailable = true,
                    qrCodeId = "QR-MAC-02"
                )
            )
            productRepository.save(
                ProductEntity(
                    id = "prod_3",
                    name = "Telar Ceremonial Boda",
                    category = "Matrimonios",
                    price = 120000.0,
                    description = "Estructura de telar monumental para ceremonias de matrimonio al aire libre.",
                    dimensions = "2.0x1.8 metros",
                    materials = "Yute, algodón virgen, tronco de eucalipto",
                    isAvailable = true,
                    qrCodeId = "QR-MAC-03"
                )
            )
        }

        if (campaignRepository.count() == 0L) {
            campaignRepository.save(
                CampaignEntity(
                    id = "camp_1",
                    title = "Especial Matrimonios Boho 2025",
                    subtitle = "15% de descuento en altares y decoración para bodas contratando este mes.",
                    category = "Matrimonios",
                    startDate = "01/03/2025",
                    endDate = "31/03/2025",
                    isActive = true,
                    badgeText = "Promoción Especial"
                )
            )
        }

        if (eventRepository.count() == 0L) {
            eventRepository.save(
                EventEntity(
                    id = "evt_1",
                    title = "Feria de Diseño de Autor Vívela",
                    locationName = "Parque Bicentenario Vitacura",
                    address = "Av. Bicentenario 3230",
                    city = "Santiago",
                    latitude = -33.4005,
                    longitude = -70.5985,
                    startDate = "15/04/2025",
                    endDate = "17/04/2025",
                    schedule = "11:00 - 20:00 hrs",
                    standNumber = "Stand 42",
                    description = "Exhibición presencial de nuestra colección de murales y talleres en vivo de nudos básicos de macramé.",
                    status = "Próximo",
                    featuredCollection = "Colección Decoración Hogar"
                )
            )
        }
    }
}
