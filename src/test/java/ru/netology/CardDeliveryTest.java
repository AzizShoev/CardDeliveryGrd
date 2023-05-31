package ru.netology;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;

public class CardDeliveryTest {

    @BeforeEach
    void setup() {
        Configuration.headless = true;
        open("http://localhost:9999/");
    }

    String dateGenerator(int dayToAdd, String pattern) {
        return java.time.LocalDate.now()
                .plusDays(dayToAdd).format(java.time.format.DateTimeFormatter.ofPattern(pattern));
    }

    @Test
    void shouldSuccessfullyOrderDeliveryCard() {
        $("[data-test-id='city'] input").setValue("Казань");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        $("[data-test-id='date'] input").setValue(dateGenerator(7, "dd.MM.yyyy"));
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+71234567890");
        $("[data-test-id='agreement'] .checkbox__text").click();
        $("button.button_theme_alfa-on-white").click();
        $("[data-test-id='notification'] .notification__content").should(visible, Duration.ofMillis(13000));

        assertEquals("Встреча успешно забронирована на " + dateGenerator(7, "dd.MM.yyyy") + "",
                $("[data-test-id='notification'] .notification__content").getText());
    }

    @Test
    void shouldWrongCityInCityInput() {
        $("[data-test-id='city'] input").setValue("Югра");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        $("[data-test-id='date'] input").setValue(dateGenerator(4, "dd.MM.yyyy"));
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
        $("[data-test-id='date'] input").setValue(dateGenerator(2, "dd.MM.yyyy"));
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
        $("[data-test-id='date'] input").setValue(dateGenerator(3, "dd.MM.yyyy"));
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
        $("[data-test-id='date'] input").setValue(dateGenerator(3, "dd.MM.yyyy"));
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
        $("[data-test-id='date'] input").setValue(dateGenerator(3, "dd.MM.yyyy"));
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+71234567890");
        $("button.button_theme_alfa-on-white").click();

        assertTrue($("[data-test-id='agreement'].input_invalid .checkbox__text").isDisplayed());
    }

    @Test
    void shouldSuccessfullyOrderDeliveryCardWithDropListCityAndCalendarWidget() {
        $(byXpath("//span[@data-test-id='city'] //input")).setValue("Ка");
        $(byXpath("//span[@class='menu-item__control' and text()='Махачкала']")).click();
        $(byXpath("//span[@data-test-id='date'] //button")).click();
        $(byXpath("//td[contains(text(),'" + dateGenerator(7, "d") + "')]")).click();
        $(byXpath("//span[@data-test-id='name'] //input")).setValue("Иванов Иван");
        $(byXpath("//span[@data-test-id='phone'] //input")).setValue("+71234567890");
        $(byXpath("//label[@data-test-id='agreement']/span[@class='checkbox__text']")).click();
        $(byXpath("//span[@class='button__text']")).click();
        $(byXpath("//div[@class='notification__content']")).should(visible, Duration.ofMillis(15000));

        assertEquals("Встреча успешно забронирована на " + dateGenerator(7, "dd.MM.yyyy") + "",
                $(byXpath("//div[@class='notification__content']")).getText());
    }
}