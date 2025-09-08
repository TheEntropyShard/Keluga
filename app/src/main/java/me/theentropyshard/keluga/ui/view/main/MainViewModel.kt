/*
 * Keluga - A simple Beluga reader
 * Copyright (C) 2025 TheEntropyShard (https://github.com/TheEntropyShard)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.theentropyshard.keluga.ui.view.main

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.theentropyshard.keluga.model.Blog
import okhttp3.OkHttpClient
import okhttp3.Request

enum class UiState {
    Initial,
    Loading,
    Failed,
    NotFound,
    Success
}

private val httpClient: OkHttpClient = OkHttpClient()
private val gson: Gson = Gson()

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var _state = MutableStateFlow(UiState.Initial)
    val state = _state.asStateFlow()

    var instanceUrl = mutableStateOf("https://beluga.gcollazo.com")

    private var _blog = MutableStateFlow(
        Blog(
            homePageUrl = "",
            authors = listOf(),
            posts = listOf()
        )
    )
    val blog = _blog.asStateFlow()

    fun load(url: String) {
        _state.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$url/beluga.json")
                .build()

            try {
                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val blog = gson.fromJson(
                            response.body.charStream(),
                            Blog::class.java
                        )

                        viewModelScope.launch {
                            _blog.value = blog
                            _state.value = UiState.Success
                        }
                    } else {
                        if (response.code == 404) {
                            viewModelScope.launch { _state.value = UiState.NotFound }
                        } else {
                            viewModelScope.launch { _state.value = UiState.Failed }
                            println(response.code)
                            println(response.body.string())
                        }
                    }
                }
            } catch (e: Exception) {
                viewModelScope.launch { _state.value = UiState.Failed }
                e.printStackTrace()
            }
        }
    }
}