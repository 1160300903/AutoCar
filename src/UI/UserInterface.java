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
import java.io.IOException;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

import socket.Client;
import socket.Location;

import javax.swing.SwingConstants;
import javax.swing.Timer;

public class UserInterface extends JFrame {
	private JPanel contentPane;
	private JTextField speedJTextField;
	private JTextField directionJTextField;
	private NewPanel rightPanel,head;
	private LogOn logOn;
	private UserInterface jf = this;
	private JLabel userNameLabel;
	private boolean authorized = false;
	private Client client = new Client();
	private Timer timer;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
					UserInterface frame = new UserInterface();
					frame.setVisible(true);
					try {
						System.in.read();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					frame.updateUI();
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
                if(jf.authorized) 
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
		
		JMenu logOnJmenu = new JMenu("\u767B\u5F55\u4E0E\u6CE8\u9500");

		menuBar.add(logOnJmenu);
		
		JMenuItem logOnJmenuitem = new JMenuItem("\u767B\u5F55");
		logOnJmenuitem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logOn.setVisible(true);
				logOn.setAlwaysOnTop(true);
			}
		});
		logOnJmenu.add(logOnJmenuitem);
		
		JMenuItem logOffJmenuitem = new JMenuItem("\u6CE8\u9500");
		logOffJmenuitem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.logOff();
				jf.head.resetPath("src//default.png");
				jf.userNameLabel.setText("�û���");
				jf.authorized = false;
			}
		});
		logOnJmenu.add(logOffJmenuitem);
		
		JMenu cameraJmenu = new JMenu("\u6444\u50CF\u5934");
		menuBar.add(cameraJmenu);
		
		JMenuItem preCameraJmenuitem = new JMenuItem("\u524D\u6444\u50CF\u5934");
		cameraJmenu.add(preCameraJmenuitem);
		
		JMenuItem postCameraJmenuitem = new JMenuItem("\u540E\u6444\u50CF\u5934");
		cameraJmenu.add(postCameraJmenuitem);
		
		JMenu mapJmenu = new JMenu("\u5730\u56FE");
		menuBar.add(mapJmenu);
		
		JMenu aboutUsJmenu = new JMenu("\u5173\u4E8E\u6211\u4EEC");
		menuBar.add(aboutUsJmenu);
		
		rightPanel = new NewPanel("src//map.png");
		rightPanel.setBounds(215, 35, 765, 535);
		contentPane.add(rightPanel);
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
		
		timer = new Timer(500,new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Graphics g = rightPanel.getGraphics();
				rightPanel.updateUI();
				//g.clearRect(rightPanel.getX(), rightPanel.getY(), rightPanel.getWidth(), rightPanel.getHeight());
				for (Map.Entry<String, Location> entry : client.carLocation.entrySet()) {
					int x = (int) (entry.getValue().x - 10);
					int y = (int) (entry.getValue().y - 10);
					g.drawOval(x, y, 20, 20);
				}
			}
			
		});
		
		
		JPanel leftPanel = new JPanel();
		leftPanel.setBounds(0, 35, 212, 536);
		contentPane.add(leftPanel);
		leftPanel.setLayout(null);
		
		JRadioButton autoCorrectRadio = new JRadioButton("\u81EA\u52A8\u7EA0\u6B63\u529F\u80FD");
		autoCorrectRadio.setBounds(43, 212, 119, 27);
		autoCorrectRadio.setSelected(true);
		leftPanel.add(autoCorrectRadio);
		
		speedJTextField = new JTextField();
		speedJTextField.setText("3m/s");
		speedJTextField.setBounds(43, 264, 119, 38);
		leftPanel.add(speedJTextField);
		speedJTextField.setColumns(10);
		
		JLabel speedJlabel = new JLabel("\u9664\u51B0\u8F66\u884C\u9A76\u901F\u5EA6");
		speedJlabel.setBounds(43, 315, 119, 18);
		leftPanel.add(speedJlabel);
		
		directionJTextField = new JTextField();
		directionJTextField.setText("\u6B63\u5317");
		directionJTextField.setBounds(43, 346, 119, 38);
		leftPanel.add(directionJTextField);
		directionJTextField.setColumns(10);
		
		JLabel directionJlabel = new JLabel("\u9664\u51B0\u8F66\u884C\u9A76\u65B9\u5411");
		directionJlabel.setBounds(43, 397, 119, 18);
		leftPanel.add(directionJlabel);
		
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
		selectedCarJlabel.setBounds(43, 479, 119, 18);
		leftPanel.add(selectedCarJlabel);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"\u5C0F\u8F661", "\u5C0F\u8F662", "\u5C0F\u8F663"}));
		comboBox.setBounds(43, 419, 119, 38);
		leftPanel.add(comboBox);
		String s = "abc";
		s.split("\\n");
		
		
	}
	public class LogOn extends JFrame {    //�̳�JFrame����������
		
		//�������
		JPanel jp1,jp2,jp3,jp4;    //�������
		JTextField jtf1;   		//�����ı���
		JLabel jl1;
		JPasswordField jpf1;    //���������
		JLabel jlb1,jlb2;        //�����ǩ
		JButton jb1,jb2;        //���尴ť
		JFrame lg = this;
		UserInterface superFrame;
		public LogOn(UserInterface sf)        //���캯��
		{
			this.superFrame = sf;
			//�������
			jp1=new JPanel();    //�����������
			jp2=new JPanel();
			jp3=new JPanel();
			jp4=new JPanel();
			
			
			jlb1=new JLabel("�û���");    //����������ǩ
			jlb2=new JLabel("��	  ��");
			
			jb1=new JButton("��¼");    //����������ť
			jb2=new JButton("���");
			jtf1=new JTextField(10);//�����ı���
			
			jl1=new JLabel(); 
			
			jpf1=new JPasswordField(10);    //���������
			
			jb1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String userName = jtf1.getText();
					String password = new String(jpf1.getPassword());
					String prompt = sf.client.logOn(userName, password);
					if(prompt.equals("Connection built")) {
						sf.authorized = true;
						jl1.setText("��¼�ɹ�");
						sf.userNameLabel.setText("�û���"+userName);
						sf.head.resetPath("src//"+userName+".jpg");
						jtf1.setText("");
						jpf1.setText("");
						lg.setVisible(false);
					}
					else if(prompt.equals("Invalid username"))
						jl1.setText("�û�����Ч");
					else if(prompt.equals("Invalid password"))
						jl1.setText("�������");
					else if(prompt.equals("Data transfer wrongly"))
						jl1.setText("�������Ӵ���");
				}
			});
			jb2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					jtf1.setText("");
					jpf1.setText("");
				}
			});
			//���ò��ֹ�����
			getContentPane().setLayout(new GridLayout(4,1));    //���񲼾֣�3��һ��
			
			//������
			getContentPane().add(jp1);    //������
			getContentPane().add(jp2);
			getContentPane().add(jp3);
			getContentPane().add(jp4);
			
			jp1.add(jlb1);    //������1�ı�ǩ���ı���
			jp1.add(jtf1);
			
			jp2.add(jlb2);    //������2�ı�ǩ�������
			jp2.add(jpf1);
			
			jp3.add(jb1);    //������3�İ�ť
			jp3.add(jb2);
			
			jp4.add(jl1);
			
			//���ô�������
			this.setTitle("��¼����");    //�����������
			this.setSize(300, 200);        //���ý�������
			this.setLocation(500, 100);    //���ý����ʼλ��
		}
	}
	public void updateUI() {
		this.rightPanel.resetPath("src//camera.jpg");
	}
}
class NewPanel extends JPanel {
	String path;
	  public NewPanel(String path) {
		  this.path = path;
	  }
      public void resetPath(String path) {
    	  this.path = path;
    	  this.updateUI();
      }
	  public void paintComponent(Graphics g) {
	   int x = 0, y = 0;
	   ImageIcon icon = new ImageIcon(path);// 003.jpg�ǲ���ͼƬ����Ŀ�ĸ�Ŀ¼��
	   g.clearRect(x, y, this.getSize().width, this.getSize().height);
	   g.drawImage(icon.getImage(), x, y, getSize().width,
	     getSize().height, this);// ͼƬ���Զ�����
	  }
}
