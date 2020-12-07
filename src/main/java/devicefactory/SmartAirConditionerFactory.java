package devicefactory;

import devices.IoTDevice;
import devices.SmartAirConditioner;

public class SmartAirConditionerFactory extends IoTDeviceFactory{
    @Override
    IoTDevice createIoTDevice() {
        return new SmartAirConditioner();
    }
}
