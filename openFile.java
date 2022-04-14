import java.io.*;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class openFile {

    public static void main(String[] args) throws IOException {
        
        // create window to select a jpg or png file
        JFileChooser openFile = new JFileChooser();
        openFile.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("JPG or PNG file", "jpg", "png");
        openFile.addChoosableFileFilter(extensionFilter);
        int fileOpened = openFile.showOpenDialog(null);

        // layers for Python script
        int layer1 = -1;
        int layer2 = -1;

        Scanner userInput = new Scanner(System.in);
        boolean flag = true;
        
        // loop to get layers from user
        // only accepts whole numbers between 0 and 9
        while(flag) {
            try {
                System.out.println("Enter first layer (0-9).");
                layer1 = Integer.parseInt(userInput.nextLine());
                if (!(layer1 > -1 && layer1 < 10)) continue;
                
                System.out.println("Enter second layer (0-9).");
                layer2 = Integer.parseInt(userInput.nextLine());
                if (!(layer1 > -1 && layer1 < 10)) continue;
                
                userInput.close();
                flag = false;
            } catch (Exception notWholeNumber) {}
        }
        
        // execute the following code if user selected a file
        if(fileOpened == JFileChooser.APPROVE_OPTION) {
            // grab path to file
            String path = openFile.getSelectedFile().toString();

            // start python script with file path and layers as arguments
            ProcessBuilder processBuilder = new ProcessBuilder("python", System.getProperty("user.dir") + "\\main.py", path, ""+layer1, ""+layer2);
            Process pythonScript = processBuilder.start();

            // read output from script for debugging
            BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(pythonScript.getErrorStream()));
            String pythonOutput = null;
            while((pythonOutput = bufferedReader.readLine()) != null) {
                System.out.println(pythonOutput);
            }

        }

    }

}