package tools;

import java.util.Properties;

public class PropertyTool {

    private Properties props = new Properties();

    public PropertyTool(Properties props) {
        this.props = props;
    }

    public byte getSettingByte(String key, byte def) {
        String property = this.props.getProperty(key);
        if (property != null) {
            return Byte.parseByte(property);
        }
        return def;
    }

    public short getSettingShort(String key, short def) {
        String property = this.props.getProperty(key);
        if (property != null) {
            return Short.parseShort(property);
        }
        return def;
    }

    public int getSettingInt(String key, int def) {
        String property = this.props.getProperty(key);
        if (property != null) {
            return Integer.parseInt(property);
        }
        return def;
    }

    public long getSettingLong(String key, long def) {
        String property = this.props.getProperty(key);
        if (property != null) {
            return Long.parseLong(property);
        }
        return def;
    }

    public String getSettingStr(String key, String def) {
        String property = this.props.getProperty(key);
        if (property != null) {
            return property;
        }
        return def;
    }

    public byte getSettingByte(String key) {
        return getSettingByte(key, (byte) -1);
    }

    public short getSettingShort(String key) {
        return getSettingShort(key, (short) -1);
    }

    public int getSettingInt(String key) {
        return getSettingInt(key, -1);
    }

    public long getSettingLong(String key) {
        return getSettingLong(key, -1L);
    }

    public String getSettingStr(String key) {
        return getSettingStr(key, null);
    }
}