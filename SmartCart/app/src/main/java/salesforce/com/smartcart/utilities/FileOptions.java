package salesforce.com.smartcart.utilities;

/**
 * Created by jarndt on 11/22/17.
 */

import java.io.File;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

public class FileOptions {
    private static Gson gson = new GsonBuilder()
            .serializeSpecialFloatingPointValues()
            .create();

    public static Gson getGson() {
        return gson;
    }

    public final static String OS = System.getProperty("os.name").toLowerCase();

    public static final String SEPERATOR = System.getProperty("file.separator"),
            DEFAULT_DIR = System.getProperty("user.dir") + SEPERATOR;

    private static String convertOctalToText(int octal) {
        StringBuilder sb = new StringBuilder();
        for (char s : (octal + "").toCharArray()) {
            int num = Integer.parseInt(s + "");
            sb.append((num & 4) == 0 ? '-' : 'r');
            sb.append((num & 2) == 0 ? '-' : 'w');
            sb.append((num & 1) == 0 ? '-' : 'x');
        }
        return sb.toString();
    }

    public static ExecutorService runConcurrentProcess(Callable callable) {
        return runConcurrentProcess(Arrays.asList(callable));
    }

    public static ExecutorService runConcurrentProcess(Callable callable, int threads) {
        return runConcurrentProcess(Arrays.asList(callable), threads);
    }

    public static ExecutorService runConcurrentProcess(Callable callable, int time, TimeUnit timeUnit) {
        return runConcurrentProcess(Arrays.asList(callable), time, timeUnit);
    }

    public static ExecutorService runConcurrentProcess(Callable callable, int threads, int time, TimeUnit timeUnit) {
        return runConcurrentProcess(Arrays.asList(callable), threads, time, timeUnit);
    }

    public static ExecutorService runConcurrentProcess(List<Callable> callables) {
        return runConcurrentProcess(callables, callables.size(), 5, TimeUnit.MINUTES);
    }

    public static ExecutorService runConcurrentProcess(List<Callable> callables, int threads) {
        return runConcurrentProcess(callables, threads, 5, TimeUnit.MINUTES);
    }

    public static ExecutorService runConcurrentProcess(List<Callable> callables, int time, TimeUnit timeUnit) {
        return runConcurrentProcess(callables, callables.size(), time, timeUnit);
    }

    public static ExecutorService runConcurrentProcess(List<Callable> callables, int threads, int time, TimeUnit timeUnit) {
        ExecutorService service = Executors.newFixedThreadPool(threads);
        for (Callable a : callables)
            service.submit(a);

        try {
//            System.out.println("attempt to shutdown executor");
            service.shutdown();
            service.awaitTermination(time, timeUnit);
        } catch (InterruptedException e) {
//            System.err.println("tasks interrupted");
        } finally {
//            if (!service.isTerminated()) {
////                System.err.println("cancel non-finished tasks");
//            }
            service.shutdownNow();
        }
//        while (!service.isTerminated() && !service.isShutdown())
//            Thread.sleep(1000);
        return service;
    }


    public static ExecutorService runConcurrentProcessNonBlocking(Callable callable) {
        return runConcurrentProcessNonBlocking(Arrays.asList(callable));
    }

    public static ExecutorService runConcurrentProcessNonBlocking(Callable callable, int threads) {
        return runConcurrentProcessNonBlocking(Arrays.asList(callable), threads);
    }

    public static ExecutorService runConcurrentProcessNonBlocking(Callable callable, int time, TimeUnit timeUnit) {
        return runConcurrentProcessNonBlocking(Arrays.asList(callable), time, timeUnit);
    }

    public static ExecutorService runConcurrentProcessNonBlocking(Callable callable, int threads, int time, TimeUnit timeUnit) {
        return runConcurrentProcessNonBlocking(Arrays.asList(callable), threads, time, timeUnit);
    }

    public static ExecutorService runConcurrentProcessNonBlocking(List<Callable> callables) {
        return runConcurrentProcessNonBlocking(callables, callables.size(), 5, TimeUnit.MINUTES);
    }

    public static ExecutorService runConcurrentProcessNonBlocking(List<Callable> callables, int threads) {
        return runConcurrentProcessNonBlocking(callables, threads, 5, TimeUnit.MINUTES);
    }

    public static ExecutorService runConcurrentProcessNonBlocking(List<Callable> callables, int time, TimeUnit timeUnit) {
        return runConcurrentProcessNonBlocking(callables, callables.size(), time, timeUnit);
    }

    public static ExecutorService runConcurrentProcessNonBlocking(List<Callable> callables, int threads, int time, TimeUnit timeUnit) {
        ExecutorService service = Executors.newFixedThreadPool(threads);
        for (Callable a : callables)
            service.submit(a);
        service.shutdown();
        return service;
    }


    public static File[] getBaseDirectories() {
        return File.listRoots();
    }

    public static String cleanFilePath(String filePath) {
        String regex = "\\[\\*replace_me\\*\\]";
        filePath = filePath.replaceAll("/", regex);
        filePath = filePath.replaceAll("\\\\", regex);
        return filePath.replaceAll(regex, Matcher.quoteReplacement(System.getProperty("file.separator")));
    }

    public static void downloadFile(String link, String path) throws IOException {
        URL website = new URL(link);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(path);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
    }


    public static void moveAllFiles(String in, String out) throws IOException {
        String s = System.getProperty("file.separator");
        for (File f : new File(in).listFiles())
            if (f.isFile() && f.getAbsolutePath().endsWith(".jar"))
                copyFile(f, new File(out + s + f.getName()));
            else if (f.isDirectory())
                moveAllFiles(f.getAbsolutePath(), out);
    }

    public static void copyFile(String source, String dest) throws IOException {
        copyFile(new File(source), new File(dest));
    }

    public static void copyFile(File source, File dest) throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            input.close();
            output.close();
        }
    }

    public static void renameAllFiles(String path, String ext) {
        for (File f : new File(path).listFiles())
            if (f.isFile())
                f.renameTo(new File(f.getAbsolutePath() + ext));
    }

    private static int next = 0;

    public static int getNext() {
        return next++;
    }

}


