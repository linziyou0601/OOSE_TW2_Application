package main;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ViewModelProviders {
    private static final ViewModelProviders mProvider = new ViewModelProviders();
    private HashMap<String, Object> mViewModelStore = new HashMap<>();

    private ViewModelProviders(){}

    public static ViewModelProviders getInstance(){
        return mProvider;
    }

    public <T> T get(Class<T> modelClass){
        T viewModel = null;
        String viewModelName = modelClass.getSimpleName();
        if(mViewModelStore.containsKey(viewModelName)){
            viewModel = (T) mViewModelStore.get(viewModelName);
        } else {
            try {
                viewModel = modelClass.getConstructor().newInstance();
                mViewModelStore.put(viewModelName, viewModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return viewModel;
    }
}