import java.util.LinkedList;

public class FNV1aHashTable <String> {
    private LinkedList<String>[] hashTable;
    private int load;
    private int size;

    public FNV1aHashTable(int load) {
        size = 0;
        this.load = load;
        hashTable = new LinkedList[load];
    }

    public void add(String value){
        size++;
        int hash = FNV1a.fnv1aHash((java.lang.String) value) % load;
        if (hashTable[hash] == null) {
            hashTable[hash] = new LinkedList<>();
        }
        hashTable[hash].add(value);
    }

    public boolean contains(String value){
        int hash = FNV1a.fnv1aHash((java.lang.String) value) % load;
        if (hashTable[hash] == null) {return false;}
        return hashTable[hash].contains(value);
    }

    public void remove(String value){
        if (!contains(value)){
            throw new RuntimeException("Object not found.");
        }
        int hash = FNV1a.fnv1aHash((java.lang.String) value) % load;
        hashTable[hash].remove(value);
        size--;
    }

    public int[] getDistribution(){
        int[] buckets = new int[load];
        for (int i = 0; i < load; i++) {
            if (hashTable[i] == null) {
                buckets[i] = 0;
            } else {
                buckets[i] = hashTable[i].size();
            }
        }
        return buckets;
    }

    public int getCollision(){
        int count = 0;
        for (int i = 0; i < load; i++) {
            if (hashTable[i] != null) {
                if (hashTable[i].size() > 1) {
                    count += hashTable[i].size() - 1;
                }
            }
        }
        return count;
    }

    public int getSize() {
        return size;
    }
}
