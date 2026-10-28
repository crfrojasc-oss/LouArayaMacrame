package com.example.louarayamacrame.ui.screens

import android.util.Patterns
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.louarayamacrame.ui.theme.EarthenBrown
import com.example.louarayamacrame.ui.theme.PurpleDark
import com.example.louarayamacrame.ui.theme.PurplePrimary
import com.example.louarayamacrame.ui.theme.SageGreen
import com.example.louarayamacrame.ui.theme.TerracottaPrimary
import com.example.louarayamacrame.ui.viewmodel.AuthViewModel

data class AvatarOption(val id: String, val name: String, val iconText: String, val color: Color)

val avatarOptions = listOf(
    AvatarOption("avatar_1", "Amatista Violeta", "🔮", PurplePrimary),
    AvatarOption("avatar_2", "Hilo Terracota", "🧵", TerracottaPrimary),
    AvatarOption("avatar_3", "Hoja Oliva", "🌿", SageGreen),
    AvatarOption("avatar_4", "Madera Nativa", "🪵", EarthenBrown)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onRegisterSuccessToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var alias by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedAvatar by remember { mutableStateOf(avatarOptions[0]) }

    // Touched / Focus state for each field
    var nameTouched by remember { mutableStateOf(false) }
    var aliasTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var phoneTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var confirmPasswordTouched by remember { mutableStateOf(false) }
    var formSubmitted by remember { mutableStateOf(false) }

    val registerError by authViewModel.registerError.collectAsState()
    val registerSuccess by authViewModel.registerSuccess.collectAsState()

    // Validation Functions
    fun getNombreError(value: String): String? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return "Este campo es obligatorio"
        if (trimmed.length < 5) return "El nombre debe tener más de 4 caracteres"
        return null
    }

    fun getAliasError(value: String): String? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return "Este campo es obligatorio"
        if (trimmed.length < 5) return "El alias debe tener más de 4 caracteres"
        return null
    }

    fun getEmailError(value: String): String? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return "Este campo es obligatorio"
        if (!trimmed.contains("@") || !Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
            return "Ingresa un correo válido"
        }
        return null
    }

    fun getPhoneError(value: String): String? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return "Este campo es obligatorio"
        if (trimmed.length != 9 || !trimmed.all { it.isDigit() }) {
            return "El teléfono debe tener 9 dígitos"
        }
        return null
    }

    fun getPasswordError(value: String): String? {
        if (value.isEmpty()) return "Este campo es obligatorio"
        if (value.length < 7) return "La contraseña debe tener más de 6 caracteres"
        return null
    }

    fun getConfirmPasswordError(value: String, pass: String): String? {
        if (value.isEmpty()) return "Este campo es obligatorio"
        if (value != pass) return "Las contraseñas no coinciden"
        return null
    }

    val nombreError = getNombreError(name)
    val aliasError = getAliasError(alias)
    val emailError = getEmailError(email)
    val phoneError = getPhoneError(phone)
    val passwordError = getPasswordError(password)
    val confirmPasswordError = getConfirmPasswordError(confirmPassword, password)

    if (registerSuccess) {
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
                    text = "¡Perfil Creado con Éxito!",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "Tu perfil ($alias) se ha guardado localmente. Ya puedes iniciar sesión para ver tus favoritos y solicitudes.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.clearErrors()
                        onRegisterSuccessToLogin()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Text("Ir al Inicio de Sesión")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Registro de Cliente",
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
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Info Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
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
                            text = "Registra un perfil para personalizar tu experiencia. Puedes tener varios perfiles en este mismo celular.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            lineHeight = 15.sp
                        )
                    }
                }

                // Avatar Selection Header
                Text(
                    text = "Selecciona tu Avatar Artesanal",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    avatarOptions.forEach { avatar ->
                        val isSelected = selectedAvatar.id == avatar.id
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { selectedAvatar = avatar }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(avatar.color.copy(alpha = if (isSelected) 0.22f else 0.08f))
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) avatar.color else Color.LightGray,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = avatar.iconText,
                                    fontSize = 24.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = avatar.name,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) avatar.color else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Field 1: Nombre
                val showNombreErr = (nameTouched || formSubmitted) && nombreError != null
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameTouched = true
                    },
                    label = { Text("Nombre Completo", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PurplePrimary) },
                    singleLine = true,
                    isError = showNombreErr,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { if (!it.isFocused && name.isNotEmpty()) nameTouched = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        focusedLabelColor = PurplePrimary
                    )
                )
                if (showNombreErr && nombreError != null) {
                    Text(
                        text = nombreError,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 2.dp, bottom = 4.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Field 2: Alias
                val showAliasErr = (aliasTouched || formSubmitted) && aliasError != null
                OutlinedTextField(
                    value = alias,
                    onValueChange = {
                        alias = it
                        aliasTouched = true
                    },
                    label = { Text("Alias / Usuario", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null, tint = PurplePrimary) },
                    singleLine = true,
                    isError = showAliasErr,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { if (!it.isFocused && alias.isNotEmpty()) aliasTouched = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        focusedLabelColor = PurplePrimary
                    )
                )
                if (showAliasErr && aliasError != null) {
                    Text(
                        text = aliasError,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 2.dp, bottom = 4.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Field 3: Email
                val showEmailErr = (emailTouched || formSubmitted) && emailError != null
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailTouched = true
                    },
                    label = { Text("Correo Electrónico", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = PurplePrimary) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    isError = showEmailErr,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { if (!it.isFocused && email.isNotEmpty()) emailTouched = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        focusedLabelColor = PurplePrimary
                    )
                )
                if (showEmailErr && emailError != null) {
                    Text(
                        text = emailError,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 2.dp, bottom = 4.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Field 4: Teléfono
                val showPhoneErr = (phoneTouched || formSubmitted) && phoneError != null
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        phoneTouched = true
                    },
                    label = { Text("Teléfono de Contacto", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = PurplePrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    isError = showPhoneErr,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { if (!it.isFocused && phone.isNotEmpty()) phoneTouched = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        focusedLabelColor = PurplePrimary
                    )
                )
                if (showPhoneErr && phoneError != null) {
                    Text(
                        text = phoneError,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 2.dp, bottom = 4.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Field 5: Contraseña
                val showPasswordErr = (passwordTouched || formSubmitted) && passwordError != null
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordTouched = true
                    },
                    label = { Text("Contraseña", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PurplePrimary) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Mostrar contraseña"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    isError = showPasswordErr,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { if (!it.isFocused && password.isNotEmpty()) passwordTouched = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        focusedLabelColor = PurplePrimary
                    )
                )
                if (showPasswordErr && passwordError != null) {
                    Text(
                        text = passwordError,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 2.dp, bottom = 4.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Field 6: Confirmar Contraseña
                val showConfirmPasswordErr = (confirmPasswordTouched || formSubmitted) && confirmPasswordError != null
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordTouched = true
                    },
                    label = { Text("Confirmar Contraseña", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PurplePrimary) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    isError = showConfirmPasswordErr,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { if (!it.isFocused && confirmPassword.isNotEmpty()) confirmPasswordTouched = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        focusedLabelColor = PurplePrimary
                    )
                )
                if (showConfirmPasswordErr && confirmPasswordError != null) {
                    Text(
                        text = confirmPasswordError,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, top = 2.dp, bottom = 4.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Global Auth ViewModel Error Display (e.g. Duplicate User)
                registerError?.let { err ->
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )
                }

                Button(
                    onClick = {
                        formSubmitted = true
                        nameTouched = true
                        aliasTouched = true
                        emailTouched = true
                        phoneTouched = true
                        passwordTouched = true
                        confirmPasswordTouched = true

                        val hasAnyError = nombreError != null || aliasError != null || emailError != null ||
                                phoneError != null || passwordError != null || confirmPasswordError != null

                        if (!hasAnyError) {
                            authViewModel.register(
                                name = name,
                                alias = alias,
                                email = email,
                                password = password,
                                confirmPassword = confirmPassword,
                                phone = phone,
                                avatarId = selectedAvatar.id
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurplePrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Guardar y Crear Perfil",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onRegisterSuccessToLogin) {
                    Text(
                        text = "¿Ya tienes cuenta? Inicia Sesión aquí",
                        fontSize = 12.sp,
                        color = PurpleDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
