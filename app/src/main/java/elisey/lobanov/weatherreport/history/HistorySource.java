package elisey.lobanov.weatherreport.history;

import java.util.List;

public class HistorySource{

    private final HistoryDao historyDao;

    private List<HistoryField> historyFields;

    public HistorySource(HistoryDao historyDao){
        this.historyDao = historyDao;
    }

    public List<HistoryField> getHistoryFields(){
        if (historyFields == null){
            LoadHistoryFields();
        }
        return historyFields;
    }

    public void LoadHistoryFields(){
        historyFields = historyDao.getAllHistoryFields();
    }

    public long getCountHistoryFields(){
        return historyDao.getCountHistoryFields();
    }

    public void addHistoryField(HistoryField historyField){
        historyDao.insertHistoryField(historyField);
        LoadHistoryFields();
    }

    public void updateHistoryField(HistoryField historyField){
        historyDao.updateHistoryField(historyField);
        LoadHistoryFields();
    }

    public void removeHistoryField(long id){
        historyDao.deteleHistoryFieldById(id);
        LoadHistoryFields();
    }

}
