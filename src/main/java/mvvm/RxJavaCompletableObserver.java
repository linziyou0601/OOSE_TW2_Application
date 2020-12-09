package mvvm;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import org.jetbrains.annotations.NotNull;

public abstract class RxJavaCompletableObserver implements CompletableObserver {
    @Override
    public void onSubscribe(@NotNull Disposable d) { }
    @Override
    public void onError(@NotNull Throwable e) { }
}
