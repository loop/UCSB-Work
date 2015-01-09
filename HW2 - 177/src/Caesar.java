import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
 
public class Caesar {
 
    private Map<Character, Integer> frequency;
 
    public String analyse(String code) {
        frequency = new HashMap<Character, Integer>();
        count(code);
        return getAnalysisString();
    }
   
    public Map<Character, Integer> getFrequency(){
        return new HashMap<Character, Integer>(frequency);
    }
 
    private String getAnalysisString() {
        StringBuilder b = new StringBuilder();
        long sumcount = getSumCount();
        b.append("Character\tCounts\tFrequency\n");
        for (Character c : getSortedKeyList()) {
            b.append(c + "\t" + frequency.get(c) + "\t"
                    + (frequency.get(c) / (double) sumcount) + "\n");
        }
        b.append("\nletters: " + sumcount);
        b.append("\ncharacters: " + frequency.size());
        return b.toString();
    }
 
        //insertion sort is used here
    private List<Character> getSortedKeyList() {
        List<Character> list = new LinkedList<Character>();
        for(Entry<Character, Integer> entry : frequency.entrySet()){
            for(int i = 1; i < list.size(); i++){
                if(frequency.get(list.get(i)) > entry.getValue()){
                    list.add(i, entry.getKey());
                    break;
                }
            }
            if(!list.contains(entry.getKey())){
                list.add(entry.getKey());
            }
        }
        return list;
    }
 
    public long getSumCount() {
        long sum = 0;
        for (Integer i : frequency.values()) {
            sum += i;
        }
        return sum;
    }
 
    private void count(String code) {
        char[] text = code.toCharArray();
        for (char c : text) {
            if (frequency.containsKey(c)) {
                frequency.put(c, frequency.get(c) + 1);
            } else {
                frequency.put(c, 1);
            }
        }
    }
 
}