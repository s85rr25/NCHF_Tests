import static java.lang.Integer.toHexString;
import static java.lang.Math.abs;

public class FNV1a {
    public static int fnv1aHash(String element) {
        return abs(fnv1aHashHelper(element.toCharArray()));
    }
    private static int fnv1aHashHelper(char[] input) {
        final int FNV_PRIME = 0x01000193;
        final int FNV_OFFSET_BASIS = 0x811c9dc5;

        int hash = FNV_OFFSET_BASIS;
        for (char b: input) {
            hash ^= (b & 0xff);
            hash *= FNV_PRIME;
            hash &= 0xFFFFFFFF;
        }

        return hash;
    }

    public static void main(String[] argument){
//        System.out.println(Integer.toHexString(fnv1aHash("h")));
//        System.out.println(Integer.toHexString(fnv1aHash("a")));
        System.out.println(toHexString(fnv1aHash("He11o")));

//        System.out.println(Integer.toHexString(fnv1aHash("Hello, World!")));
//        System.out.println(Integer.toHexString(fnv1aHash((Integer) 29)));
//        System.out.println(Integer.toHexString(fnv1aHash("skfhwelfhsjfhsklhfkslhfskehf")));
    }

}
