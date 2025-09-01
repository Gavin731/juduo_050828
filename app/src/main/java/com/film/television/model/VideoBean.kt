package com.film.television.model

import android.os.Parcel
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.google.gson.annotations.SerializedName

data class VideoBean(
    @SerializedName("title") val title: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("genre") val genre: String?,
    @SerializedName("playUrl") val playUrl: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("releaseYear") val releaseYear: String?,
    @SerializedName("rating") val rating: String?,
    @SerializedName("actors") val actors: String?,
    @SerializedName("introduction") val introduction: String?,
    @SerializedName("region") val region: String?
) : Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(category)
        dest.writeString(genre)
        dest.writeString(playUrl)
        dest.writeString(imageUrl)
        dest.writeString(releaseYear)
        dest.writeString(rating)
        dest.writeString(actors)
        dest.writeString(introduction)
        dest.writeString(region)
    }

    companion object CREATOR : DiffUtil.ItemCallback<VideoBean>(), Parcelable.Creator<VideoBean> {
        override fun areItemsTheSame(
            oldItem: VideoBean,
            newItem: VideoBean
        ): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(
            oldItem: VideoBean,
            newItem: VideoBean
        ): Boolean {
            return oldItem.imageUrl == newItem.imageUrl && oldItem.rating == newItem.rating
        }

        override fun createFromParcel(source: Parcel): VideoBean {
            return VideoBean(
                source.readString(),
                source.readString(),
                source.readString(),
                source.readString(),
                source.readString(),
                source.readString(),
                source.readString(),
                source.readString(),
                source.readString(),
                source.readString()
            )
        }

        override fun newArray(size: Int): Array<out VideoBean?> {
            return Array<VideoBean?>(size) { null }
        }

    }
}
