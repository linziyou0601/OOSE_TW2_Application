package main;

import database.DBMgr;
import database.MySQLDBMgrImpl;
import database.MySQLDBMgrImplProxy;
import devices.IoTDevice;

public class APIService {
    private static DBMgr dbMgr = new DBMgr(new MySQLDBMgrImplProxy());
    public static String loadIoTState(int iotId) {
        return dbMgr.getStateFromIoTDevicesById(iotId);
    }
    public static void shareIoTState(IoTDevice iotDevice) {
        dbMgr.updateIotDevice(iotDevice);
    }
}
