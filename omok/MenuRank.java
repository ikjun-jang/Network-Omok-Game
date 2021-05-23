package omok;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MenuRank extends JFrame {
	JLabel[] jlbId, jlbRank, jlbTotal, jlbWin, jlbRate;
	JLabel jlbColumn;
	JList jl;
	OmokDAO dao = new OmokDAO();

	public MenuRank() {
		super("Ελ°θ");
		setBounds(50, 50, 260, 600);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(null);
		setResizable(false);

		jlbColumn = new JLabel(
				"           ID                    P            W          WIN(%)");
		jlbColumn.setBounds(10, 5, 300, 20);
		add(jlbColumn);

		jlbId = new JLabel[50];
		jlbRank = new JLabel[50];
		jlbTotal = new JLabel[50];
		jlbWin = new JLabel[50];
		jlbRate = new JLabel[50];

		ArrayList<OmokVO> list = dao.selectAll();

		for (int i = 0; i < list.size(); i++) {
			jlbId[i] = new JLabel();
			jlbRank[i] = new JLabel();
			jlbTotal[i] = new JLabel();
			jlbWin[i] = new JLabel();
			jlbRate[i] = new JLabel();

			jlbId[i].setText(list.get(i).getId());
			jlbRank[i].setText(String.valueOf(list.get(i).getRank()));
			jlbTotal[i].setText(String.valueOf(list.get(i).getTotal()));
			jlbWin[i].setText(String.valueOf(list.get(i).getWin()));
			jlbRate[i].setText(String.valueOf(list.get(i).getRate()));

			jlbRank[i].setBounds(10, 20 + (i * 40), 50, 30);
			jlbId[i].setBounds(30, 20 + (i * 40), 50, 30);
			jlbTotal[i].setBounds(110, 20 + (i * 40), 50, 30);
			jlbWin[i].setBounds(160, 20 + (i * 40), 50, 30);
			jlbRate[i].setBounds(210, 20 + (i * 40), 50, 30);

			add(jlbId[i]);
			add(jlbRank[i]);
			add(jlbTotal[i]);
			add(jlbWin[i]);
			add(jlbRate[i]);
		}

		setVisible(true);
	}
}
