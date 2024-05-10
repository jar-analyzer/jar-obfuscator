package me.n1ar4.jar.obfuscator.config;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.utils.IOUtils;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TagInspector;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Parser {
    private static final Logger logger = LogManager.getLogger();
    private String TEMPLATE;
    private final Yaml yaml;

    public Parser() {
        LoaderOptions options = new LoaderOptions();
        TagInspector taginspector =
                tag -> tag.getClassName().equals(BaseConfig.class.getName());
        options.setTagInspector(taginspector);
        yaml = new Yaml(new Constructor(BaseConfig.class, options));
        InputStream is = Parser.class.getClassLoader().getResourceAsStream("config.yaml");
        if (is == null) {
            return;
        }
        try {
            byte[] data = IOUtils.readAllBytes(is);
            TEMPLATE = new String(data, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            logger.error("read template error: {}", ex);
        }
    }

    public void generateConfig() {
        try {
            Files.write(Const.configPath, TEMPLATE.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            logger.error("write config file error: {}", ex.toString());
        }
    }

    @SuppressWarnings("all")
    public void generateConfigSTD() {
        BaseConfig config = new BaseConfig();
        config.setEnableClassName(true);
        config.setEnableJunk(true);
        config.setEnableAdvanceString(true);
        config.setEnableFieldName(true);
        config.setEnableEncryptString(true);
        config.setEnableXOR(true);
        config.setEnableDeleteCompileInfo(true);
        config.setEnableParamName(true);
        config.setEnableMethodName(true);
        config.setShowAllMainMethods(true);

        config.setJunkLevel(3);
        config.setMaxJunkOneClass(1000);

        config.setLogLevel("info");
        config.setObfuscateChars(new String[]{"i", "l", "L", "1", "I"});
        config.setObfuscatePackage(new String[]{"me.n1ar4", "org.n1ar4"});
        config.setMainClass("me.n1ar4.fake.gui.Application");
        config.setAdvanceStringName("GLOBAL_LLLiii");

        config.setEnableSuperObfuscate(true);
        config.setSuperObfuscateKey("4ra1n4ra1n4ra1n1");
        config.setSuperObfuscatePackage("me.n1ar4");

        String prefix = "# jar obfuscator config\n";

        String data = yaml.dump(config);
        data = prefix + data;
        try {
            Files.write(Const.configPath, data.getBytes());
        } catch (Exception ex) {
            logger.error("write config file error: {}", ex.toString());
        }
    }

    public BaseConfig parse(Path file) {
        if (!Files.exists(file)) {
            logger.error("config file not exist");
            return null;
        }
        try {
            InputStream is = new ByteArrayInputStream(Files.readAllBytes(file));
            return yaml.load(is);
        } catch (Exception ex) {
            logger.error("parse config error: {}", ex.toString());
        }
        return null;
    }
}
