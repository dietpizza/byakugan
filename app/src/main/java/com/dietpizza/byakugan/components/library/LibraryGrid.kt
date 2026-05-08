package com.dietpizza.byakugan.components.library

import android.content.Intent
import android.util.Log
import android.view.View
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.dietpizza.byakugan.utils.FormatUtils
import kotlinx.coroutines.launch

@Composable
fun LibraryGrid(
    mangaList: List<MangaMetadataModel>?,
    isRefreshing: Boolean,
    onOpenFolderClick: () -> Unit,
    resetScrollProvider: () -> Boolean,
    onResetScroll: () -> Unit,
) {
    val lifecycleScope = rememberCoroutineScope()

    val onCommitCallback: (view: RecyclerView) -> Unit = { view ->
        lifecycleScope.launch {
            if (resetScrollProvider()) {
                view.scrollToPosition(0)
                onResetScroll()
            }
        }
    }

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
            update = { view ->
                (view.adapter as? MangaGridAdapter)?.submitList(mangaList) {
                    onCommitCallback(view)
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

    init {
        setHasStableIds(true)
    }

    class MangaViewHolder(val binding: WidgetMangaCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemId(position: Int): Long {
        return try {
            getItem(position)?.id?.hashCode()?.toLong() ?: RecyclerView.NO_ID
        } catch (_: IndexOutOfBoundsException) {
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
        val progress = manga.lastPage?.div(manga.pageCount.toFloat())?.times(100)
        Log.d(TAG, "${manga.lastPage} Progress: ${manga.pageCount}")

        holder.binding.imageName.text = manga.title
        holder.binding.mangaSize.text =
            "${FormatUtils.formatFileSize(manga.size)} • ${manga.pageCount} Pages"

        if (progress != null && progress > 1) {
            holder.binding.progressLayout.visibility = View.VISIBLE
            if (manga.lastPage < manga.pageCount - 1) {
                holder.binding.progressLabel.text = "In Progress"
                holder.binding.mangaProgress.progress = progress.toInt()
            } else {
                holder.binding.progressLabel.text = "Complete"
                holder.binding.mangaProgress.visibility = View.INVISIBLE
            }
        } else {
            holder.binding.progressLayout.visibility = View.GONE
        }

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

