package pro.sky.telegrambot.types;

public enum Period {

    ONETIME("one time"),
    DAILY("daily"),
    WEEKLY("weakly"),
    MONTHLY("monthly"),
    YEARLY("yearly");

    String description;

    Period(String description) {
        this.description = description;
    }

    Period() {
    }

    @Override
    public String toString() {
        return description;
    }
}
