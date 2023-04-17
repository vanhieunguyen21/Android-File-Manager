package com.ryan.filemanager.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ryan.filemanager.ui.theme.FileManagerTheme
import com.ryan.filemanager.R
import com.ryan.filemanager.ui.screen.BrowserScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    // Legacy storage permissions for API < 30
    private val storagePermissions = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Storage manager permission for API >= 30
            var storageManager by remember {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) mutableStateOf(true)
                else mutableStateOf(Environment.isExternalStorageManager())
            }

            // Observe lifecycle event to recheck storage manager permission on resume
            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        storageManager =
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) true
                            else Environment.isExternalStorageManager()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            // Legacy storage permissions for API < 30
            val storagePermissionState = rememberMultiplePermissionsState(
                permissions = storagePermissions,
                onPermissionsResult = {
                    it.forEach { (_, granted) ->
                        if (!granted) {
                            Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            )

            FileManagerTheme(darkTheme = viewModel.darkTheme) {
                // Request all files access permission if not granted
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !storageManager) {
                    AlertDialog(
                        onDismissRequest = {},
                        confirmButton = {
                            Button(onClick = {
                                val intent =
                                    Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                                startActivity(intent)
                            }) {
                                Text(text = getString(R.string.confirm_grant_all_files_access))
                            }
                        },
                        dismissButton = {
                            Button(onClick = { finish() }) {
                                Text(text = getString(R.string.deny_grant_all_files_access))
                            }
                        },
                        text = { Text(text = getString(R.string.prompt_grant_all_file_access)) }
                    )
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R && !storagePermissionState.allPermissionsGranted) {
                    // Request legacy storage permissions
                    LaunchedEffect(true) {
                        storagePermissionState.launchMultiplePermissionRequest()
                    }
                } else {
                    BrowserScreen()
                }
            }
        }
    }
}