package com.dietpizza.byakugan.components.library

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
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.dietpizza.byakugan.R
import com.dietpizza.byakugan.activities.ReaderActivity
import com.dietpizza.byakugan.models.MangaMetadataModel
import java.io.File

@Composable
fun LibraryGridItem(manga: MangaMetadataModel) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = {
            val intent = Intent(context, ReaderActivity::class.java).apply {
                putExtra("MANGA_ID", manga.id)
            }
            context.startActivity(intent)
        }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            AsyncImage(
                model = manga.coverImagePath?.let { File(it) },
                contentDescription = manga.path,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.85f)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
                placeholder = rememberAsyncImagePainter(R.drawable.placeholder_image_loading),
                error = rememberAsyncImagePainter(R.drawable.placeholder_image_error)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = manga.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "${manga.pageCount} pages",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
