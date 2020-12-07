package devicefactory;

import devices.IoTDevice;
import devices.SmartSpeaker;

public class SmartSpeakerFactory extends IoTDeviceFactory{
    @Override
    IoTDevice createIoTDevice() {
        return new SmartSpeaker();
    }
}
