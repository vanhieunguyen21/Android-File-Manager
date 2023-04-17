package com.ryan.filemanager.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ryan.filemanager.R
import com.ryan.filemanager.model.SortOrder
import com.ryan.filemanager.model.SortType
import com.ryan.filemanager.ui.component.EmptyDirectory
import com.ryan.filemanager.ui.component.FileItem
import com.ryan.filemanager.ui.component.SimpleTopAppBar
import java.io.File

@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel = hiltViewModel()
) {
    BackHandler(viewModel.canGoBack) {
        viewModel.goBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
    ) {
        SimpleTopAppBar(
            title = if (viewModel.currentPathSegment.isEmpty())
                stringResource(R.string.app_name) else viewModel.currentPathSegment.last(),
            navigationButton = {
                if (viewModel.canGoBack) {
                    IconButton(onClick = { viewModel.goBack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = null,
                            tint = MaterialTheme.colors.onSurface,
                        )
                    }
                }
            },
            menuButton = {
                IconButton(onClick = { viewModel.toggleDarkTheme() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dark_mode),
                        contentDescription = null,
                        tint = MaterialTheme.colors.onSurface
                    )
                }
            }
        )

        SortBar(
            sortType = viewModel.sortType,
            sortOrder = viewModel.sortOrder,
            onSortTypeChange = { viewModel.onSortTypeChange(it) },
            onSortOrderChange = { viewModel.onSortOrderChange(it) },
        )

        FileList(
            files = viewModel.files,
            onFileClick = {
                if (it.isDirectory) {
                    viewModel.goToDirectory(it)
                }
            }
        )
    }
}

@Composable
fun FileList(
    files: List<File>,
    onFileClick: (File) -> Unit,
) {
    if (files.isEmpty()) {
        EmptyDirectory(
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .fillMaxSize()
        )
    } else {
        LazyColumn(modifier = Modifier.background(MaterialTheme.colors.surface)) {
            items(files, key = { it.name }) {
                FileItem(
                    file = it,
                    onClick = onFileClick,
                )
                Divider(thickness = 0.2.dp, color = MaterialTheme.colors.onSurface)
            }
        }
    }
}

@Composable
fun SortBar(
    sortType: SortType,
    sortOrder: SortOrder,
    onSortTypeChange: (SortType) -> Unit,
    onSortOrderChange: (SortOrder) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Box {
            var dropdownExpanded by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .clickable { dropdownExpanded = !dropdownExpanded }
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painterResource(R.drawable.ic_sort),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.Gray,
                )
                Text(
                    text = when (sortType) {
                        SortType.Name -> stringResource(R.string.sort_type_name)
                        SortType.LastModified -> stringResource(R.string.sort_type_last_modified)
                        SortType.Size -> stringResource(R.string.sort_type_size)
                    },
                    style = MaterialTheme.typography.caption.copy(
                        fontSize = 14.sp,
                        color = Color.Gray,
                    )
                )
            }
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                DropdownMenuItem(onClick = {
                    onSortTypeChange(SortType.Name)
                    dropdownExpanded = false
                }) {
                    Text(stringResource(R.string.sort_type_name))
                }
                DropdownMenuItem(onClick = {
                    onSortTypeChange(SortType.LastModified)
                    dropdownExpanded = false
                }) {
                    Text(stringResource(R.string.sort_type_last_modified))
                }
                DropdownMenuItem(onClick = {
                    onSortTypeChange(SortType.Size)
                    dropdownExpanded = false
                }) {
                    Text(stringResource(R.string.sort_type_size))
                }
            }
        }
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(onClick = {
            onSortOrderChange(
                when (sortOrder) {
                    SortOrder.Ascending -> SortOrder.Descending
                    SortOrder.Descending -> SortOrder.Ascending
                }
            )
        }, modifier = Modifier.size(18.dp)) {
            Icon(
                when (sortOrder) {
                    SortOrder.Ascending -> painterResource(R.drawable.ic_arrow_upward)
                    SortOrder.Descending -> painterResource(R.drawable.ic_arrow_downward)
                },
                contentDescription = null,
                tint = Color.Gray,
            )
        }
    }
}