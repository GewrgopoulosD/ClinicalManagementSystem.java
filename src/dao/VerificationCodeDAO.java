package dao;

import jsondatamanager.JsonHandler;
import models.VerificationCode;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;


//class to handle verification code from json
public class VerificationCodeDAO {

    private static final String FILE_PATH = "data/verification.json";


    public static boolean isValidCode(String inputCode) {
        if (inputCode == null || inputCode.isBlank()) {
            throw new IllegalArgumentException("Verification code cannot be null or empty.");
        }

        try {

            Type listType = new TypeToken<List<VerificationCode>>(){}.getType();

            //fetch the list from json
            List<VerificationCode> codes = JsonHandler.readList(FILE_PATH, listType);

            if (codes != null) {
                for (VerificationCode vc : codes) {
                    if (vc.getCode().equals(inputCode)) {
                        return true;//code found and validated
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Data access error: Unable to read verification file. " + e.getMessage(), e);
        }

        return false;//code was not found in the database
    }
}