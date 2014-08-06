package com.insightfullogic.honest_profiler.core;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Util {

    public static File log0() {
        return logFile("log0.hpl");
    }

    public static File logFile(String file) {
        URL url = Util.class.getResource("../../../../" + file);
        return urlToFile(url);
    }

    private static File urlToFile(URL url) {
        try {
            return new File(url.toURI());
        } catch(URISyntaxException e) {
            return new File(url.getPath());
        }
    }

    public static <T> List<T> list(T ... values) {
        return new ArrayList<>(Arrays.asList(values));
    }
}
