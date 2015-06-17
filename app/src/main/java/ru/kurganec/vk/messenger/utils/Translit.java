/*******************************************************************************
 * Copyright (c) 2009, Adobe Systems Incorporated
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * ·        Redistributions of source code must retain the above copyright 
 *          notice, this list of conditions and the following disclaimer. 
 *
 * ·        Redistributions in binary form must reproduce the above copyright 
 *		   notice, this list of conditions and the following disclaimer in the
 *		   documentation and/or other materials provided with the distribution. 
 *
 * ·        Neither the name of Adobe Systems Incorporated nor the names of its 
 *		   contributors may be used to endorse or promote products derived from
 *		   this software without specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR 
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package ru.kurganec.vk.messenger.utils;

import android.text.TextUtils;

import java.util.Hashtable;

public class Translit {

    final static int NEUTRAL = 0;

    final static int UPPER = 1;

    final static int LOWER = 2;

    final static Hashtable<String, String> map = makeTranslitMap();

    final static Hashtable<String, String> reverseMap = makeReverseMap();



    private static Hashtable<String, String> makeTranslitMap() {
        Hashtable<String, String> map = new Hashtable<String, String>();
        map.put("а", "a");
        map.put("б", "b");
        map.put("в", "v");
        map.put("г", "g");
        map.put("д", "d");
        map.put("е", "e");
        map.put("ё", "yo");
        map.put("ж", "zh");
        map.put("з", "z");
        map.put("и", "i");
        map.put("й", "j");
        map.put("к", "k");
        map.put("л", "l");
        map.put("м", "m");
        map.put("н", "n");
        map.put("о", "o");
        map.put("п", "p");
        map.put("р", "r");
        map.put("с", "s");
        map.put("т", "t");
        map.put("у", "u");
        map.put("ф", "f");
        map.put("х", "h");
        map.put("ц", "ts");
        map.put("ч", "ch");
        map.put("ш", "sh");
        map.put("щ", "sh'");
        map.put("ы", "y");
        map.put("э", "e");
        map.put("ю", "yu");
        map.put("я", "ya");
        return map;
    }


    private static Hashtable<String, String> makeReverseMap() {
        Hashtable<String, String> map = new Hashtable<String, String>();
        map.put("a",  "а");
        map.put("b",  "б");
        map.put("v",  "в");
        map.put("g",  "г");
        map.put("d",  "д");
        map.put("e",  "е");
        map.put("z",  "з");
        map.put("i",  "и");
        map.put("j",  "й");
        map.put("k",  "к");
        map.put("l",  "л");
        map.put("m",  "м");
        map.put("n",  "н");
        map.put("o",  "о");
        map.put("p",  "п");
        map.put("r",  "р");
        map.put("s",  "с");
        map.put("t",  "т");
        map.put("u",  "у");
        map.put("f",  "ф");
        map.put("h",  "х");
        map.put("y",  "ы");
        map.put("e",  "э");
        return map;
    }




    private static int charClass(char c) {
        if (Character.isLowerCase(c))
            return LOWER;
        if (Character.isUpperCase(c))
            return UPPER;
        return NEUTRAL;
    }

    public static String translit(String text, boolean reverse) {
        int len = text.length();
        if (len == 0)
            return text;
        StringBuilder sb = new StringBuilder();
        int pc = NEUTRAL;
        char c = text.charAt(0);
        int cc = charClass(c);
        for (int i = 1; i <= len; i++) {
            char nextChar = (i < len ? text.charAt(i) : ' ');
            int nc = charClass(nextChar);
            String co = String.valueOf(Character.toLowerCase(c));
            String tr = reverse ?  reverseMap.get(co): map.get(co);
            if (tr == null) {
                break;
            } else {
                switch (cc) {
                    case LOWER:
                    case NEUTRAL:
                        sb.append(tr);
                        break;
                    case UPPER:
                        if (nc == LOWER || (nc == NEUTRAL && pc != UPPER)) {
                            sb.append(Character.toUpperCase(tr.charAt(0)));
                            if (tr.length() > 0) {
                                sb.append(tr.substring(1));
                            }
                        } else {
                            sb.append(tr.toUpperCase());
                        }
                }
            }
            c = nextChar;
            pc = cc;
            cc = nc;
        }
        String ret = sb.toString();
        if (TextUtils.isEmpty(ret)){
            return text;
        }
        return ret;
    }


}
