package utilities;

import com.google.gson.Gson;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpService {
    public static void main(String[] args) throws IOException, ParseException, InterruptedException {
//        System.out.println(new HttpService("localhost:8080/login")
//                .addHeader("username","a").addHeader("password","a").sendRequest()
//        );

//        String json = "{\"freePhysicalMemorySize\":632737792,\"availableProcessors\":8,\"openFileDescriptorCount\":133,\"freeSwapSpaceSize\":860356608,\"maxFileDescriptorCount\":10240,\"totalSwapSpaceSize\":1073741824,\"version\":\"10.12.6\",\"committedVirtualMemorySize\":8521535488,\"processCpuLoad\":3.981181496381303E-4,\"systemLoadAverage\":2.0205078125,\"systemCpuLoad\":0.04225528191023878,\"processCpuTime\":6464640000,\"name\":\"Mac OS X\",\"totalPhysicalMemorySize\":17179869184,\"objectName\":{},\"arch\":\"x86_64\"}";
//
//        System.out.println(
//                new HttpService("localhost:9200/cxamon/json_data/1/?pretty")
//                    .addHeader("Content-Type","application/json")
//                    .setBody(json)
//                    .sendRequest()
//        );

//        String example = "curl -XPUT 'localhost:9200/twitter/tweet/1?pretty' -H 'Content-Type: application/json' -d '\n" +
//                "{\n" +
//                "    \"user\" : \"kimchy\",\n" +
//                "    \"post_date\" : \"2009-11-15T14:12:12\",\n" +
//                "    \"message\" : \"trying out Elasticsearch\"\n" +
//                "}\n" +
//                "'";
//
//        System.out.println(HttpService.buildServiceFromString(example).sendRequest());

//        Options options = new Options();
//        options.addOption("XPUT",true,"Type");
//        options.addOption("H",true,"Header");
//        options.addOption("d",true,"Data");
//        CommandLineParser parser = new DefaultParser();
//        HelpFormatter formatter = new HelpFormatter();
//        CommandLine cmd = parser.parse(options, example.split(" "));
//        if(cmd.hasOption("XPUT"))
//            System.out.println(cmd.getOptionValue("XPUT"));

//        System.out.println(new Gson().toJson(new String[]{"a=b","b=c"}));


        Process proc = Runtime.getRuntime().exec("/Users/jarndt/code_projects/work/cxa-monitoring-2.0/test.sh a",
                new String[]{"TMX_PASSWORD=tmx","b=b"});
        proc.waitFor();
    }

    private static CommandLineParser parser = new DefaultParser();
    public static HttpService buildServiceFromString(String curlString) throws ParseException, UnsupportedEncodingException {
        List<String> list = new ArrayList<>();
        Matcher m = Pattern.compile("([^\']\\S*|[\'].+?[\'])\\s*").matcher(curlString.replace("\n",""));
        while (m.find())
            list.add(m.group(1).replace("'","")); // Add .replace("\"", "") to remove surrounding quotes.
        CommandLine cmd = parser.parse(getOptions(), list.toArray(new String[list.size()]));
        HttpService httpService;
        List<String> v = Arrays.asList("XPUT", "XPOST", "PUT", "POST").stream().filter(a -> cmd.hasOption(a)).collect(Collectors.toList());
        if(v.size() > 0)
            httpService = new HttpService(cmd.getOptionValue(v.get(0)));
        else {
            v = Arrays.asList("GET","XGET").stream().filter(a -> cmd.hasOption(a)).collect(Collectors.toList());
            httpService = new HttpService("GET",cmd.getOptionValue(v.get(0)));
        }
        if(cmd.hasOption("H"))
            for(String s : cmd.getOptionValues("H")) {
                String[] ss = s.split(": ");
                httpService.addHeader(ss[0],ss[1]);
            }
        if(cmd.hasOption("d"))
            httpService.setBody(cmd.getOptionValue("d"));
        return httpService;
    }

    private static Options getOptions(){
        Options options = new Options();
        for(String s : Arrays.asList("XPUT","XPOST","PUT","POST","GET","XGET"))
            options.addOption(s,true,"Type");
        options.addOption("H",true,"Header");
        options.addOption("d",true,"Data");
        return options;
    }

    private String method = "POST", url;
    private List allowedMethods = Arrays.asList("POST","GET");
    private HttpPost post;
    private HttpGet get;
    private CloseableHttpClient httpClient;
    private CloseableHttpResponse response;
    private HttpEntity entity;

    public HttpService(String url){
        this.url = urlFixer(url);
        init();
    }

    public HttpService(String method, String url) {
        method = method.toUpperCase();
        if(!allowedMethods.contains(method))
            throw new IllegalArgumentException("METHOD: "+method+", not allowed only: \n\t"+allowedMethods);
        this.method = method;
        this.url = urlFixer(url);
        init();
    }

    private void init(){
        if("POST".equals(method))
            post = new HttpPost(url);
        else if("GET".equals(method))
            get = new HttpGet(url);
        httpClient = HttpClientBuilder.create().build();
    }

    private String urlFixer(String url) {
        if(!url.startsWith("http"))
            url = "http://"+url;
        return url;
    }

    public HttpService addHeader(String name, String value){
        if(post != null)
            post.setHeader(new BasicHeader(name,value));
        if(get != null)
            get.setHeader(new BasicHeader(name,value));
        return this;
    }

    public HttpService setBody(String body) throws UnsupportedEncodingException {
        if(post!=null)
            post.setEntity(new ByteArrayEntity(body.getBytes("UTF-8")));
        return this;
    }

    public HttpService sendRequest() throws IOException {
        if(post != null)
            response = httpClient.execute(post);
        if(get != null)
            response = httpClient.execute(get);
        entity = response.getEntity();
        return this;
    }

    public String getResult() throws IOException {
        return IOUtils.toString(entity.getContent(), "UTF-8");
    }

    public void close() throws IOException {
        try{EntityUtils.consume(entity);}
        finally {
            response.close();
        }
    }

    @Override
    public String toString() {
        try {
            try {
                return getResult();
            }catch (NullPointerException e){
                return url;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
