package com.example.louarayamacrame.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Texture
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.louarayamacrame.model.CustomRequest
import com.example.louarayamacrame.model.UserProfile
import com.example.louarayamacrame.model.UserRole
import com.example.louarayamacrame.ui.theme.PurplePrimary
import com.example.louarayamacrame.ui.theme.PurpleSurface
import com.example.louarayamacrame.ui.viewmodel.AuthViewModel
import com.example.louarayamacrame.ui.viewmodel.RequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomRequestScreen(
    authViewModel: AuthViewModel,
    requestViewModel: RequestViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val activeUser by authViewModel.activeUser.collectAsState()
    val user = activeUser ?: UserProfile(
        id = "user_guest",
        name = "Cliente General",
        alias = "cliente",
        email = "cliente@mail.com",
        passwordHash = "",
        role = UserRole.CLIENTE
    )

    var eventType by remember { mutableStateOf("Matrimonio") }
    var dimensions by remember { mutableStateOf("") }
    var materialPreference by remember { mutableStateOf("") }
    var colorPalette by remember { mutableStateOf("") }
    var requiredDate by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var createdRequest by remember { mutableStateOf<CustomRequest?>(null) }

    if (createdRequest != null) {
        val req = createdRequest!!
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PurplePrimary,
                    modifier = Modifier.size(44.dp)
                )
            },
            title = {
                Text(
                    text = "¡Solicitud Registrada!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "Tu cotización fue asignada con el Folio ${req.folio}.\nRecibirás actualizaciones de estado y confirmación de presupuesto en la app.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        createdRequest = null
                        onNavigateToHistory()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Text("Ver Historial de Solicitudes")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Solicitud de Personalización",
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
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Info Banner
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
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Describe tu proyecto artesanal a medida (arcos, backdrops, tapices) y enviaremos una propuesta de presupuesto personalizada.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }

                errorMessage?.let { err ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // Form Fields
                OutlinedTextField(
                    value = eventType,
                    onValueChange = { eventType = it },
                    label = { Text("Tipo de Evento o Destino *", fontSize = 12.sp) },
                    placeholder = { Text("Ej. Matrimonio, Festival, Decoración Hogar", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Event, contentDescription = null, tint = PurplePrimary) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PurplePrimary)
                )

                OutlinedTextField(
                    value = dimensions,
                    onValueChange = { dimensions = it },
                    label = { Text("Medidas / Dimensiones Aproximadas *", fontSize = 12.sp) },
                    placeholder = { Text("Ej. 220cm alto x 180cm ancho", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Straighten, contentDescription = null, tint = PurplePrimary) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PurplePrimary)
                )

                OutlinedTextField(
                    value = materialPreference,
                    onValueChange = { materialPreference = it },
                    label = { Text("Preferencia de Material", fontSize = 12.sp) },
                    placeholder = { Text("Ej. Algodón 100% natural 5mm, Argollas de roble", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Texture, contentDescription = null, tint = PurplePrimary) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PurplePrimary)
                )

                OutlinedTextField(
                    value = colorPalette,
                    onValueChange = { colorPalette = it },
                    label = { Text("Paleta de Colores Preferida", fontSize = 12.sp) },
                    placeholder = { Text("Ej. Crema, Terracota y Detalles Morados", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.ColorLens, contentDescription = null, tint = PurplePrimary) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PurplePrimary)
                )

                OutlinedTextField(
                    value = requiredDate,
                    onValueChange = { requiredDate = it },
                    label = { Text("Fecha Requerida de Entrega *", fontSize = 12.sp) },
                    placeholder = { Text("Ej. 20 de Diciembre 2024", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PurplePrimary) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PurplePrimary)
                )

                OutlinedTextField(
                    value = comments,
                    onValueChange = { comments = it },
                    label = { Text("Observaciones / Detalles Adicionales", fontSize = 12.sp) },
                    placeholder = { Text("Ubicación del evento, condiciones de montaje, etc.", fontSize = 11.sp) },
                    minLines = 3,
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PurplePrimary)
                )

                Button(
                    onClick = {
                        if (eventType.isBlank() || dimensions.isBlank() || requiredDate.isBlank()) {
                            errorMessage = "Por favor completa el tipo de evento, dimensiones y fecha requerida."
                        } else {
                            errorMessage = null
                            val req = requestViewModel.createRequest(
                                user = user,
                                eventType = eventType,
                                dimensions = dimensions,
                                materialPreference = materialPreference.ifBlank { "Algodón ecocertificado 100% natural" },
                                colorPalette = colorPalette.ifBlank { "Tonos tierra y acentos morados" },
                                requiredDate = requiredDate,
                                comments = comments
                            )
                            createdRequest = req
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurplePrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Enviar Solicitud de Cotización", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
