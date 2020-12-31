package vo;

public class RentalVO {
	private String name;
	private int user_number;
	private int book_number;
	private String book_name;
	private String rental_date;
	private String return_date;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getUser_number() {
		return user_number;
	}
	public void setUser_number(int user_number) {
		this.user_number = user_number;
	}
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
	public String getRental_date() {
		return rental_date;
	}
	public void setRental_date(String rental_date) {
		this.rental_date = rental_date;
	}
	public String getReturn_date() {
		return return_date;
	}
	public void setReturn_date(String return_date) {
		this.return_date = return_date;
	}
	@Override
	public String toString() {
		return "회원 이름 : " + name + ", 회원 번호 : " + user_number + ", 도서번호 : " + book_number
				+ ", 도서명 : " + book_name + ", 대여일 : " + rental_date + ", 반납일 : " + return_date;
	}
	
}
