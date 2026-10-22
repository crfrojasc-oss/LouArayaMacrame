package com.example.louarayamacrame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.louarayamacrame.model.MacrameProduct
import com.example.louarayamacrame.ui.theme.PurpleDark
import com.example.louarayamacrame.ui.theme.PurplePrimary
import com.example.louarayamacrame.ui.theme.PurpleSurface
import com.example.louarayamacrame.ui.theme.SageGreen
import com.example.louarayamacrame.ui.viewmodel.AdminViewModel
import com.example.louarayamacrame.ui.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(
    initialProductId: String? = null,
    catalogViewModel: CatalogViewModel,
    adminViewModel: AdminViewModel,
    onNavigateBack: () -> Unit,
    onNotice: (String) -> Unit
) {
    val products by catalogViewModel.products.collectAsState()
    val editingProduct = products.find { it.id == initialProductId }

    var productId by remember { mutableStateOf(editingProduct?.id) }
    var name by remember { mutableStateOf(editingProduct?.name ?: "") }
    var category by remember { mutableStateOf(editingProduct?.category ?: "Matrimonios") }
    var priceStr by remember { mutableStateOf(editingProduct?.price?.let { String.format("%.0f", it) } ?: "") }
    var description by remember { mutableStateOf(editingProduct?.description ?: "") }
    var dimensions by remember { mutableStateOf(editingProduct?.dimensions ?: "") }
    var materials by remember { mutableStateOf(editingProduct?.materials ?: "") }
    var isAvailable by remember { mutableStateOf(editingProduct?.isAvailable ?: true) }
    var isCustomOrder by remember { mutableStateOf(editingProduct?.isCustomOrder ?: false) }
    var qrCodeId by remember { mutableStateOf(editingProduct?.qrCodeId ?: "") }

    var photoCaptured by remember { mutableStateOf(false) }
    var showSavedDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Matrimonios", "Festivales", "Interiores")

    if (showSavedDialog) {
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
                Text("¡Producto Guardado!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Text("El producto '$name' fue actualizado correctamente en el catálogo y caché local.", fontSize = 13.sp)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSavedDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Text("Volver al Panel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (productId == null) "Crear Producto" else "Editar Producto",
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
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                // Camera Photo Capture Simulation Tile
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable {
                            photoCaptured = !photoCaptured
                            onNotice("Simulación Cámara: Fotografía de pieza de macramé capturada.")
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PurpleSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(PurplePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Capturar Foto",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (photoCaptured) "📸 Fotografía Capturada con Éxito" else "Tomar Foto del Producto con Cámara",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PurpleDark
                            )
                            Text(
                                text = "Pulsa aquí para capturar o reemplazar foto de la pieza.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                errorMessage?.let { err ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(err, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 12.sp, modifier = Modifier.padding(10.dp))
                    }
                }

                // Form Fields
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del Producto *", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PurplePrimary)
                )

                Text("Categoría del Diseño *", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PurplePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Precio Referencial (CLP) *", fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PurplePrimary)
                )

                OutlinedTextField(
                    value = dimensions,
                    onValueChange = { dimensions = it },
                    label = { Text("Dimensiones / Medidas *", fontSize = 12.sp) },
                    placeholder = { Text("Ej. 220 cm x 180 cm", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PurplePrimary)
                )

                OutlinedTextField(
                    value = materials,
                    onValueChange = { materials = it },
                    label = { Text("Materiales Usados *", fontSize = 12.sp) },
                    placeholder = { Text("Ej. Algodón 100% natural, Argollas roble", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PurplePrimary)
                )

                OutlinedTextField(
                    value = qrCodeId,
                    onValueChange = { qrCodeId = it },
                    label = { Text("Código de Etiqueta QR", fontSize = 12.sp) },
                    placeholder = { Text("Ej. QR-MAC-06", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null, tint = PurplePrimary) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PurplePrimary)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción Ornamental", fontSize = 12.sp) },
                    minLines = 2,
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PurplePrimary)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                // Switches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Disponible Entrega Inmediata", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Switch(
                        checked = isAvailable,
                        onCheckedChange = { isAvailable = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PurplePrimary, checkedTrackColor = PurpleSurface)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Solo Trabajo Confeccionado a Pedido", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Switch(
                        checked = isCustomOrder,
                        onCheckedChange = { isCustomOrder = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PurplePrimary, checkedTrackColor = PurpleSurface)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val parsedPrice = priceStr.toDoubleOrNull()
                        if (name.isBlank() || parsedPrice == null || dimensions.isBlank() || materials.isBlank()) {
                            errorMessage = "Por favor completa nombre, precio válido, dimensiones y materiales."
                        } else {
                            errorMessage = null
                            adminViewModel.saveProduct(
                                id = productId,
                                name = name,
                                category = category,
                                price = parsedPrice,
                                description = description.ifBlank { "Pieza artesanal de macramé elaborada a mano." },
                                dimensions = dimensions,
                                materials = materials,
                                isAvailable = isAvailable,
                                isCustomOrder = isCustomOrder,
                                qrCodeId = qrCodeId
                            )
                            showSavedDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Text("Guardar Producto en Catálogo", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
