package moe.lukas.shiro.util

import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean
import java.text.SimpleDateFormat

class SystemInfo {
    public static RuntimeMXBean rmx = ManagementFactory.getRuntimeMXBean()
    public static Runtime runtime = Runtime.getRuntime()

    public static String getUptime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss")
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"))
        return sdf.format(rmx.uptime)
    }

    public static String getOS() {
        return "${System.getProperty("os.name")} [Version: ${System.getProperty("os.version")} | Arch: ${System.getProperty("os.arch")}]"
    }

    public static String getProcess() {
        return rmx.name
    }

    public static String getJVM() {
        return "${rmx.vmName}@${rmx.vmVersion} by ${rmx.vmVendor}"
    }

    public static String getSpec() {
        return "${rmx.specName}@${rmx.specVersion} by ${rmx.specVendor}"
    }

    public static String getAllocatedRam() {
        return "${Math.round(runtime.totalMemory() / 1048576)}mb"
    }

    public static String getUsedAllocatedRam() {
        return "${Math.round((runtime.totalMemory() - runtime.freeMemory()) / 1048576)}mb"
    }

    public static String getFreeAllocatedRam() {
        return "${Math.round(runtime.freeMemory() / 1048576)}mb"
    }

    public static String getMaxUsableRam() {
        return "${Math.round(runtime.maxMemory() / 1048576)}mb"
    }
}
