package com.aguiabranca.inovacao.presentation.screens.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aguiabranca.inovacao.di.AppContainer
import com.aguiabranca.inovacao.domain.AppResult
import com.aguiabranca.inovacao.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

data class ProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun updateProfilePicture(uri: Uri, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            
            val base64String = try {
                withContext(Dispatchers.IO) {
                    processUriToBase64(uri)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Falha ao processar a imagem: ${e.message}") }
                return@launch
            }
            
            when (val result = userRepository.updateProfilePicture(base64String)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, successMessage = "Foto atualizada com sucesso!") }
                    onSuccess()
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    private fun processUriToBase64(uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri) ?: throw IllegalArgumentException("Não foi possível abrir a imagem")
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        
        // Scale to a maximum of 256x256
        val maxWidth = 256
        val maxHeight = 256
        val ratio = minOf(maxWidth.toFloat() / originalBitmap.width, maxHeight.toFloat() / originalBitmap.height)
        
        val scaledBitmap = if (ratio < 1f) {
            Bitmap.createScaledBitmap(
                originalBitmap,
                (originalBitmap.width * ratio).toInt(),
                (originalBitmap.height * ratio).toInt(),
                true
            )
        } else {
            originalBitmap
        }
        
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val byteArray = outputStream.toByteArray()
        // Use NO_WRAP to ensure the Base64 string does not contain newline characters
        val base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
        
        return "data:image/jpeg;base64,$base64"
    }

    fun deleteProfilePicture(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            when (val result = userRepository.deleteProfilePicture()) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, successMessage = "Foto removida com sucesso!") }
                    onSuccess()
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }

    class Factory(private val appContainer: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(
                userRepository = appContainer.userRepository,
                context = appContainer.context
            ) as T
        }
    }
}
