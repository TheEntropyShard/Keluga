/*
 * Keluga - A simple Beluga reader
 * Copyright (C) 2025 TheEntropyShard (https://github.com/TheEntropyShard)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.theentropyshard.keluga.model

import com.google.gson.annotations.SerializedName

data class BelugaAttachment(
    @SerializedName("id")
    val id: String,

    @SerializedName("hash_digest")
    val hashDigest: String,

    @SerializedName("link_title")
    val linkTitle: String,

    @SerializedName("link_url")
    val linkUrl: String,

    @SerializedName("index")
    val index: Int,
)
