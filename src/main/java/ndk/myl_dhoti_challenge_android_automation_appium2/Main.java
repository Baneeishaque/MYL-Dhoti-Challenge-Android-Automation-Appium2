package ndk.myl_dhoti_challenge_android_automation_appium2;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

public class Main {

    public static void main(String[] args) {

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();

        desiredCapabilities.setCapability("appium:automationName", "UiAutomator2");
        desiredCapabilities.setCapability("appium:udid", "127.0.0.1:21503");
        desiredCapabilities.setCapability(CapabilityType.PLATFORM_NAME, "android");
        desiredCapabilities.setCapability("appium:platformVersion", "7.1.2");
        desiredCapabilities.setCapability("appium:appPackage", "com.spine.dhothi");
        desiredCapabilities.setCapability("appium:appActivity", "com.spine.dhothi.MainActivity");
        desiredCapabilities.setCapability("appium:noReset", "true");

        try {

            AndroidDriver remoteWebDriver = new AndroidDriver(new URI("http://127.0.0.1:4723/").toURL(), desiredCapabilities);
            remoteWebDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(8));
            remoteWebDriver.findElement(By.xpath("//android.widget.ImageView[@content-desc=\"Report\"]")).click();

        } catch (MalformedURLException | URISyntaxException e) {

            System.out.println(e.getLocalizedMessage());
        }
    }
}
