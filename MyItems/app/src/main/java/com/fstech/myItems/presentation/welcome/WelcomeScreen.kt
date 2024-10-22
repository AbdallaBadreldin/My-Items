package com.fstech.myItems.presentation.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fstech.myItems.R
import com.fstech.myItems.navigation.NavRoute
import com.fstech.myItems.presentation.getAppLanguage
import com.jetawy.domain.utils.UiState
import com.jetawy.domain.utils.Utils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = viewModel(), navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    FlowColumn(
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(64.dp, 64.dp, 64.dp, 64.dp)
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = stringResource(R.string.welcome),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
        )
        Image(
            modifier = Modifier.align(Alignment.CenterHorizontally).clip(RoundedCornerShape(16.dp)),
            painter = painterResource(id = R.drawable.welcome_screen),
            contentDescription = stringResource(id = R.string.welcome)
        )
        if (uiState is UiState.Initial) {
            if (Utils.isInternetAvailable(context = context))
                viewModel.run {
                    sendPrompt(
                        stringResource(
                            R.string.generate_only_one_random_quote_to_help_people_to_take_care_of_their_items_or_money_that_can_lose_it_or_being_stole_for_my_items_finder_android_app_in_language,
                            getAppLanguage()
                        )
                    )
                }
            else
                viewModel.noInternetConnection(stringResource(id = R.string.no_internet_connection))
        }
        when (uiState) {
            is UiState.Error -> {
                Text(
                    textAlign = TextAlign.Center, text = (uiState as UiState.Error).message
                )
            }

            UiState.Initial -> {
                Text(
                    textAlign = TextAlign.Center, text = ""
                )
            }

            UiState.Loading -> {
                Text(
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.generating_today_s_quote)
                )
            }

            is UiState.Success<*> -> {
                Text(
                    textAlign = TextAlign.Center,
                    text = (uiState as UiState.Success<*>).outputData as String
                )
            }
        }

        // add your column here (with align modifier)
        Column(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
        ) {
            Button(
                onClick = { navController.navigate(NavRoute.HomeNavRoute.path) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(R.string.start_using_the_app))
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}