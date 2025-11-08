package com.angelcabrera.unabshop

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.angelcabrera.unabshop.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(navController: NavHostController) {
    val db = Firebase.firestore

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }

    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Agregar producto", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del producto") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (nombre.isNotBlank() && descripcion.isNotBlank() && precio.isNotBlank()) {
                    val producto = Producto(
                        nombre = nombre,
                        descripcion = descripcion,
                        precio = precio.toDoubleOrNull() ?: 0.0
                    )

                    // 🔹 Guardar en Firestore
                    db.collection("productos")
                        .add(producto)
                        .addOnSuccessListener {
                            mensaje = "Producto agregado con éxito "
                            // 🔹 Navegar de regreso al HomeScreen
                            navController.popBackStack()
                        }
                        .addOnFailureListener {
                            mensaje = "Error al guardar: ${it.message}"
                        }
                } else {
                    mensaje = "Por favor, completa todos los campos."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9900))
        ) {
            Text("Guardar", color = Color.White)
        }

        Spacer(Modifier.height(16.dp))

        if (mensaje.isNotEmpty()) {
            Text(mensaje, color = Color.Gray)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun AddProductScreenPreview() {
    val navController = rememberNavController()
    AddProductScreen(navController = navController)
}