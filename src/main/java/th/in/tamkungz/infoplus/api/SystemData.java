package th.in.tamkungz.infoplus.api;

public class SystemData {
    // CPU Info
    public String cpuModel = "Unknown";
    public int cpuPhysical = 0;
    public int cpuLogical = 0;
    public String cpuFreqStr = "Unknown";
    public double cpuLoad = 0.0;

    // RAM Info
    public String ramTotal = "Unknown";
    public String ramAvail = "Unknown";
    public String ramUsed = "Unknown";

    // GPU Info
    public String gpuInfo = "Unknown GPU";

    // Motherboard / Firmware
    public String motherboardModel = "Unknown";
    public String motherboardVendor = "Unknown";
    public String biosVersion = "Unknown";

    // Disk Info
    public String diskInfo = "Unknown";

    // Network Interfaces
    public String netInfo = "Unknown";

    // Battery
    public String batteryInfo = "Unknown";

    // OS Info
    public String osInfo = "Unknown OS";
    public String bootTime = "Unknown";

    // FileSystem
    public String fileSystems = "Unknown";

    // Java Info
    public String javaVersion = "Unknown";
    public String javaVendor = "Unknown";
    public String javaHome = "Unknown";

    // Minecraft Info
    public String mcVersion = "Unknown";
    public String loaderVersion = "Unknown";
}
