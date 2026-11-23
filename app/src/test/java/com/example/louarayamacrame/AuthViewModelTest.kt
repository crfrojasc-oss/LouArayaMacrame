package com.example.louarayamacrame

import app.cash.turbine.test
import com.example.louarayamacrame.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun login_emptyFields_setsError() = runTest {
        val result = viewModel.login("", "")
        assertFalse(result)
        viewModel.loginError.test {
            assertEquals("Por favor ingresa correo/alias y contraseña.", awaitItem())
        }
    }

    @Test
    fun register_invalidEmail_setsError() = runTest {
        val success = viewModel.register(
            name = "Test User",
            alias = "test_alias",
            email = "invalidemail",
            password = "password123",
            confirmPassword = "password123",
            phone = "+56912345678",
            avatarId = "avatar_1"
        )
        assertFalse(success)
        viewModel.registerError.test {
            assertEquals("Ingresa un correo electrónico válido.", awaitItem())
        }
    }

    @Test
    fun register_passwordMismatch_setsError() = runTest {
        val success = viewModel.register(
            name = "Test User",
            alias = "test_alias",
            email = "test@mail.com",
            password = "password123",
            confirmPassword = "password456",
            phone = "+56912345678",
            avatarId = "avatar_1"
        )
        assertFalse(success)
        viewModel.registerError.test {
            assertEquals("Las contraseñas no coinciden.", awaitItem())
        }
    }
}
