package view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import crawling.Crawling;
import dao.BookDAO;
import dao.RentalDAO;
import dao.UserDAO;
import vo.BookVO;
import vo.RentalVO;

public class View_admin {
	static Scanner sc = new Scanner(System.in);
	BookDAO book;
	RentalDAO rent;
	ArrayList<BookVO> books;
	ArrayList<RentalVO> rents;
	//등록된 도서 현황 
	void showBookList() {
		books = book.bookList(2);
//		books.forEach(value -> System.out.println(value));
		for(int i = 0;i<books.size();i++) {
			System.out.print((i+1)+"번, ");
			System.out.println(books.get(i));
		}
	}
	//대여된 도서 현황
	void showRentalBookList() {
		//현재 날짜의 일만 추출
		SimpleDateFormat format1 = new SimpleDateFormat ("dd");		
		Date time = new Date();
		String time1 = format1.format(time);
//		System.out.println(time1);
		rents = rent.rentalBookList(2);
		//대여된 도서가 존재해야지만 도서리스트를 출력합니다.
		if(!rents.isEmpty()) {
			for(int i = 0;i<rents.size();i++) {
				//도서 리스트의 사이즈만큼 반복문을 진행하면서, 현재 일자와 반납 일자가 동일할 경우 BOOK_NAME에 구분표시(★★★ 반납요망!!)
				if(rents.get(i).getReturn_date().substring(8, 10).equals(time1)){
					rents.get(i).setBook_name(String.format("★★★  %s 반납요망!!!!", rents.get(i).getBook_name()));
				}
			}
			//변경을 마친 후 리스트 출력
//			rents.forEach(value -> System.out.println(value));
			for(int i = 0;i<rents.size();i++) {
				System.out.print((i+1)+"번, ");
				System.out.println(rents.get(i));
			}
		}else {
			System.out.println("현재 대여된 도서가 없습니다.");
		}
	}
	//등록된 도서 삭제
	void deleteBook() {
		int select = 0;
		int choice = 0;
		//등록된 도서 현황
		books = book.bookList(2);
//		books.forEach(value -> System.out.println(value));
		for(int i = 0;i<books.size();i++) {
			System.out.print((i+1)+"번, ");
			System.out.println(books.get(i));
		}
		System.out.println("몇 번째 도서를 삭제하시겠습니까?"); select = sc.nextInt();
		
		//도서를 삭제하려면 해당 도서가 '대여가능' 상태여야 도서 삭제가 가능하게 설정했습니다.
		//도서가 '대여중'일 때는 회원이 가지고 있는 것이라 가정하여, 반납이 완료된 후에 삭제가 가능하도록 합니다.
		if(book.rentalCheck(books.get(select-1).getBook_number()).equals("대여가능")) {		//대여 여부 확인
				System.out.println(books.get(select-1).getBook_name()+" 책을 삭제하시겠습니까?");	choice = sc.nextInt();
				if(choice == 1) {
					book.deleteBookList(books.get(select-1).getBook_number());
				}else if (choice == 2) {
					System.out.println("취소합니다.");
				}
		}else {		//대여여부가 '대여중'일 경우
			System.out.println(books.get(select-1).getBook_name()+" 책은 대여중입니다.");
			System.out.println("반납이 된 상태에서 삭제가 가능합니다.");
		}
	}
	//관리자 메인 페이지
	void adminMain() {
		book = new BookDAO();
		rent = new RentalDAO();
		int choice = -1;
		while(choice != 0) {
			System.out.println("관리자 페이지");
			System.out.println("메뉴 선택\n1.회원 현황\n2.도서 현황\n3.대여 현황\n4.도서 추가\n5.도서 삭제\n0.메인페이지로이동");
			choice = sc.nextInt();
			switch(choice) {
			case 1: new UserDAO().selectAll(); break;
			case 2: showBookList(); break; //등록된 도서현황을 대여횟수로 정렬
			case 3: showRentalBookList(); break; 
			case 4: new Crawling().searchingBook(); break;
			case 5: deleteBook(); break;
			}//end switch
		}//end while
	}//end adminMain()
}
