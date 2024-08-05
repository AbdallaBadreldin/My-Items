package com.fstech.myItems.presentation.matchmaking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fstech.myItems.R

@Composable
fun MatchMakingSuccessScreen(viewModel: MatchMakingViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { viewModel.triggerCloseActivity() }) {
            Text(
                text = stringResource(R.string.congratulations_we_notified_user_successfully_you_can_contact_him_now),
            )
        }
    }
}