package devicefactory;

import devices.IoTDevice;
import devices.SmartComputer;

public class SmartComputerFactory extends IoTDeviceFactory{
    @Override
    IoTDevice createIoTDevice() {
        return new SmartComputer();
    }
}
