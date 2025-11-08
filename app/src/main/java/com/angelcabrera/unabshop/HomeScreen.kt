package com.angelcabrera.unabshop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.angelcabrera.unabshop.model.Producto
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController? = null, // ← opcional para que no cause error
    onAddClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val db = Firebase.firestore
    var productos by remember { mutableStateOf(listOf<Producto>()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val isInPreview = LocalInspectionMode.current

    // 🔹 Si NO está en modo preview, escucha Firebase
    if (!isInPreview) {
        DisposableEffect(Unit) {
            val registration: ListenerRegistration = db.collection("productos")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        errorMsg = error.message
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        productos = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(Producto::class.java)?.copy(id = doc.id)
                        }
                    } else {
                        productos = emptyList()
                    }
                }
            onDispose { registration.remove() }
        }
    } else {
        // 🔹 Datos falsos para que se vea en el Preview
        productos = listOf(
            Producto("1", "Camiseta UNAB", "Camiseta oficial de la universidad", 45000.0),
            Producto("2", "Gorra UNAB", "Gorra ajustable con logo UNAB", 25000.0),
            Producto("3", "Botella", "Botella térmica con logo UNAB", 32000.0)
        )
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "UnabShop",
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    )
                },
                actions = {
                    IconButton(onClick = { /* Notificaciones */ }) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notificaciones")
                    }
                    IconButton(onClick = { /* Carrito */ }) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Salir")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFFFF9900),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController?.navigate("addProduct") }, // <- CORRECCIÓN
                containerColor = Color(0xFFFF9900),
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar producto")
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        when {
            errorMsg != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: $errorMsg", color = Color.Red)
                }
            }

            productos.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay productos aún", color = Color.Gray, fontSize = 18.sp)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(12.dp)
                ) {
                    items(productos) { producto ->
                        ProductoItem(
                            producto = producto,
                            onDelete = { id ->
                                if (id.isNotBlank()) {
                                    db.collection("productos").document(id)
                                        .delete()
                                        .addOnFailureListener { e -> errorMsg = e.message }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoItem(
    producto: Producto,
    onDelete: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(producto.descripcion, fontSize = 14.sp, color = Color.Gray)
                Text(
                    "$${producto.precio}",
                    fontSize = 16.sp,
                    color = Color(0xFFFF9900),
                    fontWeight = FontWeight.Medium
                )
            }
            IconButton(onClick = { producto.id?.let(onDelete) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color.Red)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}