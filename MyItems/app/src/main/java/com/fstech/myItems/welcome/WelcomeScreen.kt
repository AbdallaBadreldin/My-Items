package com.fstech.myItems.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.fstech.myItems.Screen
import com.fstech.myItems.UiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = viewModel(), navController: NavHostController
) {
//    var prompt by rememberSaveable { mutableStateOf(placeholderPrompt) }
//    var result by rememberSaveable { mutableStateOf(placeholderResult) }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    FlowColumn(
        verticalArrangement = Arrangement.spacedBy(64.dp),
//        horizontalArrangement = Alignment.CenterVertically,
        modifier = Modifier
//            .fillMaxWidth()
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
            modifier = Modifier.clip(RoundedCornerShape(16.dp)),
            painter = painterResource(id = R.drawable.welcome_screen),
            contentDescription = stringResource(id = R.string.welcome)
        )
        if (uiState is UiState.Initial) {
            viewModel.sendPrompt("generate only one random quote to help people to take care of their items or money that can lose it or being stole for my Items finder androdi app")
        }
        when (uiState) {
            is UiState.Error -> Text(
                textAlign = TextAlign.Center, text = (uiState as UiState.Error).errorMessage
            )

            UiState.Initial -> Text(
                textAlign = TextAlign.Center, text = ""
            )

            UiState.Loading -> Text(
                textAlign = TextAlign.Center, text = "generating today's quote :)"
            )

            is UiState.Success -> Text(
                textAlign = TextAlign.Center, text = (uiState as UiState.Success).outputText
            )
        }


    }
    Spacer(modifier = Modifier.height(64.dp))

    Box(modifier = Modifier.fillMaxSize()) {
        // add your column here (with align modifier)
        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            Button(onClick = { navController.navigate(Screen.MapScreen.route) }) {
                Text(text = stringResource(R.string.start_using_the_app))
            }
            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}