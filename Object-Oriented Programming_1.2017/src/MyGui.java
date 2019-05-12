import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
public class MyGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField x; //מפקדי משימות
	private JTextField y; //משאיות 
	private JTextField z; //מטוסים
	private JTextField StationWorkTime; //זמן עבודה של עובד תחנה
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyGui frame = new MyGui();
					frame.setVisible(true);
					frame.StationWorkTime.setText("1");
					frame.x.setText("0");
					frame.y.setText("0");
					frame.z.setText("0");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MyGui() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setForeground(Color.ORANGE);
		contentPane.setBackground(Color.ORANGE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblWelcome = new JLabel("Welcome to Fatma unit fire station!");
		lblWelcome.setForeground(Color.RED);
		lblWelcome.setBackground(new Color(165, 42, 42));
		lblWelcome.setFont(new Font("Tahoma", Font.BOLD, 21));
		lblWelcome.setBounds(24, 11, 400, 69);
		contentPane.add(lblWelcome);
		
		JLabel lblStationWorkersTime = new JLabel("Work Time for station worker:");
		lblStationWorkersTime.setForeground(Color.BLACK);
		lblStationWorkersTime.setFont(new Font("Arial", Font.BOLD, 15));
		lblStationWorkersTime.setBounds(10, 145, 241, 39);
		contentPane.add(lblStationWorkersTime);
		
		JLabel lblNumOfCommanders = new JLabel("Number of event commander to add:");
		lblNumOfCommanders.setForeground(Color.BLACK);
		lblNumOfCommanders.setFont(new Font("Arial", Font.BOLD, 14));
		lblNumOfCommanders.setBounds(0, 72, 266, 39);
		contentPane.add(lblNumOfCommanders);
		
		JLabel lblNumOfTrucks = new JLabel("Number of trucks to add:");
		lblNumOfTrucks.setFont(new Font("Arial", Font.BOLD, 15));
		lblNumOfTrucks.setForeground(Color.BLACK);
		lblNumOfTrucks.setBounds(260, 72, 212, 39);
		contentPane.add(lblNumOfTrucks);
		
		JLabel lblNumOfPlanes = new JLabel("Number of planes to add:");
		lblNumOfPlanes.setFont(new Font("Arial", Font.BOLD, 15));
		lblNumOfPlanes.setForeground(Color.BLACK);
		lblNumOfPlanes.setBounds(245, 144, 212, 39);
		contentPane.add(lblNumOfPlanes);
		
		StationWorkTime = new JTextField();
		StationWorkTime.setBounds(58, 175, 107, 42);
		contentPane.add(StationWorkTime);
		StationWorkTime.setColumns(10);
		
		x = new JTextField();
		x.setBounds(58, 105, 107, 43);
		contentPane.add(x);
		x.setColumns(10);
		
		y = new JTextField();
		y.setBounds(276, 105, 107, 42);
		contentPane.add(y);
		y.setColumns(10);
		
		z = new JTextField();
		z.setBounds(276, 175, 107, 43);
		contentPane.add(z);
		z.setColumns(10);
		
		JButton btnStart = new JButton("START");
		btnStart.setBackground(Color.WHITE);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				double stationWorkTime = Double.parseDouble(StationWorkTime.getText());	
				int X = Integer.parseInt(x.getText());
				if (X>8) {
					X=8;
					String message = "the limit for commanders is 8. 8 commanders was added";
					JOptionPane.showMessageDialog(x, message);
				}
				int Y = Integer.parseInt(y.getText());
				int Z = Integer.parseInt(z.getText());
				FatmaUnit fatmaUnit = new FatmaUnit(X,Y,Z,stationWorkTime); 
				fatmaUnit.StartDay();
				}
		});
		btnStart.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnStart.setForeground(new Color(50, 205, 50));
		btnStart.setBounds(50, 228, 115, 25);
		contentPane.add(btnStart);
		
		JButton btnExit = new JButton("EXIT");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnExit.setBackground(Color.WHITE);
		btnExit.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnExit.setForeground(new Color(255, 0, 0));
		btnExit.setBounds(286, 228, 97, 25);
		contentPane.add(btnExit);
	}
}
