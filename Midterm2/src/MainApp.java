 
public class MainApp {
  public static void main(String[] args) {
    for(int x = 0; x < 17; x++) {
    	System.out.println(Math.sqrt(((x*x*x) + ((3*x) + 1)) % 17));
    }
  }
}