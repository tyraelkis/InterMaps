package uji.es.intermaps.View.navBar

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun NavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf("home", "userDataScreen")

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen,
                onClick = { onNavigate(screen) },
                icon = { /* Puedes agregar iconos aquí */ },
                label = { Text(screen.capitalize()) }
            )
        }
    }
}