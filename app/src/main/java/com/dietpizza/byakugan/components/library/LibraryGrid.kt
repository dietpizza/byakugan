package com.dietpizza.byakugan.components.library

import android.content.Intent
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dietpizza.byakugan.R
import com.dietpizza.byakugan.activities.ReaderActivity
import com.dietpizza.byakugan.databinding.WidgetMangaCardBinding
import com.dietpizza.byakugan.models.MangaMetadataModel

@Composable
fun LibraryGrid(
    mangaList: List<MangaMetadataModel>?,
    isRefreshing: Boolean,
    onOpenFolderClick: () -> Unit
) {
    if (mangaList != null) {
        if (mangaList.isEmpty() && !isRefreshing) {
            return LibraryEmpty(onOpenFolderClick)
        }
        AndroidView(
            factory = { context ->
                RecyclerView(context).apply {
                    layoutManager = GridLayoutManager(context, 2)
                    adapter = MangaGridAdapter()
                    clipToPadding = false
                    val padding = (6 * context.resources.displayMetrics.density).toInt()
                    setPadding(padding, padding, padding, padding)
                }
            },
            update = { recyclerView ->
                (recyclerView.adapter as? MangaGridAdapter)?.submitList(mangaList) {
                    recyclerView.smoothScrollToPosition(5)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LibraryEmpty(onOpenFolderClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Lonely here, it is",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Select a folder, you must",
            style = MaterialTheme.typography.bodyMediumEmphasized
        )
        Spacer(modifier = Modifier.height(16.dp))
        FilledTonalButton(
            onClick = onOpenFolderClick,
            modifier = Modifier.fillMaxWidth(0.5f),
            shapes = ButtonDefaults.shapes()
        ) {
            Text(text = "Select Folder", style = MaterialTheme.typography.labelMedium)
        }
    }
}

class MangaGridAdapter :
    ListAdapter<MangaMetadataModel, MangaGridAdapter.MangaViewHolder>(MangaDiffCallback()) {

//    init {
//        setHasStableIds(true)
//    }

    class MangaViewHolder(val binding: WidgetMangaCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemId(position: Int): Long {
        return try {
            getItem(position)?.id?.hashCode()?.toLong() ?: RecyclerView.NO_ID
        } catch (e: IndexOutOfBoundsException) {
            RecyclerView.NO_ID
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaViewHolder {
        val binding = WidgetMangaCardBinding.inflate(
            android.view.LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MangaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MangaViewHolder, position: Int) {
        val manga = getItem(position)
        val file = manga.coverImagePath?.let { java.io.File(it) }

        holder.binding.imageName.text = manga.title
        holder.binding.imageSize.text = "${manga.pageCount} pages"

        holder.binding.imageView.load(file) {
            placeholder(R.drawable.placeholder_image_loading)
            error(R.drawable.placeholder_image_error)
            crossfade(true)
        }

        holder.binding.root.setOnClickListener {
            val ctx = holder.itemView.context
            val intent = Intent(ctx, ReaderActivity::class.java).apply {
                putExtra("MANGA_ID", manga.id)
            }
            ctx.startActivity(intent)
        }
    }

    class MangaDiffCallback : DiffUtil.ItemCallback<MangaMetadataModel>() {
        override fun areItemsTheSame(
            oldItem: MangaMetadataModel,
            newItem: MangaMetadataModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MangaMetadataModel,
            newItem: MangaMetadataModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}

