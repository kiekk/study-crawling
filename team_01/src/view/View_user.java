package view;

import java.util.ArrayList;
import java.util.Scanner;

import dao.BookDAO;
import dao.RentalDAO;
import dao.UserDAO;
import vo.BookVO;
import vo.RentalVO;

public class View_user {
	static Scanner sc = new Scanner(System.in);
	UserDAO user = new UserDAO();
	BookDAO book = new BookDAO();
	RentalDAO rent = new RentalDAO();
	ArrayList<BookVO> books = new ArrayList<BookVO>();
	ArrayList<RentalVO> rents = new ArrayList<RentalVO>();
	//비밀번호 변경 - 입력받는 메소드
	void inputUpdate() {
		String pw = "";
		String new_pw = "";
		System.out.println("비밀번호 변경");
		System.out.print("기존 비밀번호 : "); pw = sc.next();
		System.out.print("바꿀 비밀번호 : "); new_pw = sc.next();
		//입력받은 정보를 update()로 전달
		if(user.update(pw, new_pw)) {
			System.out.println("비밀번호 변경 성공");
		}else {
			System.out.println("비밀번호 변경 실패");
		}
	}

	//도서 위치 찾기 - 입력받는 메소드
	void inputFindBook() {
		sc.nextLine();
		String find = "";
		int flag = 0;
		System.out.println("찾을 도서의 도서명, 또는 도서 번호를 입력해주세요");
		//먼저 String으로 입력받고
		find = sc.nextLine();
		//입력받은 값이 숫자인지 문자인지 확인
		for(int i = 0; i<find.length();i++) {
			if(find.charAt(i) >= '0' && find.charAt(i) <= '9') {
				flag++;
			}
		}//end for
//		System.out.println(flag);
		if(flag == find.length()) {
			int num = Integer.parseInt(find);
			//해당 도서의 대여여부를 확인.
			if((book.rentalCheck(num)).equals("대여가능")) {
				//해당 도서의 대여여부가 '대여가능'일 경우 findbook()으로 도서위치를 찾습니다.
				System.out.println(num+"번 책은 "+ book.findBook(num)+" 위치에 있습니다.");				
			}else {
				System.out.println(num+"번 책은 대여중입니다.");
			}
		}else {
			if(book.rentalCheck(find).equals("대여가능")) {
				System.out.println(find+" 책은 "+ book.findBook(find)+" 위치에 있습니다.");							
			}else {
				System.out.println(find+" 책은 대여중입니다.");
			}
		}//end if
	}
	//대여 하기 - 입력 받을 메소드
	void inputRentalBook() {
		int choice = -1;			//대여할 도서의 번호
		int select = 0;				//대여 확인 
		books = book.bookList(1);	//대여 가능한 도서 리스트

		//도서 리스트 출력
//		books.forEach(value -> System.out.println(value));
		for(int i = 0;i<books.size();i++) {
			System.out.print((i+1)+"번, ");
			System.out.println(books.get(i));
		}
		System.out.println("몇번째 도서를 대여 하시겠습니까?(0.뒤로가기) : "); choice = sc.nextInt();
		if(choice == 0) return;
		System.out.println(books.get(choice-1).getBook_name()+"도서를 대여하시겠습니까?\n1.예\t2.아니오"); select = sc.nextInt();
		if(select == 1) {
			System.out.println(books.get(choice-1).getBook_name()+"도서를 대여합니다");
			rent.rentalBook(books.get(choice-1).getBook_number());
		}else if(select == 2) {
			System.out.println("다시 선택해 주세요.");
		}//end if
	}
	//반납 하기 - 입력 받을 메소드
	void inputReturnBook() {
		int choice = -1;
		int select = 0;
		rents = rent.rentalBookList(1);	//회원이 대여한 도서 리스트
		//도서 리스트 출력
		//대여된 도서가 있는 경우에만 도서 리스트를 출력합니다.
		if(!rents.isEmpty()) {
			System.out.println("현재 대여한 도서 리스트");
//			rents.forEach(value -> System.out.println(value));
			for(int i = 0;i<rents.size();i++) {
				System.out.print((i+1)+"번, ");
				System.out.println(rents.get(i));
			}
		}else {
			System.out.println("반납할 도서가 없습니다.");
			return;
		}
		System.out.println("반납할 도서의 '도서번호'를 입력해주세요 (0.뒤로가기): "); choice = sc.nextInt();
		if(choice == 0) return;
		
		System.out.println(rents.get(choice-1).getBook_name()+"도서를 반납하시겠습니까?\n1.예\t2.아니오"); select = sc.nextInt();
		if(select == 1) {
			System.out.println(rents.get(choice-1).getBook_name()+"도서를 반납합니다");
			rent.returnBook(rents.get(choice-1).getBook_number());
		}else if(select == 2) {
			System.out.println("다시 선택해 주세요.");
		}//end if
	}
	//회원이 대여한 도서 리스트
	void inputRentalBookList() {
		ArrayList<RentalVO> rents = new ArrayList<RentalVO>();
		rents = rent.rentalBookList(1);
		if(!rents.isEmpty()) {
//			rents.forEach(value -> System.out.println(value));
			System.out.println("현재 대여한 도서 리스트");
			for(int i = 0;i<rents.size();i++) {
				System.out.print((i+1)+"번, ");
				System.out.println(rents.get(i));
			}
		}else {
			System.out.println("대여한 도서가 없습니다.");
			return;
		}
	}
	void inputadd() {
	}
	//회원 메인 페이지
	public void userMain() { 
		int choice = -1;
		System.out.println("현재 로그인 된 ID : " + user.session_id);
		while(choice != 0) {
			System.out.println("메뉴 선택\n1.정보 보기\n2.비밀번호 변경\n3.도서 위치 찾기\n4.대여 확인\n5.대여 하기\n6.반납 하기\n0.로그 아웃");
			choice = sc.nextInt();
			if(choice == 0) user.logout();
			switch(choice) {
			case 1: System.out.println(user.select()); break;
			case 2: inputUpdate(); break;
			case 3: inputFindBook(); break;
			case 4: inputRentalBookList(); break;
			case 5: inputRentalBook(); break;
			case 6: inputReturnBook(); break;
			}//end switch
		}//end while
		System.out.println("로그아웃 되었습니다.");
	}//end userMain()
}
