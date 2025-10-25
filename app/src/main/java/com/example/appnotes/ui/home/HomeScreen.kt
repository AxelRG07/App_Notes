package com.example.appnotes.ui.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appnotes.ui.HomeViewModelProvider

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModelProvider.Factory)
) {

}