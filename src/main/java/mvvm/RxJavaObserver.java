package mvvm;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class RxJavaObserver<T> implements Observer<T> {
    @Override
    public void onSubscribe(Disposable d) {}
}
