package com.fittrack.util

/**
 * Prosty sealed wrapper na wyniki operacji asynchronicznych.
 * Używany przez wszystkie ViewModele i repozytoria.
 */
sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}
