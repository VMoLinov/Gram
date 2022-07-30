package ru.molinov.gram.utilites

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import ru.molinov.gram.activities.RegisterActivity

class AppValueEventListener(val onSuccess: (DataSnapshot) -> Unit) : ValueEventListener {

    override fun onDataChange(snapshot: DataSnapshot) {
        onSuccess(snapshot)
    }

    override fun onCancelled(error: DatabaseError) {
        MAIN_ACTIVITY.replaceActivity(RegisterActivity())
    }
}
