package knight.gsp.protogentool;

public class ProtocolConfig {
    private String name;
    private String type;
    private String from = "server";
    private String format = "lua"; //协议默认是lua的，这个字段只对client生效

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
