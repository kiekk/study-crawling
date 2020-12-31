package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import vo.RentalVO;

public class RentalDAO {
	Connection conn;
	PreparedStatement pstm;
	ResultSet rs;
	UserDAO user;
	BookDAO book = new BookDAO();
	RentalVO rent;
	ArrayList<RentalVO> rents;
	//대여중인 도서 리스트
	public ArrayList<RentalVO> rentalBookList(int select){
		user = new UserDAO();
		//회원이 대여한 도서 리스트 - 서브 쿼리
		String query = "SELECT * FROM TL_RENTAL WHERE USER_NUMBER = (SELECT USER_NUMBER FROM TL_USER WHERE PHONE_NUMBER = ?)";
		//현재 대여된 도서 리스트
		String query2 = "SELECT * FROM TL_RENTAL";
		try {
			conn = DBConnection.getConnection();
			switch(select) {
				//회원이 대여한 도서리스트를 출력할때, select라는 flag변수가 1인경우 query를 전송
				case 1: 
					pstm = conn.prepareStatement(query); 
					pstm.setString(1, user.session_id);
					break;
				//현재 대여된 도서리스트를 출력할때, select라는 flag변수가 2인경우 query2를 전송
				case 2: pstm = conn.prepareStatement(query2); break;
			}//end switch
		
			rs = pstm.executeQuery();
			//rs != null -> 도서리스트가 존재하는 경우이기 때문에 객체 생성. rs == null이면 생성 X
			if(rs != null) {rents = new  ArrayList<RentalVO>();}
			
			while (rs.next()) {
				rent = new RentalVO();
				rent.setName(rs.getString(1));
				rent.setUser_number(rs.getInt(2));
				rent.setBook_number(rs.getInt(3));
				rent.setBook_name(rs.getString(4));
				rent.setRental_date(rs.getString(5));
				rent.setReturn_date(rs.getString(6));
				rents.add(rent);
			}//end while
		} catch (SQLException sqle) {
			System.out.println("RentalDAO.java:53, rentalBookList() 쿼리 오류 : " + sqle);
		} catch (Exception e) {
			System.out.println("RentalDAO.java:55, rentalBookList() 오류 " + e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstm != null) {
					pstm.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				throw new RuntimeException("RentalDAO.java:68, rentalBookList() close 오류" + sqle.getMessage());
			}//end sub-try
		}//end main-try
		return rents;
	}//end rentalBookList()
	//대여 하기
	public void rentalBook(int book_number) {
		//TL_USER 테이블에서 NAME, USER_NUMBER
		//TL_BOOK 테이블에서 BOOK_NUMBER, BOOK_NAME
		String query = "INSERT INTO TL_RENTAL "
				+ "VALUES("
				+ "(SELECT NAME FROM TL_USER WHERE PHONE_NUMBER = ?),"				//TL_USER 테이블에서 NAME
				+ "(SELECT USER_NUMBER FROM TL_USER WHERE PHONE_NUMBER = ?), "		//TL_USER 테이블에서 USER_NUMBER
				+ "(SELECT BOOK_NUMBER FROM TL_BOOK WHERE BOOK_NUMBER = ?),"		//TL_BOOK 테이블에서 BOOK_NUMBER
				+ "(SELECT BOOK_NAME FROM TL_BOOK WHERE BOOK_NUMBER = ?),"			//TL_BOOK 테이블에서 BOOK_NAME
				+ "SYSDATE,"														//대여 날짜(현재시간)
				+ "(SYSDATE)"													//반납 날짜(대여 날짜 + 7일)
				+ ")";
		try {
			conn = DBConnection.getConnection();
			pstm = conn.prepareStatement(query);
			pstm.setString(1, user.session_id);
			pstm.setString(2, user.session_id);
			pstm.setInt(3, book_number);
			pstm.setInt(4, book_number);

			if(pstm.executeUpdate() == 1) {
				//해당 도서의 대여여부를 변경 '대여가능'->'대여중'
				if(book.switchRental(book_number)) {
					System.out.println("대여 성공");					
				}else {
					System.out.println("대여 실패");
				}//end sub-if
			}//end main-if
		} catch (SQLException sqle) {
			System.out.println("RentalDAO.java:103, rentalBook() 쿼리 오류 : " + sqle);
		} catch (Exception e) {
			System.out.println("RentalDAO.java:105, rentalBook() 오류 " + e);
		} finally {
			try {
				if (pstm != null) {
					pstm.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				throw new RuntimeException("RentalDAO.java:115, rentalBook() close 오류" + sqle.getMessage());
			}//end sub-try
		} // end main-try
	}//end rentalBook()
	//반납 하기
	public void returnBook(int book_number) {
		String query = "DELETE FROM TL_RENTAL WHERE BOOK_NUMBER = ?";
		try {
			conn = DBConnection.getConnection();
			pstm = conn.prepareStatement(query);
			pstm.setInt(1, book_number);

			if(pstm.executeUpdate() == 1) {
				//해당 도서의 대여여부를 변경 '대여중' -> '대여가능'
				if(book.switchRental(book_number)) {
					System.out.println("반납 성공");					
				}
			}
		} catch (SQLException sqle) {
			System.out.println("RentalDAO.java:134, returnBook() 쿼리 오류 : " + sqle);
		} catch (Exception e) {
			System.out.println("RentalDAO.java:136, returnBook() 오류 " + e);
		} finally {
			try {
				if (pstm != null) {
					pstm.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				throw new RuntimeException("RentalDAO.java:146, returnBook() close 오류" + sqle.getMessage());
			}
		} // end try
	}//end returnBook()
}
