package ndk.myl_dhoti_challenge_android_automation_appium2;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.BreakIterator;
import com.opencsv.CSVWriter;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;

public class Main {

    public static final String DISTRICT_NAME = "MALAPPURAM";
    public static final String PANCHAYATH_NAME = "Tanalur";

    //    public static final String[] WARD_NAMES = {"MOOCHIKKAL", "KERALADEESWARAPURAM", "PUTHANTHERU", "PANDIYATT"};
//    public static final String[] WARD_NAMES = {"MOOCHIKKAL", "KERALADEESWARAPURAM", "PUTHANTHERU"};
//    public static final String[] WARD_NAMES = {"KERALADEESWARAPURAM", "PUTHANTHERU"};
//    public static final String[] WARD_NAMES = {"PUTHANTHERU"};
    public static final String[] WARD_NAMES = {"PANDIYATT"};
//    public static final String[] WARD_NAMES = {"MOOCHIKKAL"};

    public static final int FLUTTER_ENGINE_REDRAW_TIME = 8;
    public static final By loadMoreLocator = By.xpath("//android.widget.Button[@content-desc='Load More']");
    public static final int API_RESPONSE_WAITING_TIME_IN_SECONDS = 5;
    public static Set<WebElement> dataRecords;

    public static void main(String[] args) {

        for (String wardName : WARD_NAMES) {
            dataRecords = new LinkedHashSet<>();
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

            printDataRecords(wardName);

            androidDriver.quit();

        } catch (MalformedURLException | URISyntaxException e) {

            System.out.println(e.getLocalizedMessage());
        }
    }

    private static void printDataRecords(String wardName) {

        File file = new File(UCharacter.toTitleCase(DISTRICT_NAME.toLowerCase(), BreakIterator.getWordInstance()) + "_" + UCharacter.toTitleCase(PANCHAYATH_NAME.toLowerCase(), BreakIterator.getWordInstance()) + "_" + UCharacter.toTitleCase(wardName.toLowerCase(), BreakIterator.getWordInstance()) + ".csv");

        String[] header = {"Sl. No.", "Name", "Transaction Number", "Ward Name", "Panchayath Name", "District Name", "Date", "Time", "Amount", "Count"};

        try {

            FileWriter outputFile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputFile);

            writer.writeNext(header);

            dataRecords.forEach((webElement -> {

                String[] transactionData = webElement.getAttribute("content-desc").split("\\R");
                String[] transactionDataPlaces = transactionData[3].split(",");
                String[] transactionTimeStamp = transactionData[5].split(",");

                writer.writeNext(new String[]{transactionData[0], transactionData[1], transactionData[2], transactionDataPlaces[0], transactionDataPlaces[1], transactionData[4], transactionTimeStamp[0], transactionTimeStamp[1].trim(), transactionData[6].substring(2), transactionData[7]});
            }));

            writer.close();

        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private static void processListContents(WebElement scrollView) {

        dataRecords.addAll(scrollView.findElements(By.className("android.widget.ImageView")));
//        printDataRecords();
    }

    private static void scrollToLoadMore(AndroidDriver androidDriver) {

        TouchAction actions = new TouchAction(androidDriver);
        do {
            try {

                WebElement scrollView = androidDriver.findElement(By.xpath("//android.view.View[@content-desc='S.No']/parent::node()"));
                processListContents(scrollView);

            } catch (NoSuchElementException noSuchElementException) {

                WebElement scrollView = androidDriver.findElement(By.className("android.widget.ScrollView"));
                processListContents(scrollView);
            }

            try {
                androidDriver.findElement(loadMoreLocator).click();
                androidDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(API_RESPONSE_WAITING_TIME_IN_SECONDS));

            } catch (NoSuchElementException noSuchElementException) {

                try {
                    androidDriver.findElement(By.xpath("//android.view.View[@content-desc='No More Payments']"));
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
        } catch (NoSuchElementException | StaleElementReferenceException noSuchElementException) {
            findElement(remoteWebDriver, desiredText, element);
        }
    }
}
