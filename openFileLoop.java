import java.io.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class openFileLoop {

    public static void main(String[] args) throws IOException {
        
        // create window to select a jpg or png file
        JFileChooser openFile = new JFileChooser(System.getProperty("user.dir") + "\\input");
        openFile.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("JPG file", "jpg");
        openFile.addChoosableFileFilter(extensionFilter);
        int fileOpened = openFile.showOpenDialog(null);
        
        // execute the following code if user selects a file
        if(fileOpened == JFileChooser.APPROVE_OPTION) {
            
            // grab path to file
            String path = openFile.getSelectedFile().toString();
            
            // loop to continue creating images
            for (int i = 0; i <= 9; i++) {
                for (int k = 0; k <= 9; k++) {                    
                    // don't make image if layers are the same
                    if (i == k) continue;
                    
                    String layer1 = Integer.toString(i);
                    String layer2 = Integer.toString(k);

                    // start python script with file path and layers as arguments
                    // last argument signals this is the loop file
                    ProcessBuilder processBuilder = new ProcessBuilder("python", System.getProperty("user.dir") + "\\main.py", path, layer1, layer2, "1");
                    Process pythonScript = processBuilder.start();

                    // read output from script for debugging
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pythonScript.getErrorStream()));
                    String pythonOutput = null;
                    while((pythonOutput = bufferedReader.readLine()) != null) {
                        System.out.println(pythonOutput);
                    }
                }
            }
            
        }

    }

}