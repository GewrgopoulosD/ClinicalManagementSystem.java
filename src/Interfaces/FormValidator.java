package Interfaces;
import alert.AlertView;
import  models.FieldDescriptor;

import java.util.List;

public interface FormValidator {

    static boolean validateFields(List<FieldDescriptor> fields) {
        String checkMessage = "";

        for (FieldDescriptor fd : fields) {
            String label = fd.getLabel();
            String value = fd.getValue();
            double min = fd.getMin();
            double max = fd.getMax();
            String type = fd.getType();
            boolean required = fd.isRequired();

            if (value == null || value.isBlank()) {
                if (required) {
                    checkMessage += label + " cannot be empty!\n";
                }
                continue;
            }

            switch (type) {
                case "string" -> {
                    if (value.length() < min || value.length() > max) {
                        checkMessage += label + " must be between " + min + " and " + max + " characters.\n";
                    }
                    if (!value.matches("[\\p{L} ]+")) {
                        checkMessage += label + " must contain only letters and spaces.\n";
                    }
                }
                case "int" -> {
                    try {
                        int number = Integer.parseInt(value);
                        if (number < min || number > max) {
                            checkMessage += label + " must be between " + (int)min + " and " + (int)max + ".\n";
                        }
                    } catch (NumberFormatException e) {
                        checkMessage += label + " must be an integer!\n";
                    }
                }
                case "double" -> {
                    try {
                        double number = Double.parseDouble(value);
                        if (number < min || number > max) {
                            checkMessage += label + " must be between " + min + " and " + max + ".\n";
                        }
                    } catch (NumberFormatException e) {
                        checkMessage += label + " must be a number!\n";
                    }
                }
                case "long" -> {
                    try {
                        long number = Long.parseLong(value);
                        if (number < min || number > max) {
                            checkMessage += label + " must be between " + (long)min + " and " + (long)max + ".\n";
                        }
                    } catch (NumberFormatException e) {
                        checkMessage += label + " must be between 0 and 8000000000!\n";
                    }
                }
                case "email" -> {
                    String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
                    if (!value.matches(emailRegex)) {
                        checkMessage += label + " must be a valid email address.\n";
                    }
                }
                case "phone" -> {
                    String digitsOnly = value.replaceAll("\\D", "");

                    if (!digitsOnly.matches("\\d+")) {
                        checkMessage += label + " must contain only digits.\n";
                    } else if (digitsOnly.length() < min || digitsOnly.length() > max) {
                        checkMessage += label + " must be between " + (int)min + " and " + (int)max + " digits.\n";
                    }
                }
                case "password" -> {
                    if (value.length() < min || value.length() > max) {
                        checkMessage += label + " must be between " + (int)min + " and " + (int)max + " characters.\n";
                    }
                    String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$";
                    if (!value.matches(pattern)) {
                        checkMessage += label + " must contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character.\n";
                    }
                }
                case "amka" -> {
                    if (value.length() != 11) {
                        checkMessage += label + " must be exactly 11 digits.\n";
                    }

                    if (!value.matches("\\d{11}")) {
                        checkMessage += label + " must contain only digits.\n";
                    }
                }

            }
        }

        if (!checkMessage.isEmpty()) {
            AlertView.showWarning("Form Errors", "Please fix the following:", checkMessage);
            return false;

        }

        return true;
    }
}
