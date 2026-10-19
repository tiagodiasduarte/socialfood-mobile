package pt.socialfood.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val dbFile = context.applicationContext.getDatabasePath(DATABASE_NAME)
    return Room.databaseBuilder(
        context = context.applicationContext,
        name = dbFile.absolutePath,
    )
}
