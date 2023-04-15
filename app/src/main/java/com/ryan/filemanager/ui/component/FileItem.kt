package com.ryan.filemanager.ui.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ryan.filemanager.R
import com.ryan.filemanager.util.getFileSizeShortString
import com.ryan.filemanager.util.getFullDateTimeString
import java.io.File
import java.text.DateFormat
import java.time.format.DateTimeFormatter
import java.util.*


@Composable
fun FileItem(
    file: File,
    onClick: (File) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .height(80.dp)
            .padding(5.dp)
            .verticalScroll(rememberScrollState())
            .clickable { onClick(file) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = if (file.isDirectory) painterResource(R.drawable.ic_folder)
                else painterResource(R.drawable.ic_file),
            contentDescription = null,
            modifier = Modifier.size(50.dp),
            tint = MaterialTheme.colors.onSurface,
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = file.name,
                fontSize = 16.sp,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = getFullDateTimeString(file.lastModified()),
                    style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Light),
                    color = MaterialTheme.colors.onSurface,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.weight(1f))
                if (file.isFile) {
                    Text(
                        text = getFileSizeShortString(file.length()),
                        style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Light),
                        color = MaterialTheme.colors.onSurface,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}