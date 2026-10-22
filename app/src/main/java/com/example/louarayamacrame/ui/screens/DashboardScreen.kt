package com.example.louarayamacrame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.louarayamacrame.model.MacrameCollection
import com.example.louarayamacrame.model.MacrameProduct
import com.example.louarayamacrame.model.UserProfile
import com.example.louarayamacrame.model.UserRole
import com.example.louarayamacrame.ui.theme.EarthenBrown
import com.example.louarayamacrame.ui.theme.GoldAccent
import com.example.louarayamacrame.ui.theme.SageGreen
import com.example.louarayamacrame.ui.theme.TerracottaDark
import com.example.louarayamacrame.ui.theme.TerracottaPrimary
import com.example.louarayamacrame.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel,
    onNavigateToMap: () -> Unit,
    onLogout: () -> Unit,
    onQuickActionNotice: (String) -> Unit
) {
    val activeUser by dashboardViewModel.activeUser.collectAsState()
    val collections = dashboardViewModel.collections
    val products = dashboardViewModel.products

    val user = activeUser ?: UserProfile(
        id = "guest",
        name = "Cliente Invitado",
        alias = "invitado",
        email = "invitado@louaraya.cl",
        passwordHash = "",
        role = UserRole.CLIENTE
    )

    val isAdmin = user.role == UserRole.ADMIN
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(user.avatarColorHex).copy(alpha = 0.2f))
                                .border(1.5.dp, Color(user.avatarColorHex), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.alias.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = Color(user.avatarColorHex),
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "¡Hola, ${user.name.split(" ").first()}!",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isAdmin) GoldAccent else SageGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isAdmin) "Administrador" else "Cliente registrado",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = TerracottaDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                // Role Demo Switcher Toolbar Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isAdmin) TerracottaDark.copy(alpha = 0.12f) else SageGreen.copy(alpha = 0.12f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isAdmin) TerracottaDark else SageGreen
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isAdmin) "Modo Administrador Activo" else "Modo Cliente Activo",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isAdmin) "Gestión comercial, productos y eventos" else "Catálogo, favoritos, QR y mapa",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = { dashboardViewModel.toggleRoleDemo() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAdmin) TerracottaPrimary else EarthenBrown
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Cambiar Rol",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar arcos, tapices, lámparas en macramé...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TerracottaPrimary,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                if (!isAdmin) {
                    // ================= CLIENT DASHBOARD VIEW =================
                    Text(
                        text = "Accesos Rápidos del Cliente",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Grid of 6 Client Quick Action Cards
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            QuickActionCard(
                                title = "Catálogo",
                                subtitle = "Piezas & Precios",
                                icon = Icons.Default.ShoppingBag,
                                color = TerracottaPrimary,
                                modifier = Modifier.weight(1f),
                                onClick = { onQuickActionNotice("Abriendo Catálogo de Diseños Artesanales (Pantalla 6)...") }
                            )
                            QuickActionCard(
                                title = "Mapa Ferias",
                                subtitle = "Ver Showrooms",
                                icon = Icons.Default.LocationOn,
                                color = SageGreen,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToMap
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            QuickActionCard(
                                title = "Escáner QR",
                                subtitle = "Leer Etiquetas",
                                icon = Icons.Default.QrCodeScanner,
                                color = EarthenBrown,
                                modifier = Modifier.weight(1f),
                                onClick = { onQuickActionNotice("Activando Cámara Lector QR de Producto (Pantalla 8)...") }
                            )
                            QuickActionCard(
                                title = "Solicitudes",
                                subtitle = "Cotizaciones",
                                icon = Icons.Default.ListAlt,
                                color = TerracottaDark,
                                modifier = Modifier.weight(1f),
                                onClick = { onQuickActionNotice("Abriendo Solicitudes & Personalización a Medida (Pantallas 10 y 13)...") }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            QuickActionCard(
                                title = "Mis Favoritos",
                                subtitle = "Colección Guardada",
                                icon = Icons.Default.Favorite,
                                color = TerracottaPrimary,
                                modifier = Modifier.weight(1f),
                                onClick = { onQuickActionNotice("Abriendo Colección de Favoritos del Cliente (Pantalla 9)...") }
                            )
                            QuickActionCard(
                                title = "Mi Perfil",
                                subtitle = "Cuenta & Ajustes",
                                icon = Icons.Default.Person,
                                color = SageGreen,
                                modifier = Modifier.weight(1f),
                                onClick = { onQuickActionNotice("Abriendo Perfil de Cliente y Configuración (Pantalla 15)...") }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Featured Collections Carousel
                    Text(
                        text = "Colecciones Destacadas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(collections) { col ->
                            CollectionCardItem(collection = col, onClick = {
                                onQuickActionNotice("Explorando Colección '${col.title}'...")
                            })
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Featured Products Showcase
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nuevos Diseños en Macramé",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        AssistChip(
                            onClick = { onQuickActionNotice("Ver todo el catálogo...") },
                            label = { Text("Ver Todo", fontSize = 11.sp) },
                            colors = AssistChipDefaults.assistChipColors(labelColor = TerracottaPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        products.forEach { product ->
                            ProductShowcaseCard(product = product, onClick = {
                                onQuickActionNotice("Detalle de Producto: ${product.name} (Pantalla 7)")
                            })
                        }
                    }

                } else {
                    // ================= ADMINISTRATOR DASHBOARD VIEW =================
                    Text(
                        text = "Resumen de Gestión Comercial",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Metrics Cards Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricSummaryCard(
                            title = "Cotizaciones",
                            count = "3 Nuevas",
                            icon = Icons.Default.ListAlt,
                            color = TerracottaPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        MetricSummaryCard(
                            title = "Productos",
                            count = "24 Activos",
                            icon = Icons.Default.Inventory,
                            color = SageGreen,
                            modifier = Modifier.weight(1f)
                        )
                        MetricSummaryCard(
                            title = "Eventos",
                            count = "2 Este mes",
                            icon = Icons.Default.LocationOn,
                            color = EarthenBrown,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Panel de Administración",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Admin Quick Actions Grid
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        AdminActionTile(
                            title = "Gestión de Productos",
                            description = "Crear, editar, subir fotos y ajustar precios o disponibilidad",
                            icon = Icons.Default.Add,
                            color = TerracottaPrimary,
                            onClick = { onQuickActionNotice("Abriendo Gestión de Productos (Pantalla 11)...") }
                        )

                        AdminActionTile(
                            title = "Gestión de Colecciones y Campañas",
                            description = "Publicar colecciones nupciales, eventos y notificaciones",
                            icon = Icons.Default.Campaign,
                            color = SageGreen,
                            onClick = { onQuickActionNotice("Abriendo Gestión de Colecciones y Campañas (Pantalla 12)...") }
                        )

                        AdminActionTile(
                            title = "Historial y Solicitudes de Clientes",
                            description = "Revisar solicitudes de cotización a medida y actualizar estados",
                            icon = Icons.Default.ListAlt,
                            color = EarthenBrown,
                            onClick = { onQuickActionNotice("Abriendo Historial de Solicitudes (Pantalla 13)...") }
                        )

                        AdminActionTile(
                            title = "Administrar Ferias y Showrooms",
                            description = "Ver y agregar puntos en el mapa interactivo de eventos",
                            icon = Icons.Default.LocationOn,
                            color = TerracottaDark,
                            onClick = onNavigateToMap
                        )

                        AdminActionTile(
                            title = "Lector & Verificador de Código QR",
                            description = "Escanear etiquetas presenciales de macramé en ferias",
                            icon = Icons.Default.QrCodeScanner,
                            color = SageGreen,
                            onClick = { onQuickActionNotice("Activando Lector QR de Administrador (Pantalla 8)...") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CollectionCardItem(
    collection: MacrameCollection,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(TerracottaPrimary, EarthenBrown)
                    )
                )
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoldAccent)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = collection.badgeText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = collection.title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Text(
                        text = collection.subtitle,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductShowcaseCard(
    product: MacrameProduct,
    onClick: () -> Unit
) {
    var isFav by remember { mutableStateOf(product.isFavorite) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TerracottaDark.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = TerracottaPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SageGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = product.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SageGreen
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${product.dimensions} • ${product.materials}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$${String.format("%,.0f", product.price)} CLP",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = TerracottaPrimary
                )
            }

            IconButton(onClick = { isFav = !isFav }) {
                Icon(
                    imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFav) TerracottaPrimary else Color.Gray
                )
            }
        }
    }
}

@Composable
private fun MetricSummaryCard(
    title: String,
    count: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = count,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AdminActionTile(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
