package xs.spring.learn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by xs on 2018/3/13
 */
public class MyApplicationContext {
    private static Map<String, Object> beanMap = new HashMap<>();
    private static String classpath = null;
    private static Properties prop = new Properties();
    public static void main(String[] args) throws IOException {
        init();
    }

    private static void init() throws IOException {
        System.out.println("init start!");
        classpath = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(prop + "config/testconfig.properties"));
        prop.load(bufferedReader);
        String value = prop.getProperty("auto.scan.package");
        System.out.println("init end!");
    }
}
