package com.example.naturehunter.adapter

import android.net.Uri
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.naturehunter.R
import com.example.naturehunter.constant.C
import com.example.naturehunter.constant.Rank
import com.example.naturehunter.constant.Type
import com.example.naturehunter.data.model.Item
import com.example.naturehunter.model.TypeItem

@BindingAdapter("imageUri")
fun setImageUri(imageView: ImageView, imageUri: Uri) {
    imageView.setImageURI(imageUri)
}

@BindingAdapter("huntCount")
fun setHuntCount(textView: TextView, huntCount: Int) {
    textView.text = huntCount.toString()
}

@BindingAdapter("typePoints")
fun setTypePoints(textView: TextView, points: Int) {
    textView.text = points.toString()
}

@BindingAdapter("itemListData")
fun bindItemListRecyclerView(recyclerView: RecyclerView, items: List<Item>?) {
    val adapter = recyclerView.adapter as ItemListAdapter
    adapter.submitList(items)
}

@BindingAdapter("typeImageSource")
fun setTypeImage(imageView: ImageView, type: Type) {
    val context = imageView.context
    imageView.setImageResource(
        context.resources.getIdentifier(
            "ic_${type.name.lowercase()}",
            "drawable",
            context.packageName
        )
    )
}

@BindingAdapter("multiplierProgress")
fun setMultiplierProgress(textView: TextView, typeItem: TypeItem) {
    val context = textView.context
    if (typeItem.multiplier == C.MAXIMUM_MULTIPLIER) {
        textView.text = context.resources.getString(R.string.maximum_multiplier)
    } else {
        textView.text = context.resources.getString(
            R.string.progress_percentage_multiplier,
            typeItem.multiplierProgress
        )
    }
}

@BindingAdapter("rankProgressText")
fun setRankProgressText(textView: TextView, rankProgress: Int) {
    val context = textView.context
    if (rankProgress >= C.MAXIMUM_PERCENTAGE) {
        textView.text = context.resources.getString(R.string.maximum_rank)
    } else {
        textView.text = context.resources.getString(R.string.progress_percentage, rankProgress)
    }
}

@BindingAdapter("itemUri")
fun setItemUri(imageView: ImageView, item: Item?) {
    item?.let {
        imageView.setImageURI(it.itemUri.toUri())
    }
}

@BindingAdapter("multiplierProgressBar")
fun setMultiplierProgressBar(progressBar: ProgressBar, typeItem: TypeItem) {
    if (typeItem.multiplier == C.MAXIMUM_MULTIPLIER) {
        progressBar.progress = progressBar.max
    } else {
        progressBar.progress = typeItem.multiplierProgress
    }
}

@BindingAdapter("huntReward")
fun setHuntReward(textView: TextView, typeItem: TypeItem) {
    val context = textView.context
    val reward = typeItem.type.reward
    val multiplier = typeItem.multiplier
    textView.text = context.resources.getString(R.string.hunt_reward, reward * multiplier)
}

@BindingAdapter("rankName")
fun setRankName(textView: TextView, rank: Int) {
    textView.text = Rank.values()[rank - 1].name.lowercase()
        .replaceFirstChar { char -> char.uppercase() }
}

@BindingAdapter("rankImageSource")
fun setRankImageSource(imageView: ImageView, rank: Int) {
    val context = imageView.context
    val rankName = Rank.values()[rank - 1].name
    imageView.setImageResource(
        context.resources.getIdentifier(
            if (rankName == Rank.SPIDER.name) "ic_${rankName.lowercase()}_long" else "ic_${rankName.lowercase()}",
            "drawable",
            context.packageName
        )
    )
}