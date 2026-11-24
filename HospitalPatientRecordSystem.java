import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Hospital Patient Record System (PS 36)
 * Concepts: ArrayList, Searching, Sorting, Methods, File Handling
 * Menu: 1-Register Patient, 2-Display All, 3-Search by Name, 4-Sort by Age
 */
public class HospitalPatientRecordSystem {

    // ---------- Patient class (each record is an object) ----------
    static class Patient {
        private final String id;
        private final String name;
        private final int age;
        private final String disease;

        Patient(String id, String name, int age, String disease) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.disease = disease;
        }

        String getId() { return id; }
        String getName() { return name; }
        int getAge() { return age; }
        String getDisease() { return disease; }

        @Override
        public String toString() {
            return id + " | " + name + " | " + age + " | " + disease;
        }

        // File line format: ID|Name|Age|Disease
        String toFileLine() {
            return id + "|" + name + "|" + age + "|" + disease;
        }
    }

    // ---------- Data ----------
    private static final String FILE_NAME = "patients.txt";
    private static final int FIRST_ID = 101;
    private static ArrayList<Patient> patients = new ArrayList<>();
    private static int nextIdNumber = FIRST_ID;

    // ---------- File handling ----------
    // Load data from the file at start
    static void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return; // first run: hospital has no patients
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = splitByBar(line);
                if (parts.length != 4) continue; // skip damaged lines
                try {
                    int age = Integer.parseInt(parts[2]);
                    patients.add(new Patient(parts[0], parts[1], age, parts[3]));
                    int num = Integer.parseInt(parts[0].substring(2));
                    if (num >= nextIdNumber) {
                        nextIdNumber = num + 1;
                    }
                } catch (NumberFormatException e) {
                    // ignore bad line
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read " + FILE_NAME + ": " + e.getMessage());
        }
    }

    // Save data to the file on exit
    static void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Patient p : patients) {
                pw.println(p.toFileLine());
            }
        } catch (IOException e) {
            System.out.println("Could not save " + FILE_NAME + ": " + e.getMessage());
        }
    }

    // Own split method (manual, on '|')
    static String[] splitByBar(String line) {
        ArrayList<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '|') {
                list.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        list.add(sb.toString());
        return list.toArray(new String[0]);
    }

    // ---------- Feature 1: Register ----------
    static void registerPatient(Scanner sc) {
        System.out.print("Name: ");
        String name = sc.nextLine().trim().replace('|', ' ');
        if (name.isEmpty()) {
            System.out.println("Invalid name. Name cannot be empty.");
            return;
        }

        System.out.print("Age: ");
        String ageText = sc.nextLine().trim();
        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            System.out.println("Invalid age. Age must be between 0 and 120.");
            return;
        }
        if (age < 0 || age > 120) {
            System.out.println("Invalid age. Age must be between 0 and 120.");
            return;
        }

        System.out.print("Disease: ");
        String disease = sc.nextLine().trim().replace('|', ' ');
        if (disease.isEmpty()) {
            System.out.println("Invalid disease. Disease cannot be empty.");
            return;
        }

        String id = "PT" + nextIdNumber;
        nextIdNumber++;
        Patient p = new Patient(id, name, age, disease);
        patients.add(p);
        System.out.println("Patient registered. ID: " + p);
    }

    // ---------- Feature 2: Display all ----------
    static void displayAll() {
        if (patients.isEmpty()) {
            System.out.println("No patients found.");
            return;
        }
        for (int i = 0; i < patients.size(); i++) {
            System.out.println((i + 1) + ". " + patients.get(i));
        }
    }

    // ---------- Feature 3: Search by name (linear search, own logic) ----------
    static void searchByName(Scanner sc) {
        System.out.print("Enter name to search: ");
        String key = sc.nextLine().trim().toLowerCase();
        if (key.isEmpty()) {
            System.out.println("Search name cannot be empty.");
            return;
        }
        boolean found = false;
        for (int i = 0; i < patients.size(); i++) {
            if (containsText(patients.get(i).getName().toLowerCase(), key)) {
                System.out.println("Found: " + patients.get(i));
                found = true;
            }
        }
        if (!found) {
            System.out.println("Patient not found.");
        }
    }

    // Manual substring check (no built-in search methods used)
    static boolean containsText(String text, String key) {
        int n = text.length();
        int m = key.length();
        if (m > n) return false;
        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m && text.charAt(i + j) == key.charAt(j)) {
                j++;
            }
            if (j == m) return true;
        }
        return false;
    }

    // ---------- Feature 4: Sort by age, low to high (insertion sort, own logic) ----------
    static void sortByAge() {
        for (int i = 1; i < patients.size(); i++) {
            Patient current = patients.get(i);
            int j = i - 1;
            while (j >= 0 && patients.get(j).getAge() > current.getAge()) {
                patients.set(j + 1, patients.get(j));
                j--;
            }
            patients.set(j + 1, current);
        }
        displayAll();
    }

    // ---------- Main ----------
    public static void main(String[] args) {
        loadFromFile();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println();
            System.out.println("===== Hospital Patient Record System =====");
            System.out.println("1. Register Patient");
            System.out.println("2. Display All");
            System.out.println("3. Search by Name");
            System.out.println("4. Sort by Age (low to high)");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            if (!sc.hasNextLine()) { // input ended (e.g. piped test input)
                break;
            }
            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                registerPatient(sc);
            } else if (choice.equals("2")) {
                displayAll();
            } else if (choice.equals("3")) {
                searchByName(sc);
            } else if (choice.equals("4")) {
                sortByAge();
            } else if (choice.equals("5")) {
                break;
            } else {
                System.out.println("Invalid option. Choose 1-5.");
            }
        }

        saveToFile();
        System.out.println("Data saved to " + FILE_NAME + ". Goodbye!");
        sc.close();
    }
}