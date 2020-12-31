package view;

import java.util.Scanner;

import dao.UserDAO;

public class View_main {
	static Scanner sc = new Scanner(System.in);
	static UserDAO user = new UserDAO();
	//input메소드에서 사용할 변수들을 전역변수로 선언합니다.
	String id, pw, name, phone_number;
	int age;
	
	//로그인 - 입력받을 메소드
	void inputLogin() {
		System.out.println("로그인");
		System.out.print("전화번호 : "); phone_number = sc.next();
		System.out.print("비밀번호 : "); pw = sc.next();
		
		//입력받은 정보를 login()로 전달
		if(user.login(phone_number, pw)) {
			new View_user().userMain();
		}
	}
	//회원가입 - 입력받을 메소드
	void inputJoin() {
		System.out.println("회원가입");
		System.out.print("전화번호(전화번호는 ID로 사용됩니다.) : "); phone_number = sc.next();
		System.out.print("비밀번호 : "); pw = sc.next();
		System.out.print("이름 : "); name = sc.next();
		System.out.print("나이 : "); age = sc.nextInt();
		
		//입력받은 정보를 join()로 전달
		if(user.join(phone_number, pw, name, age)) {
			System.out.println("회원가입 성공\n로그인 해주세요");			
		}else {
			System.out.println("회원가입 실패\n다시 확인해주세요.");
		}
	}
	//비밀번호찾기 - 입력받을 메소드
	void inputFindPw() {
		System.out.println("비밀번호 찾기");
		System.out.print("전화번호 : "); phone_number = sc.next();
		
		//입력받은 전화번호를 findPw()로 전달
		user.findPw(phone_number);
	}
	//메인 페이지
	public void mainPage() {
		int choice = -1;
		while(choice != 0) {
		System.out.println("   KOREA 도서관");
		System.out.println("━━━━━━━━━━━━━━━━");
		System.out.println("메뉴 선택\n1.로그인\n2.회원 가입\n3.비밀번호 찾기\n0.종료 하기");
			choice = sc.nextInt();
			if(choice == 9999) new View_admin().adminMain(); 
			switch(choice) {
			case 1: inputLogin(); break;
			case 2: inputJoin(); break;
			case 3: inputFindPw(); break;
			}//end switch
		}//end while
		System.out.println("종료합니다.");
	}
}
