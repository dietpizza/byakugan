package com.dietpizza.byakugan.components.library

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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
                    // Disable item change/reorder animations to avoid items "following" previous positions
//                    itemAnimator = null
                    clipToPadding = false
                    val padding = (6 * context.resources.displayMetrics.density).toInt()
                    setPadding(padding, padding, padding, padding)
                }
            },
            update = { recyclerView ->
                (recyclerView.adapter as? MangaGridAdapter)?.submitList(mangaList) {
                    recyclerView.scrollToPosition(0)
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
        // Improve stability and reduce UI churn
        setHasStableIds(true)
    }

    class MangaViewHolder(val composeView: ComposeView, val itemState: MutableState<MangaMetadataModel?>) : RecyclerView.ViewHolder(composeView)

    override fun getItemId(position: Int): Long {
        return try {
            getItem(position)?.id?.hashCode()?.toLong() ?: RecyclerView.NO_ID
        } catch (e: IndexOutOfBoundsException) {
            RecyclerView.NO_ID
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaViewHolder {
        val state = mutableStateOf<MangaMetadataModel?>(null)
        val composeView = ComposeView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        val holder = MangaViewHolder(composeView, state)
        // Set content once per ViewHolder and update via state to avoid recreating composition on every bind
        holder.composeView.setContent {
            val manga = holder.itemState.value
            if (manga != null) {
                LibraryGridItem(manga)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: MangaViewHolder, position: Int) {
        val manga = getItem(position)
        holder.itemState.value = manga
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

