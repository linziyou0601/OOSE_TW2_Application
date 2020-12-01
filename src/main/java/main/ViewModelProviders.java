package main;

import database.DBMgr;

import java.util.HashMap;

public class ViewModelProviders {
    private static final ViewModelProviders vmProvider = new ViewModelProviders();
    private HashMap<String, IViewModel> viewModelStore = new HashMap<>();
    private DBMgr dbMgr = new DBMgr();

    private ViewModelProviders(){}

    public static ViewModelProviders getInstance(){
        return vmProvider;
    }

    public <T> T get(Class<T> modelClass){
        T viewModel = null;
        String viewModelName = modelClass.getSimpleName();
        if(viewModelStore.containsKey(viewModelName)){
            viewModel = (T) viewModelStore.get(viewModelName);
        } else {
            try {
                viewModel = modelClass.getConstructor(DBMgr.class).newInstance(dbMgr);
                viewModelStore.put(viewModelName, (IViewModel) viewModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return viewModel;
    }
}