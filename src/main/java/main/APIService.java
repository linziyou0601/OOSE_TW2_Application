package main;

import database.DBMgr;
import database.MySQLDBMgrImplProxy;
import devices.IoTDevice;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import io.reactivex.schedulers.Schedulers;
import mvvm.RxJavaCompletableObserver;
import mvvm.RxJavaObserver;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class APIService {
    private static DBMgr dbMgr = new DBMgr(new MySQLDBMgrImplProxy());
    private static Map<Integer, String> deviceStateCache = new HashMap<>(); //裝置狀態快取
    private static LocalDateTime lastUpdateTime = LocalDateTime.now();      //上次抓取時間
    private static boolean isLoading = false;                               //背景是否抓取中
    private static Logger apiServiceLogger = Logger.getGlobal();

    public static String loadIoTState(int iotId) {
        // 若與上次抓取資料「相隔3秒以上」，則抓新的資料
        Duration duration = Duration.between(lastUpdateTime, LocalDateTime.now());
        long seconds = Math.abs(duration.getSeconds());
        if(seconds>=3 && !isLoading) {
            isLoading = true;
            apiServiceLogger.info(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " 正在同步裝置狀態...");
            dbMgr.getStateFromIoTDevices()
                    .subscribeOn(Schedulers.newThread())        //請求在新執行緒中執行
                    .observeOn(JavaFxScheduler.platform())      //最後在主執行緒中執行
                    .subscribe(new RxJavaObserver<>() {
                        @Override
                        public void onNext(Map<Integer, String> result) {
                            if(result!=null) deviceStateCache = result;
                            lastUpdateTime = LocalDateTime.now();
                            isLoading = false;
                            apiServiceLogger.info(lastUpdateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))  + " 同步完成！");
                        }
                        @Override
                        public void onComplete() {}
                        @Override
                        public void onError(Throwable e) {}
                    });
        }

        // 直接從快取回傳
        return deviceStateCache.get(iotId);
    }
    public static void shareIoTState(IoTDevice iotDevice) {
        deviceStateCache.put(iotDevice.getId(), iotDevice.getState());
        apiServiceLogger.info("正在上傳裝置" + iotDevice.getId() + ": " + iotDevice.getName() + "的狀態...");
        dbMgr.updateIotDevice(iotDevice)
                .subscribeOn(Schedulers.newThread())            //請求在新執行緒中執行
                .observeOn(JavaFxScheduler.platform())          //最後在主執行緒中執行;
                .subscribe(new RxJavaCompletableObserver() {
                    @Override
                    public void onComplete() {
                        apiServiceLogger.info("上傳完成！");}
                });
        if(iotDevice.getId()==3){
            new Thread(() -> WebduinoService.requestGet(iotDevice.getState())).start();
        }
    }
}
