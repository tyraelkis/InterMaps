package uji.es.intermaps.Model

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.crashlytics.internal.Logger.TAG
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object DataBase {
    @SuppressLint("StaticFieldLeak")
    val db = Firebase.firestore

    fun getNumberUsers(): Int {
        var count = 0
        val query = db.collection("Users")
        val countQuery = query.count()
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                count = task.result.count.toInt()
            } else {
                Log.d(TAG, "Count failed: ", task.exception)
            }
        }
        return count
    }
}