package th.in.tamkungz.infoplus.api;

import net.minecraftforge.fml.loading.FMLLoader;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.*;
import oshi.util.FormatUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class SystemInfoAPI {

    private static SystemInfo si;

    static {
        try {
            si = new SystemInfo();
        } catch (Throwable t) {
            si = null;
            System.err.println("[InfoPlus] SystemInfo init failed: " + t.getMessage());
        }
    }

    public static SystemData getSystemData() {
        SystemData data = new SystemData();
        if (si == null) return data;

        try {
            // CPU
            CentralProcessor cpu = si.getHardware().getProcessor();
            if (cpu != null && cpu.getProcessorIdentifier() != null) {
                data.cpuModel = safeString(cpu.getProcessorIdentifier().getName());
                data.cpuPhysical = cpu.getPhysicalProcessorCount();
                data.cpuLogical = cpu.getLogicalProcessorCount();
                long freq = cpu.getProcessorIdentifier().getVendorFreq();
                if (freq <= 0) freq = cpu.getMaxFreq();
                data.cpuFreqStr = freq > 0 ? String.format("%.2f GHz", freq / 1e9) : "Unknown";

                try {
                    long[] ticks = cpu.getSystemCpuLoadTicks();
                    Thread.sleep(200);
                    data.cpuLoad = cpu.getSystemCpuLoadBetweenTicks(ticks) * 100.0;
                } catch (Exception ignored) {}
            }

            // RAM
            GlobalMemory mem = si.getHardware().getMemory();
            data.ramTotal = FormatUtil.formatBytes(mem.getTotal());
            data.ramAvail = FormatUtil.formatBytes(mem.getAvailable());
            data.ramUsed = FormatUtil.formatBytes(mem.getTotal() - mem.getAvailable());

            // GPU
            List<GraphicsCard> gpus = si.getHardware().getGraphicsCards();
            data.gpuInfo = gpus.stream().map(gpu ->
                    String.format("%s (%s, %sMB VRAM)",
                            safeString(gpu.getName()),
                            safeString(gpu.getVendor()),
                            gpu.getVRam() / 1024 / 1024)
            ).collect(Collectors.joining("; "));

            // Motherboard
            ComputerSystem cs = si.getHardware().getComputerSystem();
            Baseboard bb = cs.getBaseboard();
            Firmware fw = cs.getFirmware();
            data.motherboardModel = safeString(bb.getModel());
            data.motherboardVendor = safeString(bb.getManufacturer());
            data.biosVersion = safeString(fw.getVersion());

            // Disk Info
            List<HWDiskStore> disks = si.getHardware().getDiskStores();
            data.diskInfo = disks.stream().map(disk ->
                    String.format("%s (%,.2f GB)",
                            safeString(disk.getModel()),
                            disk.getSize() / 1e9)
            ).collect(Collectors.joining("; "));

            // Network
            List<NetworkIF> nics = si.getHardware().getNetworkIFs();
            data.netInfo = nics.stream().map(nic ->
                    String.format("%s (%s)", safeString(nic.getName()), FormatUtil.formatBytes(nic.getSpeed()))
            ).collect(Collectors.joining("; "));

            // Battery
            List<PowerSource> batteries = si.getHardware().getPowerSources();
            if (!batteries.isEmpty()) {
                PowerSource bat = batteries.get(0);
                data.batteryInfo = String.format("%.0f%% (%s remaining)",
                        bat.getRemainingCapacityPercent() * 100,
                        FormatUtil.formatElapsedSecs((long) bat.getTimeRemainingEstimated()));
            }

            // OS Info
            OperatingSystem os = si.getOperatingSystem();
            data.osInfo = os.toString();

            // Boot Time
            Instant bootTime = Instant.ofEpochSecond(os.getSystemBootTime());
            data.bootTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault()).format(bootTime);

            // Filesystem
            FileSystem fs = os.getFileSystem();
            List<OSFileStore> stores = fs.getFileStores();
            data.fileSystems = stores.stream().map(store ->
                    String.format("%s (%s, %.2f GB used)",
                            safeString(store.getMount()),
                            store.getType(),
                            (store.getTotalSpace() - store.getUsableSpace()) / 1e9)
            ).collect(Collectors.joining("; "));

            // Java
            data.javaVersion = safeString(System.getProperty("java.version"));
            data.javaVendor = safeString(System.getProperty("java.vendor"));
            data.javaHome = safeString(System.getProperty("java.home"));

            // Minecraft
            data.mcVersion = FMLLoader.versionInfo().mcVersion();
            data.loaderVersion = FMLLoader.versionInfo().forgeVersion();

        } catch (Throwable t) {
            System.err.println("[InfoPlus] Failed to gather full system data: " + t.getMessage());
        }

        return data;
    }

    private static String safeString(String s) {
        return (s != null && !s.trim().isEmpty()) ? s.trim() : "Unknown";
    }
}
