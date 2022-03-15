package si.um.feri.roomtodo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.reflect.KParameter

@Database(entities = arrayOf(Todo::class), version = 1, exportSchema = false)
public abstract class TodoRoomDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: TodoRoomDatabase? = null

            fun getDatabase(context:Context, scope: CoroutineScope): TodoRoomDatabase {
                val tempInstance = INSTANCE
                if (tempInstance != null) {
                    return tempInstance
                }
                synchronized(this) { //do synchronized metode lahko dostopa samo ena nit naenkrat
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        TodoRoomDatabase::class.java,
                        "todo_database"
                    ).addCallback(TodoDatabaseCallback(scope)).build()
                    INSTANCE = instance
                    return instance
                }
            }

    }

    private class TodoDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.todoDao())
                }
            }
        }

        suspend fun populateDatabase(todoDao: TodoDao) {

            todoDao.deleteAll()

            var todo = Todo("Pravilno nosi masko")
            todoDao.insert(todo)
            todo = Todo("Pridi na vaje")
            todoDao.insert(todo)
        }
    }
}

