package project.ksy.com.yuhancommunity.user.current.freeboard;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by kor on 2017-09-11.
 */

public class Freeboard_item {
    private String name;
    private String major;
    private String profile_photo;
    private String board_photo;
    private String comment;
    private String date;
    public int starCount = 0;

    public Freeboard_item() {

    }

    public Freeboard_item(String name, String major, String profile_photo, String board_photo, String comment, int starCount) {
        this.name = name;
        this.major = major;
        this.profile_photo = profile_photo;
        this.board_photo = board_photo;
        this.comment = comment;
        this.date = currentTime();
        this.starCount = starCount;
    }

    private String currentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy년 M월 d일", Locale.KOREAN);
        String date = format.format(cal.getTime());
        return date;
    }

    public String getName() {
        return name;
    }

    public String getMajor() {
        return major;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public String getBoard_photo() {
        return board_photo;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }
}

