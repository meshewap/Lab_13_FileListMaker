import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFileChooser;

public class FileListMaker {

    // Global Static Variables
    private static ArrayList<String> list = new ArrayList<>();
    private static Scanner in = new Scanner(System.in);
    private static boolean needsToBeSaved = false;
    private static String currentFileName = "";

    public static void main(String[] args) {

        final String MENU_PROMPT = "Select an option: A(Add) D(Delete) I(Insert) M(Move) V(View) O(Open) S(Save) C(Clear) Q(Quit)";
        final String MENU_REGEX = "[AaDdIiMmOoVvSsCcQq]";
        boolean running = true;

        do {
            displayList();

            String command = SafeInput.getRegExString(in, MENU_PROMPT, MENU_REGEX);
            command = command.toUpperCase();

            // Lab 13 requires a try-catch block in main
            try {
                switch (command) {
                    case "A":
                        addToList();
                        needsToBeSaved = true;
                        break;
                    case "D":
                        deleteFromList();
                        needsToBeSaved = true;
                        break;
                    case "I":
                        insertToList();
                        needsToBeSaved = true;
                        break;
                    case "M":
                        moveItem();
                        needsToBeSaved = true;
                        break;
                    case "V":
                        displayList();
                        break;
                    case "C":
                        list.clear();
                        needsToBeSaved = true;
                        currentFileName = "";
                        System.out.println("List has been cleared.");
                        break;
                    case "O":
                        if (needsToBeSaved) {
                            if (SafeInput.getYNConfirm(in, "Current list has unsaved changes. Save?")) {
                                saveFile();
                            }
                        }
                        openFile();
                        needsToBeSaved = false;
                        break;
                    case "S":
                        saveFile();
                        needsToBeSaved = false;
                        break;
                    case "Q":
                        if (needsToBeSaved) {
                            if (SafeInput.getYNConfirm(in, "You have unsaved changes. Save before quitting?")) {
                                saveFile();
                            }
                        }
                        if (confirmQuit()) {
                            running = false;
                        }
                        break;
                }
            } catch (IOException e) {
                System.out.println("File Error: " + e.getMessage());
                e.printStackTrace();
            }

        } while (running);

        in.close();
    }

    // --- Original Lab 11 Methods ---

    private static void displayList() {
        System.out.println("\n-------------------------------------------");
        if (list.isEmpty()) {
            System.out.println("The list is currently empty.");
        } else {
            for (int i = 0; i < list.size(); i++) {
                System.out.printf("%d: %s\n", (i + 1), list.get(i));
            }
        }
        System.out.println("-------------------------------------------");
    }

    private static void addToList() {
        String item = SafeInput.getNonZeroLenString(in, "Enter the item to add");
        list.add(item);
    }

    private static void deleteFromList() {
        if (list.isEmpty()) {
            System.out.println("List is empty. Nothing to delete.");
            return;
        }
        int low = 1;
        int high = list.size();
        int itemNum = SafeInput.getRangedInt(in, "Enter the number of the item to delete", low, high);
        list.remove(itemNum - 1);
    }

    private static void insertToList() {
        String item = SafeInput.getNonZeroLenString(in, "Enter the item to insert");
        int low = 1;
        int high = list.size() + 1;
        int position = SafeInput.getRangedInt(in, "Enter the position to insert", low, high);
        list.add(position - 1, item);
    }

    private static boolean confirmQuit() {
        return SafeInput.getYNConfirm(in, "Are you sure you want to quit?");
    }

    // --- New Methods for Lab 13 ---

    private static void moveItem() {
        if (list.size() < 2) {
            System.out.println("Need at least two items to move.");
            return;
        }
        int from = SafeInput.getRangedInt(in, "Enter index of item to move", 1, list.size());
        int to = SafeInput.getRangedInt(in, "Enter index of new location", 1, list.size());

        String item = list.remove(from - 1);
        list.add(to - 1, item);
        System.out.println("Moved '" + item + "' from " + from + " to " + to + ".");
    }

    private static void openFile() throws IOException {
        JFileChooser chooser = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir") + File.separator + "src");
        chooser.setCurrentDirectory(workingDirectory);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            Path path = selectedFile.toPath();

            list.clear();
            list.addAll(Files.readAllLines(path));

            currentFileName = selectedFile.getName();
            System.out.println("Successfully loaded " + currentFileName);
        } else {
            System.out.println("File open cancelled.");
        }
    }

    private static void saveFile() throws IOException {
        File workingDirectory = new File(System.getProperty("user.dir") + File.separator + "src");
        Path path;

        if (currentFileName == null || currentFileName.isEmpty()) {
            String newFileName = SafeInput.getNonZeroLenString(in, "Enter file name (without .txt)");
            if (!newFileName.toLowerCase().endsWith(".txt")) {
                newFileName += ".txt";
            }
            currentFileName = newFileName;
        }

        path = Paths.get(workingDirectory.getPath() + File.separator + currentFileName);
        Files.write(path, list);

        System.out.println("List saved to " + currentFileName);
    }
}