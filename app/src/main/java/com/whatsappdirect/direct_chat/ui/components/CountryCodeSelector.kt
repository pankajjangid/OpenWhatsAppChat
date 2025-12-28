package com.whatsappdirect.direct_cha.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Country(
    val name: String,
    val code: String,
    val dialCode: String,
    val flag: String
)

val popularCountries = listOf(
    Country("India", "IN", "91", "🇮🇳"),
    Country("United States", "US", "1", "🇺🇸"),
    Country("United Kingdom", "GB", "44", "🇬🇧"),
    Country("Canada", "CA", "1", "🇨🇦"),
    Country("Australia", "AU", "61", "🇦🇺"),
    Country("Germany", "DE", "49", "🇩🇪"),
    Country("France", "FR", "33", "🇫🇷"),
    Country("Brazil", "BR", "55", "🇧🇷"),
    Country("Japan", "JP", "81", "🇯🇵"),
    Country("China", "CN", "86", "🇨🇳"),
    Country("South Korea", "KR", "82", "🇰🇷"),
    Country("Singapore", "SG", "65", "🇸🇬"),
    Country("UAE", "AE", "971", "🇦🇪"),
    Country("Saudi Arabia", "SA", "966", "🇸🇦"),
    Country("South Africa", "ZA", "27", "🇿🇦"),
    Country("Mexico", "MX", "52", "🇲🇽"),
    Country("Italy", "IT", "39", "🇮🇹"),
    Country("Spain", "ES", "34", "🇪🇸"),
    Country("Netherlands", "NL", "31", "🇳🇱"),
    Country("Russia", "RU", "7", "🇷🇺"),
    Country("Indonesia", "ID", "62", "🇮🇩"),
    Country("Malaysia", "MY", "60", "🇲🇾"),
    Country("Thailand", "TH", "66", "🇹🇭"),
    Country("Philippines", "PH", "63", "🇵🇭"),
    Country("Vietnam", "VN", "84", "🇻🇳"),
    Country("Pakistan", "PK", "92", "🇵🇰"),
    Country("Bangladesh", "BD", "880", "🇧🇩"),
    Country("Nigeria", "NG", "234", "🇳🇬"),
    Country("Egypt", "EG", "20", "🇪🇬"),
    Country("Turkey", "TR", "90", "🇹🇷")
)

@Composable
fun CountryCodeSelector(
    selectedCode: String,
    onCodeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    val selectedCountry = popularCountries.find { it.dialCode == selectedCode }
        ?: popularCountries.first()
    
    Card(
        modifier = modifier.clickable { showDialog = true }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedCountry.flag,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "+${selectedCountry.dialCode}",
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Select country"
            )
        }
    }
    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Select Country") },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp)
                ) {
                    val filteredCountries = if (searchQuery.isEmpty()) {
                        popularCountries
                    } else {
                        popularCountries.filter {
                            it.name.contains(searchQuery, ignoreCase = true) ||
                            it.dialCode.contains(searchQuery)
                        }
                    }
                    
                    items(filteredCountries) { country ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onCodeSelected(country.dialCode)
                                    showDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = country.flag,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = country.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "+${country.dialCode}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
