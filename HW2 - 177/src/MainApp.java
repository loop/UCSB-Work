import java.util.Scanner;

public class MainApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.println("Your encoded message:\n");
		String code = scan.nextLine();
		Caesar ic = new Caesar();
		System.out.println(ic.analyse(code));
	}

}
