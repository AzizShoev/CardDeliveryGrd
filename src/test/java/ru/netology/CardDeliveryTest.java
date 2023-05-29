package ru.netology;

import com.codeborne.selenide.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;

public class CardDeliveryTest {

    WebDriver driver;
    ChromeOptions options;

    @BeforeAll
    static void setupAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        Configuration.headless = true;
        options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        open("http://localhost:9999/");
    }

    @AfterEach
    void teardown() {
        driver.quit();
        driver = null;
    }

    String fullDateGenerator(int dayToAdd) {
        return java.time.LocalDate.now()
                .plusDays(dayToAdd).format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    String dayGenerator(int addDays) {
        return java.time.LocalDate.now()
                .plusDays(addDays).format(java.time.format.DateTimeFormatter.ofPattern("d"));
    }

    @Test
    void shouldSuccessfullyOrderDeliveryCard() {
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        $("[data-test-id='date'] input").setValue(fullDateGenerator(7));
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+71234567890");
        $("[data-test-id='agreement'] .checkbox__text").click();
        $("button.button_theme_alfa-on-white").click();
        $(withText("Успешно!")).should(visible, Duration.ofMillis(15000));
    }

    @Test
    void shouldWrongCityInCityInput() {
        $("[data-test-id='city'] input").setValue("Югра");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        $("[data-test-id='date'] input").setValue(fullDateGenerator(4));
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+71234567890");
        $("[data-test-id='agreement'] .checkbox__text").click();
        $("button.button_theme_alfa-on-white").click();

        assertEquals("Доставка в выбранный город недоступна",
                $("[data-test-id='city'].input_invalid .input__sub").getText());
    }

    @Test
    void shouldWrongDateInDateInput() {
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        $("[data-test-id='date'] input").setValue(fullDateGenerator(2));
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+71234567890");
        $("[data-test-id='agreement'] .checkbox__text").click();
        $("button.button_theme_alfa-on-white").click();

        assertEquals("Заказ на выбранную дату невозможен",
                $("[data-test-id='date'] .input_invalid .input__sub").getText());
    }

    @Test
    void shouldWrongNameInNameInput() {
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        $("[data-test-id='date'] input").setValue(fullDateGenerator(3));
        $("[data-test-id='name'] input").setValue("Ivanov Ivan");
        $("[data-test-id='phone'] input").setValue("+71234567890");
        $("[data-test-id='agreement'] .checkbox__text").click();
        $("button.button_theme_alfa-on-white").click();

        assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.",
                $("[data-test-id='name'].input_invalid .input__sub").getText());
    }

    @Test
    void shouldWrongPhoneInPhoneInput() {
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        $("[data-test-id='date'] input").setValue(fullDateGenerator(3));
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("81234567890");
        $("[data-test-id='agreement'] .checkbox__text").click();
        $("button.button_theme_alfa-on-white").click();

        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.",
                $("[data-test-id='phone'].input_invalid .input__sub").getText().trim());
    }

    @Test
    void shouldUncheckedAgreement() {
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        $("[data-test-id='date'] input").setValue(fullDateGenerator(3));
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+71234567890");
        $("button.button_theme_alfa-on-white").click();

        assertTrue($("[data-test-id='agreement'].input_invalid .checkbox__text").isDisplayed());
    }

    @Test
    void shouldChoseInDropListCityWithKeyboard() {
        $("[data-test-id='city'] input").setValue("Ка");
        $("[data-test-id='city'] input").sendKeys(Keys.DOWN, Keys.DOWN, Keys.DOWN, Keys.UP, Keys.ENTER);

        assertEquals("Владикавказ",
                $("[data-test-id='city'] .input__control").getValue());
    }

    @Test
    void shouldChoseInDropListCityClick() {
        $("[data-test-id='city'] input").setValue("Ка");
        $(byXpath("//span[@class='menu-item__control' and text()='Краснодар']")).click();

        assertEquals("Краснодар",
                $("[data-test-id='city'] .input__control").getValue());
    }

    @Test
    void shouldChoseDateInCalendarWidget() {
        $("[data-test-id='date'] .icon-button").click();
        $(".popup_visible [data-step='1'].calendar__arrow_direction_right").click();
        $(".popup_visible [data-step='-1'].calendar__arrow_direction_left").click();
        $(".popup_visible [data-step='12'].calendar__arrow_direction_right").click();
        $(".popup_visible [data-step='-12'].calendar__arrow_direction_left").click();
        $(byXpath("//td[contains(text(),'" + dayGenerator(7) + "')]")).click();

        assertEquals("" + fullDateGenerator(7) + "",
                $("[data-test-id='date'] .input__control").getValue());
    }
}