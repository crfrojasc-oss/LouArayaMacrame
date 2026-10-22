package com.example.louarayamacrame.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.louarayamacrame.model.EventShowroom
import com.example.louarayamacrame.ui.theme.EarthenBrown
import com.example.louarayamacrame.ui.theme.GoldAccent
import com.example.louarayamacrame.ui.theme.SageGreen
import com.example.louarayamacrame.ui.theme.TerracottaDark
import com.example.louarayamacrame.ui.theme.TerracottaPrimary
import com.example.louarayamacrame.ui.viewmodel.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    isAdmin: Boolean = false,
    onNavigateBack: () -> Unit,
    onNotice: (String) -> Unit
) {
    val events by mapViewModel.filteredEvents.collectAsState()
    val selectedCity by mapViewModel.selectedCity.collectAsState()
    val searchQuery by mapViewModel.searchQuery.collectAsState()
    val selectedEvent by mapViewModel.selectedEvent.collectAsState()

    val cities = listOf("Todas", "Santiago", "Valparaíso", "Viña del Mar", "Pucón")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mapa de Ferias y Showrooms",
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
                    if (isAdmin) {
                        IconButton(onClick = { onNotice("Abriendo formulario para agregar evento en mapa...") }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar Evento",
                                tint = TerracottaPrimary
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
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { mapViewModel.setSearchQuery(it) },
                    placeholder = { Text("Buscar feria, recinto o ciudad...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TerracottaPrimary
                    )
                )

                // City Filter Chips
                Text(
                    text = "Filtrar por Ciudad",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    items(cities) { city ->
                        FilterChip(
                            selected = selectedCity == city,
                            onClick = { mapViewModel.setCity(city) },
                            label = { Text(city, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TerracottaPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Interactive Visual Map Canvas Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .padding(vertical = 10.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFEFE8DE))
                    ) {
                        // Map grid background styling
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val gridSpacing = 40.dp.toPx()
                            val width = size.width
                            val height = size.height

                            for (x in 0..(width / gridSpacing).toInt()) {
                                drawLine(
                                    color = Color.Gray.copy(alpha = 0.15f),
                                    start = Offset(x * gridSpacing, 0f),
                                    end = Offset(x * gridSpacing, height),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
                                )
                            }
                            for (y in 0..(height / gridSpacing).toInt()) {
                                drawLine(
                                    color = Color.Gray.copy(alpha = 0.15f),
                                    start = Offset(0f, y * gridSpacing),
                                    end = Offset(width, y * gridSpacing),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
                                )
                            }
                        }

                        // Map Legend & Current Position Badge
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.9f))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = TerracottaPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Puntos de Exhibición (${events.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Render Interactive Map Pins
                        val totalEvents = events.ifEmpty { listOf() }
                        totalEvents.forEachIndexed { index, event ->
                            val isSelected = selectedEvent?.id == event.id
                            // Generate mock spatial coordinates on map box based on index
                            val offsetX = when (index % 4) {
                                0 -> 0.22f
                                1 -> 0.48f
                                2 -> 0.72f
                                else -> 0.82f
                            }
                            val offsetY = when (index % 3) {
                                0 -> 0.28f
                                1 -> 0.58f
                                else -> 0.78f
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        start = (offsetX * 280).dp,
                                        top = (offsetY * 160).dp
                                    )
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable { mapViewModel.selectEvent(event) }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(if (isSelected) 42.dp else 32.dp)
                                            .clip(CircleShape)
                                            .background(if (event.status == "En curso") SageGreen else TerracottaPrimary)
                                            .border(
                                                width = if (isSelected) 3.dp else 1.dp,
                                                color = if (isSelected) GoldAccent else Color.White,
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Storefront,
                                            contentDescription = event.title,
                                            tint = Color.White,
                                            modifier = Modifier.size(if (isSelected) 22.dp else 16.dp)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.White.copy(alpha = 0.95f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = event.city,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        if (events.isEmpty()) {
                            Text(
                                text = "No se encontraron ferias con los filtros seleccionados.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Selected Event Details Card
                selectedEvent?.let { event ->
                    Text(
                        text = "Feria / Showroom Seleccionado",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (event.status == "En curso") SageGreen else TerracottaPrimary)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = event.status.uppercase(),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    Text(
                                        text = event.standNumber,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EarthenBrown
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = event.title,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = TerracottaPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${event.locationName} • ${event.address}, ${event.city}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = SageGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Fechas: ${event.startDate} al ${event.endDate} (${event.schedule})",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = event.description,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 17.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.background)
                                        .padding(10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ShoppingBag,
                                            contentDescription = null,
                                            tint = TerracottaDark,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Colección Exhibida:",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = event.featuredCollection,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TerracottaDark
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Action buttons for selected fair
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { onNotice("Iniciando GPS hacia ${event.locationName} (${event.address})...") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = TerracottaPrimary,
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Directions,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Cómo Llegar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { onNotice("Recordatorio guardado para '${event.title}' en tu calendario.") },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.BookmarkBorder,
                                            contentDescription = null,
                                            tint = EarthenBrown,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Guardar", fontSize = 12.sp, color = EarthenBrown)
                                    }
                                }
                            }
                        }
                    }
                }

                // List of All Fairs and Showrooms
                Text(
                    text = "Todas las Ferias y Puntos de Exhibición",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    events.forEach { item ->
                        EventListItemTile(
                            event = item,
                            isSelected = selectedEvent?.id == item.id,
                            onClick = { mapViewModel.selectEvent(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EventListItemTile(
    event: EventShowroom,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) TerracottaPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) BorderStroke(1.5.dp, TerracottaPrimary) else null
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (event.status == "En curso") SageGreen else EarthenBrown),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = event.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = "${event.city} • ${event.locationName}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "🗓️ ${event.startDate} - ${event.endDate} | ${event.standNumber}",
                    fontSize = 10.sp,
                    color = EarthenBrown,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
