package com.dietpizza.byakugan.composables.ui

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.dietpizza.byakugan.R
import com.dietpizza.byakugan.activities.MangaViewerActivity
import com.dietpizza.byakugan.models.MangaMetadataModel
import java.io.File

@Composable
fun MangaCard(manga: MangaMetadataModel) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = {
            val intent = Intent(context, MangaViewerActivity::class.java).apply {
                putExtra("MANGA_FILE_PATH", manga.filename)
            }
            context.startActivity(intent)
        }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Image with 3:4 aspect ratio
            AsyncImage(
                model = manga.coverImagePath?.let { File(it) },
                contentDescription = manga.filename,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
                placeholder = rememberAsyncImagePainter(R.drawable.placeholder_image_loading),
                error = rememberAsyncImagePainter(R.drawable.placeholder_image_error)
            )


            Spacer(modifier = Modifier.height(8.dp))

            // Image name
            Text(
                text = manga.filename,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            // Image size (page count)
            Text(
                text = "${manga.pageCount} pages",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
