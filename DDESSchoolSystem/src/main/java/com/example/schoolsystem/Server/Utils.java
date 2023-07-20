package com.example.schoolsystem.Server;

import com.example.schoolsystem.Testing.Mocker;
import org.json.JSONObject;

public class Utils {
    public static String encrypt(String originalText) {
        StringBuilder result = new StringBuilder();
        for (char character : originalText.toCharArray()) {
            if (character != ' ') {
                int originalAlphabetPosition = character - 'a';
                int newAlphabetPosition = (originalAlphabetPosition + 1) % 26;
                char newCharacter = (char) ('a' + newAlphabetPosition);
                result.append(newCharacter);
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }

}
