package knight.gsp.protogentool;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    private static Map<Short, ProtocolConfig> protocolConfigs = new HashMap<>();

//    private static Map<Short,String> ptype2from = new HashMap<>();

    private static Map<String,String> namespace2protoContents = new HashMap<>();

    private static String WorkDir;

    private static String protocolDir;

    private static String ProtocPath;

    private static String mode = "server";

    public static void main(String[] args) throws Exception {
        // protocol.xml
        String protocolMainFile = args[0];
        File protoEntryFile = new File(protocolMainFile);
        if (!protoEntryFile.exists()) {
            throw new IllegalArgumentException("protocol.xml不存在");
        }

        //1.创建Reader对象
        SAXReader reader = new SAXReader();

        //2.加载xml
        Document document = reader.read(protoEntryFile);
        Element projectRoot = document.getRootElement();

        WorkDir = new File("").getAbsolutePath();
        protocolDir = protoEntryFile.getParentFile().getAbsolutePath();
        mode = projectRoot.elementText("mode");

        //获取protoc路径
        parseProtocPath(projectRoot.element("protoc"));

        Iterator<Element> moduleIter = projectRoot.elementIterator();
        while (moduleIter.hasNext()) {
            Element projectElement = moduleIter.next();
            if (projectElement.getName().equals("module")) {
                //解析module
                parseModuleConfig(projectElement);
            }
        }

    }

    private static void parseProtocPath(Element protocElement) {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("windows")) {
            ProtocPath = WorkDir + File.separator +  protocElement.element("windows").getText().replace("/",File.separator );
        }
    }


    private static void parseModuleConfig(Element moduleElement) throws Exception {
        ModuleConfig module = new ModuleConfig(moduleElement.attributeValue("name"));

        Iterator<Element> propIter = moduleElement.elementIterator();
        while (propIter.hasNext()) {
            Element propElement = propIter.next();
            if (propElement.getName().equals("dir")) {
                module.setDir(propElement.getText());
            } else if (propElement.getName().equals("language")) {
                module.setLanguage(propElement.getText());
            } else if (propElement.getName().equals("outputdir")) {
                module.setOuputdir(propElement.getText());
            } else if (propElement.getName().equals("handler")) {
                ModuleHandlerConfig handlerConfig = new ModuleHandlerConfig();
                handlerConfig.setDir(propElement.elementText("dir").replace("/",File.separator));
                handlerConfig.setTemplate(propElement.elementText("template"));
                handlerConfig.setExtension(propElement.elementText("extension"));
                handlerConfig.setHandleMode(propElement.elementText("handleMode"));
                module.setHandlerConfig(handlerConfig);
            }
        }

        genProtofiles(module);
    }


    private static void genProtofiles(ModuleConfig module) throws Exception {
        String moduleDir = protocolDir + File.separator + module.getDir();
        File dir = new File(moduleDir);

        File[] protocolXmlFiles = dir.listFiles();
        for (File protocolXmlFile :protocolXmlFiles) {
            //1.创建Reader对象
            SAXReader reader = new SAXReader();

            //2.加载xml
            Document document = reader.read(protocolXmlFile);
            Element rootElement = document.getRootElement();

            parseNamespace("",rootElement, module);
        }
    }

    private static void parseNamespace(String parentNamespace,Element rootElement, ModuleConfig module) throws Exception {

        String namespace = rootElement.attributeValue("name");
        if (!parentNamespace.trim().isEmpty()) {
            namespace = String.format("%s.%s",parentNamespace,namespace);
        }

        StringWriter protocolWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(protocolWriter);

        List<ProtocolConfig> protocolList = new ArrayList<>();

        Iterator<Element> iterator = rootElement.elementIterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            if (element.getName().equals("protocol")) {
                String protocolName = element.attributeValue("name");
                Short protocolType = Short.valueOf(element.attributeValue("type"));

                if (protocolConfigs.containsKey(protocolType)) {
                    throw new IllegalArgumentException(String.format("协议号冲突: %s -> %s", protocolName, protocolConfigs.get(protocolType).getName()));
                }

                if (element.attributeValue("from") == null) {
                    throw new IllegalArgumentException(String.format("协议必须配置from: %s", protocolName));
                }

                ProtocolConfig protocolConfig = new ProtocolConfig();
                protocolConfig.setName(element.attributeValue("name"));
                protocolConfig.setType(element.attributeValue("type"));
                protocolConfig.setFormat(element.attributeValue("format"));
                protocolConfig.setFrom(element.attributeValue("from"));

                protocolConfigs.put(protocolType,protocolConfig);
                protocolList.add(protocolConfig);

                logger.info(String.format("gen protocol %s", protocolName));

                writer.println(String.format("message %s {", protocolName));

                Iterator<Element> protolVarIter = element.elementIterator();
                int varCount = 0;
                while (protolVarIter.hasNext()) {
                    varCount++;
                    Element varElement = protolVarIter.next();
                    String varName = varElement.attributeValue("name");
                    String varType = varElement.attributeValue("type");
                    writer.println(String.format("\t%s %s = %d;", varType, varName, varCount));
                }

                writer.println("}\n");
            } else if (element.getName().equals("namespace")) {
                //嵌套了namespace,递归
                parseNamespace(namespace,element,module);
            }
        }

        String protoContent = protocolWriter.toString();
        if (!protoContent.isEmpty()) {
            //(1) 步骤1: 生成 proto文件
            //说明namespace内部有protocol或者bean,需要生成 proto 文件(之后protoc需要用)
            String protoFileDir = "protogen" + File.separator + "protos" + File.separator + namespace.replace(".",File.separator);

            File dir = new File(protoFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(String.format("%s%sprotogen.proto",dir.getPath(),File.separator));
            if (!file.exists()) {
                file.createNewFile();
            }

            PrintWriter fileWriter = new PrintWriter(file);
            fileWriter.println("syntax=\"proto3\";\n");
            fileWriter.println(String.format("package %s;\n",namespace));
            fileWriter.println("option java_outer_classname = \"protogen\";\n");

            fileWriter.write(protoContent);

            fileWriter.flush();
            fileWriter.close();

            //(2) 步骤2: protoc生成相应的协议文件
            String outputDir = WorkDir + File.separator + module.getOuputdir().replace("/",File.separator);
            File outputDirFile = new File(outputDir);
            if (!outputDirFile.exists()) {
                outputDirFile.mkdirs();
            }

            String protocCmd = String.format("%s --java_out=%s %s",ProtocPath, outputDir,file.getPath());
            logger.info(protocCmd);
            Process p = Runtime.getRuntime().exec(protocCmd);
            p.waitFor();

            //(3) 步骤3: 生成协议处理文类
            if (module.getHandlerConfig() != null) {
                ModuleHandlerConfig handlerConfig = module.getHandlerConfig();
                for (ProtocolConfig protocol : protocolList) {

                    if (!protocol.getFrom().equals(handlerConfig.getHandleMode())) {
                        continue;
                    }

                    String protocolFullClassName = (namespace.isEmpty() ? "": namespace + ".")  +  "protogen." + protocol.getName();

                    //需要实现类
                    String templateCode = FileUtils.readFileToString(new File(handlerConfig.getTemplate()),"utf-8");

                    String code = String.format(templateCode,namespace,protocol.getName(),protocolFullClassName);

                    String handlerFileDirPath = WorkDir + File.separator + handlerConfig.getDir() + File.separator + namespace.replace(".",File.separator);
                    File handlerFileDir = new File(handlerFileDirPath);
                    if (!handlerFileDir.exists()) {
                        handlerFileDir.mkdirs();
                    }

                    File handlerFile = new File(handlerFileDirPath + File.separator + protocol.getName() + "Handler" + handlerConfig.getExtension());
                    if (!handlerFile.exists()) {
                        handlerFile.createNewFile();
                    }

                    PrintWriter handlerWriter = new PrintWriter(handlerFile);
                    handlerWriter.write(code);
                    handlerWriter.flush();
                    handlerWriter.close();
                }
            }
        }
    }
}
