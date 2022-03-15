package si.um.feri.roomtodo

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TodoDao {

    @Query("SELECT * from todo_table ORDER BY todo ASC")
    fun getAlphabetizedTodos(): LiveData<List<Todo>>

    @Insert(onConflict = OnConflictStrategy.IGNORE) //dobra praksa da dodamo onConflict
    suspend fun insert(todo: Todo) //suspend funkcija poskrbi, da se ta nit pavzira (prednost da drugim nitim)

    @Query("DELETE FROM todo_table")
    suspend fun deleteAll()

}