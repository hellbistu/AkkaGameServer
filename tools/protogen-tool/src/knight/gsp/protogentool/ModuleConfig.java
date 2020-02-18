package knight.gsp.protogentool;

public class ModuleConfig {

    private String name;

    private String dir;

    private String language;

    private String ouputdir;

    private ModuleHandlerConfig handlerConfig = null;

    public ModuleConfig(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getOuputdir() {
        return ouputdir;
    }

    public void setOuputdir(String ouputdir) {
        this.ouputdir = ouputdir;
    }

    public ModuleHandlerConfig getHandlerConfig() {
        return handlerConfig;
    }

    public void setHandlerConfig(ModuleHandlerConfig handlerConfig) {
        this.handlerConfig = handlerConfig;
    }
}
