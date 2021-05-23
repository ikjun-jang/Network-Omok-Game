package omok;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class OmokClient extends JFrame implements ActionListener, KeyListener,
		Runnable, WindowListener, MouseListener, MouseMotionListener {
	JPanel jpLogin, jpLobby, jpGame;
	CardLayout layout;
	JLabel jlbIp, jlbId, jlbFirstId, jlbLoginImg, jlbLoginBtn, jlbSecondId,
			jlbUser, jlbGamePan, jlbLobbyImg, jlbEnter, jlbLoginExit, jlbStart,
			jlbSend, jlbReturn, jlbIds, jlbGiveUp, jlbExit, jlbTimeBlack,
			jlbTimeWhite, jlbRank, jlbTotal, jlbWin, jlbRate, jlbRank2,
			jlbTotal2, jlbWin2, jlbRate2;
	JTextField jtfId, jtfIp, jtfLobby, jtfGame;
	JButton jbtnLogin, jbtnLoginExit, jbtnGiveUp, jbtnReturn, jbtnSend,
			jbtnLobbyExit, jbtnEnterRoom, jbtnSend2, jbtnGameExit;
	JTextArea jtaGame, jtaLobby, jtaUser;
	JScrollPane jspLobby, jspGame, jspUser;
	ImageIcon imgGamePan, imgLoginPanel, imgLoginBtn, imgEnter, imgLoginExit,
			imgSendLobby, imgStart, imgSend, imgReturn, imgIds, imgGiveUp,
			imgExit, imgExitLobby;
	JList jlRoom;
	String[] roomName;
	String ip = "localhost";
	String id;
	BufferedReader br;
	PrintWriter pw;
	Socket s;
	JMenuBar jmb;
	JMenu jmGame;
	JMenuItem jmiStat;
	JMenuItem jmiAbout;
	boolean turn;
	boolean isInRoom;
	boolean isReturn;
	OmokDAO dao = new OmokDAO();

	// 게임 변수
	ImageIcon imgBlack, imgWhite;
	JLabel[] jlbStone = new JLabel[391];
	JLabel jlbBoard;
	int count = 1;
	int timeBlack = 120;
	int timeWhite = 120;

	public OmokClient(String title) {
		super(title);
		layout = new CardLayout();
		setLayout(layout);
		setBounds(350, 50, 900, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);

		jpLogin = new JPanel();
		jpLobby = new JPanel();
		jpGame = new JPanel();

		jpLogin.setLayout(null);
		jpLobby.setLayout(null);
		jpGame.setLayout(null);

		add(jpLogin, "login");
		add(jpLobby, "lobby");
		add(jpGame, "game");

		// Login Panel
		imgLoginPanel = new ImageIcon("src/Omok/images/loginimg.jpg");
		imgLoginBtn = new ImageIcon("src/Omok/images/login.png");
		imgLoginExit = new ImageIcon("src/Omok/images/loginexit.png");
		jlbLoginImg = new JLabel(imgLoginPanel);
		jlbIp = new JLabel("IP");
		jlbId = new JLabel("ID");
		jtfIp = new JTextField("localhost");
		jtfId = new JTextField("");
		jbtnLogin = new JButton(imgLoginBtn);
		jbtnLoginExit = new JButton(imgLoginExit);

		jlbLoginImg.setBounds(0, 0, 930, 570);
		jlbIp.setBounds(370, 270, 100, 30);
		jlbId.setBounds(370, 310, 100, 30);
		jtfIp.setBounds(400, 270, 150, 30);
		jtfId.setBounds(400, 310, 150, 30);
		jbtnLogin.setBounds(370, 350, 90, 40);
		jbtnLoginExit.setBounds(460, 350, 90, 40);

		jpLogin.add(jlbIp);
		jpLogin.add(jlbId);
		jpLogin.add(jtfId);
		jpLogin.add(jtfIp);
		jpLogin.add(jbtnLogin);
		jpLogin.add(jbtnLoginExit);
		jpLogin.add(jlbLoginImg);

		jbtnLogin.addActionListener(this);
		jbtnLoginExit.addActionListener(this);

		// Lobby Panel

		// Menu
		jmb = new JMenuBar();
		jmGame = new JMenu("GAME");
		jmiStat = new JMenuItem("Statistics");
		jmiAbout = new JMenuItem("About");

		jmGame.add(jmiStat);
		jmGame.add(jmiAbout);
		jmb.add(jmGame);

		roomName = new String[5];
		// 1~5 번방 이름 지정
		for (int i = 0; i < roomName.length; i++) {
			roomName[i] = (i + 1) + "번방";
		}

		// List 에 방 이름 넣기
		jlRoom = new JList(roomName);

		imgEnter = new ImageIcon("src/Omok/images/enter.jpg");
		imgSend = new ImageIcon("src/Omok/images/send.png");
		imgSendLobby = new ImageIcon("src/Omok/images/send_lobby.jpg");
		imgExitLobby = new ImageIcon("src/Omok/images/exit.jpg");
		imgExit = new ImageIcon("src/Omok/images/exit.png");
		jtfLobby = new JTextField();
		jbtnLobbyExit = new JButton(imgExitLobby);
		jbtnEnterRoom = new JButton(imgEnter);
		jtaLobby = new JTextArea();
		jspLobby = new JScrollPane(jtaLobby,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jlbUser = new JLabel("[접속자 ID]");
		jbtnSend2 = new JButton(imgSendLobby);
		jtaUser = new JTextArea();
		jlbLobbyImg = new JLabel(imgLoginPanel);
		jspUser = new JScrollPane(jtaUser,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		LineBorder border = new LineBorder(Color.black);
		jlRoom.setBorder(border);
		jtfLobby.setBorder(border);
		jspLobby.setBorder(border);
		jspUser.setBorder(border);

		jlbLobbyImg.setBounds(0, 0, 500, 550);
		jspLobby.setBounds(30, 200, 400, 200);
		jtfLobby.setBounds(30, 405, 285, 30);
		jbtnLobbyExit.setBounds(320, 440, 110, 30);
		jbtnSend2.setBounds(320, 405, 110, 30);
		jlRoom.setBounds(30, 50, 180, 100);
		jspUser.setBounds(250, 50, 180, 100);
		jtaUser.setEditable(false);
		jbtnEnterRoom.setBounds(70, 160, 110, 30);
		jlbUser.setBounds(250, 30, 100, 20);

		jpLobby.add(jbtnLobbyExit);
		jpLobby.add(jspLobby);
		jpLobby.add(jtfLobby);
		jpLobby.add(jlRoom);
		jpLobby.add(jbtnEnterRoom);
		jpLobby.add(jlbUser);
		jpLobby.add(jspUser);
		jpLobby.add(jbtnSend2);
		jpLobby.add(jlbLobbyImg);

		jtfLobby.addKeyListener(this);
		jbtnLobbyExit.addActionListener(this);
		jbtnEnterRoom.addActionListener(this);
		jbtnSend2.addActionListener(this);
		jmiStat.addActionListener(this);
		jmiAbout.addActionListener(this);

		// Game Panel
		turn = false;
		isInRoom = false;
		isReturn = true;

		jlbRank = new JLabel("");
		jlbTotal = new JLabel("");
		jlbWin = new JLabel("");
		jlbRate = new JLabel("");
		jlbRank2 = new JLabel("");
		jlbTotal2 = new JLabel("");
		jlbWin2 = new JLabel("");
		jlbRate2 = new JLabel("");

		// jlbRank.setBounds(880, 670, 250, 30);
		jlbTotal.setBounds(920, 630, 250, 30);
		jlbWin.setBounds(920, 670, 250, 30);
		jlbRate.setBounds(920, 710, 250, 30);
		// jlbRank2.setBounds(20, 630, 250, 30);
		jlbTotal2.setBounds(60, 630, 250, 30);
		jlbWin2.setBounds(60, 670, 250, 30);
		jlbRate2.setBounds(60, 710, 250, 30);

		// jpGame.add(jlbRank);
		jpGame.add(jlbTotal);
		jpGame.add(jlbWin);
		jpGame.add(jlbRate);
		// jpGame.add(jlbRank2);
		jpGame.add(jlbTotal2);
		jpGame.add(jlbWin2);
		jpGame.add(jlbRate2);

		jlbTotal.setFont(jlbTotal.getFont().deriveFont(20.0f));
		jlbWin.setFont(jlbWin.getFont().deriveFont(20.0f));
		jlbRate.setFont(jlbRate.getFont().deriveFont(20.0f));
		jlbTotal2.setFont(jlbTotal2.getFont().deriveFont(20.0f));
		jlbWin2.setFont(jlbTotal2.getFont().deriveFont(20.0f));
		jlbRate2.setFont(jlbTotal2.getFont().deriveFont(20.0f));

		jlbTotal2.setForeground(Color.white);
		jlbWin2.setForeground(Color.white);
		jlbRate2.setForeground(Color.white);

		imgGamePan = new ImageIcon("src/Omok/images/backboard.png");
		imgExit = new ImageIcon("src/Omok/images/exit.png");
		imgReturn = new ImageIcon("src/Omok/images/return.png");
		imgGiveUp = new ImageIcon("src/Omok/images/giveup.png");
		imgReturn = new ImageIcon("src/Omok/images/return.png");

		jlbFirstId = new JLabel("");
		jlbSecondId = new JLabel("");
		jlbSecondId.setForeground(Color.white);
		jlbGamePan = new JLabel(imgGamePan);

		jbtnGiveUp = new JButton(imgGiveUp);
		jbtnReturn = new JButton(imgReturn);
		jbtnSend = new JButton(imgSend);
		jbtnGameExit = new JButton(imgExit);

		jlbTimeBlack = new JLabel("120");
		jlbTimeWhite = new JLabel("120");

		jtaGame = new JTextArea();
		jspGame = new JScrollPane(jtaGame,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jtfGame = new JTextField();

		jlbTimeBlack.setBounds(880, 550, 250, 30);
		jlbTimeWhite.setBounds(20, 550, 250, 30);
		jspGame.setBounds(295, 585, 425, 165);
		jtfGame.setBounds(295, 755, 425, 30);
		jbtnSend.setBounds(745, 755, 105, 30);
		jbtnGiveUp.setBounds(745, 590, 105, 30);
		jbtnReturn.setBounds(745, 630, 105, 30);
		jbtnGameExit.setBounds(745, 670, 105, 30);
		jlbGamePan.setBounds(0, 0, 1150, 799);
		jlbFirstId.setBounds(880, 590, 250, 30);
		jlbSecondId.setBounds(20, 590, 250, 30);
		jlbFirstId.setFont(jlbFirstId.getFont().deriveFont(30.0f));
		jlbSecondId.setFont(jlbFirstId.getFont().deriveFont(30.0f));

		jlbFirstId.setHorizontalAlignment(SwingConstants.CENTER);
		jlbSecondId.setHorizontalAlignment(SwingConstants.CENTER);

		jbtnGiveUp.addActionListener(this);
		jbtnReturn.addActionListener(this);
		jbtnSend.addActionListener(this);
		jbtnGameExit.addActionListener(this);
		jtfGame.addKeyListener(this);

		// 게임 소스 시작
		imgBlack = new ImageIcon("src/Omok/images/Bstone.png");
		imgWhite = new ImageIcon("src/Omok/images/Wstone.png");

		for (int i = 0; i < jlbStone.length; i++) {
			jlbStone[i] = new JLabel();
			if (i % 2 == 0) {
				jlbStone[i].setIcon(imgBlack);
			} else if (i % 2 != 0) {
				jlbStone[i].setIcon(imgWhite);
			}
		}

		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		for (int i = 0; i < jlbStone.length; i++) {
			jpGame.add(jlbStone[i]);
		}
		// 게임 소스 끝

		jpGame.add(jspGame);
		jpGame.add(jtfGame);
		jpGame.add(jbtnSend);
		jpGame.add(jbtnGiveUp);
		jpGame.add(jbtnReturn);
		jpGame.add(jlbFirstId);
		jpGame.add(jlbSecondId);
		jpGame.add(jbtnGameExit);
		jpGame.add(jlbGamePan);

		jtaGame.setEditable(false);
		jtaLobby.setEditable(false);

		addWindowListener(this);

		setVisible(true);
		jtfId.requestFocus();

		Thread th = new Thread(this);
		th.start();
	}

	@Override
	public void run() {
		try {
			s = new Socket(ip, 5000);
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					s.getOutputStream())));

			String msg = null;
			String[] parse;
			while (true) {
				msg = br.readLine();
				parse = msg.split(":");
				if (parse[0].equals("LOGIN")) {
					if (parse[1].equals(id)) {
						layout.show(getContentPane(), "lobby");
						setResizable(true);
						setBounds(500, 50, 500, 550);
						setResizable(false);
						setJMenuBar(jmb);
						jtfLobby.requestFocus();
					}
					jtaLobby.append(parse[1] + " 님 로그인..\n");
					JScrollBar jsb = jspLobby.getVerticalScrollBar();
					int max = jsb.getMaximum();
					jsb.setValue(max);
					jtaUser.setText("");
					for (int i = 2; i < parse.length; i++) {
						jtaUser.append(parse[i] + "\n");
					}
				} else if (parse[0].equals("LOGOUT")) {
					jtaLobby.append(parse[1] + " 님 종료..\n");
					JScrollBar jsb = jspLobby.getVerticalScrollBar();
					int max = jsb.getMaximum();
					jsb.setValue(max);
					jtaUser.setText("");
					for (int i = 2; i < parse.length; i++) {
						jtaUser.append(parse[i] + "\n");
					}
				} else if (parse[0].equals("JOIN_ROOM")) {
					isInRoom = true;
					layout.show(getContentPane(), "game");
					setBounds(170, 0, 1155, 850);
					jtfGame.requestFocus();
					jtaLobby.append(parse[2] + " 님 "
							+ jlRoom.getSelectedValue() + " 입장..\n");
					JScrollBar jsb = jspLobby.getVerticalScrollBar();
					int max = jsb.getMaximum();
					jsb.setValue(max);
					jtaGame.append(parse[2] + " 님 입장..\n");
					jtaGame.append("게임 시작..\n");
					JScrollBar jsb2 = jspGame.getVerticalScrollBar();
					int max2 = jsb2.getMaximum();
					jsb2.setValue(max2);
					jlbFirstId.setText(parse[1]);
					jlbSecondId.setText(parse[2]);
					timeWhite = 120;

					// Read and print 1st (black) player info from Database
					OmokVO player1 = new OmokVO(parse[1]);
					player1 = dao.selectOne(parse[1]);
					// jlbRank.setText("RANK: " + player1.getRank());
					jlbTotal.setText("GAME: " + player1.getTotal());
					jlbWin.setText("WIN: " + player1.getWin());
					jlbRate.setText("WIN(%): " + player1.getRate());

					// Read and print 2nd (white) player info from Database
					OmokVO player2 = new OmokVO(parse[2]);
					player2 = dao.selectOne(parse[2]);
					// jlbRank2.setText("RANK: " + player2.getRank());
					jlbTotal2.setText("GAME: " + player2.getTotal());
					jlbWin2.setText("WIN: " + player2.getWin());
					jlbRate2.setText("WIN(%): " + player2.getRate());

				} else if (parse[0].equals("ENTER_ROOM")) {
					isInRoom = true;
					turn = true; // 먼저들어온 사람: 흑돌
					layout.show(getContentPane(), "game");
					setBounds(170, 0, 1155, 850);
					jtfGame.requestFocus();
					jlbFirstId.setText(id);
					jtaLobby.append(id + " 님 " + jlRoom.getSelectedValue()
							+ " 입장..\n");
					JScrollBar jsb = jspLobby.getVerticalScrollBar();
					int max = jsb.getMaximum();
					jsb.setValue(max);
					jtaGame.setText("");
					jtaGame.append(id + " 님 입장..\n");
					timeBlack = 120;

					// jlbRank2.setText("");
					jlbTotal2.setText("");
					jlbWin2.setText("");
					jlbRate2.setText("");

					// Read and print 1st (black) player info from Database
					OmokVO player1 = new OmokVO(id);
					player1 = dao.selectOne(id);
					// jlbRank.setText("RANK: " + player1.getRank());
					jlbTotal.setText("GAME: " + player1.getTotal());
					jlbWin.setText("WIN: " + player1.getWin());
					jlbRate.setText("WIN(%): " + player1.getRate());

				} else if (parse[0].equals("EXIT_ROOM")) {
					jtaLobby.append(parse[2] + " 님 " + parse[1] + " 퇴장..\n");
					JScrollBar jsb = jspLobby.getVerticalScrollBar();
					int max = jsb.getMaximum();
					jsb.setValue(max);
					if (id.equals(parse[2])) {
						jlbFirstId.setText("");
						jlbSecondId.setText("");
						jtaGame.setText("");
						layout.show(getContentPane(), "lobby");
						setResizable(true);
						setBounds(500, 50, 500, 550);
						setResizable(false);
						turn = false;
						count = 1;
						isReturn = true;
						for (int i = 0; i < jlbStone.length; i++) {
							jlbStone[i].setVisible(false);
						}
					}
				} else if (parse[0].equals("FULL")) {
					JOptionPane.showMessageDialog(this, "게임중..");
				} else if (parse[0].equals("LOBBY_CHAT")) {
					jtaLobby.append(parse[1] + ": " + parse[2] + "\n");
					JScrollBar jsb = jspLobby.getVerticalScrollBar();
					int max = jsb.getMaximum();
					jsb.setValue(max);
				} else if (parse[0].equals("GAME_CHAT")) {
					jtaGame.append(parse[1] + ": " + parse[2] + "\n");
					JScrollBar jsb = jspGame.getVerticalScrollBar();
					int max = jsb.getMaximum();
					jsb.setValue(max);
				} else if (parse[0].equals("SEND_STONE")) {
					jlbStone[Integer.parseInt(parse[1])].setBounds(
							Integer.parseInt(parse[2]),
							Integer.parseInt(parse[3]), 32, 32);
					count = Integer.parseInt(parse[1]);
					jlbStone[count].setVisible(true);
				} else if (parse[0].equals("WHITE_WIN")) {
					JOptionPane.showMessageDialog(this, "백이 이겼습니다.");
					OmokVO player = new OmokVO(id);
					dao.updateTotal(player);
					if (parse[1].equals(id))
						dao.updateWin(player);
					dao.updateRate(player);
					pw.println("EXIT_ROOM:" + jlRoom.getSelectedValue() + ":"
							+ id);
					pw.flush();
					layout.show(getContentPane(), "lobby");
					jlbFirstId.setText("");
					jlbSecondId.setText("");
					setResizable(true);
					setBounds(500, 50, 500, 550);
					setResizable(false);
					isInRoom = false;
				} else if (parse[0].equals("BLACK_WIN")) {
					JOptionPane.showMessageDialog(this, "흑이 이겼습니다.");
					OmokVO player = new OmokVO(id);
					dao.updateTotal(player);
					if (parse[1].equals(id))
						dao.updateWin(player);
					dao.updateRate(player);
					pw.println("EXIT_ROOM:" + jlRoom.getSelectedValue() + ":"
							+ id);
					pw.flush();
					layout.show(getContentPane(), "lobby");
					jlbFirstId.setText("");
					jlbSecondId.setText("");
					setResizable(true);
					setBounds(500, 50, 500, 550);
					setResizable(false);
					isInRoom = false;
				} else if (parse[0].equals("GIVEUP")) {
					JOptionPane.showMessageDialog(this, "상대방의 기권으로 승리하셨습니다.");
					pw.println("EXIT_ROOM:" + jlRoom.getSelectedValue() + ":"
							+ id);
					pw.flush();
					layout.show(getContentPane(), "lobby");
					jlbFirstId.setText("");
					jlbSecondId.setText("");
					setResizable(true);
					setBounds(500, 50, 500, 550);
					setResizable(false);
					isInRoom = false;
				} else if (parse[0].equals("RETURN")) {
					if (parse[2].equals(id))
						isReturn = false;
					jlbStone[Integer.parseInt(parse[1]) - 1].setVisible(false);
					jlbStone[Integer.parseInt(parse[1]) - 2].setVisible(false);
				} else if (parse[0].equals("RETURN_REQUEST")) {
					int result = JOptionPane.showConfirmDialog(this,
							"상대방이 무르기를 요청했습니다. 승인하시겠습니까?", "무르기",
							JOptionPane.YES_NO_OPTION);
					if (result == 0) {
						pw.println("RETURN:" + jlRoom.getSelectedValue() + ":"
								+ parse[1]);
						pw.flush();
					}
				} else if (parse[0].equals("SAME_STONE")) {
					jtaGame.append("돌이 이미 놓여있습니다.\n");
					JScrollBar jsb = jspGame.getVerticalScrollBar();
					int max = jsb.getMaximum();
					jsb.setValue(max);
				} else if (parse[0].equals("SAME_ID")) {
					JOptionPane.showMessageDialog(this,
							"이미 접속중인 ID입니다. 다시 입력해주세요.");
					jtfId.setText("");
				}
			}

		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					"서버와의 통신에 이상이 있습니다. 잠시 후 이용해 주세요..");
			System.exit(1);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();

		ip = jtfIp.getText();
		id = jtfId.getText();

		if (obj == jbtnLogin) {

			if (id.equals(""))
				JOptionPane.showMessageDialog(this, "ID를 입력하셔야 합니다.");
			else {
				try {
					if (dao.selectOne(id) == null) {
						OmokVO user = new OmokVO(id);
						dao.insertNew(user);
					}
					pw.println("LOGIN:" + id);
					pw.flush();
				} catch (Exception e2) {
				}
			}
		} else if (obj == jbtnLoginExit) {
			dao.close();
		} else if (obj == jbtnEnterRoom) {
			pw.println("ENTER_ROOM:" + jlRoom.getSelectedValue() + ":" + id);
			pw.flush();
		} else if (obj == jbtnLobbyExit) {
			pw.println("LOGOUT:" + id);
			pw.flush();
			System.exit(0);
		} else if (obj == jbtnReturn) {
			if (count < 3) {
				JOptionPane.showMessageDialog(this, "수가 적어 무르기 하실 수 없습니다.");
			} else if (isReturn == false) {
				JOptionPane.showMessageDialog(this, "이미 무르기 하셨습니다.");
			} else {
				int result = JOptionPane.showConfirmDialog(this,
						"무르기는 한번만 가능합니다. 정말 무르시겠습니까?", "무르기",
						JOptionPane.YES_NO_OPTION);
				if (result == 0) {
					pw.println("RETURN_REQUEST:" + jlRoom.getSelectedValue()
							+ ":" + id);
					pw.flush();
				}
			}
		} else if (obj == jbtnGiveUp) {
			if (jlbSecondId.getText() == "") {
				JOptionPane.showMessageDialog(this, "게임 중이 아닙니다.");
			} else {
				int result = JOptionPane.showConfirmDialog(this,
						"정말 기권하시겠습니까?", "기권", JOptionPane.YES_NO_OPTION);
				if (result == 0) {
					pw.println("GIVEUP:" + jlRoom.getSelectedValue());
					pw.flush();
				}
			}
		} else if (obj == jbtnSend) {
			String data = jtfGame.getText();
			jtfGame.setText("");
			if (!data.equals("")) {
				pw.println("GAME_CHAT:" + jlRoom.getSelectedValue() + ":" + id
						+ ":" + data);
				pw.flush();
			}
		} else if (obj == jbtnSend2) {
			String data = jtfLobby.getText();
			if (!data.equals("")) {
				pw.println("LOBBY_CHAT:" + id + ":" + data);
				pw.flush();
			}
			jtfLobby.setText("");
		} else if (obj == jbtnGameExit) {
			int result = JOptionPane.showConfirmDialog(this, "정말 나가시겠습니까?",
					"나가기", JOptionPane.YES_NO_OPTION);
			if (result == 0) {
				pw.println("EXIT_ROOM:" + jlRoom.getSelectedValue() + ":" + id);
				pw.flush();
				layout.show(getContentPane(), "lobby");
				jlbFirstId.setText("");
				jlbSecondId.setText("");
				setResizable(true);
				setBounds(500, 50, 500, 550);
				setResizable(false);
				isInRoom = false;
			}
		} else if (obj == jmiStat) {
			new MenuRank();
		} else if (obj == jmiAbout) {
			new MenuData();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		Object obj = e.getSource();
		int code = e.getKeyCode();
		if (obj == jtfLobby && code == 10) {
			String data = jtfLobby.getText();
			if (!data.equals("")) {
				pw.println("LOBBY_CHAT:" + id + ":" + data);
				pw.flush();
				jtfLobby.setText("");
			}
		} else if (obj == jtfGame && code == 10) {
			String data = jtfGame.getText();
			if (!data.equals("")) {
				jtfGame.setText("");
				pw.println("GAME_CHAT:" + jlRoom.getSelectedValue() + ":" + id
						+ ":" + data);
				pw.flush();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	public static void main(String[] args) {
		new OmokClient("Omok Client ver. 0.1");
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		dao.close();
		if (isInRoom == true) {
			pw.println("EXIT_ROOM:" + jlRoom.getSelectedValue() + ":" + id);
			pw.flush();
		}
		pw.println("LOGOUT:" + id);
		pw.flush();
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(jlbSecondId.getText().equals("") && turn == true) {
			JOptionPane.showMessageDialog(this, "상대방이 입장해야 게임을 시작할 수 있습니다.");
		} else if (turn == true && !jlbSecondId.getText().equals("")) {
			if (count % 2 == 1) {
				int x = e.getX();
				int y = e.getY();
				pw.println("SEND_STONE:" + jlRoom.getSelectedValue() + ":" + x
						+ ":" + y + ":" + id + ":" + count);
				pw.flush();
			} else if (count % 2 == 0 && !jlbSecondId.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "백이 놓을 차례입니다.");
				jtaGame.append("백이 놓을 차례입니다.\n");
			}
		} else if (turn == false) {
			if (count % 2 == 0) {
				int x = e.getX();
				int y = e.getY();
				pw.println("SEND_STONE:" + jlRoom.getSelectedValue() + ":" + x
						+ ":" + y + ":" + id + ":" + count);
				pw.flush();
			} else if (count % 2 == 1 && !jlbSecondId.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "흑이 놓을 차례입니다.");
				jtaGame.append("흑이 놓을 차례입니다.\n");
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}