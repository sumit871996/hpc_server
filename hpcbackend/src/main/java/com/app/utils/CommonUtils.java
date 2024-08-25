package com.app.utils;
import java.util.Map;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CommonUtils {

    public static String replaceVars(String data, Map<String, Object> replacements) {
        if (data == null || replacements == null) {
            return data;
        }

        for (Map.Entry<String, Object> entry : replacements.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();  // Convert the value to a string
            String placeholder = "${" + key + "}";

            data = data.replace(placeholder, value);
        }

        return data;
    }
    
    public static String replaceDockerArgs(String input, Map<String, Object> replacements) {
        // Regular expression to match lines with the format ARG arg_1
        String regex = "(ARG)\\s+(\\w+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        // StringBuilder to build the resulting string
        StringBuilder result = new StringBuilder();

        int lastEnd = 0;
        while (matcher.find()) {
            result.append(input, lastEnd, matcher.start());
            String argKey = matcher.group(2);
            
            if (replacements.containsKey(argKey)) {
                result.append(matcher.group(1))
                      .append(" ")
                      .append(argKey)
                      .append("=")
                      .append(replacements.get(argKey));
                System.out.println(result);
            } else {
                result.append(matcher.group(0)); // No replacement, append the whole match
            }

            lastEnd = matcher.end();
        }
        result.append(input.substring(lastEnd));

        return result.toString();
    }
}

