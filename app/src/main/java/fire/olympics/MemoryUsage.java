package fire.olympics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.text.StringCharacterIterator;
import java.text.CharacterIterator;

public class MemoryUsage {

    private static class Record {
        Object type;
        String identifier;
        String method;
        int count;
    }

    private static ArrayList<Record> records = new ArrayList<>();

    public static <T> void record(Class<T> type, String method, String identifier, int count) {
        Record r = new Record();
        r.identifier = identifier;
        r.method = method;
        r.count = count;
        r.type = type;
        records.add(r);
    }

    public static void print() {
        HashSet<Object> locations = new HashSet<>();
        for (Record r : records) {
            locations.add(r.type);
        }
        for (Object location : locations) {
            printUnsafe(location);
        }
        printTotal();
    }

    public static void summary() {
        System.out.println("GPU Memory Usage Summary");
        HashMap<Object, Integer> counts = new HashMap<>();
        for (Record r : records) {
            counts.putIfAbsent(r.type, 0);
            int count = counts.get(r.type);
            counts.put(r.type, count + r.count);
        }

        for (var e : counts.entrySet()) {
            System.out.println(String.format("%s: %s", e.getKey().toString(), humanReadableByteCountBin(e.getValue())));
        }
        printTotal();
    }

    private static <T> void printUnsafe(Object type) {
        System.out.println(type);
        int count = 0;
        for (Record r : records) {
            if (r.type == type) {
                System.out.println(String.format("%s %s: %s", r.method, r.identifier, humanReadableByteCountBin(r.count)));
                count += r.count;
            }
        }
        System.out.println(String.format("Memory GPU consumption: %s", humanReadableByteCountBin(count)));
        printTotal();
    }

    private static void printTotal() {
        int count = 0;
        for (Record r : records) {
            count += r.count;
        }
        System.out.println(String.format("Total GPU memory consumption: %s", humanReadableByteCountBin(count)));
    }

    public static <T> void print(Class<T> type) {
        printUnsafe(type);
    }

    // Source: https://programming.guide/java/formatting-byte-size-to-human-readable-format.html
    private static String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }
}
