package com.hyd.jfapps.zkclient;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

public class ZkUtils {

    public static String concatPathWithSlash(String... parts) {
        return "/" + concatPath(parts);
    }

    public static String concatPath(String... parts) {
        return Stream.of(parts)
            .filter(Objects::nonNull)
            .map(s -> s.endsWith("/")? s.substring(0, s.length() - 1) : s)
            .map(s -> s.startsWith("/")? s.substring(1) : s)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining("/"));
    }
}
