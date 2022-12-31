package ndk.myl_dhoti_challenge_android_automation_appium2;

import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static final String DISTRICT_NAME = "MALAPPURAM";
    public static final String PANCHAYATH_NAME = "Tanalur";
    public static final String[] WARD_NAMES = {"MOOCHIKKAL", "KERALADEESWARAPURAM", "PUTHANTHERU"};
    public static final int FLUTTER_ENGINE_REDRAW_TIME = 5;
    public static final By loadMoreLocator = By.xpath("//android.widget.Button[@content-desc='Load More']");
    public static final int API_RESPONSE_WAITING_TIME_IN_SECONDS = 15;
    public static List<WebElement> dataRecords = new ArrayList<>();

    public static void main(String[] args) {

        for (String wardName : WARD_NAMES) {
            processWardName(wardName);
        }
    }

    private static void processWardName(String wardName) {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();

        desiredCapabilities.setCapability("appium:automationName", "UiAutomator2");
        desiredCapabilities.setCapability("appium:udid", "47968108");
        desiredCapabilities.setCapability(CapabilityType.PLATFORM_NAME, "android");
        desiredCapabilities.setCapability("appium:platformVersion", "12");
        desiredCapabilities.setCapability("appium:appPackage", "com.spine.dhothi");
        desiredCapabilities.setCapability("appium:appActivity", "com.spine.dhothi.MainActivity");
        desiredCapabilities.setCapability("appium:maxInstances", 1);

        try {

            AndroidDriver androidDriver = new AndroidDriver(new URI("http://127.0.0.1:4723/").toURL(), desiredCapabilities);
            androidDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(12));
            androidDriver.findElement(By.xpath("//android.widget.ImageView[@content-desc='Report']")).click();
            performSearch(androidDriver, API_RESPONSE_WAITING_TIME_IN_SECONDS, "//android.widget.EditText[@text='Select District']", DISTRICT_NAME);
            performSearch(androidDriver, FLUTTER_ENGINE_REDRAW_TIME, "//android.widget.EditText[@text='Select Panjayth/Municipality']", PANCHAYATH_NAME);
            performSearch(androidDriver, FLUTTER_ENGINE_REDRAW_TIME, "//android.widget.EditText[@text='Select Unit/Ward']", wardName);
            androidDriver.findElement(By.xpath("//android.widget.ImageView[starts-with(@content-desc,'" + wardName + "')]")).click();
            androidDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(FLUTTER_ENGINE_REDRAW_TIME));
            androidDriver.findElement(By.xpath("//android.view.View[@content-desc='OK']")).click();
            androidDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(FLUTTER_ENGINE_REDRAW_TIME));

            scrollToLoadMore(androidDriver);

            dataRecords.forEach((webElement -> System.out.println(webElement.getAttribute("content-desc"))));

        } catch (MalformedURLException | URISyntaxException e) {

            System.out.println(e.getLocalizedMessage());
        }
    }

    private static void processListContents(WebElement scrollView) {

        dataRecords.addAll(scrollView.findElement(By.xpath("//android.view.View[//android.view.View]")).findElements(By.className("android.widget.ImageView")));
    }

    private static void scrollToLoadMore(AndroidDriver androidDriver) {

        TouchAction actions = new TouchAction(androidDriver);
        do {
            try {

                WebElement scrollView = androidDriver.findElement(By.className("android.widget.ScrollView"));
                processListContents(scrollView);

            } catch (NoSuchElementException noSuchElementException) {

                WebElement scrollView = androidDriver.findElement(By.xpath("//android.view.View[@content-desc='S.No']/parent::node()"));
                processListContents(scrollView);
            }

            try {
                androidDriver.findElement(loadMoreLocator).click();
                androidDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(API_RESPONSE_WAITING_TIME_IN_SECONDS));

            } catch (NoSuchElementException noSuchElementException) {

                try {
                    androidDriver.findElements(By.xpath("//android.view.View[@content-desc='No More Payments']"));
                    break;

                } catch (NoSuchElementException noSuchElementException1) {

                    Dimension dimension = androidDriver.manage().window().getSize();
                    int startY = (int) (dimension.height * 0.8);
                    int endY = (int) (dimension.height * 0.2);
                    int startX = dimension.width / 2;
                    actions.press(PointOption.point(startX, startY)).moveTo(PointOption.point(startX, endY)).release().perform();
                }
            }
        } while (true);
    }

    private static void performSearch(AndroidDriver remoteWebDriver, int waitMinutes, String xpathExpression, String desiredText) {
        remoteWebDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(waitMinutes));
        WebElement dhotiCountElement = remoteWebDriver.findElement(By.xpath("//android.view.View[@index='3']"));
        int dhotiCount = Integer.parseInt(dhotiCountElement.getAttribute("content-desc"));
        WebElement element = remoteWebDriver.findElement(By.xpath(xpathExpression));
        findElement(remoteWebDriver, desiredText, element);
        while (true) {
            if (Integer.parseInt(dhotiCountElement.getAttribute("content-desc")) != dhotiCount) break;
        }
    }

    private static void findElement(AndroidDriver remoteWebDriver, String desiredText, WebElement element) {
        try {
            element.click();
            remoteWebDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(FLUTTER_ENGINE_REDRAW_TIME));
            element.sendKeys(desiredText);
            remoteWebDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(FLUTTER_ENGINE_REDRAW_TIME));
            remoteWebDriver.findElement(By.xpath("//android.view.View[@content-desc='" + desiredText + "']")).click();
        } catch (NoSuchElementException noSuchElementException) {
            findElement(remoteWebDriver, desiredText, element);
        }
    }
}
