package com.example.louarayamacrame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.louarayamacrame.model.CustomRequest
import com.example.louarayamacrame.model.UserProfile
import com.example.louarayamacrame.model.UserRole
import com.example.louarayamacrame.ui.theme.EarthenBrown
import com.example.louarayamacrame.ui.theme.GoldAccent
import com.example.louarayamacrame.ui.theme.PurpleDark
import com.example.louarayamacrame.ui.theme.PurplePrimary
import com.example.louarayamacrame.ui.theme.PurpleSurface
import com.example.louarayamacrame.ui.theme.SageGreen
import com.example.louarayamacrame.ui.viewmodel.AuthViewModel
import com.example.louarayamacrame.ui.viewmodel.RequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestHistoryScreen(
    authViewModel: AuthViewModel,
    requestViewModel: RequestViewModel,
    onNavigateBack: () -> Unit,
    onNewRequest: () -> Unit
) {
    val activeUser by authViewModel.activeUser.collectAsState()
    val allRequests by requestViewModel.allRequests.collectAsState()

    val user = activeUser ?: UserProfile(
        id = "user_guest",
        name = "Cliente General",
        alias = "cliente",
        email = "cliente@mail.com",
        passwordHash = "",
        role = UserRole.CLIENTE
    )

    val isAdmin = user.role == UserRole.ADMIN

    val requests = if (isAdmin) {
        allRequests
    } else {
        allRequests.filter { it.userId == user.id }
    }

    val statusOptions = listOf("Pendiente", "En Revisión", "Aprobada", "En Confección", "Finalizada")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isAdmin) "Gestión de Solicitudes (Admin)" else "Historial de Mis Cotizaciones",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    if (!isAdmin) {
                        IconButton(onClick = onNewRequest) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Nueva Cotización",
                                tint = PurplePrimary
                            )
                        }
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
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                // Header Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PurpleSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PurplePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isAdmin) "Panel Central de Solicitudes" else "Seguimiento de Cotizaciones",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PurpleDark
                            )
                            Text(
                                text = if (isAdmin) "Actualiza el estado de producción de los clientes." else "Revisa el estado de tus proyectos a medida.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (requests.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ListAlt,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isAdmin) "No hay solicitudes de clientes registradas." else "Aún no has enviado ninguna solicitud de cotización.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        requests.forEach { req ->
                            RequestTileCard(
                                request = req,
                                isAdmin = isAdmin,
                                statusOptions = statusOptions,
                                onUpdateStatus = { newStatus ->
                                    requestViewModel.updateStatus(req.id, newStatus)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestTileCard(
    request: CustomRequest,
    isAdmin: Boolean,
    statusOptions: List<String>,
    onUpdateStatus: (String) -> Unit
) {
    val statusColor = when (request.status) {
        "Pendiente" -> EarthenBrown
        "En Revisión" -> PurplePrimary
        "Aprobada" -> SageGreen
        "En Confección" -> PurpleDark
        "Finalizada" -> GoldAccent
        else -> PurplePrimary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.folio,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = PurplePrimary
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = request.status.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Evento: ${request.eventType} • @${request.userAlias}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Dimensiones: ${request.dimensions} | Fecha Requerida: ${request.requiredDate}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Materiales/Colores: ${request.materialPreference} (${request.colorPalette})",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (request.comments.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Notas: \"${request.comments}\"",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }

            if (isAdmin) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text("Actualizar Estado (Admin):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PurpleDark)

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    statusOptions.forEach { st ->
                        val selected = request.status == st
                        FilterChip(
                            selected = selected,
                            onClick = { onUpdateStatus(st) },
                            label = { Text(st, fontSize = 9.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PurplePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}
