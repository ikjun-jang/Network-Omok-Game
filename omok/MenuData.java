package omok;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class MenuData extends JFrame {
	JLabel jlb1;
	JLabel jlb2;
	JLabel jlb3;
	JLabel jlb4;
	JLabel jlb5;
	JLabel jlb6;
	JLabel jlb7;

	public MenuData() {
		setTitle("��������");
		setBounds(50, 50, 300, 370);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		
		jlb1 = new JLabel("1���� ���� ���� �����Դϴ�.");
		jlb2 = new JLabel("�����̵�:");
		jlb3 = new JLabel("������");
		jlb4 = new JLabel("������");
		jlb5 = new JLabel("������");
		jlb6 = new JLabel("�´뼷");
		jlb7 = new JLabel("������");

		setLayout(null);

		jlb1.setBounds(50, 30, 200, 30);
		jlb2.setBounds(50, 70, 200, 30);
		jlb3.setBounds(50, 110, 200, 30);
		jlb4.setBounds(50, 150, 200, 30);
		jlb5.setBounds(50, 190, 200, 30);
		jlb6.setBounds(50, 230, 200, 30);
		jlb7.setBounds(50, 270, 200, 30);

		add(jlb1);
		add(jlb2);
		add(jlb3);
		add(jlb4);
		add(jlb5);
		add(jlb6);
		add(jlb7);

		setVisible(true);
	}

}
