
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class OpenFileGUI extends JFrame {
    private String baseImage;
    private static String openImage;
    private JFrame appWindow;
    public static JPanel imageSpace;
    private JButton dreamButton;
    private JButton resetButton;
    private JComboBox<String> styleSelect;
    private JComboBox<String> layer1Select;
    private JComboBox<String> layer2Select;
    private static BufferedImage drawImage;
    private int imgWidth;
    private int imgHeight;
    private static boolean flag;

    public static void main(String[] args) {
        
        OpenFileGUI openFile = new OpenFileGUI();
        openFile.setupGUI();

    }

    private void setupGUI() {
		
		// set up app window
        appWindow = new JFrame("yumemitai");
        appWindow.setLayout(new BorderLayout());
        appWindow.setMinimumSize(new Dimension(300, 300));
        appWindow.setSize(500, 500);
        appWindow.setLocationRelativeTo(null);

        // set up menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openItem = new JMenuItem("Open File...");
        JMenuItem urlItem = new JMenuItem("Open URL...");
        menuBar.add(fileMenu);
        fileMenu.add(openItem);
        fileMenu.add(urlItem);
        appWindow.add(menuBar, BorderLayout.NORTH);
        
        // set up image space
        imageSpace = new JPanel(new BorderLayout());
        appWindow.add(imageSpace, BorderLayout.CENTER);
        
        // set up user options
        JPanel userOptions = new JPanel(new BorderLayout());
        appWindow.add(userOptions, BorderLayout.SOUTH);
        
        // set up main options
        JPanel mainOptions = new JPanel();
        // mainOptions.setLayout(new BoxLayout(mainOptions, BoxLayout.X_AXIS));
        userOptions.add(mainOptions, BorderLayout.CENTER);

        // section to select styles
        JPanel styleSpace = new JPanel();
        styleSpace.setLayout(new BoxLayout(styleSpace, BoxLayout.Y_AXIS));
        JLabel styleLabel = new JLabel("Style");
        styleSpace.add(styleLabel);
        styleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        styleSpace.add(Box.createVerticalStrut(2));
        styleSelect = new JComboBox<String>(new String[] {"Glitch", "Disease", "Electric", "Custom"});
        styleSpace.add(styleSelect);
        styleSelect.setEnabled(false);
        mainOptions.add(styleSpace);

        // section to select layers
        JPanel layerSpace = new JPanel();
        layerSpace.setLayout(new BoxLayout(layerSpace, BoxLayout.Y_AXIS));
        JLabel layerLabel = new JLabel("Layers");
        layerSpace.add(layerLabel);
        layerLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        layerSpace.add(Box.createVerticalStrut(2));
        JPanel layerSelect = new JPanel();
        layerSelect.setLayout(new BoxLayout(layerSelect, BoxLayout.X_AXIS));
        String[] layerOptions = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        layer1Select = new JComboBox<String>(layerOptions);
        layer2Select = new JComboBox<String>(layerOptions);
        layerSelect.add(layer1Select);
        layerSelect.add(layer2Select);
        layer1Select.setSelectedIndex(9);
        layer1Select.setEnabled(false);
        layer2Select.setSelectedIndex(6);
        layer2Select.setEnabled(false);
        layerSpace.add(layerSelect, BorderLayout.SOUTH);
        mainOptions.add(layerSpace);

        // button to "dreamify" image
        JPanel dreamSpace = new JPanel(); 
        dreamSpace.setLayout(new BoxLayout(dreamSpace, BoxLayout.Y_AXIS));
        dreamSpace.add(Box.createVerticalStrut(1));
        dreamSpace.add(new JLabel(" "), BorderLayout.NORTH);
        dreamButton = new JButton("Dreamify");
        dreamSpace.add(dreamButton, BorderLayout.SOUTH);
        dreamButton.setEnabled(false);
        mainOptions.add(dreamSpace);
        
        // button to reset image
        JPanel resetSpace = new JPanel(); 
        resetSpace.setLayout(new BoxLayout(resetSpace, BoxLayout.Y_AXIS));
        resetSpace.add(Box.createVerticalStrut(1));
        resetSpace.add(new JLabel(" "), BorderLayout.NORTH);
        resetButton = new JButton("Reset");
        resetSpace.add(resetButton, BorderLayout.SOUTH);
        resetButton.setEnabled(false);
        mainOptions.add(resetSpace);

        appWindow.setVisible(true);
        
        // add listeners
        openItem.setActionCommand("open");
        openItem.addActionListener(new ButtonClickListener());
        urlItem.setActionCommand("url");
        urlItem.addActionListener(new ButtonClickListener());
        dreamButton.setActionCommand("dream");
        dreamButton.addActionListener(new ButtonClickListener());
        styleSelect.setActionCommand("style");
        styleSelect.addActionListener(new ButtonClickListener());
        layer1Select.setActionCommand("layer1");
        layer1Select.addActionListener(new ButtonClickListener());
        layer2Select.setActionCommand("layer1");
        layer2Select.addActionListener(new ButtonClickListener());
        resetButton.setActionCommand("reset");
        resetButton.addActionListener(new ButtonClickListener());

        appWindow.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event){
               System.exit(0);
            }        
         }); 
    }

    private void dreamify() {
        if (imgWidth > 1000 || imgHeight > 1000) {
            int confirm = JOptionPane.showConfirmDialog(appWindow, "This may take a while to dreamify. Are you sure?", 
                        "Large Image", JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        
        // get layes from selection
        String layer1 = String.valueOf(Integer.parseInt(layer1Select.getSelectedItem().toString())-1);
        String layer2 = String.valueOf(Integer.parseInt(layer2Select.getSelectedItem().toString())-1);
        
        // start python script with file path and layers as arguments
        // last argument signals this is not the loop file
        ProcessBuilder startProcess = new ProcessBuilder("python", System.getProperty("user.dir") + "\\main.py", 
                                    openImage, layer1, 
                                    layer2);
        try {
            Process pythonScript = startProcess.start();

            // read output from script for debugging
            BufferedReader debugging = new BufferedReader(new InputStreamReader(pythonScript.getInputStream()));
            String pythonOutput = null;
            while((pythonOutput = debugging.readLine()) != null) {
				if (pythonOutput.contains("&&&")) {
                    String dreamImage = pythonOutput.substring(3, pythonOutput.length());
                    setImage("./output/" + dreamImage);   
                    resetButton.setEnabled(true);
                    break;
                }
            }


        } catch (IOException e) {
            JOptionPane.showMessageDialog(appWindow, "Cannot load Python script!", "Error 117", JOptionPane.ERROR_MESSAGE);
        }

    }
    
    private void openFile() {
        // create window to select a jpg or png file
        JFileChooser chooseFile = new JFileChooser(System.getProperty("user.dir"));
        chooseFile.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("JPG file", "jpg");
        chooseFile.addChoosableFileFilter(extensionFilter);

        if (chooseFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            baseImage = chooseFile.getSelectedFile().toString();
            if (baseImage == openImage) return;
            setImage(baseImage);
        }
    }
    
    private void setImage(String imagePath) {    
        if (openImage != null && openImage.equals(imagePath)) {
            return;
        }
        flag = true;
        openImage = imagePath; 
            
        ImageLoader imageLoader = new ImageLoader();
        imageLoader.execute();

        dreamButton.setEnabled(true);
        styleSelect.setEnabled(true);

        // imgWidth = OpenFileGUI.drawImage.getWidth();
        // imgHeight = OpenFileGUI.drawImage.getWidth();

        appWindow.addComponentListener(new ComponentAdapter( ) {
            public void componentResized(ComponentEvent ev) {
                loadImage();
            }
        });
            
    }

    public static void loadImage() {
        if (flag) {
            try {
                drawImage = ImageIO.read(new File(OpenFileGUI.openImage));
            } catch (Exception adwad) {}
        }

        double drawRatio = 0;
        if (((double)drawImage.getWidth() - imageSpace.getWidth()) > ((double)drawImage.getHeight() - imageSpace.getHeight())) {
            drawRatio = (double)imageSpace.getWidth() / drawImage.getWidth();
        } else {
            drawRatio = (double)imageSpace.getHeight() / drawImage.getHeight();
        }
        BufferedImage drawCanvas = new BufferedImage((int)(drawImage.getWidth()*drawRatio), (int)(drawImage.getHeight()*drawRatio), BufferedImage.TYPE_INT_RGB);
        
        Graphics2D draw = drawCanvas.createGraphics();
        draw.drawImage(drawImage, 0, 0, (int)(drawImage.getWidth()*drawRatio), (int)(drawImage.getHeight()*drawRatio), null);
        draw.dispose();
        ImageIcon imageIcon = new ImageIcon(drawCanvas);

        imageSpace.removeAll();
        imageSpace.add(new JLabel(imageIcon));
        imageSpace.revalidate();
        
        flag = false;
    }


    private class ButtonClickListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals("open")) {
                openFile();
            } else if (command.equals("url")) {
                try {
                    String url = JOptionPane.showInputDialog(appWindow, "", "Enter a URL that ends in .jpg", JOptionPane.PLAIN_MESSAGE);
                    if (url != null) {
                        String[] split = url.split("/");
                        String fileName = split[split.length - 1];
                        InputStream inputStream = new URL(url).openStream() ;
                        Path path = Paths.get("./input/" + fileName);
                        if (!Files.exists(path)) {
                            Files.copy(inputStream, Paths.get("./input/" + fileName));
                        }
                        baseImage = "./input/download.jpg";
                        setImage(baseImage);
                    }
                } catch (IOException ei) {
                }
            } else if (command.equals("dream")) {
                dreamify();
            } else if (command.equals("reset")){
                setImage(baseImage);
                resetButton.setEnabled(false);
            }else if (command.equals("style")) {
                if (((String)styleSelect.getSelectedItem()).equals("Glitch")) {
                    layer1Select.setSelectedIndex(9);
                    layer1Select.setEnabled(false);
                    layer2Select.setSelectedIndex(6);
                    layer2Select.setEnabled(false);
                }
                else if (((String)styleSelect.getSelectedItem()).equals("Disease")) {
                    layer1Select.setSelectedIndex(8);
                    layer1Select.setEnabled(false);
                    layer2Select.setSelectedIndex(9);
                    layer2Select.setEnabled(false);
                }
                else if (((String)styleSelect.getSelectedItem()).equals("Electric")) {
                    layer1Select.setSelectedIndex(8);
                    layer1Select.setEnabled(false);
                    layer2Select.setSelectedIndex(1);
                    layer2Select.setEnabled(false);
                }
                else {
                    layer1Select.setEnabled(true);
                    layer2Select.setEnabled(true);
                }
            }

        }		
     }

}

class ImageLoader extends SwingWorker<Void, Void>{

    protected Void doInBackground() throws Exception {
        // add loading image text
        OpenFileGUI.imageSpace.removeAll();
        JLabel imageLoading = new JLabel("Loading image...", SwingConstants.CENTER);
        imageLoading.setFont(new Font("Sans", Font.BOLD, 16));
        OpenFileGUI.imageSpace.add(imageLoading);
        OpenFileGUI.imageSpace.revalidate();
        return null;
    }

    protected void done() {
        OpenFileGUI.loadImage();
    }

}
