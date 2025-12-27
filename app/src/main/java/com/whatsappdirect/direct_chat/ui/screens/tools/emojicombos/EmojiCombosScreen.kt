package com.whatsappdirect.direct_chat.ui.screens.tools.emojicombos

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class EmojiCombo(
    val category: String,
    val emojis: String,
    val description: String
)

val emojiCombos = listOf(
    // Love & Romance
    EmojiCombo("Love", "❤️💕💖💗💓💞💝", "Hearts collection"),
    EmojiCombo("Love", "😍🥰😘💋💑👫💏", "Romantic"),
    EmojiCombo("Love", "🌹💐🌷🌸💮🏵️🌺", "Flowers"),
    EmojiCombo("Love", "💍💒👰🤵💑💏💋", "Wedding"),
    
    // Happy & Celebration
    EmojiCombo("Celebration", "🎉🎊🥳🎈🎁🪅🎀", "Party time"),
    EmojiCombo("Celebration", "🎂🍰🧁🎈🎁🥳🎉", "Birthday"),
    EmojiCombo("Celebration", "🏆🥇🎖️🏅🎗️👑✨", "Winner"),
    EmojiCombo("Celebration", "✨🌟⭐💫🌠🎇🎆", "Sparkles"),
    
    // Greetings
    EmojiCombo("Greetings", "👋😊🤗💖✨🌟🎉", "Hello"),
    EmojiCombo("Greetings", "🌅🌄☀️🌞😊☕🍳", "Good morning"),
    EmojiCombo("Greetings", "🌙✨💫🌟😴💤🛏️", "Good night"),
    EmojiCombo("Greetings", "👍😊🙏💪✨🎉👏", "Thank you"),
    
    // Emotions
    EmojiCombo("Emotions", "😂🤣😆😹💀☠️🤪", "Laughing"),
    EmojiCombo("Emotions", "😢😭💔😿🥺😞😔", "Sad"),
    EmojiCombo("Emotions", "😱😨😰😥😓🫣😬", "Scared"),
    EmojiCombo("Emotions", "😤😠😡🤬👿💢🔥", "Angry"),
    EmojiCombo("Emotions", "🤔🧐🤨🫤😐😑🙄", "Thinking"),
    
    // Food & Drinks
    EmojiCombo("Food", "🍕🍔🍟🌭🥪🌮🌯", "Fast food"),
    EmojiCombo("Food", "🍎🍊🍋🍇🍓🫐🍑", "Fruits"),
    EmojiCombo("Food", "☕🍵🧋🥤🍺🍷🍸", "Drinks"),
    EmojiCombo("Food", "🍰🧁🍩🍪🎂🍫🍬", "Desserts"),
    
    // Nature & Weather
    EmojiCombo("Nature", "🌸🌺🌻🌼🌷🌹💐", "Flowers"),
    EmojiCombo("Nature", "🌲🌳🌴🌵🌿🍀🍃", "Trees & Plants"),
    EmojiCombo("Nature", "☀️🌤️⛅🌥️☁️🌧️⛈️", "Weather"),
    EmojiCombo("Nature", "🌈🌅🌄🌠🌌🌃🏞️", "Sky"),
    
    // Animals
    EmojiCombo("Animals", "🐶🐕🦮🐕‍🦺🐩🐾🦴", "Dogs"),
    EmojiCombo("Animals", "🐱🐈🐈‍⬛😺😸😻🙀", "Cats"),
    EmojiCombo("Animals", "🦋🐛🐝🐞🦗🪲🪳", "Insects"),
    EmojiCombo("Animals", "🐠🐟🐡🦈🐬🐳🐋", "Sea life"),
    
    // Activities
    EmojiCombo("Activities", "⚽🏀🏈⚾🎾🏐🏉", "Sports balls"),
    EmojiCombo("Activities", "🏃‍♂️🚴‍♂️🏊‍♂️🤸‍♂️🧘‍♂️🏋️‍♂️🤾‍♂️", "Sports"),
    EmojiCombo("Activities", "🎮🕹️👾🎲🎯🎰🃏", "Games"),
    EmojiCombo("Activities", "🎵🎶🎤🎧🎸🎹🥁", "Music"),
    
    // Travel
    EmojiCombo("Travel", "✈️🛫🛬🚀🛸🚁🪂", "Flying"),
    EmojiCombo("Travel", "🚗🚕🚙🚌🚎🏎️🚓", "Cars"),
    EmojiCombo("Travel", "🏖️🏝️🏕️⛺🏔️🗻🌋", "Vacation"),
    EmojiCombo("Travel", "🗼🗽🏰🏯⛩️🕌🕍", "Landmarks"),
    
    // Special Occasions
    EmojiCombo("Special", "🎄🎅🤶🦌🎁❄️⛄", "Christmas"),
    EmojiCombo("Special", "🎃👻💀🦇🕷️🕸️🧙", "Halloween"),
    EmojiCombo("Special", "🪔🎆🎇✨🌟💫🙏", "Diwali"),
    EmojiCombo("Special", "🐰🥚🐣🌷🌸🌼🎀", "Easter"),
    
    // Work & Study
    EmojiCombo("Work", "💼👔👩‍💼👨‍💼📊📈💰", "Business"),
    EmojiCombo("Work", "📚📖✏️📝🎓👩‍🎓👨‍🎓", "Study"),
    EmojiCombo("Work", "💻🖥️⌨️🖱️📱💾📀", "Tech"),
    EmojiCombo("Work", "✅☑️✔️👍👏🎯🏆", "Success"),
    
    // Decorative
    EmojiCombo("Decorative", "═══════════════", "Line"),
    EmojiCombo("Decorative", "•°•°•°•°•°•°•°•", "Dots"),
    EmojiCombo("Decorative", "★彡★彡★彡★彡★彡", "Stars"),
    EmojiCombo("Decorative", "▀▄▀▄▀▄▀▄▀▄▀▄▀▄", "Pattern"),
    EmojiCombo("Decorative", "꧁༺ TEXT ༻꧂", "Fancy border"),
    EmojiCombo("Decorative", "✧･ﾟ: *✧･ﾟ:*", "Sparkle border")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiCombosScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emoji Combos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = "Tap any emoji combo to copy it to clipboard",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }
            
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val groupedCombos = emojiCombos.groupBy { it.category }
                
                groupedCombos.forEach { (category, combos) ->
                    item {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    items(combos) { combo ->
                        EmojiComboCard(
                            combo = combo,
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("emoji", combo.emojis))
                                Toast.makeText(context, "Copied: ${combo.description}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmojiComboCard(
    combo: EmojiCombo,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCopy() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = combo.emojis,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = combo.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
