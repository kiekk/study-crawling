package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	public static Connection getConnection() {
		Connection conn = null;
		//연결을 하기 위해서는 url, user, pw가 필요합니다.
		try {
			String url = "jdbc:oracle:thin:@localhost:1521:XE";
			String user = "hr";
			String pw = "hr";
			String driver = "oracle.jdbc.driver.OracleDriver";
			
			//1. 클래스가 아닌 경로 혹은 파일을 클래스화 시키는 작업
			//2. 외부 클래스를 가져오기 위함
			Class.forName(driver);
			conn = DriverManager.getConnection(url,user,pw);
			
			//Exception 객체명은 예외 클래스 대문자를 가져와서 만듭니다.
		} catch (ClassNotFoundException cnfe) {
			//예외 발생시 어떤 파일, 몇번 째 줄인지를 작성해주고, 해당 내용을 작성합니다.
			System.out.println("DBConnection.java:25, 드라이버 로딩 실패 : "+ cnfe);
		} catch (SQLException sqle) {
			System.out.println("DBConnection.java:27, DB 접속 실패 : "+ sqle);	
		} catch (Exception e) {
			System.out.println("DBConnection.java:29,: "+ e);
		}
		return conn;
//		컨트롤러에서 close해줍니다.
	}
}
