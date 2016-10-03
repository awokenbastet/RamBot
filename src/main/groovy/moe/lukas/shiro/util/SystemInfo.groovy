package moe.lukas.shiro.util

import groovy.transform.CompileStatic

import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean
import java.text.SimpleDateFormat

@CompileStatic
class SystemInfo {
    static RuntimeMXBean rmx = ManagementFactory.getRuntimeMXBean()
    static Runtime runtime = Runtime.getRuntime()

    static String getUptime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss")
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"))
        return sdf.format(rmx.uptime)
    }

    static String getOS() {
        return "${System.getProperty("os.name")} [Version: ${System.getProperty("os.version")} | Arch: ${System.getProperty("os.arch")}]"
    }

    static String getProcess() {
        return rmx.name
    }

    static String getJVM() {
        return "${rmx.vmName}@${rmx.vmVersion} by ${rmx.vmVendor}"
    }

    static String getSpec() {
        return "${rmx.specName}@${rmx.specVersion} by ${rmx.specVendor}"
    }

    static String getAllocatedRam() {
        return "${Math.round(runtime.totalMemory() / 1048576F)}mb"
    }

    static String getUsedAllocatedRam() {
        return "${Math.round((runtime.totalMemory() - runtime.freeMemory()) / 1048576F)}mb"
    }

    static String getFreeAllocatedRam() {
        return "${Math.round(runtime.freeMemory() / 1048576F)}mb"
    }

    static String getMaxUsableRam() {
        return "${Math.round(runtime.maxMemory() / 1048576F)}mb"
    }
}
