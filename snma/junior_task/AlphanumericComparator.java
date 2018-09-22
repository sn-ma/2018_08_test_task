package snma.junior_task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AlphanumericComparator implements Comparator<String> {
    private static final Pattern NUMBER = Pattern.compile("\\d+");

    /** Split String to list of tokens -- Integers and Strings.
     * e.g. "user123thats-me9876" -> ["user", 123, "thats-me", 9876]
     * 
     * @param str Given string
     * @return unmodifiable list of tokens (List<Object> with Integer and String entries)
     */
    private static List<Object> tokenizeString(String str) {
        List<Object> answ = new ArrayList<>();
        Matcher m = NUMBER.matcher(str);
        int prevEnd = 0;
        while (m.find()) {
            final int start = m.start(), end = m.end();
            if (prevEnd != start) {
                answ.add(str.substring(prevEnd, start));
            }
            answ.add(Integer.valueOf(str.substring(start, end)));
            prevEnd = end;
        }
        if (prevEnd != str.length()) {
            answ.add(str.substring(prevEnd, str.length()));
        }

        return Collections.unmodifiableList(answ);
    }

    @Override
    public int compare(String a, String b) {
        List<Object> aLst = tokenizeString(a);
        List<Object> bLst = tokenizeString(b);
        for (int i = 0; i < aLst.size(); ++i) {
            if (bLst.size() <= i) {
                return 1;
            }
            Object o1 = aLst.get(i), o2 = bLst.get(i);
            if (o1.getClass() == Integer.class) {
                if (o2.getClass() == Integer.class) {
                    int res = ((Integer)o1).compareTo((Integer)o2);
                    if (res != 0) {
                        return res;
                    }
                } else if (o2.getClass() == String.class) {
                    return -1;
                } else {
                    throw new RuntimeException("Unexpected type " + o2.getClass());
                }
            } else if (o1.getClass() == String.class) {
                if (o2.getClass() == String.class) {
                    int res = ((String)o1).compareTo((String)o2);
                    if (res != 0) {
                        return res;
                    }
                } else if (o2.getClass() == Integer.class) {
                    return 1;
                } else {
                    throw new RuntimeException("Unexpected type " + o2.getClass());
                }
            } else {
                throw new RuntimeException("Unexpected type " + o1.getClass());
            }
        }
        if (bLst.size() == aLst.size()) {
            return 0;
        } else {
            return -1;
        }
    }
}
