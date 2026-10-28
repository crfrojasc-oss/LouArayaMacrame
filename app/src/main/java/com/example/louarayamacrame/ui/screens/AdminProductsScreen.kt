package com.example.louarayamacrame.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import com.example.louarayamacrame.ui.theme.AnimationSpecs
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
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.louarayamacrame.model.MacrameProduct
import com.example.louarayamacrame.ui.theme.PurpleDark
import com.example.louarayamacrame.ui.theme.PurplePrimary
import com.example.louarayamacrame.ui.theme.PurpleSurface
import com.example.louarayamacrame.ui.theme.SageGreen
import com.example.louarayamacrame.ui.viewmodel.AdminViewModel
import com.example.louarayamacrame.ui.viewmodel.CatalogViewModel
import java.util.Locale

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

    var isFormMode by remember { mutableStateOf(initialProductId != null) }
    var editingProductId by remember { mutableStateOf<String?>(initialProductId) }
    var productToDelete by remember { mutableStateOf<MacrameProduct?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Confirmation Dialog for Deleting Product
    productToDelete?.let { prod ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Eliminar Producto",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "¿Seguro que quieres eliminar este producto?",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        adminViewModel.deleteProduct(prod.id)
                        onNotice("🗑️ Producto '${prod.name}' eliminado con éxito.")
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { productToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isFormMode) {
                            if (editingProductId == null) "Nuevo Producto" else "Editar Producto"
                        } else {
                            "Gestión de Productos (Admin)"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isFormMode) {
                                isFormMode = false
                                editingProductId = null
                            } else {
                                onNavigateBack()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    if (!isFormMode) {
                        IconButton(onClick = {
                            editingProductId = null
                            isFormMode = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Nuevo Producto",
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
            AnimatedContent(
                targetState = isFormMode,
                transitionSpec = {
                    fadeIn(tween(AnimationSpecs.DURATION_MEDIUM, easing = AnimationSpecs.StandardEasing)) togetherWith
                    fadeOut(tween(AnimationSpecs.DURATION_MEDIUM, easing = AnimationSpecs.StandardEasing))
                },
                label = "AdminModeTransition"
            ) { targetIsForm ->
                if (targetIsForm) {
                    // Form View Mode (Create / Update)
                    AdminProductFormView(
                        editingProductId = editingProductId,
                        products = products,
                        adminViewModel = adminViewModel,
                        onSaveSuccess = { msg ->
                            onNotice(msg)
                            isFormMode = false
                            editingProductId = null
                        }
                    )
                } else {
                    // List View Mode (Read)
                    AdminProductListView(
                        products = products,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        onEditProduct = { prod ->
                            editingProductId = prod.id
                            isFormMode = true
                        },
                        onDeleteProduct = { prod ->
                            productToDelete = prod
                        },
                        onCreateProductClick = {
                            editingProductId = null
                            isFormMode = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminProductListView(
    products: List<MacrameProduct>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onEditProduct: (MacrameProduct) -> Unit,
    onDeleteProduct: (MacrameProduct) -> Unit,
    onCreateProductClick: () -> Unit
) {
    val filteredProducts = remember(products, searchQuery) {
        if (searchQuery.isBlank()) {
            products
        } else {
            products.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true) ||
                it.qrCodeId.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // Search & Filter Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Buscar por nombre, categoría o QR...", fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PurplePrimary)
        )

        // Summary Header & Create Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Catálogo Total (${products.size})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Button(
                onClick = onCreateProductClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nuevo Producto", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Empty State Handler
        if (products.isEmpty() || filteredProducts.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PurpleSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(PurplePrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (products.isEmpty()) "No hay productos registrados" else "No se encontraron coincidencias",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (products.isEmpty()) "Agrega el primer producto al catálogo presencial y digital de macramé." else "Intenta buscar con otro nombre o término.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    if (products.isEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onCreateProductClick,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                        ) {
                            Text("Crear Primer Producto", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Product List Items
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredProducts.forEach { product ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditProduct(product) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Product Icon Tile / Image Avatar
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PurpleSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Inventory,
                                    contentDescription = null,
                                    tint = PurplePrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Details
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "$ ${String.format(Locale.US, "%.0f", product.price)} CLP",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PurplePrimary
                                    )
                                    Text(
                                        text = " • ${product.category}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (product.isAvailable) SageGreen.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.3f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (product.isAvailable) "Entrega Inmediata" else "A Pedido",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (product.isAvailable) SageGreen else Color.DarkGray
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Text(
                                        text = "QR: ${product.qrCodeId}",
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Actions: Edit and Delete
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { onEditProduct(product) }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = PurplePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(onClick = { onDeleteProduct(product) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminProductFormView(
    editingProductId: String?,
    products: List<MacrameProduct>,
    adminViewModel: AdminViewModel,
    onSaveSuccess: (String) -> Unit
) {
    val existingProduct = products.find { it.id == editingProductId }

    var name by remember(existingProduct) { mutableStateOf(existingProduct?.name ?: "") }
    var category by remember(existingProduct) { mutableStateOf(existingProduct?.category ?: "Matrimonios") }
    var priceStr by remember(existingProduct) { mutableStateOf(existingProduct?.price?.let { String.format(Locale.US, "%.0f", it) } ?: "") }
    var description by remember(existingProduct) { mutableStateOf(existingProduct?.description ?: "") }
    var dimensions by remember(existingProduct) { mutableStateOf(existingProduct?.dimensions ?: "") }
    var materials by remember(existingProduct) { mutableStateOf(existingProduct?.materials ?: "") }
    var isAvailable by remember(existingProduct) { mutableStateOf(existingProduct?.isAvailable ?: true) }
    var isCustomOrder by remember(existingProduct) { mutableStateOf(existingProduct?.isCustomOrder ?: false) }
    var qrCodeId by remember(existingProduct) { mutableStateOf(existingProduct?.qrCodeId ?: "") }

    var photoCaptured by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Matrimonios", "Festivales", "Interiores")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // Camera Photo Capture Tile
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clickable {
                    photoCaptured = !photoCaptured
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
                    errorMessage = "Por favor completa el nombre, un precio válido, las dimensiones y materiales."
                } else {
                    errorMessage = null
                    adminViewModel.saveProduct(
                        id = editingProductId,
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
                    onSaveSuccess("✅ Producto '$name' guardado correctamente en el catálogo.")
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
