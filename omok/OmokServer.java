package omok;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class OmokServer extends JFrame implements ActionListener {
	ArrayList<GMServer> list = new ArrayList<GMServer>();
	JTextArea jta;
	JButton jbExit;
	JScrollPane jsp;
	ServerSocket ss;
	BufferedReader br;
	PrintWriter pw;

	String[] roomState = new String[5];
	ArrayList<String> userid = new ArrayList<String>();
	String[] firstPlayerIP = new String[5];
	String[] secondPlayerIP = new String[5];
	String[] firstPlayerID = new String[5];
	String[] secondPlayerID = new String[5];

	// 게임 변수
	JLabel[] jlaStone = new JLabel[391];
	int[] x = new int[19];
	int[] y = new int[19];
	int[][] value = new int[19][19];
	int count = 0;
	int backX1 = 0;
	int backY1 = 0;
	int backX2 = 0;
	int backY2 = 0;

	public OmokServer(String title) {

		super(title);
		setBounds(30, 30, 400, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		jta = new JTextArea();
		jsp = new JScrollPane(jta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jbExit = new JButton("EXIT");

		add(jsp, "Center");
		add(jbExit, "South");

		jbExit.addActionListener(this);

		for (int i = 0; i < roomState.length; i++) {
			roomState[i] = "EMPTY";
		}

		// 게임 초기화

		for (int i = 0; i < value.length; i++) {
			for (int j = 0; j < value.length; j++) {
				value[i][j] = 0;
			}
		}

		// 오목판 좌표 담기
		for (int i = 0; i < x.length; i++) {
			x[i] = (29 * i) + 302;
			for (int j = 0; j < x.length / 2; j++) {
				y[j] = (29 * j) + 8;
			}
		}

		for (int i = 0; i < x.length; i++) {
			y[i] = (29 * i) + 10;
			for (int j = 0; j < x.length / 2; j++) {
				x[j] = (29 * j) + 300;
			}
		}

		setVisible(true);
		vchatting();
	}

	private void vchatting() {
		try {
			ss = new ServerSocket(5000);
			while (true) {
				Socket client = ss.accept();
				GMServer gm = new GMServer(client);
				list.add(gm);
				gm.start();
			}

		} catch (IOException e) {

		}
	}

	public static void main(String[] args) {
		OmokServer cs = new OmokServer("Omok Server ver. 0.1");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Exiting...");
		System.exit(0);

	}

	// inner class
	class GMServer extends Thread {
		Socket client;
		PrintWriter pw;
		BufferedReader br;
		ObjectInputStream ois;
		String ip;

		GMServer(Socket client) {
			this.client = client;

			ip = client.getInetAddress().getHostAddress();
			try {

				br = new BufferedReader(new InputStreamReader(
						client.getInputStream()));

				pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
						client.getOutputStream())));
			} catch (IOException e) {

			}
		}

		@Override
		public void run() {
			String[] parse;
			try {
				while (true) {
					String msg = null;
					msg = br.readLine();
					parse = msg.split(":");

					switch (parse[0]) {

					case "LOGIN":
						boolean sameid = false;
						for (Object obj : userid) {
							String str = (String) obj;
							if (parse[1].equals(str)) {
								pw.println("SAME_ID:");
								pw.flush();
								sameid = true;
							}
						}
						if (sameid == false) {
							jta.append(parse[1] + "[" + ip + "]" + " 님 로그인..\n");
							userid.add(parse[1]);
							String cli = "";
							for (Object obj : userid) {
								String str = (String) obj;
								cli += str + ":";
							}
							broadcast("LOGIN:" + parse[1] + ":" + cli);
						}
						break;

					case "LOGOUT":
						String cli = "";
						jta.append(parse[1] + "[" + ip + "]" + " 님 종료..\n");
						userid.remove(parse[1]);
						for (Object obj : userid) {
							String str = (String) obj;
							cli += str + ":";
						}
						broadcast("LOGOUT:" + parse[1] + ":" + cli);
						list.remove(this);
						break;

					case "ENTER_ROOM":
						switch (parse[1]) {
						case "1번방":
							switch (roomState[0]) {
							case "EMPTY":
								jta.append(parse[2] + "[" + ip + "]"
										+ " 님 1번방 입장.\n");
								roomState[0] = "WAITING";
								firstPlayerIP[0] = ip;
								firstPlayerID[0] = parse[2];
								pw.println("ENTER_ROOM:");
								pw.flush();
								break;
							case "WAITING":
								jta.append(parse[2] + "[" + ip + "]"
										+ "  님 1번방 입장.\n");
								roomState[0] = "FULL";
								secondPlayerIP[0] = ip;
								secondPlayerID[0] = parse[2];
								unicast("JOIN_ROOM:" + firstPlayerID[0] + ":"
										+ secondPlayerID[0], firstPlayerIP[0]);
								unicast("JOIN_ROOM:" + firstPlayerID[0] + ":"
										+ secondPlayerID[0], secondPlayerIP[0]);
								break;
							case "FULL":
								pw.println("FULL");
								pw.flush();
								break;
							}
							break;

						case "2번방":
							switch (roomState[0]) {
							case "EMPTY":
								jta.append(parse[2] + "[" + ip + "]"
										+ " 님 2번방 입장..\n");
								roomState[0] = "WAITING";
								firstPlayerIP[1] = ip;
								firstPlayerID[1] = parse[2];
								pw.println("ENTER_ROOM:");
								pw.flush();
								break;
							case "WAITING":
								jta.append(parse[2] + "[" + ip + "]"
										+ "  님 2번방 입장..\n");
								roomState[1] = "FULL";
								secondPlayerIP[1] = ip;
								secondPlayerID[1] = parse[2];
								unicast("JOIN_ROOM:" + firstPlayerID[1] + ":"
										+ secondPlayerID[1], firstPlayerIP[1]);
								unicast("JOIN_ROOM:" + firstPlayerID[1] + ":"
										+ secondPlayerID[1], secondPlayerIP[1]);
								break;
							case "FULL":
								pw.println("FULL");
								pw.flush();
								break;
							}
							break;

						case "3번방":
							switch (roomState[2]) {
							case "EMPTY":
								jta.append(parse[2] + "[" + ip + "]"
										+ " 님 3번방 입장..\n");
								roomState[2] = "WAITING";
								firstPlayerIP[2] = ip;
								firstPlayerID[2] = parse[2];
								pw.println("ENTER_ROOM:");
								pw.flush();
								break;
							case "WAITING":
								jta.append(parse[2] + "[" + ip + "]"
										+ " 님 3번방 입장..\n");
								roomState[2] = "FULL";
								secondPlayerIP[2] = ip;
								secondPlayerID[2] = parse[2];
								unicast("JOIN_ROOM:" + firstPlayerID[2] + ":"
										+ secondPlayerID[2], firstPlayerIP[2]);
								unicast("JOIN_ROOM:" + firstPlayerID[2] + ":"
										+ secondPlayerID[2], secondPlayerIP[2]);
								break;
							case "FULL":
								pw.println("FULL");
								pw.flush();
								break;
							}
							break;

						case "4번방":
							switch (roomState[3]) {
							case "EMPTY":
								jta.append(parse[2] + "[" + ip + "]"
										+ "  님 4번방 입장..\n");
								roomState[3] = "WAITING";
								firstPlayerIP[3] = ip;
								firstPlayerID[3] = parse[2];
								pw.println("ENTER_ROOM:");
								pw.flush();
								break;
							case "WAITING":
								jta.append(parse[2] + "[" + ip + "]"
										+ " 님 4번방 입장..\n");
								roomState[3] = "FULL";
								secondPlayerIP[3] = ip;
								secondPlayerID[3] = parse[2];
								unicast("JOIN_ROOM:" + firstPlayerID[3] + ":"
										+ secondPlayerID[3], firstPlayerIP[3]);
								unicast("JOIN_ROOM:" + firstPlayerID[3] + ":"
										+ secondPlayerID[3], secondPlayerIP[3]);
								break;
							case "FULL":
								pw.println("FULL");
								pw.flush();
								break;
							}
							break;

						case "5번방":
							switch (roomState[4]) {
							case "EMPTY":
								jta.append(parse[2] + "[" + ip + "]"
										+ "  님 3번방 입장..\n");
								roomState[4] = "WAITING";
								firstPlayerIP[4] = ip;
								firstPlayerID[4] = parse[2];
								pw.println("ENTER_ROOM:");
								pw.flush();
								break;
							case "WAITING":
								jta.append(parse[2] + "[" + ip + "]"
										+ "  님 5번방 입장..\n");
								roomState[4] = "FULL";
								secondPlayerIP[4] = ip;
								secondPlayerID[4] = parse[2];
								unicast("JOIN_ROOM:" + firstPlayerID[4] + ":"
										+ secondPlayerID[4], firstPlayerIP[4]);
								unicast("JOIN_ROOM:" + firstPlayerID[4] + ":"
										+ secondPlayerID[4], secondPlayerIP[4]);
								break;
							case "FULL":
								pw.println("FULL");
								pw.flush();
								break;
							}
							break;
						}
						break;

					case "EXIT_ROOM":
						for (int k = 0; k < value.length; k++) {
							for (int l = 0; l < value.length; l++) {
								value[k][l] = 0;
							}
						}
						count = 0;
						switch (parse[1]) {
						case "1번방":
							if (roomState[0] == "FULL") {
								jta.append(parse[2] + "[" + ip + "]"
										+ "  님 1번방 퇴장..\n");
								roomState[0] = "EMPTY";
								if (ip.equals(firstPlayerIP[0])) {
									jta.append(firstPlayerID[0] + "[" + ip
											+ "]" + " 님 1번방 퇴장..\n");

								} else if (ip.equals(secondPlayerIP[0])) {
									jta.append(secondPlayerID[0] + "[" + ip
											+ "]" + "  님 1번방 퇴장..\n");
								}
								broadcast("EXIT_ROOM:" + "1번방:"
										+ secondPlayerID[0]);
								broadcast("EXIT_ROOM:" + "1번방:"
										+ firstPlayerID[0]);
							} else if (roomState[0] == "WAITING") {
								jta.append(parse[2] + "[" + ip + "]"
										+ "  님 1번방 퇴장..\n");
								roomState[0] = "EMPTY";
								broadcast("EXIT_ROOM:" + "1번방:" + parse[2]);
							}
							break;
						case "2번방":
							if (roomState[1] == "FULL") {
								jta.append(parse[2] + "[" + ip + "]"
										+ " 님 2번방 퇴장..\n");
								roomState[1] = "EMPTY";
								if (ip.equals(firstPlayerIP[1])) {
									jta.append(firstPlayerID[1] + "[" + ip
											+ "]" + "  님 2번방 퇴장..\n");

								} else if (ip.equals(secondPlayerIP[1])) {
									jta.append(secondPlayerID[1] + "[" + ip
											+ "]" + "  님 2번방 퇴장..\n");
								}
								broadcast("EXIT_ROOM:" + "2번방:"
										+ secondPlayerID[1]);
								broadcast("EXIT_ROOM:" + "2번방:"
										+ firstPlayerID[1]);
							} else if (roomState[1] == "WAITING") {
								jta.append(parse[2] + "[" + ip + "]"
										+ "  님 2번방 퇴장..\n");
								roomState[1] = "EMPTY";
								broadcast("EXIT_ROOM:" + "2번방:" + parse[2]);
							}
							break;
						case "3번방":
							if (roomState[2] == "FULL") {
								jta.append(parse[2] + "[" + ip + "]"
										+ " 님 3번방 퇴장..\n");
								roomState[2] = "EMPTY";
								if (ip.equals(firstPlayerIP[2])) {
									jta.append(firstPlayerID[2] + "[" + ip
											+ "]" + " 님 3번방 퇴장..\n");

								} else if (ip.equals(secondPlayerIP[2])) {
									jta.append(secondPlayerID[2] + "[" + ip
											+ "]" + " 님 3번방 퇴장..\n");
								}
								broadcast("EXIT_ROOM:" + "3번방:"
										+ secondPlayerID[2]);
								broadcast("EXIT_ROOM:" + "3번방:"
										+ firstPlayerID[2]);
							} else if (roomState[2] == "WAITING") {
								jta.append(parse[2] + "[" + ip + "]"
										+ "  님 3번방 퇴장	..\n");
								roomState[2] = "EMPTY";
								broadcast("EXIT_ROOM:" + "3번방:" + parse[2]);
							}
							break;
						case "4번방":
							if (roomState[3] == "FULL") {
								jta.append(parse[2] + "[" + ip + "]"
										+ " 님 4번방 퇴장..\n");
								roomState[3] = "EMPTY";
								if (ip.equals(firstPlayerIP[3])) {
									jta.append(firstPlayerID[3] + "[" + ip
											+ "]" + " 님 4번방 퇴장..\n");

								} else if (ip.equals(secondPlayerIP[3])) {
									jta.append(secondPlayerID[3] + "[" + ip
											+ "]" + " 님 4번방 퇴장..\n");
								}
								broadcast("EXIT_ROOM:" + "4번방:"
										+ secondPlayerID[3]);
								broadcast("EXIT_ROOM:" + "4번방:"
										+ firstPlayerID[3]);
							} else if (roomState[3] == "WAITING") {
								jta.append(parse[2] + "[" + ip + "]"
										+ " 님 4번방 퇴장..\n");
								roomState[3] = "EMPTY";
								broadcast("EXIT_ROOM:" + "4번방:" + parse[2]);
							}
							break;
						case "5번방":
							if (roomState[4] == "FULL") {
								jta.append(parse[2] + "[" + ip + "]"
										+ " 님 5번방 퇴장..\n");
								roomState[4] = "EMPTY";
								if (ip.equals(firstPlayerIP[4])) {
									jta.append(firstPlayerID[4] + "[" + ip
											+ "]" + " 님 5번방 퇴장..\n");

								} else if (ip.equals(secondPlayerIP[4])) {
									jta.append(secondPlayerID[4] + "[" + ip
											+ "]" + " 님 5번방 퇴장..\n");
								}
								broadcast("EXIT_ROOM:" + "5번방:"
										+ secondPlayerID[4]);
								broadcast("EXIT_ROOM:" + "5번방:"
										+ firstPlayerID[4]);
							} else if (roomState[4] == "WAITING") {
								jta.append(parse[2] + "[" + ip + "]"
										+ " 님 5번방 퇴장..\n");
								roomState[4] = "EMPTY";
								broadcast("EXIT_ROOM:" + "5번방:" + parse[2]);
							}
							break;
						}
						break;

					case "LOBBY_CHAT":
						broadcast("LOBBY_CHAT:" + parse[1] + ":" + parse[2]);
						break;

					case "GAME_CHAT":
						switch (parse[1]) {
						case "1번방":
							unicast("GAME_CHAT:" + parse[2] + ":" + parse[3],
									firstPlayerIP[0]);
							unicast("GAME_CHAT:" + parse[2] + ":" + parse[3],
									secondPlayerIP[0]);
							break;
						case "2번방":
							unicast("GAME_CHAT:" + parse[2] + ":" + parse[3],
									firstPlayerIP[1]);
							unicast("GAME_CHAT:" + parse[2] + ":" + parse[3],
									secondPlayerIP[1]);
							break;
						case "3번방":
							unicast("GAME_CHAT:" + parse[2] + ":" + parse[3],
									firstPlayerIP[2]);
							unicast("GAME_CHAT:" + parse[2] + ":" + parse[3],
									secondPlayerIP[2]);
							break;
						case "4번방":
							unicast("GAME_CHAT:" + parse[2] + ":" + parse[3],
									firstPlayerIP[3]);
							unicast("GAME_CHAT:" + parse[2] + ":" + parse[3],
									secondPlayerIP[3]);
							break;
						case "5번방":
							unicast("GAME_CHAT:" + parse[2] + ":" + parse[3],
									firstPlayerIP[4]);
							unicast("GAME_CHAT:" + parse[2] + ":" + parse[3],
									secondPlayerIP[4]);
							break;
						}
						break;

					case "SEND_STONE":
						switch (parse[1]) {
						case "1번방":
							pointSet(Integer.parseInt(parse[2]),
									Integer.parseInt(parse[3]),
									firstPlayerIP[0], secondPlayerIP[0],
									firstPlayerID[0], secondPlayerID[0],
									parse[4]);
							result(firstPlayerIP[0], secondPlayerIP[0],
									parse[4]);
							break;
						case "2번방":
							pointSet(Integer.parseInt(parse[2]),
									Integer.parseInt(parse[3]),
									firstPlayerIP[1], secondPlayerIP[1],
									firstPlayerID[1], secondPlayerID[1],
									parse[4]);
							result(firstPlayerIP[1], secondPlayerIP[1],
									parse[4]);
							break;
						case "3번방":
							pointSet(Integer.parseInt(parse[2]),
									Integer.parseInt(parse[3]),
									firstPlayerIP[2], secondPlayerIP[2],
									firstPlayerID[2], secondPlayerID[2],
									parse[4]);
							result(firstPlayerIP[2], secondPlayerIP[2],
									parse[4]);
							break;
						case "4번방":
							pointSet(Integer.parseInt(parse[2]),
									Integer.parseInt(parse[3]),
									firstPlayerIP[3], secondPlayerIP[3],
									firstPlayerID[3], secondPlayerID[3],
									parse[4]);
							result(firstPlayerIP[3], secondPlayerIP[3],
									parse[4]);
							break;
						case "5번방":
							pointSet(Integer.parseInt(parse[2]),
									Integer.parseInt(parse[3]),
									firstPlayerIP[4], secondPlayerIP[4],
									firstPlayerID[4], secondPlayerID[4],
									parse[4]);
							result(firstPlayerIP[4], secondPlayerIP[4],
									parse[4]);
							break;
						}
						break;

					case "GIVEUP":
						for (int k = 0; k < value.length; k++) {
							for (int l = 0; l < value.length; l++) {
								value[k][l] = 0;
							}
						}
						count = 0;
						switch (parse[1]) {
						case "1번방":
							if (ip == firstPlayerIP[0]){
								unicast("GIVEUP:", secondPlayerIP[0]);
								//unicast("WHITE_WIN:" + secondPlayerID[0], firstPlayerIP[0]);
								unicast("WHITE_WIN:" + secondPlayerID[0], secondPlayerIP[0]);
							} else if (ip == secondPlayerIP[0]){
								unicast("GIVEUP:", firstPlayerIP[0]);
								unicast("BLACK_WIN:" + firstPlayerID[0], firstPlayerIP[0]);
								//unicast("BLACK_WIN:" + firstPlayerID[0], secondPlayerIP[0]);
							}
							break;
						case "2번방":
							if (ip == firstPlayerIP[1]){
								unicast("GIVEUP:", secondPlayerIP[1]);
								unicast("WHITE_WIN:" + secondPlayerID[1], firstPlayerIP[1]);
								unicast("WHITE_WIN:" + secondPlayerID[1], secondPlayerIP[1]);
							} else if (ip == secondPlayerIP[1]){
								unicast("GIVEUP:", firstPlayerIP[1]);
								unicast("BLACK_WIN:" + firstPlayerID[1], firstPlayerIP[1]);
								unicast("BLACK_WIN:" + firstPlayerID[1], secondPlayerIP[1]);
							}
							break;
						case "3번방":
							if (ip == firstPlayerIP[2]){
								unicast("GIVEUP:", secondPlayerIP[2]);
								unicast("WHITE_WIN:" + secondPlayerID[2], firstPlayerIP[2]);
								unicast("WHITE_WIN:" + secondPlayerID[2], secondPlayerIP[2]);
							} else if (ip == secondPlayerIP[2]){
								unicast("GIVEUP:", firstPlayerIP[2]);
								unicast("BLACK_WIN:" + firstPlayerID[2], firstPlayerIP[2]);
								unicast("BLACK_WIN:" + firstPlayerID[2], secondPlayerIP[2]);
							}
							break;
						case "4번방":
							if (ip == firstPlayerIP[3]){
								unicast("GIVEUP:", secondPlayerIP[3]);
								unicast("WHITE_WIN:" + secondPlayerID[3], firstPlayerIP[3]);
								unicast("WHITE_WIN:" + secondPlayerID[3], secondPlayerIP[3]);
							} else if (ip == secondPlayerIP[3]){
								unicast("GIVEUP:", firstPlayerIP[3]);
								unicast("BLACK_WIN:" + firstPlayerID[3], firstPlayerIP[3]);
								unicast("BLACK_WIN:" + firstPlayerID[3], secondPlayerIP[3]);
							}
							break;
						case "5번방":
							if (ip == firstPlayerIP[4]){
								unicast("GIVEUP:", secondPlayerIP[4]);
								unicast("WHITE_WIN:" + secondPlayerID[4], firstPlayerIP[4]);
								unicast("WHITE_WIN:" + secondPlayerID[4], secondPlayerIP[4]);
							} else if (ip == secondPlayerIP[4]){
								unicast("GIVEUP:", firstPlayerIP[4]);
								unicast("BLACK_WIN:" + firstPlayerID[4], firstPlayerIP[4]);
								unicast("BLACK_WIN:" + firstPlayerID[4], secondPlayerIP[4]);
							}
							break;
						}
						break;

					case "RETURN":
						switch (parse[1]) {
						case "1번방":
							stoneBack(firstPlayerIP[0], secondPlayerIP[0],
									parse[2]);
							break;
						case "2번방":
							stoneBack(firstPlayerIP[1], secondPlayerIP[1],
									parse[2]);
							break;
						case "3번방":
							stoneBack(firstPlayerIP[2], secondPlayerIP[2],
									parse[2]);
							break;
						case "4번방":
							stoneBack(firstPlayerIP[3], secondPlayerIP[3],
									parse[2]);
							break;
						case "5번방":
							stoneBack(firstPlayerIP[4], secondPlayerIP[4],
									parse[2]);
							break;
						}
						break;

					case "RETURN_REQUEST":
						switch (parse[1]) {
						case "1번방":
							if (ip == firstPlayerIP[0])
								unicast("RETURN_REQUEST:" + parse[2],
										secondPlayerIP[0]);
							if (ip == secondPlayerIP[0])
								unicast("RETURN_REQUEST:" + parse[2],
										firstPlayerIP[0]);
							break;
						case "2번방":
							if (ip == firstPlayerIP[1])
								unicast("RETURN_REQUEST:" + parse[2],
										secondPlayerIP[1]);
							if (ip == secondPlayerIP[1])
								unicast("RETURN_REQUEST:" + parse[2],
										firstPlayerIP[1]);
							break;
						case "3번방":
							if (ip == firstPlayerIP[2])
								unicast("RETURN_REQUEST:" + parse[2],
										secondPlayerIP[2]);
							if (ip == secondPlayerIP[2])
								unicast("RETURN_REQUEST:" + parse[2],
										firstPlayerIP[2]);
							break;
						case "4번방":
							if (ip == firstPlayerIP[3])
								unicast("RETURN_REQUEST:" + parse[2],
										secondPlayerIP[3]);
							if (ip == secondPlayerIP[3])
								unicast("RETURN_REQUEST:" + parse[2],
										firstPlayerIP[3]);
							break;
						case "5번방":
							if (ip == firstPlayerIP[4])
								unicast("RETURN_REQUEST:" + parse[2],
										secondPlayerIP[4]);
							if (ip == secondPlayerIP[4])
								unicast("RETURN_REQUEST:" + parse[2],
										firstPlayerIP[4]);
							break;
						}
						break;
					}
				}
			} catch (IOException e) {
				list.remove(this);
			}
		}

		private void broadcast(String string) {

			for (GMServer s : list) {
				s.pw.println(string);
				s.pw.flush();
			}
		}
	}

	public void unicast(String string, String ip) {

		for (GMServer s : list) {
			if (ip == s.ip) {
				s.pw.println(string);
				s.pw.flush();
			}
		}
	}

	// 게임 메소드
	// 좌표에 돌 놓기
	public void pointSet(int mouseX, int mouseY, String ip1, String ip2,
			String id1, String id2, String id) {
		// 마우스 좌표와 오목 좌표 간의 거리를 구하여 돌 담기
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < y.length; j++) {
				int a = mouseX - x[i] - 30;
				int b = mouseY - y[j] - 68;
				double c = a * a + b * b;
				double dis = Math.sqrt(c);

				if (dis <= 14) {
					// 중복 위치 돌 계산
					if (value[i][j] == 1 || value[i][j] == 2) {
						if (id.equals(id1))
							unicast("SAME_STONE:", ip1);
						if (id.equals(id2))
							unicast("SAME_STONE:", ip2);
					} else if (value[i][j] == 0) {
						// 검은돌 : 1
						// 흰돌 : 2
						value[i][j] = (count % 2 == 0) ? 1 : 2;

						// 무르기 할 돌의 좌표 기억
						backX1 = backX2;
						backY1 = backY2;
						backX2 = i;
						backY2 = j;

						unicast("SEND_STONE:" + count + ":" + x[i] + ":" + y[j],
								ip1);
						unicast("SEND_STONE:" + count + ":" + x[i] + ":" + y[j],
								ip2);

						count++;
					}
				}
			}
		}
	}

	// 승패 계산
	public void result(String ip1, String ip2, String id) {
		// 세로
		for (int i = 0; i < value.length - 4; i++) {
			for (int j = 0; j < value.length; j++) {
				if (value[i][j] == 1 && value[i + 1][j] == 1
						&& value[i + 2][j] == 1 && value[i + 3][j] == 1
						&& value[i + 4][j] == 1) {
					unicast("BLACK_WIN:" + id, ip1);
					unicast("BLACK_WIN:" + id, ip2);
					for (int k = 0; k < value.length; k++) {
						for (int l = 0; l < value.length; l++) {
							value[k][l] = 0;
						}
					}
					count = 0;

				} else if (value[i][j] == 2 && value[i + 1][j] == 2
						&& value[i + 2][j] == 2 && value[i + 3][j] == 2
						&& value[i + 4][j] == 2) {
					unicast("WHITE_WIN:" + id, ip1);
					unicast("WHITE_WIN:" + id, ip2);
					for (int k = 0; k < value.length; k++) {
						for (int l = 0; l < value.length; l++) {
							value[k][l] = 0;
						}
					}
					count = 0;

				}
			}
		}

		// 가로
		for (int i = 0; i < value.length; i++) {
			for (int j = 0; j < value.length - 4; j++) {
				if (value[i][j] == 1 && value[i][j + 1] == 1
						&& value[i][j + 2] == 1 && value[i][j + 3] == 1
						&& value[i][j + 4] == 1) {
					unicast("BLACK_WIN:" + id, ip1);
					unicast("BLACK_WIN:" + id, ip2);
					for (int k = 0; k < value.length; k++) {
						for (int l = 0; l < value.length; l++) {
							value[k][l] = 0;
						}
					}
					count = 0;

				} else if (value[i][j] == 2 && value[i][j + 1] == 2
						&& value[i][j + 2] == 2 && value[i][j + 3] == 2
						&& value[i][j + 4] == 2) {
					unicast("WHITE_WIN:" + id, ip1);
					unicast("WHITE_WIN:" + id, ip2);
					for (int k = 0; k < value.length; k++) {
						for (int l = 0; l < value.length; l++) {
							value[k][l] = 0;
						}
					}
					count = 0;

				}
			}
		}

		// 대각선
		for (int i = 2; i < value.length - 2; i++) {
			for (int j = 2; j < value.length - 2; j++) {
				// 검은돌
				if (value[i - 2][j - 2] == 1 && value[i - 1][j - 1] == 1
						&& value[i][j] == 1 && value[i + 1][j + 1] == 1
						&& value[i + 2][j + 2] == 1) {
					unicast("BLACK_WIN:" + id, ip1);
					unicast("BLACK_WIN:" + id, ip2);
					count = 0;
					for (int k = 0; k < value.length; k++) {
						for (int l = 0; l < value.length; l++) {
							value[k][l] = 0;
						}
					}
					count = 0;

				} else if (value[i - 2][j + 2] == 1 && value[i - 1][j + 1] == 1
						&& value[i][j] == 1 && value[i + 1][j - 1] == 1
						&& value[i + 2][j - 2] == 1) {
					unicast("BLACK_WIN:" + id, ip1);
					unicast("BLACK_WIN:" + id, ip2);
					for (int k = 0; k < value.length; k++) {
						for (int l = 0; l < value.length; l++) {
							value[k][l] = 0;
						}
					}
					count = 0;

				}
				// 흰돌
				if (value[i - 2][j - 2] == 2 && value[i - 1][j - 1] == 2
						&& value[i][j] == 2 && value[i + 1][j + 1] == 2
						&& value[i + 2][j + 2] == 2) {
					unicast("WHITE_WIN:" + id, ip1);
					unicast("WHITE_WIN:" + id, ip2);
					for (int k = 0; k < value.length; k++) {
						for (int l = 0; l < value.length; l++) {
							value[k][l] = 0;
						}
					}
					count = 0;

				} else if (value[i - 2][j + 2] == 2 && value[i - 1][j + 1] == 2
						&& value[i][j] == 2 && value[i + 1][j - 1] == 2
						&& value[i + 2][j - 2] == 2) {
					unicast("WHITE_WIN:" + id, ip1);
					unicast("WHITE_WIN:" + id, ip2);
					for (int k = 0; k < value.length; k++) {
						for (int l = 0; l < value.length; l++) {
							value[k][l] = 0;
						}
					}
					count = 0;

				}
			}
		}
	}

	// 무르기
	public void stoneBack(String ip1, String ip2, String id) {
		unicast("RETURN:" + count + ":" + id, ip1);
		unicast("RETURN:" + count + ":" + id, ip2);

		value[backX1][backY1] = 0;
		value[backX2][backY2] = 0;

		count -= 2;
	}

}
