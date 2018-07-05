package project.ksy.com.yuhancommunity.user.management;

import android.net.Uri;


/**
 * Created by kor on 2017-09-04.
 */

public class UserData {
    String userMajor;
    String userName;
    String userPhoneNum;
    String userGender;
    String userprofileimg;

    public UserData() {

    }

    public UserData(String userMajor, String userName, String userPhoneNum, String userGender, String userprofileimg) {
        this.userMajor = userMajor;
        this.userName = userName;
        this.userPhoneNum = userPhoneNum;
        this.userGender = userGender;
        this.userprofileimg = userprofileimg;
    }

    public String getUserMajor() {
        return userMajor;
    }

    public void setUserMajor(String userMajor) {
        this.userMajor = userMajor;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoneNum() {
        return userPhoneNum;
    }

    public void setUserPhoneNum(String userPhoneNum) {
        this.userPhoneNum = userPhoneNum;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserprofileimg() {
        return userprofileimg;
    }

    public void setUserprofileimg(String userprofileimg) {
        this.userprofileimg = userprofileimg;
    }
}
