package main;

import database.DBMgr;
import database.MySQLDBMgrImpl;
import database.MySQLDBMgrImplProxy;
import devices.IoTDevice;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import model.Classroom;
import mvvm.RxJavaCompletableObserver;
import mvvm.RxJavaObserver;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class APIService {
    private static DBMgr dbMgr = new DBMgr(new MySQLDBMgrImplProxy());
    public static Observable<String> loadIoTState(int iotId) {
        return dbMgr.getStateFromIoTDevicesById(iotId)
                .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                .observeOn(JavaFxScheduler.platform());         //最後在主執行緒中執行
    }
    public static void shareIoTState(IoTDevice iotDevice) {
        dbMgr.updateIotDevice(iotDevice)
                .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行;
                .subscribe(new RxJavaCompletableObserver() {
                    @Override
                    public void onComplete() {}
                });
    }
}
