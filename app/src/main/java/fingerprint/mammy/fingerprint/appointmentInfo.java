package fingerprint.mammy.fingerprint;

public class appointmentInfo
{
    private int ID;
    private int userID;
    private String loginDate;
    private int state;
    private int appID;
    private String appName;
    private int active;
    private static appointmentInfo choosenAppointment;

    public appointmentInfo()
    {

    }

    public appointmentInfo(appointmentInfo choosenAppointment)
    {
        this.choosenAppointment = choosenAppointment;
    }
    public appointmentInfo(int ıd, int userID, String loginDate, int state, int appID, String appName, int active) {
        ID = ıd;
        this.userID = userID;
        this.loginDate = loginDate;
        this.state = state;
        this.appID = appID;
        this.appName = appName;
        this.active = active;
    }


    public int getID() {
        return ID;
    }

    public int getUserID() {
        return userID;
    }

    public String getLoginDate() {
        return loginDate;
    }

    public int getState() {
        return state;
    }

    public int getAppID() {
        return appID;
    }

    public String getAppName() {
        return appName;
    }

    public int getActive() {
        return active;
    }

    public appointmentInfo getChoosenAppointment() {
        return choosenAppointment;
    }

    public void setChoosenAppointment(appointmentInfo choosenAppointment) {
        this.choosenAppointment = choosenAppointment;
    }

}
