package crawling;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class CGV {
	//브라우저 드라이버를 담을 수 있는 객체
	private WebDriver driver;
	
	//크롬 브라우저의 정보
	public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
	public static String WEB_DRIVER_PATH = "C:/chromedriver.exe";
	
	public static void main(String[] args) {
		CGV cgv = new CGV();
		
		//해당 사이트의 태그객체를 담을 수 있는 객체
		WebElement el1 = null;
		WebElement el2 = null;
		
		//드라이버 설정
		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		
		//옵션을 줄 때
		ChromeOptions options = new ChromeOptions();
		//실행 시 브라우저가 외부가 아닌 내부로 작동하게 하는 설정(브라우저가 눈에 보이지 않게 함)
		options.addArguments("headless");
		cgv.driver = new ChromeDriver(options);
		
		//옵션을 안줄 때
//		cgv.driver = new ChromeDriver();
		
		//처음에 접근할 주소를 작성한다.
		String url = "http://www.cgv.co.kr/movies/";
		try {
			//접근할 주소를 담아준다.
			cgv.driver.get(url);
			//페이지 로딩이 완료되기까지의 시간을 벌어준다.
			Thread.sleep(1000);
			
			//클래스 이름으로 태그를 찾아서 el1 객체에 태그를 담아준다.
			//btn-more-fontbold
			el1 = cgv.driver.findElement(By.className("btn-more-fontbold"));
			//해당 태그가 a, button 등 클릭이벤트가 설정되어 있다면 click()을 통해서 요청할 수 있다.
			el1.click();
			
			//클릭 후 페이지가 로딩될 시간을 벌어준다.
			Thread.sleep(1000);
			
			//sect-movie-chart
			el2 = cgv.driver.findElement(By.className("sect-movie-chart"));
			
			//가져온 태그 안에 찾을 태그가 있다면 또 한 번 findElement를 사용할 수 있고,
			//해당 태그가 여러 개일 때에는 findElements를 사용해서 List타입으로 가져온다.
			for(WebElement data : el2.findElements(By.className("title"))) {
				//해당 태그 안에 있는 텍스트를 가져올 때 getText()를 사용한다.
				System.out.println(data.getText());
			}
			
		} catch (InterruptedException e) {
			
		} finally {
			//외부와 내부 모두 브라우저를 종료시켜준다.
			cgv.driver.close();
			cgv.driver.quit();
		}
	}
}












