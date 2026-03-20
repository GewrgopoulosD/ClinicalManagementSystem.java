package models;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class FieldDescriptor {

        private String label;
        private Node field;
        private double min;
        private double max;
        private String type;
        private boolean required;

        public FieldDescriptor(String label, Node field, double min, double max, String type, boolean required){
            this.label = label;
            this.field = field;
            this.min = min;
            this.max = max;
            this.type = type.toLowerCase();
            this.required = required;
        }

        // getters for what i want
        public String getLabel() { return label; }
        public Node getField() { return field; } //return textfield to customise it if we want
        public double getMin() { return min; }
        public double getMax() { return max; }
        public String getType() { return type; }
        public boolean isRequired() { return required; }

        //getter for what i took
        public String getValue() {//return the string in the textfield
            if(type.equals("boolean") && field instanceof CheckBox) {
                return String.valueOf(((CheckBox) field).isSelected());
            } else if(field instanceof TextField) {
                return ((TextField) field).getText().trim();
            } else {
                throw new IllegalArgumentException("Field '" + label + "' has unsupported Node type.");
            }
        }

        public boolean getBooleanValue() {
            if(type.equals("boolean") && field instanceof CheckBox) {
                return ((CheckBox) field).isSelected();
            } else {
                throw new IllegalArgumentException("Field '" + label + "' is not a boolean.");
            }
        }

        public int getIntValue() {
            try {
                return Integer.parseInt(getValue());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Field '" + label + "' must be an integer.");
            }
        }

        public double getDoubleValue() {
            try {
                return Double.parseDouble(getValue());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Field '" + label + "' must be a double.");
            }
        }

        public long getLongValue() {
            try {
                return Long.parseLong(getValue());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Field '" + label + "' must be a whole number.");
            }
        }


    }
