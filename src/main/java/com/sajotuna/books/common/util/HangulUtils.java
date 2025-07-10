package com.sajotuna.books.common.util;

public class HangulUtils {

    private static final char[] CHOSUNG_TABLE = {
            'ㄱ','ㄲ','ㄴ','ㄷ','ㄸ','ㄹ','ㅁ','ㅂ','ㅃ',
            'ㅅ','ㅆ','ㅇ','ㅈ','ㅉ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'
    };

    public static String extractChosung(String text) {
        if (text == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (char c : text.toCharArray()) {
            if (c >= 0xAC00 && c <= 0xD7A3) { // 한글 음절이면
                int base = c - 0xAC00;
                int chosungIndex = base / (21 * 28);
                sb.append(CHOSUNG_TABLE[chosungIndex]);
            } else if (c >= 'ㄱ' && c <= 'ㅎ') { // 초성 문자 그대로
                sb.append(c);
            } else {
                continue;
            }
        }

        return sb.toString();
    }
}
