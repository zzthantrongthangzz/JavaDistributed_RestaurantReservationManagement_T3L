package connect;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigManager {
    private static Properties properties = new Properties();

    static {
        try (FileInputStream in = new FileInputStream("config.properties")) {
            properties.load(in);
        } catch (Exception e) {
            System.err.println("CẢNH BÁO: Không tìm thấy file config.properties ở ngoài cùng!");
            System.err.println("Hệ thống sẽ tự động sử dụng IP mặc định là 192.168.10.73");
        }
    }

    public static String getServerIp() {
        return properties.getProperty("server.ip", "192.168.10.73");
    }

    public static String getServerPort() {
        return properties.getProperty("server.port", "1099");
    }

    public static String getRmiUrl() {
        return "rmi://" + getServerIp() + ":" + getServerPort() + "/";
    }
}