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

package me.theentropyshard.keluga.ui.view.main

import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.theentropyshard.keluga.model.Author
import coil3.compose.AsyncImage
import me.theentropyshard.keluga.model.Post
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MainView(
    modifier: Modifier = Modifier,
    model: MainViewModel
) {
    val state by model.state.collectAsState()
    val blog by model.blog.collectAsState()

    var instanceUrl by remember { model.instanceUrl }

    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().weight(1f),
                value = instanceUrl,
                onValueChange = { instanceUrl = it },
                placeholder = { Text(text = "Enter instance URL") },
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        model.load(instanceUrl)
                    }
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    model.load(instanceUrl)
                },
                enabled = instanceUrl.trim().isNotEmpty() && state != UiState.Loading
            ) {
                Text(text = "Find")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (state) {
            UiState.Initial -> {

            }

            UiState.Loading -> {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            UiState.Failed -> {
                Text(text = "An error occurred. Please try again.")
            }

            UiState.NotFound -> {
                Text(
                    text = buildAnnotatedString {
                        append("Could not find file ")

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("beluga.json")
                        }

                        append(" at $instanceUrl. Make sure you entered the main page" +
                                " without slash at the end")
                    }
                )
            }

            UiState.Success -> {
                AuthorsView(authors = blog.authors)

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (post in blog.posts) {
                        PostView(author = blog.authors[0], post = post)
                    }
                }
            }
        }
    }
}

@Composable
fun AuthorsView(
    modifier: Modifier = Modifier,
    authors: List<Author>
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(state = rememberScrollState())
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (author in authors) {
            AuthorView(author = author)
        }
    }
}

@Composable
fun AuthorView(
    modifier: Modifier = Modifier,
    author: Author
) {
    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(50)),
            model = author.avatar,
            contentDescription = author.name,
            onError = {
                println("err $it")
            }
        )

        Text(
            text = author.name,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
    }
}

private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

@Composable
fun PostView(
    modifier: Modifier = Modifier,
    author: Author,
    post: Post
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(8.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(50)),
            model = author.avatar,
            contentDescription = author.name,
            onError = {
                println("err $it")
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = author.name)

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = formatter.format(OffsetDateTime.parse(post.datePublished)))
            }

            Spacer(modifier = Modifier.height(8.dp))

            SelectionContainer(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp)
            ) {
                Text(text = post.contextText)
            }
        }
    }
}