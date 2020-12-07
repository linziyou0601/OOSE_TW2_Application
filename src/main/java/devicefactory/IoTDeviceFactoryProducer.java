package devicefactory;

public class IoTDeviceFactoryProducer {
    private IoTDeviceFactoryProducer(){
        throw new AssertionError();
    }

    public static IoTDeviceFactory getFactory(String deviceType){
        //根據deviceType參數決定要Return哪個ConcreteFactory
        switch(deviceType) {
            case "SmartAirConditioner": return new SmartAirConditionerFactory();
            case "SmartComputer": return new SmartComputerFactory();
            case "SmartLight": return new SmartLightFactory();
            case "SmartLock": return new SmartLockFactory();
            case "SmartProjector": return new SmartProjectorFactory();
            case "SmartSpeaker": return new SmartSpeakerFactory();
        }
        return null;
    }
}
