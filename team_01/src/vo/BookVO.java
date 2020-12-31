package vo;

public class BookVO {
	private int book_number;			//도서번호
	private String book_name;			//도서명
	private String author;				//저자
	private String company;				//출판사
	private String book_date;			//출판일
	private String book_section;		//도서위치
	private String book_rental;			//대여여부('대여중', '대여가능')
	private int rental_count;			//대여횟수
	
	public int getBook_number() {
		return book_number;
	}
	public void setBook_number(int book_number) {
		this.book_number = book_number;
	}
	public String getBook_name() {
		return book_name;
	}
	public void setBook_name(String book_name) {
		this.book_name = book_name;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getBook_date() {
		return book_date;
	}
	public void setBook_date(String book_date) {
		this.book_date = book_date;
	}
	public String getBook_section() {
		return book_section;
	}
	public void setBook_section(String book_section) {
		this.book_section = book_section;
	}
	public String getBook_rental() {
		return book_rental;
	}
	public void setBook_rental(String book_rental) {
		this.book_rental = book_rental;
	}
	public int getRental_count() {
		return rental_count;
	}
	public void setRental_count(int rental_count) {
		this.rental_count = rental_count;
	}
	
	@Override
	public String toString() {
		return "도서 번호 : " + book_number + ", 도서명 : " + book_name + ", 저자 : " + author + ", 출판사 : "
				+ company + ", 출판일 : " + book_date + ", 도서 위치 : " + book_section + ", 대여가능여부 : "
				+ book_rental + ", [대여 횟수 : " + rental_count +"]";
		

	}
	
}
