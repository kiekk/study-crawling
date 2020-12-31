package crawling;

import java.util.List;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Melon {
	private WebDriver driver;
	private String url;
	
	public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
	public static String WEB_DRIVER_PATH = "C:/chromedriver.exe";
	
	public Melon() {
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		
//		ChromeOptions options = new ChromeOptions();
//		driver = new ChromeDriver(options);
		
		driver = new ChromeDriver();
		url = "https://www.melon.com/";
	}
	
	public static void main(String[] args) {
		Melon m = new Melon();
		m.operate();
	}
	
	public void operate() {
		//가사를 찾아주는 메소드 사용
		//드라이버 종료(close, quit)
		try {
			searchSong();
		} catch (Exception e) {
			
		} finally {
			driver.close();
			driver.quit();
		}
	}
	
	//노래 제목을 입력받고, 검색 결과를 뿌려준 후 사용자가 선택한 노래의 가사를 출력해준다.
	public void searchSong() {
		//각각의 태그 요소들을 저장할 변수
		WebElement element = null;
		Scanner sc = new Scanner(System.in);
		
		//사용자에게 보여질 메세지
		String searchMsg = "노래 제목 : ";
		String noSuchMsg = "검색된 결과가 없습니다.";
		String lyricNumMsg = "곡 번호 : ";
		
		//검색창 태그 가지고 오기
		//sendKeys("")
		//sendKeys(Keys.RETURN) : 엔터
		//ui-autocomplete-input
		try {
			//찾아갈 주소를 알려줌.
			driver.get(url);
			
			//검색창 태그 가져오기
			element = driver.findElement(By.className("ui-autocomplete-input")); 
			System.out.print(searchMsg);
			
			//사용자가 입력한 키워드를 멜론 사이트 검색창에 넣어주기
			element.sendKeys(sc.nextLine());
			
			//엔터 쳐주기
			element.sendKeys(Keys.RETURN);
			
			//검색 결과 페이지 로딩 기다려주기
			Thread.sleep(1000);
			
			//검색 결과가 없을 때의 메세지 태그 가져오기
			element = driver.findElement(By.className("section_no_data"));
			
			//여기로 왔다는 것은 검색 결과가 없다는 뜻!
			System.out.println(noSuchMsg);
			
		} catch (NoSuchElementException nsee) {
			//만약 검색 결과가 없지 않다면 예외가 발생하여 여기로 오게된다
			//검색결과가 있다면 section_no_data 태그를 찾지 못하기 때문!
			
			//곡 정보가 표시된 영역태그를 가지고 온다.
			element = driver.findElement(By.id("frm_searchSong"));
			
			//해당 영역 태그에서 곡 번호, 곡명, 아티스트명을 List타입으로 가져온다.
			//같은 클래스 이름과 id가 여러 개가 있기 때문이다.
			List<WebElement> numList = element.findElements(By.className("no"));
			List<WebElement> titleList = element.findElements(By.className("fc_gray"));
			List<WebElement> artistList = element.findElements(By.id("artistName"));
			
			for (int i = 0; i < numList.size(); i++) {
				//사용자에게 검색된 곡 정보들을 출력해준다.
				System.out.println(numList.get(i).getText() + ". " 
						+ titleList.get(i).getText() + ", 아티스트 : " + artistList.get(i).getText());
			}
			System.out.print(lyricNumMsg);
			//사용자가 선택한 곡 번호를 입력받는다.
			int num = sc.nextInt();
			
			//각 곡별로 상세보기 버튼이 배치되어 있기 때문에 List 타입으로 모두 가져온다.
			List<WebElement> detailList = element.findElements(By.className("btn_icon_detail"));
			
			//사용자가 선택한 곡 번호는 1부터 시작하기 때문에 인덱스 번호로 활용하기 위해서 -1을 해준다.
			//사용자가 선택한 곡의 상세보기 a 태그를 클릭해준다.
			detailList.get(num - 1).click();
			try {Thread.sleep(1000);} catch (InterruptedException e) {;}

			try {
				//상세보기 페이지에서 가사 펼치기 버튼 객체를 가지고 온다.
				driver.findElement(By.className("button_more")).click();
				try {Thread.sleep(1000);} catch (InterruptedException e) {;}
				
				//만약 펼치기 버튼이 있다면 가사 텍스트를 담고 있는 태그객체를 가지고 온다.
				element = driver.findElement(By.className("lyric"));
				//해당 태그 안에 있는 가사를 가져와서 출력해준다.
				System.out.println(element.getText());
				
			} catch (NoSuchElementException nsee2) {
				//펼치기 버튼이 없다면 가사가 없거나, 성인 노래이므로 경고 메세지를 출력해준다.
				System.out.println("해당 곡의 가사를 열람할 수 없습니다.");
			}
		} catch (InterruptedException itte) {;}
		
		
		//By.id("")
		//By.className("")
		
		//검색 결과로 나온 페이지에서 곡정보만 출력해주기
		//사용자가 선택한 곡의 가사 버튼 클릭 > 펼쳐보기 클릭 후 내용 텍스트 출력해주기
		
	}
}










