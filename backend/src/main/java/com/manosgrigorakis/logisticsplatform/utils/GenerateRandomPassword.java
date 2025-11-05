package com.manosgrigorakis.logisticsplatform.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GenerateRandomPassword {
    private static final SecureRandom secureRandom = new SecureRandom();

    private GenerateRandomPassword() {}

    public static String generateRandomPassword() {
        String password = "";
        int passwordLength = 16;
        List<Character> passwordList = new ArrayList<>();
        List<Character> data = new ArrayList<>();

        data.addAll(populateList('a', 'z'));
        data.addAll(populateList('A', 'Z'));
        data.addAll(populateList('0', '9'));

        for(int i = 0; i < passwordLength; i++) {
            // Select a random element
            int randomElement = secureRandom.nextInt(data.size());

            // Get value from random index
            char temp = data.get(randomElement);

            // Add random element to passwordList
            passwordList.add(temp);
        }

        // Add each generated element to password, as a string
        password = passwordList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());

        // Format password with the select separator
        return formatWithSeparator(password, "(.{4})", "$1-");
    }

    private static Collection<Character> populateList(char start, char end) {
        List<Character> list = new ArrayList<>();

        for(char c = start; c <= end; c++) {
            list.add(c);
        }

        return list;
    }

    private static String formatWithSeparator(String str, String regex, String replaceWith) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("String cannot be null or empty");
        }

        // Format string based on regex and replace with the given value
        str = str.replaceAll(regex, replaceWith);

        // Remove last element from string and return
        return str.substring(0, str.length() - 1);
    }
}
