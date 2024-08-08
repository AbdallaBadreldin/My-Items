package com.fstech.myItems.presentation.settings

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fstech.myItems.R
import com.fstech.myItems.presentation.theme.MyItemsTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {
    lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            MyItemsTheme {
                Scaffold { innerPadding ->
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        var showDialog by remember { mutableStateOf(false) }
                        ConfirmDialog(
                            showDialog = showDialog,
                            onDismiss = { showDialog = false },
                            onConfirm = {
                                // Perform confirm deleting action

                                showDialog = false
                            }
                        )
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(onClick = {
                                Firebase.auth.signOut()
                                Toast.makeText(
                                    this@SettingsActivity,
                                    this@SettingsActivity.getText(R.string.logged_out_successfully)
                                        .toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                this@SettingsActivity.finish()
                            }) {
                                Text(stringResource(R.string.logout))
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = stringResource(R.string.delete_account),
                                color = Color.Red,
                                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally)
                                    .clickable {
                                        showDialog = true
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.confirmation)) },
            text = { Text(stringResource(R.string.are_you_sure_you_want_to_delete_your_account_and_all_your_data)) },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text(stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }
}