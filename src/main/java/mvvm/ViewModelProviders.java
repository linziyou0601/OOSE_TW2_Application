package mvvm;

import database.DBMgr;
import database.MySQLDBMgrImplProxy;

import java.util.HashMap;

public class ViewModelProviders {
    private static final ViewModelProviders vmProvider = new ViewModelProviders();
    private HashMap<String, ViewModel> viewModelStore = new HashMap<>();
    private DBMgr dbMgr = new DBMgr(new MySQLDBMgrImplProxy());

    private ViewModelProviders(){}

    public static ViewModelProviders getInstance(){
        return vmProvider;
    }

    public <T> T get(Class<T> vmClass){
        T viewModel = null;
        String viewModelName = vmClass.getSimpleName();         //取得Classname
        if(viewModelStore.containsKey(viewModelName)){              //若該ViewModel已存在
            viewModel = (T) viewModelStore.get(viewModelName);      //直接轉型成該VM的class並Return
        } else {                                                //若該ViewModel不存在
            try {
                viewModel = vmClass.getConstructor(DBMgr.class).newInstance(dbMgr); //以該VM的建構子進行實例化
                viewModelStore.put(viewModelName, (ViewModel) viewModel);           //存到ViewModel的儲存區
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return viewModel;
    }
}