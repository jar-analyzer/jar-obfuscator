package me.n1ar4.jar.obfuscator.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.jar.obfuscator.config.BaseConfig;
import me.n1ar4.jar.obfuscator.config.Manager;
import me.n1ar4.jar.obfuscator.core.Runner;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainForm {
    private JPanel rootPanel;
    private JTextField jarText;
    private JButton jarBtn;
    private JLabel jarLabel;
    private JPanel mainPanel;
    private JRadioButton debugRadioButton;
    private JRadioButton infoRadioButton;
    private JRadioButton warnRadioButton;
    private JRadioButton errorRadioButton;
    private JLabel logLevelLabel;
    private JPanel logLevelPanel;
    private JTextField mainClassText;
    private JLabel mainClassLabel;
    private JTextField obfCharsText;
    private JTextField obfPackText;
    private JTextField rootPackText;
    private JPanel obfuscateListPanel;
    private JLabel obfCharsLabel;
    private JLabel obfPackLabel;
    private JLabel rootPackLabel;
    private JPanel blackWhitePanel;
    private JTextField classBlackText;
    private JTextField classBlackRegexText;
    private JTextField methodBlackText;
    private JLabel classBlackLabel;
    private JLabel classBlackRegexLabel;
    private JLabel methodBlackLabel;
    private JPanel mainConfigPanel;
    private JCheckBox classNameObfuscateCheckBox;
    private JCheckBox packageNameObfuscateCheckBox;
    private JCheckBox methodNameObfuscateCheckBox;
    private JCheckBox fieldNameObfuscateCheckBox;
    private JCheckBox parameterNameObfuscateCheckBox;
    private JCheckBox autoModifyManifestCheckBox;
    private JCheckBox enableHideMethodCheckBox;
    private JCheckBox enableHideFieldCheckBox;
    private JCheckBox enableDeleteCompileInfoCheckBox;
    private JCheckBox enableXorObfuscateCheckBox;
    private JCheckBox useCPURDRANDCheckBox;
    private JCheckBox keepTempFilesCheckBox;
    private JCheckBox enableStringEncryptCheckBox;
    private JTextField strAesText;
    private JCheckBox enableAdvanceStringCheckBox;
    private JTextField strAdvText;
    private JTextField decClassText;
    private JTextField decMethodText;
    private JTextField decFieldText;
    private JCheckBox enableJunkCodeCheckBox;
    private JRadioButton level1RadioButton;
    private JRadioButton level2RadioButton;
    private JRadioButton level3RadioButton;
    private JRadioButton level4RadioButton;
    private JRadioButton level5RadioButton;
    private JSpinner junkNumSpinner;
    private JCheckBox enableEncryptBytecodeCheckBox;
    private JTextField bytecodeKeyText;
    private JTextField bytecodePackText;
    private JButton confirmConfigBtn;
    private JTextArea logArea;
    private JButton startObfBtn;
    private JPanel thePanel;
    private JScrollPane logScroll;
    private JPanel modulePanel;
    private JPanel strEncPanel;
    private JLabel strAesKeyLabel;
    private JLabel strAdvNameLabel;
    private JPanel decPanel;
    private JLabel decClassLabel;
    private JLabel decMethodLabel;
    private JLabel decFieldLabel;
    private JPanel junkPanel;
    private JLabel junkLevelLabel;
    private JLabel junkNumLabel;
    private JPanel bytecodeEncPanel;
    private JLabel bytecodeKeyLabel;
    private JLabel bytecodePackLabel;

    public static MainForm instance;

    public static BaseConfig config;

    public static void log(String msg) {
        instance.logArea.append("[INFO] " + msg + "\n");
        instance.logArea.setCaretPosition(instance.logArea.getDocument().getLength());
    }

    public MainForm() {
        infoRadioButton.setSelected(true);
        autoModifyManifestCheckBox.setSelected(true);
        mainClassText.setText("me.n1ar4.jar.obfuscator.Main");
        obfCharsText.setText("i,l,L,1,I");
        obfPackText.setText("me.n1ar4,org.n1ar4");
        rootPackText.setText("me.n1ar4,org.n1ar4");
        classBlackText.setText("javafx.controller.DemoController");
        classBlackRegexText.setText("java/.*,com/intellij/.*");
        methodBlackText.setText("visit.*,start.*");
        classNameObfuscateCheckBox.setSelected(true);
        packageNameObfuscateCheckBox.setSelected(true);
        methodNameObfuscateCheckBox.setSelected(true);
        fieldNameObfuscateCheckBox.setSelected(true);
        parameterNameObfuscateCheckBox.setSelected(true);
        enableStringEncryptCheckBox.setSelected(true);
        strAesText.setText("Y4SuperSecretKey");
        enableAdvanceStringCheckBox.setSelected(true);
        strAdvText.setText("GIiIiLA");
        decClassText.setText("org.apache.commons.collections.list.AbstractHashMap");
        decMethodText.setText("newMap");
        decFieldText.setText("LiLiLLLiiiLLiiLLi");
        enableHideFieldCheckBox.setSelected(true);
        enableHideMethodCheckBox.setSelected(true);
        enableDeleteCompileInfoCheckBox.setSelected(true);
        enableXorObfuscateCheckBox.setSelected(true);
        enableJunkCodeCheckBox.setSelected(true);
        level5RadioButton.setSelected(true);
        junkNumSpinner.setValue(2000);
        enableEncryptBytecodeCheckBox.setSelected(false);
        bytecodeKeyText.setText("4ra1n4ra1n4ra1n1");
        bytecodePackText.setText("me.n1ar4");
        keepTempFilesCheckBox.setSelected(false);
        useCPURDRANDCheckBox.setSelected(true);

        jarBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setFileHidingEnabled(false);
            fileChooser.setCurrentDirectory(new File("."));
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getName().toLowerCase().endsWith(".jar") ||
                            f.getName().toLowerCase().endsWith(".war");
                }

                @Override
                public String getDescription() {
                    return "jar/war";
                }
            });
            int option = fileChooser.showOpenDialog(new JFrame());
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String absPath = file.getAbsolutePath();
                log("load file: " + absPath);
                jarText.setText(absPath);
            }
        });

        confirmConfigBtn.addActionListener(e -> {
            config = new BaseConfig();

            if (debugRadioButton.isSelected()) {
                config.setLogLevel("debug");
            } else if (infoRadioButton.isSelected()) {
                config.setLogLevel("info");
            } else if (warnRadioButton.isSelected()) {
                config.setLogLevel("warn");
            } else if (errorRadioButton.isSelected()) {
                config.setLogLevel("error");
            }

            config.setMainClass(mainClassText.getText());

            config.setModifyManifest(autoModifyManifestCheckBox.isSelected());

            config.setObfuscateChars(obfCharsText.getText().split(","));

            config.setObfuscatePackage(obfPackText.getText().split(","));

            config.setRootPackages(rootPackText.getText().split(","));

            config.setClassBlackList(classBlackText.getText().split(","));

            config.setClassBlackRegexList(classBlackRegexText.getText().split(","));

            config.setMethodBlackList(methodBlackText.getText().split(","));

            config.setEnableClassName(classNameObfuscateCheckBox.isSelected());

            config.setEnablePackageName(packageNameObfuscateCheckBox.isSelected());

            config.setEnableMethodName(methodNameObfuscateCheckBox.isSelected());

            config.setEnableFieldName(fieldNameObfuscateCheckBox.isSelected());

            config.setEnableParamName(parameterNameObfuscateCheckBox.isSelected());

            config.setEnableEncryptString(enableStringEncryptCheckBox.isSelected());

            config.setStringAesKey(strAesText.getText());

            config.setEnableAdvanceString(enableAdvanceStringCheckBox.isSelected());

            config.setAdvanceStringName(strAdvText.getText());

            config.setDecryptClassName(decClassText.getText());

            config.setDecryptMethodName(decMethodText.getText());

            config.setDecryptKeyName(decFieldText.getText());

            config.setEnableHideField(enableHideFieldCheckBox.isSelected());

            config.setEnableHideMethod(enableHideMethodCheckBox.isSelected());

            config.setEnableDeleteCompileInfo(enableDeleteCompileInfoCheckBox.isSelected());

            config.setEnableXOR(enableXorObfuscateCheckBox.isSelected());

            config.setEnableJunk(enableJunkCodeCheckBox.isSelected());

            if (level1RadioButton.isSelected()) {
                config.setJunkLevel(1);
            } else if (level2RadioButton.isSelected()) {
                config.setJunkLevel(2);
            } else if (level3RadioButton.isSelected()) {
                config.setJunkLevel(3);
            } else if (level4RadioButton.isSelected()) {
                config.setJunkLevel(4);
            } else if (level5RadioButton.isSelected()) {
                config.setJunkLevel(5);
            }

            config.setShowAllMainMethods(true);

            config.setEnableSuperObfuscate(enableEncryptBytecodeCheckBox.isSelected());

            config.setSuperObfuscateKey(bytecodeKeyText.getText());

            config.setSuperObfuscatePackage(bytecodePackText.getText());

            config.setKeepTempFile(keepTempFilesCheckBox.isSelected());

            config.setUseCpuRDRAND(useCPURDRANDCheckBox.isSelected());

            log("confirm jar obfuscator config ok");
        });

        startObfBtn.addActionListener(e -> {
            Path path = Paths.get(jarText.getText());
            log("init manager...");
            boolean success = Manager.initConfig(config);
            if (!success) {
                return;
            }
            log("init manager ok");
            log("start jar obfuscator...");
            Runner.run(path, config);
        });
    }

    public static void start() {
        FlatDarkLaf.setup();
        String title = "jar obfuscator " + Const.VERSION + " @ jar analyzer team by 4ra1n";
        JFrame frame = new JFrame(title);
        instance = new MainForm();
        log("init jar obfuscator finish");
        frame.setContentPane(instance.rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 3, new Insets(5, 5, 5, 5), -1, -1));
        rootPanel.add(mainPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        jarLabel = new JLabel();
        jarLabel.setText("JAR");
        mainPanel.add(jarLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        jarText = new JTextField();
        jarText.setEnabled(false);
        mainPanel.add(jarText, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        jarBtn = new JButton();
        jarBtn.setText("CHOSE");
        mainPanel.add(jarBtn, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        thePanel = new JPanel();
        thePanel.setLayout(new GridLayoutManager(8, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(thePanel, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        logLevelPanel = new JPanel();
        logLevelPanel.setLayout(new GridLayoutManager(2, 7, new Insets(0, 0, 0, 0), -1, -1));
        thePanel.add(logLevelPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        logLevelLabel = new JLabel();
        logLevelLabel.setText("log level");
        logLevelPanel.add(logLevelLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        debugRadioButton = new JRadioButton();
        debugRadioButton.setText("debug");
        logLevelPanel.add(debugRadioButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        infoRadioButton = new JRadioButton();
        infoRadioButton.setText("info");
        logLevelPanel.add(infoRadioButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        warnRadioButton = new JRadioButton();
        warnRadioButton.setText("warn");
        logLevelPanel.add(warnRadioButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        errorRadioButton = new JRadioButton();
        errorRadioButton.setText("error");
        logLevelPanel.add(errorRadioButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        confirmConfigBtn = new JButton();
        confirmConfigBtn.setText("CONFIRM CONFIG");
        logLevelPanel.add(confirmConfigBtn, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logScroll = new JScrollPane();
        logLevelPanel.add(logScroll, new GridConstraints(1, 0, 1, 7, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        logScroll.setBorder(BorderFactory.createTitledBorder(null, "log info", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setForeground(new Color(-16718519));
        logArea.setRows(5);
        logArea.setText("");
        logScroll.setViewportView(logArea);
        startObfBtn = new JButton();
        startObfBtn.setText("START OBFUSCATE");
        logLevelPanel.add(startObfBtn, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mainConfigPanel = new JPanel();
        mainConfigPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        thePanel.add(mainConfigPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        mainClassLabel = new JLabel();
        mainClassLabel.setText("main class");
        mainConfigPanel.add(mainClassLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mainClassText = new JTextField();
        mainConfigPanel.add(mainClassText, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        obfuscateListPanel = new JPanel();
        obfuscateListPanel.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        thePanel.add(obfuscateListPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        obfuscateListPanel.setBorder(BorderFactory.createTitledBorder(null, "package config", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        obfCharsLabel = new JLabel();
        obfCharsLabel.setText("obfuscate chars");
        obfuscateListPanel.add(obfCharsLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        obfCharsText = new JTextField();
        obfuscateListPanel.add(obfCharsText, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        obfPackLabel = new JLabel();
        obfPackLabel.setText("obfuscate package");
        obfuscateListPanel.add(obfPackLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        obfPackText = new JTextField();
        obfuscateListPanel.add(obfPackText, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        rootPackLabel = new JLabel();
        rootPackLabel.setText("root package");
        obfuscateListPanel.add(rootPackLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rootPackText = new JTextField();
        obfuscateListPanel.add(rootPackText, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        blackWhitePanel = new JPanel();
        blackWhitePanel.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        thePanel.add(blackWhitePanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        blackWhitePanel.setBorder(BorderFactory.createTitledBorder(null, "black / white list config", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        classBlackLabel = new JLabel();
        classBlackLabel.setText("class balck list");
        blackWhitePanel.add(classBlackLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        classBlackText = new JTextField();
        blackWhitePanel.add(classBlackText, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        classBlackRegexLabel = new JLabel();
        classBlackRegexLabel.setText("class black regex list");
        blackWhitePanel.add(classBlackRegexLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        classBlackRegexText = new JTextField();
        blackWhitePanel.add(classBlackRegexText, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        methodBlackLabel = new JLabel();
        methodBlackLabel.setText("method black list");
        blackWhitePanel.add(methodBlackLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        methodBlackText = new JTextField();
        blackWhitePanel.add(methodBlackText, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        modulePanel = new JPanel();
        modulePanel.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        thePanel.add(modulePanel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        modulePanel.setBorder(BorderFactory.createTitledBorder(null, "module", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        classNameObfuscateCheckBox = new JCheckBox();
        classNameObfuscateCheckBox.setText("class name obfuscate");
        modulePanel.add(classNameObfuscateCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        packageNameObfuscateCheckBox = new JCheckBox();
        packageNameObfuscateCheckBox.setText("package name obfuscate");
        modulePanel.add(packageNameObfuscateCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        methodNameObfuscateCheckBox = new JCheckBox();
        methodNameObfuscateCheckBox.setText("method name obfuscate");
        modulePanel.add(methodNameObfuscateCheckBox, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fieldNameObfuscateCheckBox = new JCheckBox();
        fieldNameObfuscateCheckBox.setText("field name obfuscate");
        modulePanel.add(fieldNameObfuscateCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        parameterNameObfuscateCheckBox = new JCheckBox();
        parameterNameObfuscateCheckBox.setText("parameter name obfuscate");
        modulePanel.add(parameterNameObfuscateCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        enableHideMethodCheckBox = new JCheckBox();
        enableHideMethodCheckBox.setText("enable hide method");
        modulePanel.add(enableHideMethodCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        enableHideFieldCheckBox = new JCheckBox();
        enableHideFieldCheckBox.setText("enable hide field");
        modulePanel.add(enableHideFieldCheckBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        enableDeleteCompileInfoCheckBox = new JCheckBox();
        enableDeleteCompileInfoCheckBox.setText("enable delete compile info");
        modulePanel.add(enableDeleteCompileInfoCheckBox, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        enableXorObfuscateCheckBox = new JCheckBox();
        enableXorObfuscateCheckBox.setText("enable xor obfuscate");
        modulePanel.add(enableXorObfuscateCheckBox, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        autoModifyManifestCheckBox = new JCheckBox();
        autoModifyManifestCheckBox.setText("auto modify manifest");
        modulePanel.add(autoModifyManifestCheckBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        useCPURDRANDCheckBox = new JCheckBox();
        useCPURDRANDCheckBox.setText("use CPU RDRAND");
        modulePanel.add(useCPURDRANDCheckBox, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        keepTempFilesCheckBox = new JCheckBox();
        keepTempFilesCheckBox.setText("keep temp files");
        modulePanel.add(keepTempFilesCheckBox, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        strEncPanel = new JPanel();
        strEncPanel.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        thePanel.add(strEncPanel, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        strEncPanel.setBorder(BorderFactory.createTitledBorder(null, "string encrypt", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        enableStringEncryptCheckBox = new JCheckBox();
        enableStringEncryptCheckBox.setText("enable string encrypt");
        strEncPanel.add(enableStringEncryptCheckBox, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        strAesKeyLabel = new JLabel();
        strAesKeyLabel.setText("aes key");
        strEncPanel.add(strAesKeyLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        strAesText = new JTextField();
        strEncPanel.add(strAesText, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        enableAdvanceStringCheckBox = new JCheckBox();
        enableAdvanceStringCheckBox.setText("enable advance string");
        strEncPanel.add(enableAdvanceStringCheckBox, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        strAdvNameLabel = new JLabel();
        strAdvNameLabel.setText("name");
        strEncPanel.add(strAdvNameLabel, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        strAdvText = new JTextField();
        strEncPanel.add(strAdvText, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        decPanel = new JPanel();
        decPanel.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        strEncPanel.add(decPanel, new GridConstraints(2, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        decClassLabel = new JLabel();
        decClassLabel.setText("decrypt class name");
        decPanel.add(decClassLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        decClassText = new JTextField();
        decPanel.add(decClassText, new GridConstraints(0, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        decMethodLabel = new JLabel();
        decMethodLabel.setText("decrypt method name");
        decPanel.add(decMethodLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        decMethodText = new JTextField();
        decPanel.add(decMethodText, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        decFieldLabel = new JLabel();
        decFieldLabel.setText("field name");
        decPanel.add(decFieldLabel, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        decFieldText = new JTextField();
        decPanel.add(decFieldText, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        junkPanel = new JPanel();
        junkPanel.setLayout(new GridLayoutManager(1, 9, new Insets(0, 0, 0, 0), -1, -1));
        thePanel.add(junkPanel, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        junkPanel.setBorder(BorderFactory.createTitledBorder(null, "junk code module", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        enableJunkCodeCheckBox = new JCheckBox();
        enableJunkCodeCheckBox.setText("enable junk code");
        junkPanel.add(enableJunkCodeCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        junkLevelLabel = new JLabel();
        junkLevelLabel.setText("level");
        junkPanel.add(junkLevelLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        level1RadioButton = new JRadioButton();
        level1RadioButton.setText("level 1");
        junkPanel.add(level1RadioButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        level2RadioButton = new JRadioButton();
        level2RadioButton.setText("level 2");
        junkPanel.add(level2RadioButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        level3RadioButton = new JRadioButton();
        level3RadioButton.setText("level 3");
        junkPanel.add(level3RadioButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        level4RadioButton = new JRadioButton();
        level4RadioButton.setText("level 4");
        junkPanel.add(level4RadioButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        level5RadioButton = new JRadioButton();
        level5RadioButton.setText("level 5");
        junkPanel.add(level5RadioButton, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        junkNumLabel = new JLabel();
        junkNumLabel.setText("max num in one class");
        junkPanel.add(junkNumLabel, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        junkNumSpinner = new JSpinner();
        junkPanel.add(junkNumSpinner, new GridConstraints(0, 8, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), null, null, 0, false));
        bytecodeEncPanel = new JPanel();
        bytecodeEncPanel.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        thePanel.add(bytecodeEncPanel, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        bytecodeEncPanel.setBorder(BorderFactory.createTitledBorder(null, "bytecode encrypt config", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        enableEncryptBytecodeCheckBox = new JCheckBox();
        enableEncryptBytecodeCheckBox.setText("enable encrypt bytecode");
        bytecodeEncPanel.add(enableEncryptBytecodeCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bytecodeKeyLabel = new JLabel();
        bytecodeKeyLabel.setText("key");
        bytecodeEncPanel.add(bytecodeKeyLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bytecodeKeyText = new JTextField();
        bytecodeEncPanel.add(bytecodeKeyText, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        bytecodePackLabel = new JLabel();
        bytecodePackLabel.setText("package");
        bytecodeEncPanel.add(bytecodePackLabel, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bytecodePackText = new JTextField();
        bytecodeEncPanel.add(bytecodePackText, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(debugRadioButton);
        buttonGroup.add(infoRadioButton);
        buttonGroup.add(warnRadioButton);
        buttonGroup.add(errorRadioButton);
        buttonGroup = new ButtonGroup();
        buttonGroup.add(level1RadioButton);
        buttonGroup.add(level2RadioButton);
        buttonGroup.add(level3RadioButton);
        buttonGroup.add(level4RadioButton);
        buttonGroup.add(level5RadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

}
