import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.Preferences;

public class OpenFileGUI extends JFrame {
    private String baseImage;
    private static String openImage;
    private JFrame appWindow;
    public static JPanel imageSpace;
    public static JButton dreamButton;
    private JButton resetButton;
    private JComboBox<String> styleSelect;
    private JComboBox<String> layer1Select;
    private JComboBox<String> layer2Select;
    public JComboBox<String> depthSelect;
    public static JProgressBar dreamProgress;
    private static BufferedImage drawImage;
    private static boolean flag;
    protected File output;
    private JLabel layerLabel;
    private JLabel depthLabel;
    private JLabel styleLabel;
    private JCheckBoxMenuItem advancedItem;
    private DefaultComboBoxModel<String> presets;
    private ArrayList<ArrayList<String>> stylePresets;
    private ArrayList<ArrayList<int[]>> stylePresetLayers;
    private DefaultComboBoxModel<String> layer1Options;
    private DefaultComboBoxModel<String> layer2Options;
    private DefaultComboBoxModel<String> depthOptions;
    private int dreamModel = 0;
    private JLabel dreamModelLabel;
    Color lightMain;
    Color lightAlt; 
    Color darkMain;
    Color darkAlt;
    Color lightText;
    Color darkText;
    JMenu helpMenu;
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenu settingsMenu;
    JPanel userOptions;
    JCheckBoxMenuItem setColorMenu;
    int color;
    Boolean AdvancedOptions;
    String userPresets;
    int model;
    private int depthTemp;
    private int modelTemp;
    private JMenu setModelMenu;
    private JPanel modelLabelPanel;
    private JMenuItem customPresetItem;
    JRadioButtonMenuItem mobilenetv2;
    JRadioButtonMenuItem inceptionv3;
    JRadioButtonMenuItem xception;
    JRadioButtonMenuItem resnet50;
    private String styleTemp;
    Preferences user;
    int[] layers = new int[2];


    public static void main(String[] args) {
        new OpenFileGUI().setupGUI();
    }

    private void setupGUI() {
        // create app window
        darkMain = Color.decode("#0b1622");
        darkAlt = Color.decode("#152232");
        darkText = Color.decode("#d3d5f3");
        lightMain = Color.decode("#edf1f5");;
        lightAlt = Color.decode("#fafafa");;
        lightText = Color.decode("#5C728A");
        appWindow = new JFrame("DeepDreamer");
        appWindow.setLayout(new BorderLayout());
        appWindow.setMinimumSize(new Dimension(700, 700));
        appWindow.setLocationRelativeTo(null); // centers window

        //icon
        Image icon = Toolkit.getDefaultToolkit().getImage("icons\\frog.png");
        appWindow.setIconImage(icon);

        // create menu bar
        menuBar = new JMenuBar();
        menuBar.setBorderPainted(false); // disables white outline
        
        // file options
        fileMenu = new JMenu("File");
        fileMenu.setFont(new Font("Sans", Font.BOLD, 12));
        JMenuItem openItem = new JMenuItem("Open File...");
        JMenuItem urlItem = new JMenuItem("Open URL...");
        JMenuItem saveItem = new JMenuItem("Save as...");
        fileMenu.add(openItem);
        fileMenu.add(urlItem);
        fileMenu.add(saveItem);
        
        // settings options
        settingsMenu = new JMenu("Settings");
        settingsMenu.setFont(new Font("Sans", Font.BOLD, 12));

        // color Selection
        setColorMenu = new JCheckBoxMenuItem("Light Mode");
        settingsMenu.add(setColorMenu);

        // advanced
        advancedItem = new JCheckBoxMenuItem("Advanced Features");
        advancedItem.setUI(new KeepMenuOpen());
        settingsMenu.add(advancedItem);

        // model selection
        setModelMenu = new JMenu("Dream Models");
        setModelMenu.setFont(new Font("Sans", Font.BOLD, 12));
        setModelMenu.setForeground(Color.BLACK);
        ButtonGroup modelGroup = new ButtonGroup();
        mobilenetv2 = new JRadioButtonMenuItem("MobileNetV2", true);
        inceptionv3 = new JRadioButtonMenuItem("InceptionV3");
        xception = new JRadioButtonMenuItem("Xception");
        resnet50 = new JRadioButtonMenuItem("ResNet50");
        modelGroup.add(mobilenetv2);
        modelGroup.add(inceptionv3);
        modelGroup.add(xception);
        modelGroup.add(resnet50);
        setModelMenu.add(mobilenetv2);
        setModelMenu.add(inceptionv3);
        setModelMenu.add(xception);
        setModelMenu.add(resnet50);
        settingsMenu.add(setModelMenu);
        setModelMenu.setEnabled(false);

        // custom presets
        customPresetItem = new JMenuItem("Create Custom Filter");
        settingsMenu.add(customPresetItem);
        customPresetItem.setEnabled(false);

        // get help
        helpMenu = new JMenu("Help");
        helpMenu.setFont(new Font("Sans", Font.BOLD, 12));
        JMenuItem infoItem = new JMenuItem("How This Works");
        helpMenu.add(infoItem);

        JMenuItem layerDepthItem = new JMenuItem("Layers/Depth");
        JMenuItem modelInfoItem = new JMenuItem("Models");
        helpMenu.add(layerDepthItem);
        helpMenu.add(modelInfoItem);
        
        // set model label
        modelLabelPanel = new JPanel(new BorderLayout());
        modelLabelPanel.setBorder(new EmptyBorder(0, 0, 0, 8));
        modelLabelPanel.setFont(new Font("Sans", Font.BOLD, 12));
        dreamModelLabel = new JLabel("Model: MobileNetV2");
        dreamModelLabel.setToolTipText("Current model being used. Please review \"Help.\" for more details.");
        modelLabelPanel.add(dreamModelLabel, BorderLayout.EAST);
        modelLabelPanel.setVisible(false);
        
        // add to menu bar
        menuBar.add(fileMenu);
        menuBar.add(settingsMenu);
        menuBar.add(helpMenu);
        menuBar.add(Box.createVerticalStrut(30));
        menuBar.add(modelLabelPanel);

        // add to app window
        appWindow.add(menuBar, BorderLayout.NORTH);
        
        // create image space
        imageSpace = new JPanel(new BorderLayout());
        appWindow.add(imageSpace, BorderLayout.CENTER);
        
        // --- USER OPTIONS ---
        // create options
        userOptions = new JPanel();
        userOptions.add(Box.createVerticalStrut(60));
        appWindow.add(userOptions, BorderLayout.SOUTH);

        // style selection ui
        styleLabel = new JLabel("Filter");
        styleLabel.setFont(new Font("Sans", Font.PLAIN, 14));

        // instantiate presets
        stylePresets = new ArrayList<ArrayList<String>>();
        stylePresetLayers = new ArrayList<ArrayList<int[]>>();

        // MobileNetV2 presets
        stylePresets.add(new ArrayList<>(Arrays.asList("Glitch", "Disease", "Electric")));
        stylePresetLayers.add(new ArrayList<>(Arrays.asList(new int[][]{{9, 6}, {8, 9}, {8, 1}})));

        // InceptionV3 presets
        stylePresets.add(new ArrayList<>(Arrays.asList("Scatter", "Manifest", "Bubbles")));
        stylePresetLayers.add(new ArrayList<>(Arrays.asList(new int[][]{{0, 2}, {4, 6}, {9, 10}})));

        // Xception presets
        stylePresets.add(new ArrayList<>(Arrays.asList("Vision", "Swarm", "Float")));
        stylePresetLayers.add(new ArrayList<>(Arrays.asList(new int[][]{{0, 1}, {4, 5}, {10, 11}})));

        // ResNet50 presets
        stylePresets.add(new ArrayList<>(Arrays.asList("Crust", "Squiggle", "Dazzle")));
        stylePresetLayers.add(new ArrayList<>(Arrays.asList(new int[][]{{1, 3}, {9, 10}, {14, 15}})));

        // prests gets current model
        String[] tmpPreset = new String[stylePresets.get(dreamModel).size()];
        tmpPreset = stylePresets.get(dreamModel).toArray(tmpPreset);
        presets = new DefaultComboBoxModel<String>(tmpPreset);
        
        // style selection
        styleSelect = new JComboBox<String>(presets);
        styleSelect.setPreferredSize(new Dimension(90, 30));
        styleSelect.setFont(new Font("Sans", Font.BOLD, 14));
        userOptions.add(styleLabel);
        userOptions.add(styleSelect);

        // instantiate layer options
        layer1Options = new DefaultComboBoxModel<String>(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
        layer2Options = new DefaultComboBoxModel<String>(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
        
        // layer selection
        layerLabel = new JLabel("Layers");
        layerLabel.setFont(new Font("Sans", Font.PLAIN, 14));

        layer1Select = new JComboBox<String>(layer1Options);
        layer1Select.setPreferredSize(new Dimension(45, 30));
        layer1Select.setFont(new Font("Sans", Font.BOLD, 14));
        layer2Select = new JComboBox<String>(layer2Options);
        layer2Select.setPreferredSize(new Dimension(45, 30));
        layer2Select.setFont(new Font("Sans", Font.BOLD, 14));
        layer2Select.add(Box.createHorizontalStrut(4));

        // instantiate depth options
        depthOptions = new DefaultComboBoxModel<String>(new String[]{"1", "2", "3", "4", "5", "6"});
        
        // depth selection
        depthLabel = new JLabel("Depth");
        depthLabel.setFont(new Font("Sans", Font.PLAIN, 14));

        depthSelect = new JComboBox<String>(depthOptions);
        depthSelect.setPreferredSize(new Dimension(45, 30));
        depthSelect.setFont(new Font("Sans", Font.BOLD, 14));
        
        // add all options
        userOptions.add(layerLabel);
        userOptions.add(layer1Select);
        userOptions.add(layer2Select);
        userOptions.add(depthLabel);
        userOptions.add(depthSelect);
        layerLabel.setVisible(false);
        layer1Select.setVisible(false);
        layer2Select.setVisible(false);
        depthLabel.setVisible(false);
        depthSelect.setVisible(false);

        // dreamify button
        userOptions.add(Box.createHorizontalStrut(4));
        dreamButton = new JButton("Dreamify");
        dreamButton.setPreferredSize(new Dimension(100, 30));
        dreamButton.setFont(new Font("Sans", Font.BOLD, 14));
        dreamButton.setEnabled(false);
        userOptions.add(dreamButton);
        dreamButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // reset button
        userOptions.add(Box.createHorizontalStrut(4));
        resetButton = new JButton("Reset");
        resetButton.setPreferredSize(new Dimension(100, 30));
        resetButton.setFont(new Font("Sans", Font.BOLD, 14));
        resetButton.setEnabled(false);
        userOptions.add(resetButton);
        dreamButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // --- END USER OPTIONS ---

        appWindow.setVisible(true);
        
        // listeners
        openItem.setActionCommand("open");
        openItem.addActionListener(new ButtonClickListener());
        urlItem.setActionCommand("url");
        urlItem.addActionListener(new ButtonClickListener());
        saveItem.setActionCommand("save");
        saveItem.addActionListener(new ButtonClickListener());
        advancedItem.setActionCommand("advanced");
        advancedItem.addActionListener(new ButtonClickListener());
        infoItem.setActionCommand("infoItem");
        infoItem.addActionListener(new ButtonClickListener());
        layerDepthItem.setActionCommand("layerDepthItem");
        layerDepthItem.addActionListener(new ButtonClickListener());
        modelInfoItem.setActionCommand("modelInfoItem");
        modelInfoItem.addActionListener(new ButtonClickListener());
        dreamButton.setActionCommand("dream");
        dreamButton.addActionListener(new ButtonClickListener());
        resetButton.setActionCommand("reset");
        resetButton.addActionListener(new ButtonClickListener());
        styleSelect.setActionCommand("style");
        styleSelect.addActionListener(new ButtonClickListener());
        layer1Select.setActionCommand("layer");
        layer1Select.addActionListener(new ButtonClickListener());
        layer2Select.setActionCommand("layer");
        layer2Select.addActionListener(new ButtonClickListener());
        depthSelect.setActionCommand("depth");
        depthSelect.addActionListener(new ButtonClickListener());
        customPresetItem.setActionCommand("createCustom");
        customPresetItem.addActionListener(new ButtonClickListener());
        setColorMenu.setActionCommand("theme");
        setColorMenu.addActionListener(new ButtonClickListener());
        mobilenetv2.addActionListener(e -> changeModel(0));
        inceptionv3.addActionListener(e -> changeModel(1));
        xception.addActionListener(e -> changeModel(2));
        resnet50.addActionListener(e -> changeModel(3));

        //load settings
        loadPreferences();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                user.put("Filters", userPresets);
                user.putBoolean("advancedToggle", AdvancedOptions);
                user.putInt("Color", color);
                user.putInt("model", modelTemp);
                user.putInt("depth", depthSelect.getSelectedIndex());
                user.put("filterSelection", styleTemp);
                user.putInt("layer1", layers[0]);
                user.putInt("layer2", layers[1]);

            }
        }));
        // end program when window closes
        appWindow.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                System.exit(0);
            }
        });
    }


    /*    private void setProperty(String key, String value){
            try (OutputStream output = new FileOutputStream("settings.properties")) {
                // set the properties value
                prop.setProperty(key, value);
                // save properties to project root folder
                prop.store(output, null);
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
        */
    private void setPresets() {
        String[] models = userPresets.split("\n");
        ArrayList<ArrayList<String>> tempPresets = new ArrayList<>();
        ArrayList<ArrayList<int[]>> tempPresetsLayers = new ArrayList<>();
        String[] line;
        String[] presets;
        ArrayList<String> temp = new ArrayList<>();
        ArrayList<int[]> tempLayers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            temp.clear();
            tempLayers.clear();
            line = models[i].split(",");
            for (int j = 0; j < line.length; j += 1) {
                presets = line[j].split(" ");
                temp.add(presets[0]);
                int[] tempArray = new int[]{Integer.parseInt(presets[1]), Integer.parseInt(presets[2])};
                tempLayers.add(tempArray);

            }
            tempPresets.add((ArrayList<String>) temp.clone());
            tempPresetsLayers.add((ArrayList<int[]>) tempLayers.clone());
        }
        stylePresets = tempPresets;
        stylePresetLayers = tempPresetsLayers;
    }
    /*
    private String getProperty(String key){
        try (InputStream input = new FileInputStream("settings.properties")) {
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            return prop.getProperty(key);

        } catch (IOException io) {
            io.printStackTrace();
            return "null";
        }
    }
    */


    private void loadPreferences() {
        user = Preferences.userRoot();
        // temp files
        userPresets = user.get("Presets", "Glitch 9 6,Disease 8 9,Electric 8 1\n" +
                "Scatter 0 2,Manifest 4 6,Bubbles 9 10\n" +
                "Vision 0 1,Swarm 4 5,Float 10 11\n" +
                "Crust 1 3,Squiggle 9 10,Dazzle 14 15");
        depthTemp = user.getInt("depth", 0);

        setPresets();
        // depth
        depthSelect.setSelectedIndex(depthTemp);
        // model
        setModel(user.getInt("model", 0));

        // filter selection

        // advanced
        if (user.getBoolean("advancedToggle", false) == true) advancedItem.doClick();
        else AdvancedOptions = false;
            /*
            layers[0] = user.getInt("layer1", 1);
            layers[1] = user.getInt("layer2", 1);
            styleTemp = user.get("filterSelection", presets.getElementAt(0));
            if (styleTemp.equals("Custom")) {
                System.out.println("YES");
                styleSelect.setSelectedIndex(3);
                layer1Select.setSelectedIndex(layers[0]);
                layer2Select.setSelectedIndex(layers[1]);
                System.out.println(styleSelect.getSelectedItem());
            } else {
                styleSelect.setSelectedItem(styleTemp);
                setStyle();
            }

             */

        // light mode
        if (user.getInt("Color", 0) == 1) setColorMenu.doClick();
        else setTheme(0);
    }

    private void setModel(int num) {
        switch(num) {
            case 0:
                mobilenetv2.doClick(); 
                break;
            case 1:
                inceptionv3.doClick();
                break;
            case 2:
                xception.doClick();
                break;
            case 3:
                resnet50.doClick();
                break;
        }
    }



    private void setStyle() {
        if (!styleSelect.getSelectedItem().equals("Custom")) {
            for (int i = 0; i < stylePresets.get(dreamModel).size(); i++) {
                if ((styleSelect.getSelectedItem()).equals(stylePresets.get(dreamModel).get(i))) {
                    layer1Select.setSelectedIndex(stylePresetLayers.get(dreamModel).get(i)[0]);
                    layer2Select.setSelectedIndex(stylePresetLayers.get(dreamModel).get(i)[1]);
                    break;
                }
            }
        }
        styleTemp = (String) styleSelect.getSelectedItem();
    }


    private void setTheme(int theme) {
        
        switch (theme) {
            case 0:
                menuBar.setBackground(darkAlt);
                imageSpace.setBackground(darkMain);
                userOptions.setBackground(darkAlt);
                modelLabelPanel.setBackground(darkAlt);
                fileMenu.setForeground(darkText);
                settingsMenu.setForeground(darkText);
                depthLabel.setForeground(darkText);
                layerLabel.setForeground(darkText);
                styleLabel.setForeground(darkText);
                dreamModelLabel.setForeground(darkText);
                helpMenu.setForeground(darkText);

                styleSelect.setBackground(darkMain);
                styleSelect.setForeground(darkText);
                layer1Select.setBackground(darkMain);
                layer2Select.setBackground(darkMain);
                layer1Select.setForeground(darkText);
                layer2Select.setForeground(darkText);
                depthSelect.setBackground(darkMain);
                depthSelect.setForeground(darkText);
                dreamButton.setBackground(darkMain);
                dreamButton.setForeground(darkText);
                resetButton.setBackground(darkMain);
                resetButton.setForeground(darkText);
                break;
            case 1:
                menuBar.setBackground(lightAlt);
                imageSpace.setBackground(lightMain);
                userOptions.setBackground(lightAlt);
                modelLabelPanel.setBackground(lightAlt);
                fileMenu.setForeground(lightText);
                settingsMenu.setForeground(lightText);
                depthLabel.setForeground(lightText);
                layerLabel.setForeground(lightText);
                styleLabel.setForeground(lightText);
                dreamModelLabel.setForeground(lightText);
                helpMenu.setForeground(lightText);

                styleSelect.setBackground(lightMain);
                styleSelect.setForeground(lightText);
                layer1Select.setBackground(lightMain);
                layer2Select.setBackground(lightMain);
                layer1Select.setForeground(lightText);
                layer2Select.setForeground(lightText);
                depthSelect.setBackground(lightMain);
                depthSelect.setForeground(lightText);
                dreamButton.setBackground(lightMain);
                dreamButton.setForeground(lightText);
                resetButton.setBackground(lightMain);
                resetButton.setForeground(lightText);
            break;
        }
    }


    private void changeModel(int model) {
        // get presets for model
        for (int i = 0; i < stylePresets.get(model).size(); i++)
            presets.addElement(stylePresets.get(model).get(i));

        for (int i = 0; i < stylePresets.get(dreamModel).size(); i++)
            presets.removeElement(stylePresets.get(dreamModel).get(i));
        
        if (advancedItem.isSelected()) {
            styleSelect.removeItem("Custom");
            styleSelect.addItem("Custom");
        }


        // set dream model
        dreamModel = model;
        modelTemp = model;
        
        //remember selection
        // update layers
        switch (dreamModel) {
            case 0:
                dreamModelLabel.setText("Model: MobileNetV2");

                while (layer1Options.getSize() != 10) {
                    layer1Options.removeElementAt(layer1Options.getSize() - 1);
                    layer2Options.removeElementAt(layer2Options.getSize() - 1);
                }
                break;
            case 1:
                dreamModelLabel.setText("Model: InceptionV3");
                while (layer1Options.getSize() != 11) {
                    if (layer1Options.getSize() < 11) {
                        layer1Options.addElement(Integer.toString(layer1Options.getSize() + 1));
                        layer2Options.addElement(Integer.toString(layer2Options.getSize() + 1));
                    }
                    else {
                        layer1Options.removeElementAt(layer1Options.getSize() - 1);
                        layer2Options.removeElementAt(layer2Options.getSize() - 1);
                    }   
                }
                break;
            case 2:
            dreamModelLabel.setText("Model: Xception");
                while (layer1Options.getSize() != 12) {
                    if (layer1Options.getSize() < 12) {
                        layer1Options.addElement(Integer.toString(layer1Options.getSize() + 1));
                        layer2Options.addElement(Integer.toString(layer2Options.getSize() + 1));
                    } 
                    else {
                        layer1Options.removeElementAt(layer1Options.getSize() - 1);
                        layer2Options.removeElementAt(layer2Options.getSize() - 1);
                    } 
                }
                break;
            default:
            dreamModelLabel.setText("Model: ResNet50");
                while (layer1Options.getSize() != 16) {
                    layer1Options.addElement(Integer.toString(layer1Options.getSize() + 1));
                    layer2Options.addElement(Integer.toString(layer2Options.getSize() + 1));
                }
                break;
        }

        // styleSelect.setSelectedIndex(0);
    }

    private int checkIfInteger(String output) {
        try {
            return Integer.parseInt(output);
        }
        catch (NumberFormatException nfe) {
            return -1;
        }
    }

    private void checkFileLength(String fileName, double sizeThreshhold) {
        File file = new File(fileName);
        double bytes = file.length();
        double kilobytes = (bytes / 1024);
        //if file exceeds this size, warning will be issued
        if (kilobytes > sizeThreshhold) JOptionPane.showMessageDialog(appWindow, "This is a large file, dreamification may take a while");
        return;
    }

    private void openFile() {
        // sets look and feel for JFileChooser to os look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            System.out.println("MASSIVE ERROR LMAO!");
        }
        
        // create window to select a jpg or png file
        JFileChooser chooseFile = new JFileChooser(System.getProperty("user.dir"));
        chooseFile.setAcceptAllFileFilterUsed(false);
        chooseFile.addChoosableFileFilter(new FileNameExtensionFilter("JPG or PNG Image", "jpg","jfif","pjpeg", "pjp","png"));
        chooseFile.setDialogTitle("Choose File to Dreamify!");

        if (chooseFile.showOpenDialog(appWindow) == JFileChooser.APPROVE_OPTION) {
            baseImage = chooseFile.getSelectedFile().toString();
            if (baseImage.equals(openImage)) return;
            //warn user if file is large
            checkFileLength(baseImage,120.0);
            setImage(baseImage);
        }

        // reset so it doesn't mess with other components
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e) {
            System.out.println("MASSIVE ERROR!");
        }
    }
    
    private void setImage(String imagePath) {    
        // don't set image if already set
        if (openImage != null && openImage.equals(imagePath)) {
            return;
        }

        flag = true; 
        openImage = imagePath; 
            
        ImageLoader imageLoader = new ImageLoader();
        imageLoader.execute();

        // buttons should be usable now that image exists
        dreamButton.setEnabled(true);
        styleSelect.setEnabled(true);
        layer1Select.setEnabled(true);
        layer2Select.setEnabled(true);
        
        appWindow.addComponentListener(new ComponentAdapter( ) {
            public void componentResized(ComponentEvent ev) {
                if (dreamProgress != null)
                    OpenFileGUI.imageSpace.revalidate();
                else
                    loadImage();
            }
        });
    }

    public static void loadImage() {
        // if new image is being set
        if (flag) {
            try {
                drawImage = ImageIO.read(new File(OpenFileGUI.openImage));
            } catch (Exception fileNotFound) {
                JOptionPane.showMessageDialog(null, "File not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // get image resolution based on window size
        double drawRatio = 0;
        if (((double)drawImage.getWidth() - imageSpace.getWidth()) > ((double)drawImage.getHeight() - imageSpace.getHeight())) {
            drawRatio = (double)imageSpace.getWidth() / drawImage.getWidth();
        } else {
            drawRatio = (double)imageSpace.getHeight() / drawImage.getHeight();
        }
        BufferedImage drawCanvas = new BufferedImage((int)(drawImage.getWidth()*drawRatio), (int)(drawImage.getHeight()*drawRatio), BufferedImage.TYPE_INT_RGB);
        
        // load image
        Graphics2D draw = drawCanvas.createGraphics();
        draw.drawImage(drawImage, 0, 0, (int)(drawImage.getWidth()*drawRatio), (int)(drawImage.getHeight()*drawRatio), null);
        draw.dispose();
        ImageIcon imageIcon = new ImageIcon(drawCanvas);

        // update image space
        imageSpace.removeAll();
        imageSpace.add(new JLabel(imageIcon));
        imageSpace.revalidate();
        
        flag = false;
    }

    private static void openinfoItem() {
        try {
            Desktop.getDesktop().browse(new URI("https://ai.googleblog.com/2015/06/inceptionism-going-deeper-into-neural.html"));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void openLayerDepthItem() {
        JOptionPane.showMessageDialog(appWindow, "<html><i>Note: Layers and depth can only be modified with advanced settings enabled.</i></html>\n\n" +
                                                "Layers impact what image features are recognized and enhanced in any given dream.\n" +
                                                "Low-level layers enhance simple features, while high-level layers enhance complex features.\n\n" +
                                                "Depth determines how deep the dreamification goes. A smaller depth will\n" +
                                                "result in faster processing times, but less intense dreams.\n\nFor more information on " +
                                                "layers or depth, please review \"How This Works\" in the Help Tab.", "Layers/Depth Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openModelInfoItem() {
        JOptionPane.showMessageDialog(appWindow,
                                    "<html><i>Note: The model can only be changed with advanced settings enabled.</i></html>\n\n" + 
                                    "DeepDreamer uses several models of Convulutional Neural Networks to create dreamified images.\n" +
                                    "These models have their own distinct architecture and produce wildly different styles of dreams.\n\n" +
                                    "The following models are currnetly in use: MobileNetV2, InceptionV3, Xception, and ResNet50.", "Model Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void saveImage(File output) {
        // sets look and feel for JFileChooser to os look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            System.out.println("MASSIVE ERROR LMAO!");
        }
       
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG file", "png"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JPG file", "jpg"));

        if (fileChooser.showSaveDialog(imageSpace) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String name = fileToSave.toString();

            if (!name.endsWith("png") && fileChooser.getFileFilter().getDescription().equals("PNG")) {
                name += ".png";
                save("png", name, output);
            }
            else if (!name.endsWith("jpg") && fileChooser.getFileFilter().getDescription().equals("JPG")) {
                name += ".jpg";
                save("jpg", name, output);
            }
            else if (fileChooser.getFileFilter().getDescription().equals("JPG")){
                save("jpg", name, output);
            }
            else {
                save("png", name, output);
            }
        }

        // reset so it doesn't mess with other components
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e) {
            System.out.println("MASSIVE ERROR!");
        }
    }

    private static void save(String type, String name, File output)
    {
        try {
            BufferedImage img = ImageIO.read(output);
            ImageIO.write(img, type, new File(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private class ButtonClickListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            commandParser: switch(e.getActionCommand()) {
                case ("open"):
                    openFile();
                    break;
                case ("url"):
                    try {
                        // get image from url
                        String url = JOptionPane.showInputDialog(appWindow, "", "Enter a URL that ends in .jpg", JOptionPane.PLAIN_MESSAGE);
                        if (url != null) {
                            String[] split = url.split("/");
                            String fileName = split[split.length - 1];
                            InputStream inputStream = new URL(url).openStream() ;
                            File directory = new File("./input/");
                            if (!directory.exists()){
                                directory.mkdir();
                            }
                            Path path = Paths.get("./input/" + fileName);
                            if (!Files.exists(path)) {
                                Files.copy(inputStream, Paths.get("./input/" + fileName));
                            }
                            baseImage = "./input/" + fileName;
                            //warn user if file is large
                            checkFileLength(baseImage,120.0);
                            setImage(baseImage);
                        }
                    } catch (IOException notValidImage) {
                        JOptionPane.showMessageDialog(appWindow, "Invalid image url!", "Error 303", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case ("save"):
                    if (output != null) saveImage(output);
                    break;
                case ("theme"):
                    if (setColorMenu.isSelected()) {
                        setTheme(1);
                    } else {
                        setTheme(0);
                    }
                    break;
                case ("advanced"):
                    if (advancedItem.isSelected()) {
                        depthSelect.setSelectedIndex(depthTemp);
                        if(modelTemp != dreamModel) setModel(modelTemp);

                        layerLabel.setVisible(true);
                        layer1Select.setVisible(true);
                        layer2Select.setVisible(true);
                        depthLabel.setVisible(true);
                        depthSelect.setVisible(true);

                        setModelMenu.setEnabled(true);
                        modelLabelPanel.setVisible(true);
                        customPresetItem.setEnabled(true);
                        styleSelect.removeItem("Custom");
                        styleSelect.addItem("Custom");

                        //store in settings
                        AdvancedOptions = true;
                    } else {
                        depthTemp = depthSelect.getSelectedIndex();
                        modelTemp = dreamModel;
                        depthSelect.setSelectedIndex(3);
                        setModel(0);

                        layerLabel.setVisible(false);
                        layer1Select.setVisible(false);
                        layer2Select.setVisible(false);
                        depthLabel.setVisible(false);
                        depthSelect.setVisible(false);
                        styleSelect.removeItem("Custom");

                        styleSelect.setSelectedItem(stylePresets.get(dreamModel).get(0));
                        layer1Select.setSelectedIndex(stylePresetLayers.get(dreamModel).get(0)[0]);
                        layer2Select.setSelectedIndex(stylePresetLayers.get(dreamModel).get(0)[1]);

                        setModelMenu.setEnabled(false);
                        modelLabelPanel.setVisible(false);
                        customPresetItem.setEnabled(false);
                        AdvancedOptions = true;

                    }
                    break;
                case ("infoItem"):
                    openinfoItem();
                    break;
                case ("layerDepthItem"):    
                    openLayerDepthItem();
                    break;
                case ("modelInfoItem"):
                    openModelInfoItem();
                    break;
                case ("dream"):
                    DreamWorker dw = new DreamWorker();
                    dw.execute();
                    break;
                case ("reset"):
                    setImage(baseImage);
                    resetButton.setEnabled(false);
                    break;
                case ("style"):
                    setStyle();
                    
                    break;
                case ("layer"):

                    for (int i = 0; i < stylePresets.get(dreamModel).size(); i++) {
                        if (layer1Select.getSelectedIndex() == stylePresetLayers.get(dreamModel).get(i)[0]
                                && layer2Select.getSelectedIndex() == stylePresetLayers.get(dreamModel).get(i)[1]) {
                            styleSelect.setSelectedItem(stylePresets.get(dreamModel).get(i));
                            break commandParser;
                        }
                    }
                    styleSelect.setSelectedItem("Custom");
                    layers = new int[2];
                    layers[0] = layer1Select.getSelectedIndex();
                    layers[1] = layer2Select.getSelectedIndex();


                    break;
                case ("depth"):
                    //setProperty("depth", Integer.toString(depthSelect.getSelectedIndex()));
                    break;
                case ("createCustom"):
                    // model reminder
                    JPanel modelPanel = new JPanel();
                    modelPanel.setLayout(new BoxLayout(modelPanel, BoxLayout.Y_AXIS));
                    JLabel modelLabel = new JLabel(dreamModelLabel.getText());
                    modelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    modelPanel.add(modelLabel);
                    modelPanel.add(Box.createVerticalStrut(16));

                     // layers input
                    JLabel layerLabel = new JLabel("Layers");

                    JComboBox<String> layerList1 = new JComboBox<String>(layer1Options);
                    layerList1.setPreferredSize(new Dimension(45, 30));
                    layerList1.setFont(new Font("Sans", Font.BOLD, 14));

                    JComboBox<String> layerList2 = new JComboBox<String>(layer2Options);
                    layerList2.setPreferredSize(new Dimension(45, 30));
                    layerList2.setFont(new Font("Sans", Font.BOLD, 14));
                    
                    // name input
                    JLabel nameLabel = new JLabel("Name");
                    JTextField textField = new JTextField(10);

                    // panels for main panel
                    JPanel listPanel = new JPanel();
                    listPanel.add(layerLabel);
                    listPanel.add(layerList1);
                    listPanel.add(layerList2);

                    JPanel inputPanel = new JPanel();
                    inputPanel.add(nameLabel);
                    inputPanel.add(textField);

                    JPanel mainPanel = new JPanel(new BorderLayout());
                    mainPanel.add(modelPanel, BorderLayout.NORTH);
                    mainPanel.add(listPanel, BorderLayout.CENTER);
                    mainPanel.add(inputPanel, BorderLayout.SOUTH);

                    int result = JOptionPane.showConfirmDialog(appWindow, mainPanel, "Create Custom Filter", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    // user clicks OK
                    if (result == 0) {
                        String name = textField.getText();
                        
                        if (name != null) {
                            // add name to style prests
                            stylePresets.get(dreamModel).add(name);

                            // add layers to style preset layers
                            int layer1 = Integer.parseInt(layerList1.getSelectedItem().toString()) - 1;
                            int layer2 = Integer.parseInt(layerList2.getSelectedItem().toString()) - 1;
                            stylePresetLayers.get(dreamModel).add(new int[]{layer1, layer2});

                            // add element to styles
                            presets.addElement(name);
                        }
                    }
                }
            }
        }

    class DreamWorker extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() throws Exception {
            
            OpenFileGUI.dreamButton.setEnabled(false);

            // run dream
            // get layers from selection
            String layer1 = String.valueOf(Integer.parseInt(layer1Select.getSelectedItem().toString())-1);
            String layer2 = String.valueOf(Integer.parseInt(layer2Select.getSelectedItem().toString())-1);
            String depth = String.valueOf(-Integer.parseInt(depthSelect.getSelectedItem().toString()));

            // create progress bar
            DreamProgress dp = new DreamProgress(depthSelect.getSelectedItem().toString());
            dp.execute();

            // start python script with file path and layers as arguments
            ProcessBuilder startProcess;

            String os = System.getProperty("os.name");
            if (os.contains("Windows"))
                startProcess = new ProcessBuilder("python", System.getProperty("user.dir") + "\\main.py", openImage, Integer.toString(dreamModel), layer1, layer2, depth);
            else
                startProcess = new ProcessBuilder("python3", System.getProperty("user.dir") + "\\main.py", openImage, Integer.toString(dreamModel), layer1, layer2, depth);

            try {
                Process pythonScript = startProcess.start();
                // read output from script for image file path
                BufferedReader debugging = new BufferedReader(new InputStreamReader(pythonScript.getInputStream()));

                String pythonOutput = null;
                while((pythonOutput = debugging.readLine()) != null) {
                    if (pythonOutput.contains("&&&")) {
                        String dreamImage = pythonOutput.substring(3);
                        setImage("./output/" + dreamImage);
                        OpenFileGUI.dreamButton.setEnabled(true);
                        output = new File("./output/" + dreamImage);
                        resetButton.setEnabled(true);
                        break;
                    }
                    else {
                        int output;
                        if ((output = checkIfInteger(pythonOutput)) > 0) {
                            dreamProgress.setValue(output);

                            int percent = output / Math.abs((Integer.parseInt(depth) - 1));
                            dreamProgress.setString(String.format("Dreamifying image... %d%%", percent));
                        }
                    }
                }

            } catch (IOException noScript) {
                JOptionPane.showMessageDialog(appWindow, "Python script failed!", "Error 117", JOptionPane.ERROR_MESSAGE);
            }

            dreamProgress = null;

            return null;
        }

        @Override
        protected void done() {
            super.done();
        }
    }
}

class ImageLoader extends SwingWorker<Void, Void> {
    @Override
    protected Void doInBackground() throws Exception {
        // add loading image text
        OpenFileGUI.imageSpace.removeAll();
        JLabel imageLoading = new JLabel("Loading image...", SwingConstants.CENTER);
        imageLoading.setFont(new Font("Sans", Font.BOLD, 20));
        imageLoading.setForeground(Color.decode("#d3d5f3"));
        OpenFileGUI.imageSpace.add(imageLoading);
        OpenFileGUI.imageSpace.revalidate();
        return null;
    }

    @Override // only start loading image after loading text is displayed
    protected void done() {
        OpenFileGUI.loadImage();
    }
}

class DreamProgress extends SwingWorker<Void, Void> {
    int max;
    
    DreamProgress(String depth) {
        max = (Integer.parseInt(depth) + 1) * 100;
    }
    
    @Override
    protected Void doInBackground() throws Exception {
        OpenFileGUI.dreamProgress = new JProgressBar(0, max);
        OpenFileGUI.dreamProgress.setValue(0);
        OpenFileGUI.dreamProgress.setOpaque(true);
        OpenFileGUI.dreamProgress.setStringPainted(true);
        OpenFileGUI.dreamProgress.setString("Dreamifying image... 0%");
        OpenFileGUI.dreamProgress.setFont(new Font("Sans", Font.BOLD, 20));
        
        OpenFileGUI.dreamProgress.setBackground(Color.decode("#0b1622"));
        OpenFileGUI.dreamProgress.setForeground(Color.decode("#6382bf"));
        OpenFileGUI.dreamProgress.setBorderPainted(false);

        OpenFileGUI.imageSpace.add(OpenFileGUI.dreamProgress, BorderLayout.SOUTH);
        OpenFileGUI.imageSpace.revalidate();

        return null;
    }

    @Override
    protected void done() {
        super.done();
    }
}

class KeepMenuOpen extends BasicCheckBoxMenuItemUI {

    @Override
    protected void doClick(MenuSelectionManager msm) {
       menuItem.doClick(0);
    }
 
    public static ComponentUI createUI(JComponent c) {
       return new KeepMenuOpen();
    }
 }
