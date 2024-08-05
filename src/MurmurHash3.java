import static java.lang.Integer.toHexString;
import static java.lang.Math.abs;

public class MurmurHash3 {
    private static final int SEED = 256;
    public static int murmurHash(String element) {
        return abs(murmurHash3Helper(element.toCharArray()));
    }

    private static int murmurHash3Helper(char[] input) {
        final int c1 = 0xcc9e2d51;
        final int c2 = 0x1b873593;

        int hash = SEED;
        int len = input.length;
        int k;

        final int remainingBytes = len & 3;
        final int totalBlocks = len >>> 2;

        for (int i = 0; i < totalBlocks; i++) {
            k = getBlock(input, i * 4);
            k *= c1;
            k = rotl(k, 15);
            k *= c2;

            hash ^= k;
            hash = rotl(hash, 13);
            hash = hash * 5 + 0xe6546b64;
        }

        k = 0;
        switch (remainingBytes) {
            case 3:
                k ^= (input[len - 1] & 0xff) << 16;
                k ^= (input[len - 2] & 0xff) << 8;
                k ^= (input[len - 3] & 0xff);
                break;
            case 2:
                k ^= (input[len - 1] & 0xff) << 8;
                k ^= (input[len - 2] & 0xff);
                break;
            case 1:
                k ^= (input[len - 1] & 0xff);
                break;
        }

        if (remainingBytes > 0) {
            k *= c1;
            k = rotl(k, 15);
            k *= c2;
            hash ^= k;
        }

        hash ^= len;
        return finalizeHashValue(hash);
    }

    private static int finalizeHashValue(int hash) {
        hash ^= hash >>> 16;
        hash *= 0x85ebca6b;
        hash ^= hash >>> 13;
        hash *= 0xc2b2ae35;
        hash ^= hash >>> 16;
        return hash;
    }

    private static int rotl(int hash, int i) {
        return (hash << i) | (hash >>> (32 - i));
    }

    private static int getBlock(char[] input, int i) {
        return ((input[i] & 0xff)) |
                ((input[i + 1] & 0xff) << 8) |
                ((input[i + 2] & 0xff) << 16) |
                ((input[i + 3] & 0xff) << 24);
    }

    public static void main(String[] arguments){
        System.out.println(toHexString(murmurHash("He11o")));
//        System.out.println(Integer.toHexString(murmurHash("a")));
//        System.out.println(Integer.toHexString(murmurHash("Hello, World!")));
//        System.out.println(Integer.toHexString(murmurHash("Hello World!")));
//        System.out.println(Integer.toHexString(murmurHash((Integer) 29)));
//        System.out.println(Integer.toHexString(murmurHash("skfhwelfhsjfhsklhfkslhfskehf")));
    }
}
