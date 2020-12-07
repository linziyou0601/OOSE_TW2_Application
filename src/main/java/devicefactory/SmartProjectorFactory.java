package devicefactory;

import devices.IoTDevice;
import devices.SmartProjector;

public class SmartProjectorFactory extends IoTDeviceFactory{
    @Override
    IoTDevice createIoTDevice() {
        return new SmartProjector();
    }
}
