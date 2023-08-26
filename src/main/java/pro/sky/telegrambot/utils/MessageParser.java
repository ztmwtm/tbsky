package pro.sky.telegrambot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MessageParser {

    private static final Logger logger = LoggerFactory.getLogger(MessageParser.class);

    private MessageParser() {
    }

    private static final String REGEX_FOR_DATE = "^(?:(?:31(\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
    private static final String REGEX_FOR_TIME = "^([0-1]?\\d|2[0-3])\\.[0-5]\\d$";

    public static boolean isHaveValidDateTimeMessage(String data) {
        String[] splitData = data.trim().split(" ");
        return splitData.length > 2
                && splitData[0].replace("-", ".").replace("/", ".").matches(REGEX_FOR_DATE)
                && splitData[1].replace("-", ".").replace("/", ".").replace(":", ".").matches(REGEX_FOR_TIME);
    }

    public static LocalDateTime parseDate(String data) {
        String[] splitData = data.trim().split(" ");
        splitData[0] = splitData[0].replace("-", ".").replace("/", ".");
        splitData[1] = splitData[1].replace("-", ".").replace("/", ".").replace(":", ".");
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        try {
            localDate = LocalDate.parse(splitData[0], DateTimeFormatter.ofPattern("dd.MM.[uuuu][uu]"));
            localTime = LocalTime.parse(splitData[1], DateTimeFormatter.ofPattern("HH.mm"));
        }
        catch (Exception e) {
            logger.info("Error : {}",  e.getMessage());
        }
        return localDate.atTime(localTime);
    }

    public static String parseMessage(String data) {
        return data.trim()
                .replace("-", ".")
                .replace("/", ".")
                .replace(":", ".")
                .replaceFirst("\\d{1,2}\\.\\d{1,2}.\\d{2,4} ", "")
                .replaceFirst("\\d{1,2}\\.\\d{1,2} ", "");
    }
}
