package crawling;

import java.util.List;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Melon2 {
	private WebDriver driver;
	private String url;
	
	public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
	public static String WEB_DRIVER_PATH = "C:/chromedriver.exe";
	
	public Melon2() {
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		
//		ChromeOptions options = new ChromeOptions();
//		driver = new ChromeDriver(options);
		
		driver = new ChromeDriver();
		url = "https://www.melon.com/";
	}
	public static void main(String[] args) {
		Melon2 m = new Melon2();
		m.operata();
	}
	public void operata() {
		//가사를 찾아주는 메소드 사용
		//드라이버 종료 (close, quit)
		try {
			searchSong();
		} catch (Exception e) {
			
		} finally {
			driver.close();
			driver.quit();
		}
	}
	//노래 제목을 입력받고, 검색 결과를 뿌려준 후 사용자가 선택한 노래의 가사를 출력해줍니다.
	public void searchSong() {
		Scanner sc = new Scanner(System.in);
		WebElement el1 = null;
		WebElement el2 = null;
		List<WebElement> el3 = null;
		WebElement el4 = null;
		String searchWord = "";
		int searchSong = 0;
		int i = 0;
		//검색창 태그 가지고 오기
		//sendKeys("")
		//sendKeys(Keys.RETURN) : 엔터
		try {
			driver.get(url);
			//top_search
			System.out.println("검색어를 입력해주세요 : "); searchWord = sc.next();
			el1 = driver.findElement(By.id("top_search"));
			el1.sendKeys(searchWord);
			el1.sendKeys(Keys.RETURN);
			Thread.sleep(1000);
			el2 = driver.findElement(By.className("section_song"));
			for(WebElement data : el2.findElements(By.className("fc_gray")) ) {
				System.out.println((++i)+"번 : "+data.getText());
			}
			el3 = el2.findElements(By.className("ellipsis"));
			System.out.println("몇번째 곡의 가사를 가져올까요? "); searchSong = sc.nextInt();
			el3.get(searchSong).click();
			sc.next();
			el4 = driver.findElement(By.id("d_video_summary"));
			System.out.println(el4.getText());
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//검색 결과로 나온 페이지에서 곡정보만 출력해주기
		//사용자가 선택한 곡의 가사를 펼쳐보기 클릭 후 내용 텍스트 출력해주기
	}
}
