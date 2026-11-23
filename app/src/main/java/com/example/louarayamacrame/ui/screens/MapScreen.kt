package com.example.louarayamacrame.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.louarayamacrame.model.EventShowroom
import com.example.louarayamacrame.ui.theme.PurpleDark
import com.example.louarayamacrame.ui.theme.PurplePrimary
import com.example.louarayamacrame.ui.theme.PurpleSurface
import com.example.louarayamacrame.ui.theme.SageGreen
import com.example.louarayamacrame.ui.viewmodel.MapViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Locale

val CARTO_API_KEY = "cb1_4wzl_1_a3ac119ef7bc29859a59e192"

val CARTO_VOYAGER_TILE_SOURCE = XYTileSource(
    "CartoVoyagerKey",
    0, 20, 256, ".png?key=$CARTO_API_KEY",
    arrayOf(
        "https://a.basemaps.cartocdn.com/rastertiles/voyager/",
        "https://b.basemaps.cartocdn.com/rastertiles/voyager/",
        "https://c.basemaps.cartocdn.com/rastertiles/voyager/",
        "https://d.basemaps.cartocdn.com/rastertiles/voyager/"
    ),
    "© OpenStreetMap contributors, © CARTO"
)

// =====================================================================
// Utilitarios de Permisos (iguales a Permisos.kt en referencia)
// =====================================================================
val PERMISOS_UBICACION = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

fun Context.tienePermisoUbicacion(): Boolean =
    PERMISOS_UBICACION.any { permiso ->
        ContextCompat.checkSelfPermission(this, permiso) == PackageManager.PERMISSION_GRANTED
    }

// =====================================================================
// Extensiones de osmdroid (iguales a Mapa.kt en referencia)
// =====================================================================
fun MapView.irA(punto: GeoPoint, zoom: Double = 15.0) {
    controller.animateTo(punto, zoom, 1500L)
}

fun MapView.crearCapaMiUbicacion(): MyLocationNewOverlay {
    val capa = MyLocationNewOverlay(GpsMyLocationProvider(context), this)
    if (!overlays.contains(capa)) {
        overlays.add(capa)
    }
    return capa
}

fun MapView.crearMarcador(): Marker {
    val marcador = Marker(this)
    marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    return marcador
}

fun MapView.mostrarMarcador(marcador: Marker, punto: GeoPoint, titulo: String, direccion: String) {
    marcador.position = punto
    marcador.title = titulo
    marcador.snippet = direccion
    marcador.closeInfoWindow()
    if (!overlays.contains(marcador)) overlays.add(marcador)
    invalidate()
}

fun MapView.alMantenerPresionado(accion: (GeoPoint) -> Unit) {
    val receptor = object : MapEventsReceiver {
        override fun singleTapConfirmedHelper(p: GeoPoint): Boolean = false
        override fun longPressHelper(p: GeoPoint): Boolean {
            accion(p)
            return true
        }
    }
    overlays.add(0, MapEventsOverlay(receptor))
}

// =====================================================================
// Creador con Ciclo de Vida (igual a VistaMapa.kt en referencia)
// =====================================================================
@Composable
fun rememberMapView(): MapView {
    val context = LocalContext.current

    val appUserAgent = "LouArayaMacrameApp/1.0 (${context.packageName}; contacto@louaraya.cl)"

    val mapView = remember {
        val config = Configuration.getInstance()
        config.userAgentValue = appUserAgent
        config.osmdroidTileCache = context.cacheDir
        config.osmdroidBasePath = context.cacheDir
        config.load(context, context.getSharedPreferences("osmdroid_prefs", Context.MODE_PRIVATE))

        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            isTilesScaledToDpi = true
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

            // Copyright Overlay para atribución obligatoria de OSM
            val copyrightOverlay = CopyrightOverlay(context).apply {
                setTextSize(10)
                setAlignRight(true)
            }
            overlays.add(copyrightOverlay)

            setScrollableAreaLimitLatitude(
                MapView.getTileSystem().maxLatitude,
                MapView.getTileSystem().minLatitude,
                0
            )
            minZoomLevel = 3.0

            controller.setZoom(13.0)
            controller.setCenter(GeoPoint(-33.4025, -70.5750)) // Santiago center
        }
    }

    val cicloDeVida = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(cicloDeVida) {
        val observador = LifecycleEventObserver { _, evento ->
            when (evento) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        cicloDeVida.addObserver(observador)

        onDispose {
            cicloDeVida.removeObserver(observador)
            mapView.onDetach()
        }
    }

    return mapView
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    isAdmin: Boolean = false,
    onNavigateBack: () -> Unit,
    onNotice: (String) -> Unit
) {
    val context = LocalContext.current
    val events by mapViewModel.filteredEvents.collectAsState()
    val selectedCity by mapViewModel.selectedCity.collectAsState()
    val searchQuery by mapViewModel.searchQuery.collectAsState()
    val selectedEvent by mapViewModel.selectedEvent.collectAsState()

    var userLocationPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var hasLocationPermission by remember { mutableStateOf(context.tienePermisoUbicacion()) }

    // osmdroid MapView instanciado con ciclo de vida
    val mapa = rememberMapView()
    val capaMiUbicacion = remember { mapa.crearCapaMiUbicacion() }

    fun mostrarMiUbicacion(punto: GeoPoint) {
        mapa.irA(punto)
        userLocationPoint = punto
    }

    fun buscarMiUbicacion() {
        capaMiUbicacion.enableMyLocation()
        val puntoConocido = capaMiUbicacion.myLocation
        if (puntoConocido != null) {
            mostrarMiUbicacion(puntoConocido)
        } else {
            onNotice("Buscando tu ubicación GPS...")
            capaMiUbicacion.runOnFirstFix {
                mapa.post {
                    capaMiUbicacion.myLocation?.let { mostrarMiUbicacion(it) }
                }
            }
        }
    }

    val pedirPermisosLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { respuestas ->
        if (respuestas.values.any { it }) {
            hasLocationPermission = true
            buscarMiUbicacion()
        } else {
            hasLocationPermission = false
            onNotice("Sin permiso de ubicación no se puede mostrar tu posición en tiempo real.")
        }
    }

    fun alTocarMiUbicacion() {
        if (context.tienePermisoUbicacion()) {
            hasLocationPermission = true
            buscarMiUbicacion()
        } else {
            pedirPermisosLauncher.launch(PERMISOS_UBICACION)
        }
    }

    LaunchedEffect(Unit) {
        if (context.tienePermisoUbicacion()) {
            buscarMiUbicacion()
        }

        mapa.alMantenerPresionado { punto ->
            mapa.irA(punto)
            onNotice("Punto seleccionado en coordenadas: ${String.format(Locale.US, "%.4f, %.4f", punto.latitude, punto.longitude)}")
        }
    }

    LaunchedEffect(selectedEvent) {
        selectedEvent?.let { evt ->
            mapa.irA(GeoPoint(evt.latitude, evt.longitude))
        }
    }

    fun openGoogleMapsDirections(event: EventShowroom) {
        val gmmIntentUri = Uri.parse("google.navigation:q=${event.latitude},${event.longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        try {
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${event.latitude},${event.longitude}")
            )
            context.startActivity(browserIntent)
        }
    }

    fun calculateDistanceKm(event: EventShowroom): Double? {
        val loc = userLocationPoint ?: return null
        val results = FloatArray(1)
        android.location.Location.distanceBetween(
            loc.latitude, loc.longitude,
            event.latitude, event.longitude,
            results
        )
        return (results[0] / 1000.0)
    }

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
                        IconButton(onClick = {
                            mapViewModel.addEvent(
                                title = "Feria de Diseño & Macramé",
                                locationName = "Plaza de la Cultura",
                                address = "Av. Providencia 1200",
                                city = "Santiago",
                                schedule = "11:00 - 20:00 hrs",
                                standNumber = "Stand #15",
                                description = "Muestra artesanal de tejidos en macramé y colecciones a medida."
                            )
                            onNotice("Nueva feria agregada al mapa de eventos.")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar Evento",
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
                // Location Permission Banner / GPS Status
                if (!hasLocationPermission) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
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
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Permiso de Ubicación GPS Requerido",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PurpleDark
                                )
                                Text(
                                    text = "Activa la ubicación para mostrar tu punto azul en tiempo real en el mapa.",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = { alTocarMiUbicacion() },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                            ) {
                                Text("Activar", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.GpsFixed,
                                contentDescription = null,
                                tint = SageGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "GPS Activo • osmdroid en vivo",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SageGreen
                            )
                        }

                        OutlinedButton(
                            onClick = { alTocarMiUbicacion() },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = PurplePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mi Posición", fontSize = 10.sp, color = PurplePrimary)
                        }
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { mapViewModel.setSearchQuery(it) },
                    placeholder = { Text("Buscar feria, recinto o ciudad...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PurplePrimary) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary
                    )
                )

                // City Filter Chips
                Text(
                    text = "Filtrar por Ciudad",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    items(cities) { city ->
                        FilterChip(
                            selected = selectedCity == city,
                            onClick = { mapViewModel.setCity(city) },
                            label = { Text(city, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PurplePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // OSMDROID MAP VIEWPORT BOX (INTEGRADO EN "MAPA DE FERIAS Y SHOWROOMS")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Renderiza el MapView de osmdroid en Compose
                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { mapa },
                            update = { mv ->
                                // Limpia marcadores anteriores y re-dibuja los marcadores de eventos
                                mv.overlays.removeAll { it is Marker }

                                events.forEach { event ->
                                    val marker = mv.crearMarcador()
                                    mv.mostrarMarcador(
                                        marcador = marker,
                                        punto = GeoPoint(event.latitude, event.longitude),
                                        titulo = event.title,
                                        direccion = "${event.locationName} • ${event.city} (${event.standNumber})"
                                    )
                                    marker.setOnMarkerClickListener { m, _ ->
                                        m.showInfoWindow()
                                        mapViewModel.selectEvent(event)
                                        true
                                    }
                                }
                                mv.invalidate()
                            }
                        )

                        // Botones de Zoom (+) y (-) flotantes (BotonesMapa)
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SmallFloatingActionButton(
                                onClick = { mapa.controller.zoomIn() },
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Acercar", tint = PurplePrimary)
                            }
                            SmallFloatingActionButton(
                                onClick = { mapa.controller.zoomOut() },
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Alejar", tint = PurplePrimary)
                            }
                            FloatingActionButton(
                                onClick = { alTocarMiUbicacion() },
                                containerColor = PurplePrimary,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(Icons.Default.MyLocation, contentDescription = "Mi Ubicación", tint = Color.White)
                            }
                        }

                        // Crédito obligatorio de OpenStreetMap
                        Text(
                            text = "© OpenStreetMap",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.DarkGray,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                                .background(Color.White.copy(alpha = 0.75f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Selected Event Details Card
                selectedEvent?.let { event ->
                    val distance = calculateDistanceKm(event)

                    Text(
                        text = "Feria / Showroom Seleccionado",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (event.status == "En curso") SageGreen else PurplePrimary)
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = event.status.uppercase(),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }

                                        if (distance != null) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0xFFE3F2FD))
                                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                                            ) {
                                                Text(
                                                    text = "📍 ${String.format(Locale.US, "%.1f", distance)} km de ti",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF1565C0)
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = event.standNumber,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PurpleDark
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = event.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = PurplePrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${event.locationName} • ${event.address}, ${event.city}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = SageGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Fechas: ${event.startDate} al ${event.endDate} (${event.schedule})",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = event.description,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 15.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PurpleSurface)
                                        .padding(8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ShoppingBag,
                                            contentDescription = null,
                                            tint = PurplePrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = "Colección Exhibida:",
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = event.featuredCollection,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = PurpleDark
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            openGoogleMapsDirections(event)
                                            onNotice("Abriendo Google Maps Navegación GPS hacia ${event.locationName}...")
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PurplePrimary,
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Directions,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Cómo Llegar (GPS)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { onNotice("Recordatorio guardado para '${event.title}' en tu calendario.") },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.BookmarkBorder,
                                            contentDescription = null,
                                            tint = PurpleDark,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Guardar", fontSize = 11.sp, color = PurpleDark)
                                    }
                                }
                            }
                        }
                    }
                }

                // List of All Fairs
                Text(
                    text = "Todas las Ferias y Puntos de Exhibición",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    events.forEach { item ->
                        EventListItemTile(
                            event = item,
                            isSelected = selectedEvent?.id == item.id,
                            distanceKm = calculateDistanceKm(item),
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
    distanceKm: Double?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PurpleSurface else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) BorderStroke(1.5.dp, PurplePrimary) else null
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (event.status == "En curso") SageGreen else PurplePrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (distanceKm != null) {
                        Text(
                            text = "📍 ${String.format(Locale.US, "%.1f", distanceKm)} km",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = PurplePrimary
                        )
                    }
                }

                Text(
                    text = "${event.city} • ${event.locationName}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "🗓️ ${event.startDate} - ${event.endDate} | ${event.standNumber}",
                    fontSize = 9.sp,
                    color = PurpleDark,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
