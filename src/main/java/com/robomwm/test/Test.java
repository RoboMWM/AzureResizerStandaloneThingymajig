package com.robomwm.test;

import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.compute.VirtualMachineSizeTypes;
import com.microsoft.rest.LogLevel;
import org.apache.log4j.BasicConfigurator;

import java.io.File;
import java.time.Instant;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Scanner;

/**
 * Created on 4/30/2020.
 *
 * @author RoboMWM
 */
public class Test
{
    public static void main(String[] args)
    {
        BasicConfigurator.configure();
        try
        {
            boolean isWithinPeakHours = false;

            //https://stackoverflow.com/a/39712175
            try
            {
//                System.out.println(ZoneOffset.getAvailableZoneIds());
                ZoneOffset zone = ZoneId.of("US/Pacific").getRules().getOffset(Instant.now());
                OffsetTime start = OffsetTime.of(11, 0, 0, 0, zone);
                OffsetTime stop = OffsetTime.of(21, 0, 0, 0, zone);
                OffsetTime now = OffsetTime.now(zone);
                isWithinPeakHours = now.isAfter(start) && now.isBefore(stop);
                System.out.println("start " + start);
                System.out.println("stop " + stop);
                System.out.println("now " + now);
                System.out.println("now.isAfter " + now.isAfter(start));
                System.out.println("now.isBefore " + now.isBefore(stop));
                System.out.println("isWithinPeakHours " + isWithinPeakHours);
                System.out.println("zone " + zone);
            }
            catch (Throwable rock)
            {
                rock.printStackTrace();
                System.out.println("peak hours calculation failed");
            }

            final File credFile = new File(System.getProperty("user.dir") +
                    File.separator + "plugins" +
                    File.separator + "AzureResizer" +
                    File.separator + "creds.properties");
            Azure azure = Azure.configure()
                    .withLogLevel(LogLevel.BASIC)
                    .authenticate(credFile)
                    .withDefaultSubscription();
            System.out.println("credentialed in");
            System.out.println(azure.subscriptions());
            VirtualMachine vm = azure.virtualMachines().getByResourceGroup("linux2", "linux2");
            System.out.println("updating");
            if (vm.size() == VirtualMachineSizeTypes.STANDARD_B1S)
            {
                if (isWithinPeakHours)
                {
                    System.out.println("to B2s");
                    vm.update().withSize(VirtualMachineSizeTypes.STANDARD_B2S).apply();
                }
                else
                {
                    System.out.println("to B1ms");
                    vm.update().withSize(VirtualMachineSizeTypes.STANDARD_B1MS).apply();
                }
            }
            else
            {
                System.out.println("back to B1s");
                vm.update().withSize(VirtualMachineSizeTypes.STANDARD_B1S).apply();
            }

            //new ResizeThread(vm, VirtualMachineSizeTypes.STANDARD_B1S).start();
            System.out.println("started");
//            for (int i = 0; i < Integer.MAX_VALUE; i++)
//            {
//                System.out.print(i + ", ");
//                Thread.sleep(1000);
//            }
            System.out.println("complete");
//            vm.deallocateAsync();
//            vm.startAsync();


//            System.out.println("hardwareProfile");
//            System.out.println("    vmSize: " + vm.size());
//            System.out.println("storageProfile");
//            System.out.println("  imageReference");
//            System.out.println("    publisher: " + vm.storageProfile().imageReference().publisher());
//            System.out.println("    offer: " + vm.storageProfile().imageReference().offer());
//            System.out.println("    sku: " + vm.storageProfile().imageReference().sku());
//            System.out.println("    version: " + vm.storageProfile().imageReference().version());
//            System.out.println("  osDisk");
//            System.out.println("    osType: " + vm.storageProfile().osDisk().osType());
//            System.out.println("    name: " + vm.storageProfile().osDisk().name());
//            System.out.println("    createOption: " + vm.storageProfile().osDisk().createOption());
//            System.out.println("    caching: " + vm.storageProfile().osDisk().caching());
//            System.out.println("osProfile");
//            System.out.println("    computerName: " + vm.osProfile().computerName());
//            System.out.println("    adminUserName: " + vm.osProfile().adminUsername());
//            System.out.println("    provisionVMAgent: " + vm.osProfile().windowsConfiguration().provisionVMAgent());
//            System.out.println("    enableAutomaticUpdates: " + vm.osProfile().windowsConfiguration().enableAutomaticUpdates());
//            System.out.println("networkProfile");
//            System.out.println("    networkInterface: " + vm.primaryNetworkInterfaceId());
//            System.out.println("vmAgent");
//            System.out.println("  vmAgentVersion: " + vm.instanceView().vmAgent().vmAgentVersion());
//            System.out.println("    statuses");
//            for(InstanceViewStatus status : vm.instanceView().vmAgent().statuses()) {
//                System.out.println("    code: " + status.code());
//                System.out.println("    displayStatus: " + status.displayStatus());
//                System.out.println("    message: " + status.message());
//                System.out.println("    time: " + status.time());
//            }
//            System.out.println("disks");
//            for(DiskInstanceView disk : vm.instanceView().disks()) {
//                System.out.println("  name: " + disk.name());
//                System.out.println("  statuses");
//                for(InstanceViewStatus status : disk.statuses()) {
//                    System.out.println("    code: " + status.code());
//                    System.out.println("    displayStatus: " + status.displayStatus());
//                    System.out.println("    time: " + status.time());
//                }
//            }
//            System.out.println("VM general status");
//            System.out.println("  provisioningStatus: " + vm.provisioningState());
//            System.out.println("  id: " + vm.id());
//            System.out.println("  name: " + vm.name());
//            System.out.println("  type: " + vm.type());
//            System.out.println("VM instance status");
//            for(InstanceViewStatus status : vm.instanceView().statuses()) {
//                System.out.println("  code: " + status.code());
//                System.out.println("  displayStatus: " + status.displayStatus());
//            }
//            System.out.println("Press enter to continue...");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }


    }
}
//class ResizeThread extends Thread
//{
//    private VirtualMachine vm;
//    private VirtualMachineSizeTypes type;
//
//    public ResizeThread(VirtualMachine vm, VirtualMachineSizeTypes type)
//    {
//        this.vm = vm;
//        this.type = type;
//    }
//
//    @Override
//    public void run()
//    {
//        vm.update().withSize(type).applyAsync();
//    }
//}