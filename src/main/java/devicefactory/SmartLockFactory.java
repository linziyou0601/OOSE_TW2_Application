package devicefactory;

import devices.IoTDevice;
import devices.SmartLock;

public class SmartLockFactory extends IoTDeviceFactory{
    @Override
    IoTDevice createIoTDevice() {
        return new SmartLock();
    }
}
