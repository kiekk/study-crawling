package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import vo.BookVO;
import vo.UserVO;

public class BookDAO {
	static Scanner sc = new Scanner(System.in);
	Connection conn;
	PreparedStatement pstm;
	ResultSet rs;
	//booklist를 담을 객체
	BookVO book = null;
	ArrayList<BookVO> books = null;

	//도서 리스트
	//select를 flag 변수로써 각각 조건에 맞는 쿼리문으로 데이터를 받아올 수 있도록 합니다.
	public ArrayList<BookVO> bookList(int select) {
		//대여 가능한 도서 리스트   select == 1
		String query = "SELECT * FROM TL_BOOK WHERE BOOK_RENTAL = '대여가능'";
		//도서현황 대여횟수,출판일로 정렬  select == 2
		String query2 = "SELECT * FROM TL_BOOK ORDER BY RENTAL_COUNT DESC";
		//로그인 상태를 먼저 확인
		try {
			conn = DBConnection.getConnection();
			switch(select) {
				case 1: pstm = conn.prepareStatement(query); break;
				case 2: pstm = conn.prepareStatement(query2); break;
			}//end switch
			
			rs = pstm.executeQuery();

			books = new ArrayList<BookVO>();				//도서리스트를 저장할 ArrayList생성
			while (rs.next()) {								//도서리스트가 null일 때까지 반복해서 저장
				book = new BookVO();
				book.setBook_number(rs.getInt(1)); 		 	//도서번호
				book.setBook_name(rs.getString(2));   		//도서명
				book.setAuthor(rs.getString(3));   			//저자
				book.setCompany(rs.getString(4)); 			//출판사
				book.setBook_date(rs.getString(5).substring(0,10));			//출판일
				book.setBook_section(rs.getString(6));		//도서위치
				book.setBook_rental(rs.getString(7));		//대여일
				book.setRental_count(rs.getInt(8));			//대여횟수
				books.add(book);
			}//end while
			//도서 리스트 출력  -> 테스트용
//			books.forEach(value -> System.out.println(value));

		} catch (SQLException sqle) {
			System.out.println("BookDAO.java:57, bookList() 쿼리 오류 : " + sqle);
		} catch (Exception e) {
			System.out.println("BookDAO.java:59, bookList() 오류 " + e);
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
				throw new RuntimeException("BookDAO.java:72, bookList() close 오류" + sqle.getMessage());
			}//end sub-try
		}//end main-try
		return books;
	}//end bookList()
	//도서 위치 찾기 - 도서명 - 오버로딩
	public String findBook(String book_name) {
		String bookSection = "";
		//book_name으로 해당 도서의 위치를 조회합니다. 해당 도서의 위치를 bookSection에 담아서 반환
		String query = "SELECT BOOK_SECTION FROM TL_BOOK WHERE BOOK_NAME LIKE '%' || ? || '%'";
		try {
			conn = DBConnection.getConnection();
			pstm = conn.prepareStatement(query);
			pstm.setString(1, book_name);
			rs = pstm.executeQuery();

			if (rs.next()) {
				bookSection = rs.getString(1);
//				System.out.println(bookSection);		//->테스트
			}//end if
		} catch (SQLException sqle) {
			System.out.println("BookDAO.java:93, findBook() 쿼리 오류 : " + sqle);
		} catch (Exception e) {
			System.out.println("BookDAO.java:95, findBook() 오류 " + e);
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
				throw new RuntimeException("BookDAO.java:108, findBook() close 오류" + sqle.getMessage());
			}//end sub-try
		}//end main-try
		return bookSection;
	}//end findBook()
	//도서 위치 찾기 - 도서번호 - 오버로딩
	public String findBook(int book_number) {
		String bookSection = "";
		//book_number으로 해당 도서의 위치를 조회합니다. 해당 도서의 위치를 bookSection에 담아서 반환
		String query = "SELECT BOOK_SECTION FROM TL_BOOK WHERE BOOK_NUMBER = ?";
		try {
			conn = DBConnection.getConnection();
			pstm = conn.prepareStatement(query);
			pstm.setInt(1, book_number);
			rs = pstm.executeQuery();

			rs.next(); 
			bookSection = rs.getString(1);
//			System.out.println(bookSection);
			
		} catch (SQLException sqle) {
			System.out.println("BookDAO.java:129, findBook() 쿼리 오류 : " + sqle);
		} catch (Exception e) {
			System.out.println("BookDAO.java:131, findBook() 오류 " + e);
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
				throw new RuntimeException("BookDAO.java:144, findBook() close 오류" + sqle.getMessage());
			}//end sub-try
		}//end main-try
		return bookSection;
	}//end findBook()
	//대여가능여부 변경
	public boolean switchRental (int book_number) {
		//대여할 때는 대여 횟수 1증가
		String query = "UPDATE TL_BOOK SET BOOK_RENTAL = ?, RENTAL_COUNT = RENTAL_COUNT + 1 WHERE BOOK_NUMBER = ?";
		//반납할 떄는 대여가능여부만 변경
		String query2 = "UPDATE TL_BOOK SET BOOK_RENTAL = ? WHERE BOOK_NUMBER = ?";
		boolean check = false;
		//대여여부 체크 -> 대여여부를 저장 ('대여가능'or'대여중')
		String bookRental = rentalCheck(book_number);
		try {
			conn = DBConnection.getConnection();
			//해당도서의 대여여부가 "대여가능"일경우  query를 전송
			if(bookRental.equals("대여가능")) {
				pstm = conn.prepareStatement(query);
				pstm.setString(1, "대여중");
			//해당도서의 대여여부가 "대여중"일경우 query2를 전송
			}else if(bookRental.equals("대여중")) {
				pstm = conn.prepareStatement(query2);
				pstm.setString(1, "대여가능");
			}//end if
			pstm.setInt(2, book_number);
			
			if(pstm.executeUpdate() == 1) {
				System.out.println("대여 여부 변경 성공");
				check = true;
			}//end if

		} catch (SQLException sqle) {
			System.out.println("BookDAO.java:177, switchRental() 쿼리 오류 : " + sqle);
		} catch (Exception e) {
			System.out.println("BookDAO.java:179, switchRental() 오류 " + e);
		} finally {
			try {
				if (pstm != null) {
					pstm.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				throw new RuntimeException("BookDAO.java:189, switchRental() close 오류" + sqle.getMessage());
			}//end sub-try
		}//end main-try
		return check;
	}//end switchRental()
	//대여여부 확인 - 오버로딩 String
	public String rentalCheck(String book_name) {
		//대여 여부를 저장할 변수
		String bookRental = null;
		//book_number를 통해서 해당 도서의 대여 여부를 가져옵니다.
		String query = "SELECT BOOK_RENTAL FROM TL_BOOK WHERE BOOK_NAME LIKE '%' || ? || '%'";
		try {
			conn = DBConnection.getConnection();
			pstm = conn.prepareStatement(query);
			pstm.setString(1, book_name);
			rs = pstm.executeQuery();

			rs.next();

			bookRental = rs.getString(1);
		} catch (SQLException sqle) {
			System.out.println("BookDAO.java:211, rentalCheck() 쿼리 오류 : " + sqle);
		} catch (Exception e) {
			System.out.println("BookDAO.java:213, rentalCheck() 오류 " + e);
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
				throw new RuntimeException("BookDAO.java:226, rentalCheck() close 오류" + sqle.getMessage());
			}//end sub-try
		}//end main-try
		return bookRental;
	}//end rentalCheck()
	//대여여부 확인 - 오버로딩 int
	public String rentalCheck(int book_number) {
		//대여 여부를 저장할 변수
		String bookRental = "";
		//book_number를 통해서 해당 도서의 대여 여부를 가져옵니다.
		String query = "SELECT BOOK_RENTAL FROM TL_BOOK WHERE BOOK_NUMBER = ?";
		try {
			conn = DBConnection.getConnection();
			pstm = conn.prepareStatement(query);
			pstm.setInt(1, book_number);
			rs = pstm.executeQuery();

			rs.next();

			bookRental = rs.getString(1);
			
		} catch (SQLException sqle) {
			System.out.println("BookDAO.java:248, rentalCheck() 쿼리 오류 : " + sqle);
		} catch (Exception e) {
			System.out.println("BookDAO.java:250, rentalCheck() 오류 " + e);
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
				throw new RuntimeException("BookDAO.java:263, rentalCheck() close 오류" + sqle.getMessage());
			}//end sub-try
		}//end main-try
		return bookRental;
	}//end rentalCheck()
	//도서 목록 추가
	public void addBookList(String book_name,String author,String company,String book_date) {
		Random r = new Random();
		String query = "INSERT INTO TL_BOOK VALUES (SEQ_BOOK.NEXTVAL,?,?,?,?,?,'대여가능',0)";
		String section = "ABCDEFGHIJ";	//랜덤으로 도서위치(Section)을 배정하기 위해 문자열로 선언. charAt으로 추출
		try {
			conn = DBConnection.getConnection();
			pstm = conn.prepareStatement(query);
			pstm.setString(1, book_name);
			pstm.setString(2, author);
			pstm.setString(3, company);
			pstm.setString(4, book_date);
			pstm.setString(5, String.format("%s", section.charAt(r.nextInt(section.length()))));	//랜덤하게 도서위치 배정

			if(pstm.executeUpdate() == 1) {
				System.out.println("도서 목록 추가 성공");
			}//end if
		} catch (SQLException sqle) {
			System.out.println("BookDAO.java:286, addBookList() 쿼리 오류 : " + sqle);
		} catch (Exception e) {
			System.out.println("BookDAO.java:288, addBookList() 오류 " + e);
		} finally {
			try {
				if (pstm != null) {
					pstm.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				throw new RuntimeException("UserDAO.java:298, addBookList() close 오류" + sqle.getMessage());
			}//end sub-try
		}//end main-try
	}//end addBookList()
	//도서 목록 삭제
	public void deleteBookList(int book_number) {
		//전달받은 book_number와 일치하는 도서의 데이터를 삭제
		String query = "DELETE FROM TL_BOOK WHERE BOOK_NUMBER = ?";
		try {
			conn = DBConnection.getConnection();
			pstm = conn.prepareStatement(query);
			pstm.setInt(1, book_number);

			if(pstm.executeUpdate() == 1) {
				System.out.println("도서 삭제 성공");
			}
		} catch (SQLException sqle) {
			System.out.println("UserDAO.java:315, deleteBookList() 쿼리 오류 : " + sqle);
		} catch (Exception e) {
			System.out.println("UserDAO.java:317, deleteBookList() 오류 " + e);
		} finally {
			try {
				if (pstm != null) {
					pstm.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				throw new RuntimeException("BookDAO.java:327, deleteBookList() close 오류" + sqle.getMessage());
			}//end sub-try
		}//end main-try
	}//end deleteBookList()
}
