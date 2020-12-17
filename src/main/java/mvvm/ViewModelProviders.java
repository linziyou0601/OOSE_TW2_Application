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
        String viewModelName = vmClass.getSimpleName();
        if(viewModelStore.containsKey(viewModelName)){
            viewModel = (T) viewModelStore.get(viewModelName);
        } else {
            try {
                viewModel = vmClass.getConstructor(DBMgr.class).newInstance(dbMgr);
                viewModelStore.put(viewModelName, (ViewModel) viewModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return viewModel;
    }
}