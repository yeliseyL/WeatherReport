package elisey.lobanov.weatherreport.history;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHistoryField(HistoryField historyField);

    @Update
    void updateHistoryField(HistoryField historyField);

    @Delete
    void deleteHistoryField(HistoryField historyField);

    @Query("DELETE FROM historyField WHERE id = :id")
    void deteleHistoryFieldById(long id);

    @Query("SELECT * FROM historyField")
    List<HistoryField> getAllHistoryFields();

    @Query("SELECT * FROM historyField WHERE id = :id")
    HistoryField getHistoryFieldById(long id);

    @Query("SELECT COUNT() FROM historyField")
    long getCountHistoryFields();
}

