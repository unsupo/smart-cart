package utilities;

import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration2.ex.ConfigurationException;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class ConfigManager {
    public static void main(String[] args) throws ConfigurationException, InterruptedException, IOException, ParseException {
//        Parameters params = new Parameters();
//        ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration> builder =
//                new ReloadingFileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class)
//                        .configure(params.fileBased()
//                                .setFile(new File("/Users/jarndt/Desktop/test")));
//        PeriodicReloadingTrigger trigger = new PeriodicReloadingTrigger(builder.getReloadingController(),
//                null, 1, TimeUnit.SECONDS);
//        trigger.start();
//
//        builder.addEventListener(ConfigurationBuilderEvent.ANY, (event)->{
//                System.out.println("Event:" + event);
//        });
//
//        while (true) {
//            Thread.sleep(1000);
//            System.out.println(builder.getConfiguration().getString("metrics.file"));
//        }

//        AgentConfigManager.setConfigFiles(new String[]{"/Users/jarndt/Desktop/test"});
//        while (true){
//            Thread.sleep(1000);
//            System.out.println(AgentConfigManager.getConfigs().get("metrics.file"));
//        }


//        HttpService.buildServiceFromString(
//                "curl -XPUT 'localhost:9999/jolokia' -H '"+MBEAN_PATH+"'");

        String host = "localhost";  // or some A.B.C.D
        int port = 9999;
        String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi";
        JMXServiceURL serviceUrl = new JMXServiceURL(url);
        JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceUrl, null);
        try {
            MBeanServerConnection mbeanConn = jmxConnector.getMBeanServerConnection();
            // now query to get the beans or whatever
            Set<ObjectName> beanSet = mbeanConn.queryNames(null, null);
            System.out.println(beanSet);
        } finally {
            jmxConnector.close();
        }
    }

    public static final String  STREAM_FILE = "stream:",
            MBEAN_PATH  = "config:type=AgentConfigManager";


    private List<String> extractResources = new ArrayList<>();
    private FileSystem fileSystem;
    private WatchService watchService;
    private Set<String> configFiles = new HashSet<>(), metricFiles = new HashSet<>();
    private HashMap<String,String> configs = new HashMap<>();
    private boolean watch = true;
    public ConfigManager(boolean watch){
        this.watch = watch;
    }
    public ConfigManager(List<String> extractResources) throws IOException {
        this.extractResources = extractResources;
        fileSystem = FileSystems.getDefault();
        watchService = fileSystem.newWatchService();
        FileOptions.runConcurrentProcessNonBlocking(()->{
            while (true) {
                final WatchKey wk = watchService.take();
                for (WatchEvent<?> event : wk.pollEvents()) {
                    final Path changed = (Path) event.context();
                    List<String> files = new ArrayList<>(configFiles);
                    files.addAll(metricFiles);
                    for (String file : files)
                        if (file.endsWith(changed.getFileName().toString())) {
                            if (event.kind().equals(ENTRY_CREATE) || event.kind().equals(ENTRY_MODIFY)) {
                                if (file.endsWith(".properties"))
                                    setConfigs(file);
                            }else if(event.kind().equals(ENTRY_DELETE)){
                                if(configFiles.contains(file))
                                    configFiles.remove(file);
                                if(metricFiles.contains(file))
                                    metricFiles.remove(file);
                                configs = new HashMap<>();
                                configFiles.forEach(a -> {
                                    try {
                                        setConfigs(a);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                                QuartzManager.getScheduler().clear();
                            }
                        }
                }
                // reset the key
                boolean valid = wk.reset();
            }
        });

        try {
            setDefaultConfigs();
//            ManagementFactory.getPlatformMBeanServer().registerMBean(new Config(),new ObjectName(MBEAN_PATH));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDefaultConfigs() throws IOException, URISyntaxException {
        File r = new File(FileOptions.DEFAULT_DIR+"resources");
        r.mkdirs();
        for(String s : extractResources) {
            if(FileOptions.isInJarFile()) {
                s = FileOptions.extractJarResource(r.getAbsolutePath(), s);
                //TODO null pointer when no resources
                String dest = r.getAbsolutePath() + "/" + new File(s).getName();
                FileOptions.copyFile(s, dest);
                if (dest != null && dest.endsWith(".properties"))
                    addConfigFile(dest);
//                FileOptions.deleteDirectory(new File(s).getParent());
            }else
                addConfigFile(r.getAbsolutePath() + "/" + new File(s).getName());
        }

//        for(String s : Arrays.asList("config.properties")) {
//            Properties properties = new Properties();
//            properties.load(getClass().getClassLoader().getResourceAsStream(s));
//            for(String ss : properties.stringPropertyNames())
//                updateCache(ss,properties.getProperty(ss));
//        }
    }

    public void setConfigs(String file) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(file));
        for(String s : properties.stringPropertyNames())
            updateCache(s,properties.getProperty(s));
    }

    public void setConfigValue(String config, String configValue) throws IOException {
        boolean found = false;
        if(configFiles.size() == 0){
            updateCache(config,configValue);
            return;
        }
        for(String s : configFiles) {
            Properties p = new Properties();
            p.load(new FileInputStream(s));
            if (p.stringPropertyNames().contains(config)) {
                found = true;
                p.setProperty(config,configValue);
                p.store(new FileOutputStream(s),null);
                updateCache(config,configValue);
            }
        }
        if(!found){
            Properties p = new Properties();
            ArrayList<String> cons = new ArrayList<>(configFiles);
            p.load(new FileInputStream(cons.get(0)));
            p.setProperty(config,configValue);
            p.store(new FileOutputStream(cons.get(0)), "Add config: "+config+"="+configValue);
            updateCache(config,configValue);
        }
    }

    public void updateCache(String config, String configValue) throws IOException {
        if(config.startsWith("config.file"))
            addConfigFile(configValue);
        configs.put(config,configValue);
    }

    public HashMap<String, String> getConfigs() {
        return configs;
    }
    public List<String> getConfigFiles(){
        return Collections.unmodifiableList(new ArrayList<>(configFiles));
    }
    public boolean addConfigFile(String s) throws IOException {
        File f = new File(s);
        if(f.exists()) {
            if (f.isFile() && f.getName().endsWith(".properties")) {
                configFiles.add(s);
                setConfigs(f.getAbsolutePath());
                setConfigValue("configPropertyFiles",FileOptions.getGson().toJson(configFiles));
                System.out.println("Added and watching config file: "+s);
            }else
                System.out.println("Added and watching metric file: "+s);
        }
        if(watch) {
            s = new File(f.getAbsolutePath()).getParent();
            if (s == null || watchDirs.contains(s))
                return false;
            watchDirs.add(s);
            fileSystem.getPath(s).register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        }
        return true;
    }
    private Set<String> watchDirs = new HashSet<>();
    public void addConfigFiles(List<String> configFiles) throws IOException {
        this.configFiles.addAll(configFiles);
        for (String s : configFiles)
            addConfigFile(s);
    }

    public void setAllConfigs(HashMap<String,String> allConfigs) {
        this.configs = allConfigs;
    }

    /////////////////////////// MBEANS //////////////////////////////////////
//    public class Config implements ConfigMBean{
//        @Override
//        public String getConfigValue(String config) throws IOException {
//            return AgentConfigManager.getConfigs().get(config);
//        }
//
//        @Override
//        public void setConfigValue(String config, String configValue) throws IOException {
//            AgentConfigManager.setConfigValue(config,configValue);
//        }
//
//        @Override
//        public List<String> getAllConfigs() throws IOException {
//            return new ArrayList<>(AgentConfigManager.getConfigs().keySet());
//        }
//
//        @Override
//        public String getAllMetrics() throws IOException {
//            return FileOptions.getGson().toJson(AgentConfigManager.getMetrics());
//        }
//    }

//    public interface ConfigMBean{
//        public String getConfigValue(String config) throws IOException;
//        public void setConfigValue(String config, String configValue) throws IOException;
//        public List<String> getAllConfigs() throws IOException;
//        public String getAllMetrics() throws IOException;
//    }
}
