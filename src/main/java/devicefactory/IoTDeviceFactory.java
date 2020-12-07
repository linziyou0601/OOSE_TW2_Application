package devicefactory;

import devices.IoTDevice;

public abstract class IoTDeviceFactory {
    public final IoTDevice instantiateIoTDevice(int id, String name, String state) {
        IoTDevice iotDevice = createIoTDevice();
        iotDevice.initialize(id, name, state);
        return iotDevice;
    }
    abstract IoTDevice createIoTDevice();
}
