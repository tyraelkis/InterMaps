package uji.es.intermaps.ViewModel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import uji.es.intermaps.Model.CachePrecioLuz
import uji.es.intermaps.Model.ConsultorPreciLuz
import java.util.Calendar
import java.util.concurrent.TimeUnit
import uji.es.intermaps.ViewModel.FirebaseRepository


class FuelPriceWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    val routeService = RouteService(FirebaseRepository(), CachePrecioLuz(ConsultorPreciLuz()))
    override suspend fun doWork(): Result {
        return try {
            routeService.putFuelCostAverage()
            Result.success()
        } catch (e: Exception) {
            Log.e("FuelPriceWorker", "Error al actualizar los precios: ${e.message}")
            Result.failure()
        }
    }
}

/* Ya no se usa
class ElectricPriceWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    val routeService = RouteService(FirebaseRepository(), CachePrecioLuz(ConsultorPreciLuz()))
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {
            routeService.putElectricityCost()
            Result.success()
        } catch (e: Exception) {
            Log.e("ElectricPriceWorker", "Error al actualizar los precios: ${e.message}")
            Result.failure()
        }
    }
}
*/

fun scheduleFuelPriceUpdate() {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 10)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)

    val initialDelay = calendar.timeInMillis - System.currentTimeMillis()

    val delay = if (initialDelay > 0) initialDelay else initialDelay + TimeUnit.DAYS.toMillis(1)

    val workRequest = PeriodicWorkRequestBuilder<FuelPriceWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance().enqueue(workRequest)
}

/* Ya no se usa
fun scheduleElectricPriceUpdate() {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)

    val initialDelay = calendar.timeInMillis - System.currentTimeMillis()

    val delay = if (initialDelay > 0) initialDelay else initialDelay + TimeUnit.HOURS.toMillis(1)

    val workRequest = PeriodicWorkRequestBuilder<ElectricPriceWorker>(1, TimeUnit.HOURS)
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance().enqueue(workRequest)
}
*/