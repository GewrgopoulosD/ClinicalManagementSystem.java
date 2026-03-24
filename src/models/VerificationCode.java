package models;

public class VerificationCode {
    private String code;

    public VerificationCode(String code) {
        this.code = code;
    }

    //gson
    public VerificationCode() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}