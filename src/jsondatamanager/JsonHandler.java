package jsondatamanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonHandler {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    //read
    public static <T> List<T> readList(String filePath, Type type) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) return new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<T> list = gson.fromJson(reader, type);
            return (list != null) ? list : new ArrayList<>();
        }
    }

    //write
    public static <T> void writeList(String filePath, List<T> list) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            gson.toJson(list, writer);
        }
    }
}