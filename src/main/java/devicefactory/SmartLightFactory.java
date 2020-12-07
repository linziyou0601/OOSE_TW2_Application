package devicefactory;

import devices.IoTDevice;
import devices.SmartLight;

public class SmartLightFactory extends IoTDeviceFactory{
    @Override
    IoTDevice createIoTDevice() {
        return new SmartLight();
    }
}
