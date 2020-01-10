import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) throws Exception {
		// set UI frame
		GUI gui = new GUI();
		gui.setSize(1000, 700);
		gui.setLocationRelativeTo(null);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setTitle("HW5");
		gui.setVisible(true);
	}
}
