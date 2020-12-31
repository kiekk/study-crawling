package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import vo.UserVO;

public class UserDAO {
	private static final int KEY_CODE = 3; // 암호화, 복호화 할 때 사용할 키값
	Connection conn;
	PreparedStatement pstm;
	ResultSet rs;
	UserVO user;

	public static String session_id;	//로그인된 아이디를 기록

	// 아이디 검사
	public boolean checkId(String phone_number) {
		// Flag
		boolean check = false;
		String query = "SELECT COUNT(*) FROM TL_USER WHERE PHONE_NUMBER = ?";
		try {
			conn = DBConnection.getConnection();
			pstm = conn.prepareStatement(query);
			pstm.setString(1, phone_number);
			rs = pstm.executeQuery();

			rs.next();

			if (rs.getInt(1) == 1) {
				check = true;
			}

		} catch (SQLException sqle) {
			System.out.println("UserDAO.java:39, checkId()쿼리 오류 " + sqle);
		} catch (Exception e) {
			System.out.println("UserDAO.java:41, checkId() 오류 " + e);
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
				throw new RuntimeException("UserDAO.java:54, checkId() close 오류" + sqle.getMessage());
			}
		} // end try
		return check;
	}// end checkId()
	
	// 회원가입
	public boolean join(String phone_number, String pw, String name, int age) {
		String query = "";
		boolean check = false;
		if (!checkId(phone_number)) {
			query = "INSERT INTO TL_USER (USER_NUMBER,PHONE_NUMBER, PW, NAME, AGE)"
					+ "VALUES(SEQ_USER.NEXTVAL, ?, ?, ?, ?)";
			try {
				int idx = 0;
				conn = DBConnection.getConnection();
				pstm = conn.prepareStatement(query);
				pstm.setString(++idx, phone_number);
				pstm.setString(++idx, encrypt(pw));
				pstm.setString(++idx, name);
				pstm.setInt(++idx, age);
				// select 이외에 결과 값을 반환받으려면 excuteUpdate()를 써야합니다. 0,1을 반환합니다.
				if (pstm.executeUpdate() == 1) {
					check = true;
				}
			} catch (SQLException sqle) {
				System.out.println("UserDAO.java:80, join() 쿼리 오류 : " + sqle);
			} catch (Exception e) {
				System.out.println("UserDAO.java:82, join() 오류 " + e);
			} finally {
				try {
					if (pstm != null) {
						pstm.close();
					}
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException sqle) {
					throw new RuntimeException("UserDAO.java:92, join() close 오류" + sqle.getMessage());
				}
			} // end try
		} // end if
		return check;
	}// end join()
	
	// 암호화
	public String encrypt(String pw) {
		String enc_pw = "";
		// pw를 각각 문자로 나눈뒤 설정한 암호화 KEY_CODE를 곱한(아스키코드로 연산)뒤의 문자를 enc_pw 변수에 누적합니다.
		for (int i = 0; i < pw.length(); i++) {
			enc_pw += (char) (pw.charAt(i) * KEY_CODE);
		}
		// System.out.println("기존 암호 : "+pw+"\tenc암호 : "+enc_pw);
		return enc_pw;
	}

	// 복호화
	public String decrypt(String enc_pw) {
		// String dec_pw;
		// dec_pw에는 null값이 들어가는데, 이때 "1234"문자열을 +=연산을 할 경우 null1234로 null값이 입력됩니다.
		String dec_pw = "";
		for (int i = 0; i < enc_pw.length(); i++) {
			dec_pw += (char) (enc_pw.charAt(i) / KEY_CODE);
		}
		// System.out.println("end암호 : "+enc_pw+"\tdec암호 : "+dec_pw);
		return dec_pw;
	}

	// 로그인
	// 외부에서 사용자가 입력한 phone_number와 PW를 전달받습니다.
	public boolean login(String phone_number, String pw) {
		String query = "SELECT COUNT(*) FROM TL_USER WHERE phone_number = ? AND PW = ?";
		// Flag
		boolean check = false;
		if (checkId(phone_number)) {
			try {
				conn = DBConnection.getConnection();
				pstm = conn.prepareStatement(query);
				pstm.setString(1, phone_number);
				pstm.setString(2, encrypt(pw));
				rs = pstm.executeQuery();
				
				rs.next();

				if(rs.getInt(1) == 1) {
					System.out.println("로그인 성공");
					session_id = phone_number;
					check = true;
				}else {
					System.out.println("전화번호 또는 비밀번호를 확인해주세요");
					check = false;
				}
			} catch (SQLException sqle) {
				System.out.println("UserDAO.java:147, login() 쿼리 오류 : " + sqle);
			} catch (Exception e) {
				System.out.println("UserDAO.java:149, login() 오류 " + e);
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
					throw new RuntimeException("UserDAO.java:162, login() close 오류" + sqle.getMessage());
				}
			} // end try
		} else {
			System.out.println("존재하지 않는 ID입니다.");
		} // end if
		return check;
	}

	// 로그아웃
	public void logout() {
		// 저장되어 있는 session_id값을 없애줍니다.
		// 로그인 기록을 지웁니다.
		session_id = null;
	}

	// 수정 - 비밀번호 변경
	// 정보보호상, 로그인이 되었더라도 본인의 비밀번호를 한 번 더 입력받습니다.
	// 기존 비밀번호와 새롭게 설정할 비밀번호를 전달받습니다.
	public boolean update(String pw, String new_pw) {
		// 로그인된 id와 입력한 pw를 검색한 후 찾았다면 해당 사용자의
		// pw를 새로운 pw로 변경해줍니다.
		String query = "UPDATE TL_USER SET PW = ? WHERE PHONE_NUMBER = ? AND PW = ?";
		boolean check = false;
		// 로그인 상태를 먼저 확인
		if (session_id == null) {
			// 만약 로그인 상태가 아니라면 바로 false를 리턴해줍니다.
			return false;
		}
		try {
			conn = DBConnection.getConnection();
			pstm = conn.prepareStatement(query);
			pstm.setString(1, encrypt(new_pw));
			// 이미 로그인 되어있어야 비밀번호 변경이 가능하기 때문에,
			// id를 인자로 받지 않고 session_id를 통해서 id를 가져올 수 있습니다.
			pstm.setString(2, session_id);
			pstm.setString(3, encrypt(pw));

			// SQL문 결과 건수가 1이라면 비밀번호 변경 성공
			if (pstm.executeUpdate() == 1) {
				check = true;
			}
		} catch (SQLException sqle) {
			System.out.println("UserDAO.java:241, update() 쿼리 오류 : " + sqle);
		} catch (Exception e) {
			System.out.println("UserDAO.java:243, update() 오류 " + e);
		} finally {
			try {
				if (pstm != null) {
					pstm.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				throw new RuntimeException("UserDAO.java:253, update() close 오류" + sqle.getMessage());
			}
		} // end try
		return check;
	}

	//내 정보 보기
	public UserVO select() {
		String query = "SELECT * FROM TL_USER WHERE PHONE_NUMBER = ?";
		UserVO user = null;
		// 로그인 상태를 먼저 확인
		if (session_id == null) {
			return null;
		}
		try {
			conn = DBConnection.getConnection();
			pstm = conn.prepareStatement(query);
			pstm.setString(1, session_id);
			rs = pstm.executeQuery();

			// rs에 담겨있는 데이터를 user에 저장
			// 로그인된 사용자는 한명이기 때문에 while문이 아닌 if문으로 작성합니다.
			if (rs.next()) {
				user = new UserVO();
				user.setUser_number(rs.getInt(1)); 		 // USER_NUMBER
				user.setPhone_number(rs.getString(2));   // PHONE_NUMBER
				user.setPw(decrypt(rs.getString(3)));    // PW
				user.setName(rs.getString(4)); 			 // NAME
				user.setAge(rs.getInt(5));				 // AGE
			}

		} catch (SQLException sqle) {
			System.out.println("UserDAO.java:322, select() 쿼리 오류 : " + sqle);
		} catch (Exception e) {
			System.out.println("UserDAO.java:324, select() 오류 " + e);
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
				throw new RuntimeException("UserDAO.java:337, select() close 오류" + sqle.getMessage());
			}
		} // end try
		return user;
	}

	//등록된 회원 현황
	public ArrayList<UserVO> selectAll() {
		ArrayList<UserVO> users = null;
		UserVO user = null;
		String query = "SELECT * FROM TL_USER";

		try {
			conn = DBConnection.getConnection();
			pstm = conn.prepareStatement(query);
			rs = pstm.executeQuery();

			if (rs != null) {
				users = new ArrayList<UserVO>();
			}
			
			while (rs.next()) {
				user = new UserVO();
				user.setUser_number(rs.getInt(1)); 		 // USER_NUMBER
				user.setPhone_number(rs.getString(2));   // PHONE_NUMBER
				user.setPw(decrypt(rs.getString(3)));    // PW
				user.setName(rs.getString(4)); 			 // NAME
				user.setAge(rs.getInt(5));				 // AGE
				users.add(user);
			}
			users.forEach(value -> System.out.println(value));
			
		} catch (SQLException sqle) {
			System.out.println("UserDAO.java:372, selectAll() 쿼리 오류 : " + sqle);
		} catch (Exception e) {
			System.out.println("UserDAO.java:374, selectAll() 오류 " + e);
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
				throw new RuntimeException("UserDAO.java:387, selectAll() close 오류" + sqle.getMessage());
			}
		} // end try
		return users;
	}
	/**
	 * 비밀번호 찾기 시 사용자의 비밀번호를 임시 비밀번호로 변경해주는 메소드
	 * 
	 * @param user_number
	 * @param temp_pw
	 * @return boolean
	 */
	// 기존 비밀번호를 임시 비밀번호로 변경
	public boolean update(int user_number, String temp_pw) {
		String query = "UPDATE TL_USER SET PW = ? WHERE USER_NUMBER = ?";
		boolean check = false;
		try {
			conn = DBConnection.getConnection();
			pstm = conn.prepareStatement(query);
			pstm.setString(1, encrypt(temp_pw));
			pstm.setInt(2, user_number);

			if (pstm.executeUpdate() == 1) {
				check = true;
			}
		} catch (SQLException sqle) {
			System.out.println("UserDAO.java:490, update() 쿼리 오류 : " + sqle);
		} catch (Exception e) {
			System.out.println("UserDAO.java:492, update() 오류 " + e);
		} finally {
			try {
				if (pstm != null) {
					pstm.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				throw new RuntimeException("UserDAO.java:502, update() close 오류" + sqle.getMessage());
			}
		} // end try
		return check;
	}

	// 비번 찾기
	public boolean findPw(String phone_number) {
		// 랜덤한 문자의 조합으로 임시 비밀번호를 만듭니다.
		String temp = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*()_+=[]{};:/?";
		Random r = new Random();
		String query = "SELECT USER_NUMBER FROM TL_USER WHERE PHONE_NUMBER = ?";
		boolean check = false;
		String temp_pw = "";

		try {
			conn = DBConnection.getConnection();
			pstm = conn.prepareStatement(query);
			pstm.setString(1, phone_number);
			rs = pstm.executeQuery();

			if (rs.next()) {
				// 8자리 임시비밀번호 생성
				for (int i = 0; i < 8; i++) {
					temp_pw += temp.charAt(r.nextInt(temp.length()));
				}
				if (update(rs.getInt(1), temp_pw)) {
					System.out.println("임시 비밀번호 : " +temp_pw);
					System.out.println("노출될 수 있으니 반드시 비밀번호를 변경해주세요.");
					check = true;
				}
			}
		} catch (SQLException sqle) {
			System.out.println("UserDAO.java:573, findPw() 쿼리 오류 : " + sqle);
		} catch (Exception e) {
			System.out.println("UserDAO.java:575, findPw() 오류 " + e);
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
				throw new RuntimeException("UserDAO.java:585, findPw() close 오류" + sqle.getMessage());
			}
		} // end try
		return check;
	}
}
