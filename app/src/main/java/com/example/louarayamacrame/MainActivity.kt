package com.example.louarayamacrame

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.louarayamacrame.ui.navigation.LouArayaNavGraph
import com.example.louarayamacrame.ui.theme.LouArayaMacrameTheme
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración estricta de osmdroid según política de OpenStreetMap:
        // 1. User-Agent descriptivo y único con Nombre de la App, Versión, Paquete y Correo de Contacto.
        // 2. Ruta de Caché de disco local para no sobrecargar los servidores con peticiones duplicadas.
        val ctx = applicationContext
        val config = Configuration.getInstance()
        config.userAgentValue = "LouArayaMacrameApp/1.0 (${ctx.packageName}; contacto@louaraya.cl)"
        config.osmdroidTileCache = ctx.cacheDir
        config.osmdroidBasePath = ctx.cacheDir
        config.load(ctx, ctx.getSharedPreferences("osmdroid_prefs", MODE_PRIVATE))

        enableEdgeToEdge()
        setContent {
            LouArayaMacrameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LouArayaNavGraph()
                }
            }
        }
    }
}
