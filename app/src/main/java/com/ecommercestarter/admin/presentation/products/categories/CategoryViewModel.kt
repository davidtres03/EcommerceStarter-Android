package com.ecommercestarter.admin.presentation.products.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecommercestarter.admin.data.model.CategoryDto
import com.ecommercestarter.admin.data.model.CreateCategoryRequest
import com.ecommercestarter.admin.data.model.UpdateCategoryRequest
import com.ecommercestarter.admin.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    
    private val _categoriesState = MutableStateFlow<CategoriesState>(CategoriesState.Loading)
    val categoriesState: StateFlow<CategoriesState> = _categoriesState.asStateFlow()
    
    private val _formState = MutableStateFlow(CategoryFormState())
    val formState: StateFlow<CategoryFormState> = _formState.asStateFlow()
    
    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState.asStateFlow()
    
    init {
        loadCategories()
    }
    
    fun loadCategories(includeDisabled: Boolean = false) {
        viewModelScope.launch {
            _categoriesState.value = CategoriesState.Loading
            categoryRepository.getCategories(includeDisabled).fold(
                onSuccess = { categories ->
                    _categoriesState.value = CategoriesState.Success(categories)
                },
                onFailure = { error ->
                    _categoriesState.value = CategoriesState.Error(error.message ?: "Failed to load categories")
                }
            )
        }
    }
    
    fun onNameChange(name: String) {
        _formState.value = _formState.value.copy(name = name)
        validateName(name)
    }
    
    fun onDescriptionChange(description: String) {
        _formState.value = _formState.value.copy(description = description)
    }
    
    fun onIconClassChange(iconClass: String) {
        _formState.value = _formState.value.copy(iconClass = iconClass)
    }
    
    fun onDisplayOrderChange(displayOrder: String) {
        _formState.value = _formState.value.copy(displayOrder = displayOrder)
    }
    
    fun onIsEnabledChange(isEnabled: Boolean) {
        _formState.value = _formState.value.copy(isEnabled = isEnabled)
    }
    
    private fun validateName(name: String): Boolean {
        val error = when {
            name.isBlank() -> "Category name is required"
            name.length < 2 -> "Name must be at least 2 characters"
            name.length > 100 -> "Name must be less than 100 characters"
            else -> null
        }
        _formState.value = _formState.value.copy(nameError = error)
        return error == null
    }
    
    fun validateForm(): Boolean {
        val currentState = _formState.value
        return validateName(currentState.name)
    }
    
    fun createCategory() {
        if (!validateForm()) {
            return
        }
        
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            val request = CreateCategoryRequest(
                name = _formState.value.name.trim(),
                description = _formState.value.description.takeIf { it.isNotBlank() },
                iconClass = _formState.value.iconClass.takeIf { it.isNotBlank() },
                isEnabled = _formState.value.isEnabled,
                displayOrder = _formState.value.displayOrder.toIntOrNull() ?: 0
            )
            
            categoryRepository.createCategory(request).fold(
                onSuccess = { category ->
                    _operationState.value = OperationState.Success("Category created successfully")
                    loadCategories() // Reload list
                    resetForm()
                },
                onFailure = { error ->
                    _operationState.value = OperationState.Error(error.message ?: "Failed to create category")
                }
            )
        }
    }
    
    fun updateCategory(categoryId: Int) {
        if (!validateForm()) {
            return
        }
        
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            val request = UpdateCategoryRequest(
                name = _formState.value.name.trim(),
                description = _formState.value.description.takeIf { it.isNotBlank() },
                iconClass = _formState.value.iconClass.takeIf { it.isNotBlank() },
                isEnabled = _formState.value.isEnabled,
                displayOrder = _formState.value.displayOrder.toIntOrNull()
            )
            
            categoryRepository.updateCategory(categoryId, request).fold(
                onSuccess = { category ->
                    _operationState.value = OperationState.Success("Category updated successfully")
                    loadCategories() // Reload list
                    resetForm()
                },
                onFailure = { error ->
                    _operationState.value = OperationState.Error(error.message ?: "Failed to update category")
                }
            )
        }
    }
    
    fun deleteCategory(categoryId: Int) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            categoryRepository.deleteCategory(categoryId).fold(
                onSuccess = { message ->
                    _operationState.value = OperationState.Success(message)
                    loadCategories() // Reload list
                },
                onFailure = { error ->
                    _operationState.value = OperationState.Error(error.message ?: "Failed to delete category")
                }
            )
        }
    }
    
    fun loadCategoryForEdit(category: CategoryDto) {
        _formState.value = CategoryFormState(
            name = category.name,
            description = category.description ?: "",
            iconClass = category.iconClass,
            displayOrder = category.displayOrder.toString(),
            isEnabled = category.isEnabled
        )
    }
    
    fun resetForm() {
        _formState.value = CategoryFormState()
    }
    
    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}

sealed class CategoriesState {
    object Loading : CategoriesState()
    data class Success(val categories: List<CategoryDto>) : CategoriesState()
    data class Error(val message: String) : CategoriesState()
}

data class CategoryFormState(
    val name: String = "",
    val description: String = "",
    val iconClass: String = "bi-tag",
    val displayOrder: String = "0",
    val isEnabled: Boolean = true,
    val nameError: String? = null
)

sealed class OperationState {
    object Idle : OperationState()
    object Loading : OperationState()
    data class Success(val message: String) : OperationState()
    data class Error(val message: String) : OperationState()
}
