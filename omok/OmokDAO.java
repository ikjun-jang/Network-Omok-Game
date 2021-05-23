package omok;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OmokDAO {
	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";
	private String user = "scott";
	private String password = "tiger";
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	private StringBuffer sql = new StringBuffer();
	
	public OmokDAO(){
		// Connect 객체
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url,user,password);
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 로딩 실패");
		} catch (SQLException e) {
			System.out.println("db 연결 실패");
		}
	}
	
	// CRUD
	// 1. 전체 조회
	public ArrayList<OmokVO> selectAll(){
		ArrayList<OmokVO> list = new ArrayList<OmokVO>();
		// db에 가서 레코드별로 vo객체 생성후 ArrayList에 담기
		// sql 초기화
		sql.setLength(0);
		sql.append("SELECT * FROM OMOK ");
		sql.append("ORDER BY WIN DESC , RATE DESC , TOTAL DESC ");
		try {
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			for(int i=1;rs.next();i++){
				String id = rs.getString("ID");
				int total = rs.getInt("TOTAL");
				int win = rs.getInt("WIN");
				int rate = rs.getInt("RATE");
				int rank = rs.getInt("RANK");
				OmokVO vo = new OmokVO(id,total,win,rate,i);
				list.add(vo);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
// 2. 특정 데이터 조회
	public OmokVO selectOne(String id){
		sql.setLength(0);
		sql.append("SELECT ID , TOTAL , WIN , RATE FROM OMOK ");
		sql.append("WHERE ID = ? ");
		OmokVO vo = null;
		try {
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, id); // pk
			rs = pstmt.executeQuery();
			rs.next();
			int total = rs.getInt("TOTAL");
			int win = rs.getInt("WIN");
			int rate = rs.getInt("RATE");
			vo = new OmokVO(id,total,win,rate);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vo;
	}	
// 새로운 row 생성	
	public void insertNew(OmokVO vo){
		sql.setLength(0);
		sql.append("INSERT INTO OMOK ");
		sql.append("VALUES ( ? , 0 , 0 , 0 , 0 ) ");
		try {
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, vo.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
// 총 게임 횟수 갱신	
	public void updateTotal(OmokVO vo1){
		sql.setLength(0);
		sql.append("UPDATE OMOK ");
		sql.append("SET TOTAL = TOTAL + 1 " );
		sql.append("WHERE ID = ? ");
		try {
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, vo1.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}	
// 이긴 게임횟수 갱신
	public void updateWin(OmokVO vo){
		sql.setLength(0);
		sql.append("UPDATE OMOK ");
		sql.append("SET WIN = WIN + 1 ");
		sql.append("WHERE ID = ? ");
		try {
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1,vo.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
// 승률 갱신
	public void updateRate(OmokVO vo1){
		sql.setLength(0);
		sql.append("UPDATE OMOK ");
		sql.append("SET RATE = (WIN/TOTAL)*100 ");
		sql.append("WHERE ID = ? ");
		try {
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, vo1.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
// 자원반납	
	public void close(){
		try {
			if(rs != null) rs.close();
			if(pstmt != null) pstmt.close();
			if(conn != null) conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
