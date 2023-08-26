package pro.sky.telegrambot.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class MessageParserTest {
    public static Stream<Arguments> argumentsForIsHaveValidDateTimePositiveTest() {
        return Stream.of(
                Arguments.of("01.01.2000 12:00 data data"),
                Arguments.of("21/08/23 20/47 data data"),
                Arguments.of("31.01.20 12/00 data data"),
                Arguments.of("01.01.2000 23.59 data data"),
                Arguments.of("01.01.21 17-59 data data"),
                Arguments.of("29.02.2000 11-11 data data"),
                Arguments.of("28.02.2001 00-00 data data"),
                Arguments.of("30.04.2000 00-00 data data"),
                Arguments.of("31.07.2000 00-00 data data"),
                Arguments.of("31.08.2000 00-00 data data"),
                Arguments.of("01.12.2000 00-00 data data"));
    }

    public static Stream<Arguments> argumentsForIsHaveValidDateTimeNegativeTest() {
        return Stream.of(
                Arguments.of("32.01.2000 12-00 data"),
                Arguments.of("29.02.2001 12/00 data"),
                Arguments.of("31.04.2000 23.59 data"),
                Arguments.of("01/15/2000 17-59 data"),
                Arguments.of("29.02.2000 31-11 data"),
                Arguments.of("28.02.2001 40-00 data"),
                Arguments.of("30.04.2000 17-60 data"),
                Arguments.of("32.07.2000 00-00 data"),
                Arguments.of("771.08.2000 00-00 data"),
                Arguments.of("    "));
    }

    public static Stream<Arguments> argumentsForParseDatePositiveTest() {
        return Stream.of(
                Arguments.of(LocalDateTime.of(2000, 1, 1, 12, 0), "01.01.2000 12-00 data"),
                Arguments.of(LocalDateTime.of(2000, 1, 31, 12, 0), "31.01.2000 12/00 data"),
                Arguments.of(LocalDateTime.of(2000, 1, 1, 23, 59), "01.01.2000 23.59 data"),
                Arguments.of(LocalDateTime.of(2025, 1, 1, 17, 59), "01.01.2025 17-59 data"));
    }

    public static Stream<Arguments> argumentsForParseDateNegativeTest() {
        return Stream.of(
                Arguments.of("32.01.2000 12-00 data", DateTimeException.class),
                Arguments.of("29.02.2001 12/00 data", DateTimeParseException.class),
                Arguments.of("31.04.2000 23.59 data", DateTimeParseException.class),
                Arguments.of("01/15/2000 17-59 data", DateTimeException.class),
                Arguments.of("29.02.2000 31-11 data", DateTimeException.class),
                Arguments.of("28.02.2001 40-00 data", DateTimeException.class),
                Arguments.of("30.04.2000 17-60 data", DateTimeException.class),
                Arguments.of("32.07.2000 00-00 data", DateTimeException.class),
                Arguments.of("771.08.2000 00-00 data", DateTimeException.class));

    }

    @ParameterizedTest
    @MethodSource("argumentsForIsHaveValidDateTimePositiveTest")
    void isHaveValidDateTimePositiveTest(String arg) {
        assertThat(MessageParser.isHaveValidDateTimeMessage(arg)).isTrue();
    }

    @ParameterizedTest
    @MethodSource("argumentsForIsHaveValidDateTimeNegativeTest")
    void isHaveValidDateTimeNegativeTest(String arg) {
        assertThat(MessageParser.isHaveValidDateTimeMessage(arg)).isFalse();
    }


    @ParameterizedTest
    @MethodSource("argumentsForParseDatePositiveTest")
    void parseDatePositiveTest(LocalDateTime result, String arg) {
        assertThat(MessageParser.parseDate(arg)).isEqualTo(result);
    }

    @ParameterizedTest
    @MethodSource("argumentsForParseDateNegativeTest")
    void parseDateNegativeTest(String arg, Class<? extends Exception> clazz) {
        assertThatThrownBy(() -> MessageParser.parseDate(arg)).isInstanceOf(clazz);
    }

    @ParameterizedTest
    @MethodSource("argumentsForIsHaveValidDateTimePositiveTest")
    void parseMessageTest(String arg) {
        assertThat(MessageParser.parseMessage(arg)).isEqualTo("data data");
    }
}