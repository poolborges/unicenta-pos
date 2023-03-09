package io.github.kriolos.opos.machine;

import com.google.gson.Gson;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.LogicalProcessor;
import oshi.hardware.CentralProcessor.PhysicalProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.hardware.Sensors;
import oshi.software.os.NetworkParams;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

/**
 *
 * @author poolb
 */
public class KriolosPosMachine {

    public static void main(String[] args) {
        System.out.println("Hello World!");

        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();
        System.out.println(os);
        
        HardwareAbstractionLayer hal = si.getHardware();
        CentralProcessor cpu = hal.getProcessor();
        System.out.println(cpu.getLogicalProcessorCount() + " CPU(s):");
        
        System.out.println("PhysicalProcessor");
        for (PhysicalProcessor pcpu : cpu.getPhysicalProcessors()) {
            System.out.println(" " + pcpu);
        }
        
        System.out.println("LogicalProcessor");
        for (LogicalProcessor lcpu : cpu.getLogicalProcessors()) {
            System.out.println(" " + lcpu);
        }
        
        ComputerSystem comp = hal.getComputerSystem();
        System.out.println("HardwareUUID: "+comp.getHardwareUUID());
        System.out.println("SerialNumber: "+comp.getSerialNumber());

        System.out.println("Memory: "
                + FormatUtil.formatBytes(hal.getMemory().getAvailable()) + "/"
                + FormatUtil.formatBytes(hal.getMemory().getTotal()));

        for (NetworkIF lcpu : hal.getNetworkIFs()) {
            System.out.println("MAC: " + lcpu.getMacaddr());
        }
        
        Sensors sensores = hal.getSensors();
        System.out.println("CpuTemp: " + sensores.getCpuTemperature());
        System.out.println("CpuVolt: " + sensores.getCpuVoltage());
        //System.out.println("Cpu: " + sensores.get);

        
        System.out.println("getProcessorID: "+ cpu.getProcessorIdentifier().getProcessorID());
        System.out.println("getIdentifier: "+ cpu.getProcessorIdentifier().getIdentifier());
        
        NetworkParams ntP = os.getNetworkParams();
        System.out.println("getHostName: "+ ntP.getHostName());
        for(String name: ntP.getDnsServers()){
            System.out.println("DnsServer: "+ name);
        }
        
        for(String name: ntP.getDnsServers()){
            System.out.println("DnsServer: "+ name);
        }
        
        Gson json = new Gson();
        //String content = json.toJson(os, OperatingSystem.class);
        //System.out.println(os);
        System.out.println(json.toJson(ntP, NetworkParams.class));
        
    }
}
