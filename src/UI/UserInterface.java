package UI;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.BoxLayout;

import java.awt.Color;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.awt.event.ActionEvent;

import socket.Client;
import socket.Location;

import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.event.MouseAdapter;

public class UserInterface extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int wrongValue = -1000;
	private JPanel contentPane;
	private JTextField speedJTextField;
	private NewPanel rightPanel, head;
	private LogOn logOn;
	private UserInterface jf = this;
	private JLabel userNameLabel;
	private boolean authorized = false;
	private Client client = new Client();
	private int userPointx = wrongValue, userPointy = wrongValue;
	private Timer timer;
	private JComboBox<String> comboBox;
	private JRadioButton autoCorrectRadio;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		UserInterface frame = new UserInterface();
		frame.setVisible(true);
	}

	/**
	 * Create the frame.
	 */
	public UserInterface() {
		logOn = new LogOn(this);
		setTitle("\u667A\u80FD\u9664\u51B0\u8F66");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (jf.authorized)
					client.logOff();
			}
		});
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1000, 618);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		Image image = Toolkit.getDefaultToolkit().getImage("src//icon.png");
		this.setIconImage(image);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 982, 34);
		contentPane.add(menuBar);

		JMenu logOnJmenu = new JMenu("\u767B\u5F55");
		logOnJmenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				logOn.setVisible(true);
				logOn.setAlwaysOnTop(true);
			}
		});

		menuBar.add(logOnJmenu);

		JMenu logOffJmenu = new JMenu("\u6CE8\u9500");
		logOffJmenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(authorized) {
					client.logOff();
					jf.head.resetPath("src//default.png");
					jf.userNameLabel.setText("用户：");
					jf.authorized = false;
					}
					else {
						JOptionPane.showMessageDialog(null, "未登录");
					}
			}
		});
		menuBar.add(logOffJmenu);

		JMenu aboutUsJmenu = new JMenu("\u5173\u4E8E\u6211\u4EEC");
		aboutUsJmenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JOptionPane.showMessageDialog(null, "欢迎使用智能除冰车,制作人:张天怡，胡振宇，顾皞，王昭为，陈浩南","关于我们"
						,JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menuBar.add(aboutUsJmenu);

		rightPanel = new NewPanel("src//map.png");
		rightPanel.setBounds(215, 35, 765, 535);
		contentPane.add(rightPanel);
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
		rightPanel.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if(autoCorrectRadio.isSelected()) {
						Location l = coordinateHelper(e.getX(),e.getY());
						userPointx = (int) (l.x - 10);
						userPointy = (int) (l.y - 10);
						rightPanel.repaint();
					}
					else {
						userPointx = e.getX()-10;
						userPointy = e.getY()-10;
						rightPanel.repaint();
					}
					
				}
			}

		});
		

		timer = new Timer(500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rightPanel.repaint();
				synchronized(client.carVelocity) {
					double speed = 0;
					if(client.carVelocity.get(comboBox.getModel().getElementAt(comboBox.getSelectedIndex()))!=null)
						speed = client.carVelocity.get(comboBox.getModel().getElementAt(comboBox.getSelectedIndex()));
					jf.speedJTextField.setText(new DecimalFormat("#.00").format(speed)+"m/s");
				}
			}
		});

		timer.start();
		JPanel leftPanel = new JPanel();
		leftPanel.setBounds(0, 35, 212, 536);
		contentPane.add(leftPanel);
		leftPanel.setLayout(null);

		autoCorrectRadio = new JRadioButton("\u81EA\u52A8\u7EA0\u6B63\u529F\u80FD");
		autoCorrectRadio.setBounds(43, 212, 119, 27);
		autoCorrectRadio.setSelected(true);
		leftPanel.add(autoCorrectRadio);

		speedJTextField = new JTextField();
		speedJTextField.setEditable(false);
		speedJTextField.setText("0m/s");
		speedJTextField.setBounds(43, 264, 119, 38);
		leftPanel.add(speedJTextField);
		speedJTextField.setColumns(10);

		JLabel speedJlabel = new JLabel("\u9664\u51B0\u8F66\u884C\u9A76\u901F\u5EA6");
		speedJlabel.setBounds(43, 245, 119, 18);
		leftPanel.add(speedJlabel);

		Component verticalGlue = Box.createVerticalGlue();
		verticalGlue.setBounds(212, 0, 0, 536);
		leftPanel.add(verticalGlue);

		head = new NewPanel("src//default.png");
		head.setBounds(27, 13, 150, 130);
		leftPanel.add(head);

		userNameLabel = new JLabel("\u7528\u6237\uFF1A");
		userNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		userNameLabel.setBounds(37, 168, 125, 18);
		leftPanel.add(userNameLabel);

		JLabel selectedCarJlabel = new JLabel("\u9009\u62E9\u5F53\u524D\u5C0F\u8F66");
		selectedCarJlabel.setBounds(43, 315, 119, 18);
		leftPanel.add(selectedCarJlabel);

		comboBox = new JComboBox<>();
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"car1", "car2", "car3"}));
		comboBox.setBounds(43, 346, 119, 38);
		leftPanel.add(comboBox);

		JButton movingCarbutton = new JButton("\u79FB\u52A8\u5C0F\u8F66");
		movingCarbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!authorized) {
					JOptionPane.showMessageDialog(null, "未登录");
					return;
				}
				if (userPointx == wrongValue || userPointy == wrongValue) {
					JOptionPane.showMessageDialog(null, "没有选中目标点");
					return;
				}
				client.moveCar(comboBox.getModel().getElementAt(comboBox.getSelectedIndex()), userPointx, userPointy);
			}
		});
		movingCarbutton.setBounds(43, 415, 113, 27);
		leftPanel.add(movingCarbutton);
		
		JButton stopMovingButton = new JButton("\u505C\u6B62\u79FB\u52A8");
		stopMovingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!authorized) {
					JOptionPane.showMessageDialog(null, "未登录");
					return;
				}
				client.stopCar(comboBox.getModel().getElementAt(comboBox.getSelectedIndex()));
			}
		});
		stopMovingButton.setBounds(43, 468, 113, 27);
		leftPanel.add(stopMovingButton);
		String s = "abc";
		s.split("\\n");

	}
	private Location coordinateHelper(int x,int y) {
		//TODO
		return new Location(x,y);
	}


	public class LogOn extends JFrame { // 继承JFrame顶层容器类

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		// 定义组件
		JPanel jp1, jp2, jp3, jp4; // 定义面板
		JTextField jtf1; // 定义文本框
		JLabel jl1;
		JPasswordField jpf1; // 定义密码框
		JLabel jlb1, jlb2; // 定义标签
		JButton jb1, jb2; // 定义按钮
		JFrame lg = this;
		UserInterface superFrame;

		public LogOn(UserInterface sf) // 构造函数
		{
			this.superFrame = sf;
			// 创建组件
			jp1 = new JPanel(); // 创建三个面板
			jp2 = new JPanel();
			jp3 = new JPanel();
			jp4 = new JPanel();

			jlb1 = new JLabel("用户名"); // 创建两个标签
			jlb2 = new JLabel("密	  码");

			jb1 = new JButton("登录"); // 创建两个按钮
			jb2 = new JButton("清空");
			jtf1 = new JTextField(10);// 创建文本框

			jl1 = new JLabel();

			jpf1 = new JPasswordField(10); // 创建密码框

			jb1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String userName = jtf1.getText();
					String password = new String(jpf1.getPassword());
					String prompt = sf.client.logOn(userName, password);
					if (prompt.equals("Connection built")) {
						sf.authorized = true;
						jl1.setText("登录成功");
						sf.userNameLabel.setText("用户：" + userName);
						sf.head.resetPath("src//" + userName + ".jpg");
						jtf1.setText("");
						jpf1.setText("");
						//lg.setVisible(false);
						//jl1.setText("");
					} else if (prompt.equals("Invalid username"))
						jl1.setText("用户名无效");
					else if (prompt.equals("Invalid password"))
						jl1.setText("密码错误");
					else if (prompt.equals("Data transfer wrongly"))
						jl1.setText("网络连接错误");
				}
			});
			jb2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					jtf1.setText("");
					jpf1.setText("");
				}
			});
			// 设置布局管理器
			getContentPane().setLayout(new GridLayout(4, 1)); // 网格布局，3行一列

			// 添加组件
			getContentPane().add(jp1); // 添加面板
			getContentPane().add(jp2);
			getContentPane().add(jp3);
			getContentPane().add(jp4);

			jp1.add(jlb1); // 添加面板1的标签和文本框
			jp1.add(jtf1);

			jp2.add(jlb2); // 添加面板2的标签和密码框
			jp2.add(jpf1);

			jp3.add(jb1); // 添加面板3的按钮
			jp3.add(jb2);

			jp4.add(jl1);

			// 设置窗口属性
			this.setTitle("登录界面"); // 创建界面标题
			this.setSize(300, 200); // 设置界面像素
			this.setLocation(500, 100); // 设置界面初始位置
		}
	}

	public void repaint() {
		this.rightPanel.resetPath("src//camera.jpg");
	}

	class NewPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String path;

		public NewPanel(String path) {
			this.path = path;
		}

		public void resetPath(String path) {
			this.path = path;
			this.repaint();
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			ImageIcon icon = new ImageIcon(path);// 003.jpg是测试图片在项目的根目录下
			g.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);// 图片会自动缩放
			// g.clearRect(rightPanel.getX(), rightPanel.getY(), rightPanel.getWidth(),
			// rightPanel.getHeight());
			g.setColor(Color.blue);
			synchronized (client.carLocation) {
				for (Map.Entry<String, Location> entry : client.carLocation.entrySet()) {
					int x = (int) (entry.getValue().x - 10);
					int y = (int) (entry.getValue().y - 10);
					g.fillOval(x, y, 20, 20);
				}
			}
			if (userPointx != wrongValue) {
				g.setColor(Color.red);
				g.fillOval(userPointx, userPointy, 20, 20);
			}
		}
	}
}
