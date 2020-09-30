package elisey.lobanov.weatherreport.history;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {HistoryField.class}, version = 1)
public abstract class HistoryDatabase extends RoomDatabase {
    public abstract HistoryDao getHistoryDao();
}

