package gnet.config;

import com.moandjiezana.toml.Toml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ConfigManager {

    private static Logger logger = LoggerFactory.getLogger(ConfigManager.class);

    private static ConfigManager ourInstance = new ConfigManager();

    public static ConfigManager getInstance() {
        return ourInstance;
    }

    private GateConfig gateConfig;

    public void reload() {

    }

    public void load() {
        String cfgName = "gate.toml";
        logger.info("load config {}", cfgName);
//        InputStream cfgInputStream = Bootstrap.class.getClassLoader().getResourceAsStream(cfgName);
        InputStream cfgInputStream = null;
        try {
            cfgInputStream = new FileInputStream("gate.toml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Toml toml = new Toml().read(cfgInputStream);
        gateConfig = toml.to(GateConfig.class);

        if (gateConfig == null) {
            throw new RuntimeException("load gate config file faile");
        }
    }

    public GateConfig getCfg() {
        return gateConfig;
    }

    private ConfigManager() {
    }
}
