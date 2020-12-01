package main;

import java.util.HashMap;

public class ViewModelProviders {
    private static final ViewModelProviders mProvider = new ViewModelProviders();
    private HashMap<String, IViewModel> mViewModelStore = new HashMap<>();
    private SessionService sessionService = new SessionService();

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
                viewModel = modelClass.getConstructor(SessionService.class).newInstance(sessionService);
                mViewModelStore.put(viewModelName, (IViewModel) viewModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return viewModel;
    }
}