package uji.es.intermaps.ViewModel

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import uji.es.intermaps.Exceptions.AccountAlreadyRegistredException
import uji.es.intermaps.Exceptions.NotSuchElementException
import uji.es.intermaps.Exceptions.NotSuchPlaceException
import uji.es.intermaps.Exceptions.SessionNotStartedException
import uji.es.intermaps.Exceptions.UnableToDeletePlaceException
import uji.es.intermaps.Exceptions.UnableToDeleteVehicleException
import uji.es.intermaps.Exceptions.UnregistredUserException
import uji.es.intermaps.Exceptions.VehicleAlreadyExistsException
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.DataBase
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.RouteTypes
import uji.es.intermaps.Model.TransportMethods
import uji.es.intermaps.Model.User
import uji.es.intermaps.Model.Vehicle
import uji.es.intermaps.Model.VehicleFactory
import uji.es.intermaps.Model.VehicleTypes
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseRepository: Repository {
    private val db = DataBase.db
    private val auth = DataBase.auth


    override suspend fun createUser(email: String, pswd: String): User {
        return suspendCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, pswd)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val newUser = User(email, pswd)
                        db.collection("Users").document(email).set(mapOf("email" to email))
                            .addOnSuccessListener { _ ->
                                continuation.resume(newUser) //Funciona como el return de la coroutine
                            }
                            .addOnFailureListener { e ->
                                continuation.resumeWithException(e)
                            }
                    } else {
                        when (val exception = task.exception) {
                            is FirebaseAuthUserCollisionException -> {
                                continuation.resumeWithException(
                                    AccountAlreadyRegistredException("Ya existe una cuenta con este email")
                                )
                            }

                            is FirebaseAuthInvalidCredentialsException -> {
                                continuation.resumeWithException(
                                    IllegalArgumentException("El correo o la contraseña no tienen un formato válido")
                                )
                            }

                            else -> {
                                continuation.resumeWithException(
                                    exception ?: Exception("Error desconocido al crear el usuario.")
                                )
                            }
                        }
                    }
                }
        }
    }



    override suspend fun loginUser(email: String, pswd: String): Boolean {
        return suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, pswd)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(true)
                    } else {
                        val exception = task.exception
                        if (exception is FirebaseAuthInvalidCredentialsException) {
                            continuation.resumeWithException(
                                UnregistredUserException("Usuario o contraseña incorrectos")
                            )
                        } else {
                            continuation.resumeWithException(
                                exception ?: Exception("Error desconocido al iniciar sesión.")
                            )
                        }
                    }
                }
        }
    }

    override suspend fun signOut(): Boolean {
        if (auth.currentUser != null) {
            auth.signOut()
            return true
        } else {
            throw SessionNotStartedException("No hay ninguna sesión iniciada")
        }
    }

    override suspend fun viewUserData(email: String): Boolean {
        return suspendCoroutine { continuation ->
            db.collection("Users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        continuation.resume(true) // El correo existe
                    } else {
                        continuation.resume(false) // El correo no existe
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(
                        "Firestore",
                        "Error al verificar el correo: ${exception.message}",
                        exception
                    )
                    continuation.resumeWithException(exception)
                }
        }
    }

    override suspend fun editUserData(newPassword: String): Boolean {
        val user = auth.currentUser
        if (user == null) {
            Log.e("FirebaseAuth", "No hay un usuario autenticado")
            return false
        }
        var result = false
        // Actualizar la contraseña
        user.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseAuth", "Contraseña cambiada exitosamente")
                    result = true
                } else {
                    Log.e("FirebaseAuth", "Error al cambiar la contraseña", task.exception)
                    result = false
                }
            }.await()

        return result
    }

    override suspend fun updateUserAttribute(attributeName: String, attributeValue: Any) {
        val userEmail = auth.currentUser?.email
            ?: throw IllegalStateException("No hay un usuario autenticado")

        try {
            val userDocument = db.collection("Default").document(userEmail)

            val documentSnapshot = userDocument.get().await()

            if (documentSnapshot.exists()) {
                userDocument.update(attributeName, attributeValue).await()
            } else {
                val newDocumentData = mapOf(attributeName to attributeValue)
                userDocument.set(newDocumentData, SetOptions.merge()).await()
            }
        } catch (e: Exception) {
            throw Exception("Error al actualizar el atributo $attributeName: ${e.message}", e)
        }
    }

    override suspend fun getUserAttribute(attributeName: String): Any? {
        val userEmail = auth.currentUser?.email
            ?: throw IllegalStateException("No hay un usuario autenticado")

        return try {
            val userDocument = db.collection("Default").document(userEmail)
            val documentSnapshot = userDocument.get().await()

            if (documentSnapshot.exists()) {
                documentSnapshot.get(attributeName)
            } else {
                null
            }
        } catch (e: Exception) {
            throw Exception("Error al recuperar el atributo $attributeName: ${e.message}", e)
        }
    }

    override suspend fun deleteUser(email: String, password: String): Boolean {
        return try {
            val user = auth.currentUser
            if (user == null) {
                Log.e("FirebaseAuth", "No hay un usuario autenticado")
                return false
            }

            val collections = listOf("Default", "InterestPlace", "Route", "Vehicle")

            for (collection in collections) {
                val success = deleteDocument(collection, email)
                if (!success) {
                    Log.e("DeleteUser", "Error al eliminar documentos relacionados en la colección: $collection")
                }
            }

            // Buscar el documento del usuario en la colección "Users"
            val querySnapshot = FirebaseFirestore.getInstance()
                .collection("Users")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                Log.e("Firestore", "No se encontró el documento del usuario en la colección 'Users'.")

            }else {
                val documentId = querySnapshot.documents[0].id
                FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(documentId)
                    .delete()
                    .await()

                Log.d("DeleteUser", "Documento del usuario eliminado exitosamente.")
            }

            // Eliminar el usuario de FirebaseAuth
            user.delete().await()
            Log.d("FirebaseAuth", "Usuario eliminado exitosamente.")

            true
        } catch (e: Exception) {
            Log.e("DeleteUser", "Error al eliminar el usuario: ${e.message}", e)
            false
        }
    }

    override suspend fun setAlias(interestPlace: InterestPlace, newAlias: String): Boolean {
        val userEmail = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")
        var result = false
        val documentSnapshot = db.collection("InterestPlace")
            .document(userEmail)
            .get()
            .await()

        if (documentSnapshot.exists()) {
            val interestPlaces =
                documentSnapshot.get("interestPlaces") as? List<Map<String, Any>> ?: emptyList()

            val foundPlace = (interestPlaces.find { place ->
                val coordinate = place["coordinate"] as? Map<String, Double>
                val latitude = coordinate?.get("latitude") ?: 0.0
                val longitude = coordinate?.get("longitude") ?: 0.0


                latitude == interestPlace.coordinate.latitude && longitude == interestPlace.coordinate.longitude
            }?: throw NotSuchPlaceException("Lugar de interés no encontrado")).toMutableMap()


            val updatedInterestPlaces = interestPlaces.map { place ->
                if (place == foundPlace){
                    place.toMutableMap().apply {
                        this["alias"] = newAlias
                    }
                } else {
                    place
                }
            }

            db.collection("InterestPlace")
                .document(userEmail)
                .update("interestPlaces", updatedInterestPlaces)
                .await()
            result = true
        } else {
            result = false
        }
        return result
    }


    override suspend fun createInterestPlace(coordinate: Coordinate, toponym: String, alias: String): InterestPlace {
        val email = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")
        return suspendCoroutine { continuation ->
            val newPlace = mapOf(
                "coordinate" to mapOf(
                    "latitude" to coordinate.latitude,
                    "longitude" to coordinate.longitude
                ),
                "toponym" to toponym,
                "alias" to alias,
                "fav" to false
            )

            val userDocument = db.collection("InterestPlace").document(email)
            //Intenta añadir a interestPlaces el nuevo lugar evitando duplicados con el FieldValue.arrayUnion
            userDocument.update("interestPlaces", FieldValue.arrayUnion(newPlace))
                .addOnSuccessListener {
                    continuation.resume(InterestPlace(coordinate, toponym, alias, false))
                }
                .addOnFailureListener { _ ->
                    // Si falla porque el usuario aún no tiene lugares guardados crea el documento con el atributo interestPlaces y le añade el lugar
                    userDocument.set(mapOf("interestPlaces" to listOf(newPlace)))
                        .addOnSuccessListener {
                            continuation.resume(InterestPlace(coordinate, toponym, alias, false))
                        }
                        .addOnFailureListener { e ->
                            continuation.resumeWithException(e)
                        }
                }
        }
    }


    override suspend fun getInterestPlaceByToponym(toponym: String): InterestPlace{
        val userEmail = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")

        try {
            val documentSnapshot = db.collection("InterestPlace")
                .document(userEmail)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val interestPlaces = documentSnapshot.get("interestPlaces") as? List<Map<String, Any>> ?: emptyList()
                val foundPlace = interestPlaces.find { place ->
                    val actualToponym = place["toponym"] as? String ?: ""
                    toponym.equals(actualToponym)
                } ?: throw NotSuchPlaceException("Lugar de interés no encontrado")

                val lat = (foundPlace["coordinate"] as Map<String, Double>)["latitude"] ?: 0.0
                val long = (foundPlace["coordinate"] as Map<String, Double>)["longitude"] ?: 0.0
                return InterestPlace(
                    coordinate =  Coordinate(lat,long),
                    toponym = foundPlace["toponym"] as? String ?: "",
                    alias = foundPlace["alias"] as? String ?: "",
                    fav = foundPlace["fav"] as? Boolean ?: false
                )
            } else {
                throw Exception("No existe el documento para el usuario: $userEmail")
            }
        } catch (e: Exception) {
            throw NotSuchPlaceException()
        }
    }

    override suspend fun viewInterestPlaceData(coordinate: Coordinate): InterestPlace {
        val userEmail = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")

        try {
            val documentSnapshot = db.collection("InterestPlace")
                .document(userEmail)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val interestPlaces = documentSnapshot.get("interestPlaces") as? List<Map<String, Any>> ?: emptyList()

                val foundPlace = interestPlaces.find { place ->
                    val interestPlaceCoordinate = place["coordinate"] as? Map<String, Double>
                    val latitude = interestPlaceCoordinate?.get("latitude") ?: 0.0
                    val longitude = interestPlaceCoordinate?.get("longitude") ?: 0.0

                    latitude == coordinate.latitude && longitude == coordinate.longitude
                } ?: throw NotSuchPlaceException("Lugar de interés no encontrado")

                // Construir el objeto InterestPlace a partir del mapa encontrado
                return InterestPlace(
                    coordinate = coordinate,
                    toponym = foundPlace["toponym"] as? String ?: "",
                    alias = foundPlace["alias"] as? String ?: "",
                    fav = foundPlace["fav"] as? Boolean ?: false
                )
            } else {
                throw Exception("No existe el documento para el usuario: $userEmail")
            }
        } catch (e: Exception) {
            Log.e("GeneralError", "Ocurrió un error", e)
            throw e
        }
    }


    override suspend fun viewInterestPlaceList(): List<InterestPlace> {
        val userEmail =  auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")

        return try {
            val documentSnapshot = db.collection("InterestPlace")
                .document(userEmail)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                // Extraemos el array interestPlaces del documento
                val interestPlaces = documentSnapshot.get("interestPlaces") as? List<Map<String, Any>> ?: emptyList()

                // Convertimos cada elemento del array a InterestPlace
                interestPlaces.map { place ->
                    InterestPlace(
                        coordinate = Coordinate(
                            latitude = (place["coordinate"] as Map<String, Double>)["latitude"] ?: 0.0,
                            longitude = (place["coordinate"] as Map<String, Double>)["longitude"] ?: 0.0
                        ),
                        toponym = place["toponym"] as String? ?: "",
                        alias = place["alias"] as String? ?: "",
                        fav = place["fav"] as Boolean? ?: false
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()  // En caso de error, retornamos una lista vacía
        }
    }

    override suspend fun deleteInterestPlace(coordinate: Coordinate): Boolean {
        val userEmail = auth.currentUser?.email
            ?: throw IllegalStateException("No hay un usuario autenticado")

        return try {
            val documentSnapshot = db.collection("InterestPlace")
                .document(userEmail)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val interestPlaces = documentSnapshot.get("interestPlaces") as? MutableList<Map<String, Any>>
                    ?: mutableListOf()

                val placeIndex = interestPlaces.indexOfFirst { place ->
                    val placeCoordinate = place["coordinate"] as? Map<String, Double> ?: return@indexOfFirst false
                    val latitude = placeCoordinate["latitude"] ?: return@indexOfFirst false
                    val longitude = placeCoordinate["longitude"] ?: return@indexOfFirst false

                    latitude == coordinate.latitude && longitude == coordinate.longitude
                }

                if (placeIndex == -1) {
                    throw NotSuchPlaceException("Lugar de interés no encontrado")
                }

                //Mira el topónimo del lugar para ver si se usa en alguna ruta e impedir su borrado si es el caso
                val toponym = interestPlaces[placeIndex]["toponym"] as? String
                    ?: throw IllegalStateException("No se ha encontrado el topónimo del lugar")

                if (checkUsedPlace(toponym)){
                    throw UnableToDeletePlaceException("Este lugar está siendo usado en una de las rutas por lo que no se puede borrar")
                }

                interestPlaces.removeAt(placeIndex)

                db.collection("InterestPlace")
                    .document(userEmail)
                    .update("interestPlaces", interestPlaces)
                    .await()

                true
            } else {
                false
            }
        } catch (e: NotSuchPlaceException) {
            Log.e("deleteInterestPlace", "Lugar no encontrado: ${e.message}")
            false
        } catch (e: Exception) {
            Log.e("deleteInterestPlace", "Error eliminando lugar: ${e.message}", e)
            false
        }
    }

    private suspend fun checkUsedPlace(toponym: String): Boolean {
        val userEmail = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")

        val documentSnapshot = db.collection("Route")
            .document(userEmail)
            .get()
            .await()

        if (documentSnapshot.exists()) {
            val routes = documentSnapshot.get("routes") as? MutableList<Map<String, Any>> ?: mutableListOf()

            routes.find { route ->
                val routeOrigin = route["origin"] as? String ?: ""
                val routeDestination = route["destination"] as? String ?: ""

                toponym == routeOrigin || toponym == routeDestination
            }?: return false
        } else {
            return false
        }
        return true
    }

    override suspend fun createVehicle(plate: String, type: String, consumption: Double): Vehicle {
        val email = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")

        return suspendCoroutine { continuation ->
            val userDocument = db.collection("Vehicle").document(email)

            userDocument.get()
                .addOnSuccessListener { documentSnapshot ->
                    val vehicles = documentSnapshot.get("vehicles") as? List<Map<String, Any>> ?: emptyList()

                    // Verificar si ya existe un vehículo con la misma matrícula
                    if (vehicles.any { it["plate"] == plate }) {
                        continuation.resumeWithException(
                            VehicleAlreadyExistsException("Ya existe un vehículo con la matrícula $plate")
                        )
                        return@addOnSuccessListener
                    }

                    // Crea el vehículo usando el Factory Method
                    val vehicle = VehicleFactory.createVehicle(plate, type, consumption, false)

                    // Mapa para almacenar el vehículo en Firebase
                    val newVehicle = mapOf(
                        "plate" to vehicle.plate,
                        "type" to vehicle.type,
                        "consumption" to vehicle.consumption,
                        "fav" to vehicle.fav
                    )

                    // Agregar el nuevo vehículo a la colección
                    userDocument.update("vehicles", FieldValue.arrayUnion(newVehicle))
                        .addOnSuccessListener {
                            continuation.resume(vehicle)
                        }
                        .addOnFailureListener { error ->
                            // Si la actualización falla, intentar crear un nuevo documento
                            userDocument.set(mapOf("vehicles" to listOf(newVehicle)))
                                .addOnSuccessListener {
                                    continuation.resume(vehicle)
                                }
                                .addOnFailureListener { e ->
                                    continuation.resumeWithException(e)
                                }
                        }
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    override suspend fun deleteVehicle(plate: String): Boolean {
        val userEmail = auth.currentUser?.email
            ?: throw IllegalStateException("No hay un usuario autenticado")

        val documentSnapshot = db.collection("Vehicle")
            .document(userEmail)
            .get()
            .await()

        if (documentSnapshot.exists()) {
            val vehicles = documentSnapshot.get("vehicles") as? MutableList<Map<String, Any>>
                ?: mutableListOf()

            val placeIndex = vehicles.indexOfFirst { vehicle ->
                val vehiclePlate = vehicle["plate"] as? String ?: return@indexOfFirst false
                vehiclePlate == plate
            }

            if (placeIndex == -1) {
                throw NotSuchElementException("Vehículo no encontrado")
            }

            //Se mira que no se use el vehículo en una ruta y si lo está no se puede borrar
            if (checkUsedVehicle(plate)){
                throw UnableToDeleteVehicleException("Este vehículo está siendo usado en una de las rutas por lo que no se puede borrar")
            }

            vehicles.removeAt(placeIndex)

            db.collection("Vehicle")
                .document(userEmail)
                .update("vehicles", vehicles)
                .await()

            return true
        } else {
            return false
        }
    }

    private suspend fun checkUsedVehicle(plate: String): Boolean {
        val userEmail = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")

        val documentSnapshot = db.collection("Route")
            .document(userEmail)
            .get()
            .await()

        if (documentSnapshot.exists()) {
            val routes = documentSnapshot.get("routes") as? MutableList<Map<String, Any>> ?: mutableListOf()

            routes.find { route ->
                val routeVehiclePlate = route["vehiclePlate"] as? String ?: ""

                plate == routeVehiclePlate
            }?: return false
        } else {
            return false
        }
        return true
    }

    override suspend fun viewVehicleList(): List<Vehicle> {
        val userEmail =  auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")

        return try {
            val documentSnapshot = db.collection("Vehicle")
                .document(userEmail)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                // Extraemos el array vehicles del documento
                val vehicles = documentSnapshot.get("vehicles") as? List<Map<String, Any>> ?: emptyList()

                // Convertimos cada elemento del array a Vehicle usando la fábrica
                vehicles.map { vehicle ->
                    VehicleFactory.createVehicle(
                        vehicle["plate"] as String? ?: "",
                        vehicle["type"] as String? ?: "",
                        (vehicle["consumption"] as Number? ?: 0.0).toDouble(),
                        vehicle["fav"] as Boolean? ?: false
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()  // En caso de error, retornamos una lista vacía
        }
    }

    override suspend fun viewVehicleData(plate: String): Vehicle {
        val userEmail = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")

        try {
            val documentSnapshot = db.collection("Vehicle")
                .document(userEmail)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val vehicleList = documentSnapshot.get("vehicles") as? List<Map<String, Any>> ?: emptyList()

                val foundVehicle = vehicleList.find { vehicle ->
                    val foundVehiclePlate = vehicle["plate"] as? String ?: ""

                    plate == foundVehiclePlate
                } ?: throw NotSuchElementException("No se ha encontrado el vehiculo")

                return VehicleFactory.createVehicle(
                    foundVehicle["plate"] as String? ?: "",
                    foundVehicle["type"] as String? ?: "",
                    (foundVehicle["consumption"] as Number? ?: 0.0).toDouble(),
                    foundVehicle["fav"] as Boolean? ?: false
                )

            } else {
                throw Exception("No existe el documento para el usuario: $userEmail")
            }
        } catch (e: Exception) {
            Log.e("GeneralError", "Ocurrió un error", e)
            throw e
        }
    }

    override suspend fun editVehicleData(plate: String, newType: String, newConsumption: Double): Boolean {
        val userEmail = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")

        var result = false
        val documentSnapshot = db.collection("Vehicle")
            .document(userEmail)
            .get()
            .await()

        if (documentSnapshot.exists()) {
            val vehicleList =
                documentSnapshot.get("vehicles") as? List<Map<String, Any>> ?: emptyList()

            val foundVehicle = (vehicleList.find { vehicle ->
                val foundVehiclePlate = vehicle["plate"] as? String ?: ""

                plate == foundVehiclePlate
            }?: throw NotSuchElementException("Vehiculo no encontrado")).toMutableMap()

            val updatedVehicle = vehicleList.map { vehicle ->
                if (vehicle == foundVehicle){
                    vehicle.toMutableMap().apply {
                        this["type"] = newType
                        this["consumption"] = newConsumption
                    }
                } else {
                    vehicle
                }
            }

            db.collection("Vehicle")
                .document(userEmail)
                .update("vehicles", updatedVehicle)
                .await()
            result = true

        } else {
            result = false
        }
        return result
    }

    override suspend fun saveRouteToDatabase(route: Route) {
        val userEmail = auth.currentUser?.email
            ?: throw IllegalStateException("No hay un usuario autenticado")

        try {
            val userDocument = db.collection("Route").document(userEmail)

            val documentSnapshot = db.collection("InterestPlace")
                .document(userEmail)
                .get()
                .await()

            val interestPlaces = documentSnapshot.get("interestPlaces") as? List<Map<String, Any>>
                ?: throw NotSuchPlaceException("El usuario no tiene lugares de interés registrados.")

            interestPlaces.find { it["toponym"] == route.origin }
                ?: throw NotSuchPlaceException("El origen no es un lugar de interés.")
            interestPlaces.find { it["toponym"] == route.destination }
                ?: throw NotSuchPlaceException("El destino no es un lugar de interés.")

            val newRoute = mapOf(
                "origin" to route.origin,
                "destination" to route.destination,
                "transportMethod" to route.transportMethod,
                "route" to route.route.take(2),
                "distance" to route.distance,
                "duration" to route.duration,
                "cost" to route.cost,
                "routeType" to route.routeType,
                "fav" to false,
                "vehiclePlate" to route.vehiclePlate
            )
            val documentData = userDocument.get().await()
            if (documentData.exists()) {
                userDocument.update("routes", FieldValue.arrayUnion(newRoute)).await()
            } else {
                userDocument.set(mapOf("routes" to listOf(newRoute))).await()
            }

            Log.d("saveRouteToDatabase", "Ruta guardada exitosamente.")
        } catch (e: NotSuchPlaceException) {
            Log.e("saveRouteToDatabase", "Error en los lugares de interés: ${e.message}")
            throw e
        } catch (e: Exception) {
            Log.e("saveRouteToDatabase", "Error desconocido: ${e.message}", e)
            throw e
        }
    }



    override suspend fun getAverageFuelPrices(): List<Double> {
        try {
            val userDocument = db.collection("FuelPrices").document("mediaPrecios").get().await()

            if (userDocument.exists()) {
                val gasolina95 = userDocument.getDouble("gasolina95")
                val gasoilA = userDocument.getDouble("gasoleoA")

                if (gasolina95 != null && gasoilA != null) {
                    return listOf(gasolina95, gasoilA)
                } else {
                    return listOf(0.0)
                }
            } else {
                return listOf(-1.0)
            }
        } catch (e: Exception) {
            Log.e("FuelPrices", "Error al obtener los precios de combustible: ${e.message}")
            return listOf(0.0)
        }
    }

    //Ya no se usa pero si lo comento indica que no se implementa un método de la interfaz que implementa
    override suspend fun getElectricPrice(): Double {
        try {
            val userDocument = db.collection("ElectricityPrices").document("precios").get().await()
            if (userDocument.exists()) {
                val precioLuz = userDocument.getDouble("precioLuz")
                if (precioLuz != null ) {
                    return precioLuz
                } else { return 0.0 }
            } else { return -1.0 }

        } catch (e: Exception) {
            Log.e("ElectricityPrices", "Error al obtener los precios de la luz: ${e.message}")
            return 0.0
        }
    }

    override suspend fun getVehicleTypeAndConsump(route: Route): Pair<VehicleTypes, Double> {
        val userEmail = auth.currentUser?.email
            ?: throw IllegalStateException("No hay un usuario autenticado")

        return try {
            val documentSnapshot = db.collection("Vehicle")
                .document(userEmail)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val vehicles = documentSnapshot.get("vehicles") as? List<Map<String, Any>>
                    ?: throw IllegalArgumentException("No se encontró el campo 'vehicles' en el documento")

                val vehicle = vehicles.find { it["plate"] == route.vehiclePlate }
                    ?: throw IllegalArgumentException("No se encontró un vehículo con la matrícula '${route.vehiclePlate}'")

                val typeString = vehicle["type"] as? String
                    ?: throw IllegalArgumentException("No se encontró el campo 'type' para el vehículo '${route.vehiclePlate}'")

                val vehicleType = VehicleTypes.values().find { it.type == typeString }
                    ?: throw IllegalArgumentException("El tipo de vehículo '$typeString' no es válido")

                val consumption = vehicle["consumption"] as? Double
                    ?: throw IllegalArgumentException("No se encontró el campo 'consumption' para el vehículo '${route.vehiclePlate}'")

                Pair(vehicleType, consumption)
            } else {
                throw IllegalArgumentException("El documento del vehículo no existe")
            }
        } catch (e: Exception) {
            Log.e("VehicleService", "Error al obtener el tipo y consumo del vehículo: ${e.message}")
            throw e
        }
    }

    override suspend fun viewRouteList(): List<Route> {
        val userEmail = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")
        return try {
            val documentSnapshot = db.collection("Route")
                .document(userEmail)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                // Extraemos el array "routes" del documento
                val routes = documentSnapshot.get("routes") as? List<Map<String, Any>> ?: emptyList()

                // Convertimos cada elemento del array a Route
                routes.mapNotNull { route ->
                    Route(
                        origin = route["origin"] as String,
                        destination = route["destination"] as String,
                        transportMethod = TransportMethods.valueOf(route["transportMethod"] as String),
                        route = (route["route"] as List<Map<String, Any>>).mapNotNull { coordinate ->
                            val latitude = coordinate["latitude"] as Double
                            val longitude = coordinate["longitude"] as Double
                            Coordinate(latitude, longitude)
                        },
                        distance = route["distance"] as Double ,
                        duration = route["duration"] as String,
                        cost = route["cost"] as Double ,
                        routeType = RouteTypes.valueOf(route["routeType"] as String),
                        fav = route["fav"] as Boolean,
                        vehiclePlate = route["vehiclePlate"] as String
                    )
                }
            } else {
                Log.e("viewRouteList", "El documento del usuario no existe en la colección 'Route'")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("viewRouteList", "Error al obtener rutas desde Firebase: ${e.message}")
            emptyList()
        }
    }

    override suspend fun deleteRoute(route: Route): Boolean {
        val userEmail = auth.currentUser?.email
            ?: throw IllegalStateException("No hay un usuario autenticado")

        val documentSnapshot = db.collection("Route")
            .document(userEmail)
            .get()
            .await()

        if (documentSnapshot.exists()) {
            val routes = documentSnapshot.get("routes") as? MutableList<Map<String, Any>>
                ?: mutableListOf()

            val index = routes.indexOfFirst { vehicle ->
                val routeOrigin = vehicle["origin"] as? String ?: return@indexOfFirst false
                val routeDestination = vehicle["destination"] as? String ?: return@indexOfFirst false
                val routeTransportMethod = vehicle["transportMethod"] as? String ?: return@indexOfFirst false
                val routeVehiclePlate = vehicle["vehiclePlate"] as? String ?: return@indexOfFirst false
                (routeOrigin == route.origin && routeDestination == route.destination &&
                        routeTransportMethod == route.transportMethod.toString() && routeVehiclePlate == route.vehiclePlate)
            }

            if (index == -1) {
                throw NotSuchElementException("Ruta no encontrada")
            } //La excepción no tiene que ser cazada en este metodo sino en el servicio

            routes.removeAt(index)

            db.collection("Route")
                .document(userEmail)
                .update("routes", routes)
                .await()

            return true
        } else {
            return false
        }
    }
    override suspend fun setFavInterestPlace(placeCoordinate: Coordinate): Boolean {
        val userEmail = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")
        var result = false
        val documentSnapshot = db.collection("InterestPlace")
            .document(userEmail)
            .get()
            .await()

        if (documentSnapshot.exists()) {
            val interestPlaces =
                documentSnapshot.get("interestPlaces") as? List<Map<String, Any>> ?: emptyList()

            val foundPlace = (interestPlaces.find { place ->
                val coordinate = place["coordinate"] as? Map<String, Double>
                val latitude = coordinate?.get("latitude") ?: 0.0
                val longitude = coordinate?.get("longitude") ?: 0.0


                latitude == placeCoordinate.latitude && longitude == placeCoordinate.longitude
            }?: throw NotSuchPlaceException("Lugar de interés no encontrado")).toMutableMap()


            val updatedInterestPlaces = interestPlaces.map { place ->
                if (place == foundPlace){
                    place.toMutableMap().apply {
                        this["fav"] = true
                    }
                } else {
                    place
                }
            }

            db.collection("InterestPlace")
                .document(userEmail)
                .update("interestPlaces", updatedInterestPlaces)
                .await()
            result = true
        } else {
            result = false
        }
        return result
    }

    override suspend fun setFavVehicle(plate: String): Boolean {
        val userEmail = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")

        var result = false
        val documentSnapshot = db.collection("Vehicle")
            .document(userEmail)
            .get()
            .await()

        if (documentSnapshot.exists()) {
            val vehicleList =
                documentSnapshot.get("vehicles") as? List<Map<String, Any>> ?: emptyList()

            val foundVehicle = (vehicleList.find { vehicle ->
                val foundVehiclePlate = vehicle["plate"] as? String ?: ""

                plate == foundVehiclePlate
            }?: throw NotSuchElementException("Vehiculo no encontrado")).toMutableMap()

            val updatedVehicle = vehicleList.map { vehicle ->
                if (vehicle == foundVehicle){
                    vehicle.toMutableMap().apply {
                        this["fav"] = true
                    }
                } else {
                    vehicle
                }
            }

            db.collection("Vehicle")
                .document(userEmail)
                .update("vehicles", updatedVehicle)
                .await()
            result = true

        } else {
            result = false
        }
        return result
    }

    override suspend fun deleteFavInterestPlace(placeCoordinate: Coordinate): Boolean {
        val userEmail = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")
        var result = false
        val documentSnapshot = db.collection("InterestPlace")
            .document(userEmail)
            .get()
            .await()

        if (documentSnapshot.exists()) {
            val interestPlaces =
                documentSnapshot.get("interestPlaces") as? List<Map<String, Any>> ?: emptyList()

            val foundPlace = (interestPlaces.find { place ->
                val coordinate = place["coordinate"] as? Map<String, Double>
                val latitude = coordinate?.get("latitude") ?: 0.0
                val longitude = coordinate?.get("longitude") ?: 0.0


                latitude == placeCoordinate.latitude && longitude == placeCoordinate.longitude
            }?: throw NotSuchPlaceException("Lugar de interés no encontrado")).toMutableMap()


            val updatedInterestPlaces = interestPlaces.map { place ->
                if (place == foundPlace){
                    place.toMutableMap().apply {
                        this["fav"] = false
                    }
                } else {
                    place
                }
            }

            db.collection("InterestPlace")
                .document(userEmail)
                .update("interestPlaces", updatedInterestPlaces)
                .await()
            result = true
        } else {
            result = false
        }
        return result
    }

    override suspend fun deleteFavVehicle(plate: String): Boolean {
        val userEmail = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")

        var result = false
        val documentSnapshot = db.collection("Vehicle")
            .document(userEmail)
            .get()
            .await()

        if (documentSnapshot.exists()) {
            val vehicleList =
                documentSnapshot.get("vehicles") as? List<Map<String, Any>> ?: emptyList()

            val foundVehicle = (vehicleList.find { vehicle ->
                val foundVehiclePlate = vehicle["plate"] as? String ?: ""

                plate == foundVehiclePlate
            }?: throw NotSuchElementException("Vehiculo no encontrado")).toMutableMap()

            val updatedVehicle = vehicleList.map { vehicle ->
                if (vehicle == foundVehicle){
                    vehicle.toMutableMap().apply {
                        this["fav"] = false
                    }
                } else {
                    vehicle
                }
            }

            db.collection("Vehicle")
                .document(userEmail)
                .update("vehicles", updatedVehicle)
                .await()
            result = true

        } else {
            result = false
        }
        return result
    }

    private suspend fun deleteDocument(collection: String, documentId: String): Boolean {
        return try {
            val documentRef = FirebaseFirestore.getInstance()
                .collection(collection)
                .document(documentId) // Acceder directamente al documento por su ID

            val documentSnapshot = documentRef.get().await()

            if (!documentSnapshot.exists()) {
                Log.d("DeleteUser", "No se encontró un documento con ID $documentId en la colección $collection")
                return true // Consideramos que no es un error si no existe
            }

            documentRef.delete().await() // Eliminar el documento
            Log.d("DeleteUser", "Documento con ID $documentId eliminado de la colección $collection")
            true
        } catch (e: Exception) {
            Log.e("DeleteUser", "Error al eliminar documento con ID $documentId en la colección $collection: ${e.message}", e)
            false
        }
    }

    override suspend fun setFavRoute(origin: String, destination: String, transportMethod: TransportMethods, routeType: RouteTypes, vehiclePlate: String): Boolean {
        val userEmail = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")
        var result = false

        val documentSnapshot = db.collection("Route")
            .document(userEmail)
            .get()
            .await()

        if (documentSnapshot.exists()) {
            val routes = documentSnapshot.get("routes") as? MutableList<Map<String, Any>> ?: mutableListOf()

            val foundRoute = (routes.find { route ->
                val routeOrigin = route["origin"] as? String ?: ""
                val routeDestination = route["destination"] as? String ?: ""
                val routeTransportMethod = route["transportMethod"] as? String ?: ""
                val routeTypeFound = route["routeType"] as? String ?: ""
                val routeVehiclePlate = route["vehiclePlate"] as? String ?: ""

                origin == routeOrigin && destination == routeDestination &&
                        transportMethod.toString() == routeTransportMethod &&
                        routeType.toString() == routeTypeFound && vehiclePlate == routeVehiclePlate
            }?: throw NotSuchElementException("Ruta no encontrada")).toMutableMap()

            val updatedRoute = routes.map { route ->
                if (route == foundRoute){
                    route.toMutableMap().apply {
                        this["fav"] = true
                    }
                } else {
                    route
                }
            }

            db.collection("Route")
                .document(userEmail)
                .update("routes", updatedRoute)
                .await()
            result = true

        } else {
            result = false
        }
        return result
    }

    override suspend fun deleteFavRoute(origin: String, destination: String, transportMethod: TransportMethods, routeType: RouteTypes, vehiclePlate: String): Boolean {
        val userEmail = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")
        var result = false

        val documentSnapshot = db.collection("Route")
            .document(userEmail)
            .get()
            .await()

        if (documentSnapshot.exists()) {
            val routes = documentSnapshot.get("routes") as? MutableList<Map<String, Any>> ?: mutableListOf()

            val foundRoute = (routes.find { route ->
                val routeOrigin = route["origin"] as? String ?: ""
                val routeDestination = route["destination"] as? String ?: ""
                val routeTransportMethod = route["transportMethod"] as? String ?: ""
                val routeTypeFound = route["routeType"] as? String ?: ""
                val routeVehiclePlate = route["vehiclePlate"] as? String ?: ""

                origin == routeOrigin && destination == routeDestination &&
                        transportMethod.toString() == routeTransportMethod &&
                        routeType.toString() == routeTypeFound && vehiclePlate == routeVehiclePlate
            }?: throw NotSuchElementException("Ruta no encontrada")).toMutableMap()

            val updatedRoute = routes.map { route ->
                if (route == foundRoute){
                    route.toMutableMap().apply {
                        this["fav"] = false
                    }
                } else {
                    route
                }
            }

            db.collection("Route")
                .document(userEmail)
                .update("routes", updatedRoute)
                .await()
            result = true

        } else {
            result = false
        }
        return result
    }

}


