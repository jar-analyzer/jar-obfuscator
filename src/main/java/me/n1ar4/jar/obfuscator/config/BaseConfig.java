package me.n1ar4.jar.obfuscator.config;

public class BaseConfig {
    private String logLevel;

    private boolean enableFieldName;
    private boolean enablePackageName;
    private boolean enableClassName;
    private boolean enableMethodName;
    private boolean enableParamName;
    private boolean enableEncryptString;
    private boolean enableAdvanceString;

    private String decryptClassName;
    private String decryptMethodName;
    private String decryptKeyName;

    private boolean enableHideMethod;
    private boolean enableHideField;

    private boolean enableXOR;
    private boolean enableJunk;
    private boolean enableDeleteCompileInfo;

    private String stringAesKey;

    private boolean enableSuperObfuscate;
    private String superObfuscateKey;
    private String superObfuscatePackage;

    private int junkLevel;
    private int maxJunkOneClass;

    private String[] obfuscatePackage;
    private String[] obfuscateChars;
    private String[] methodBlackList;
    private String[] classBlackList;
    private String[] rootPackages;
    private String mainClass;
    private boolean modifyManifest;

    private boolean showAllMainMethods;

    private boolean keepTempFile;

    public boolean isEnableHideField() {
        return enableHideField;
    }

    public void setEnableHideField(boolean enableHideField) {
        this.enableHideField = enableHideField;
    }

    public boolean isEnableHideMethod() {
        return enableHideMethod;
    }

    public void setEnableHideMethod(boolean enableHideMethod) {
        this.enableHideMethod = enableHideMethod;
    }

    public String getDecryptKeyName() {
        return decryptKeyName;
    }

    public void setDecryptKeyName(String decryptKeyName) {
        this.decryptKeyName = decryptKeyName;
    }

    public String getDecryptClassName() {
        return decryptClassName;
    }

    public void setDecryptClassName(String decryptClassName) {
        this.decryptClassName = decryptClassName;
    }

    public String getDecryptMethodName() {
        return decryptMethodName;
    }

    public void setDecryptMethodName(String decryptMethodName) {
        this.decryptMethodName = decryptMethodName;
    }

    public String[] getRootPackages() {
        return rootPackages;
    }

    public void setRootPackages(String[] rootPackages) {
        this.rootPackages = rootPackages;
    }

    public String[] getClassBlackList() {
        return classBlackList;
    }

    public void setClassBlackList(String[] classBlackList) {
        this.classBlackList = classBlackList;
    }

    public boolean isKeepTempFile() {
        return keepTempFile;
    }

    public void setKeepTempFile(boolean keepTempFile) {
        this.keepTempFile = keepTempFile;
    }

    public String getStringAesKey() {
        return stringAesKey;
    }

    public void setStringAesKey(String stringAesKey) {
        this.stringAesKey = stringAesKey;
    }

    public String[] getMethodBlackList() {
        return methodBlackList;
    }

    public void setMethodBlackList(String[] methodBlackList) {
        this.methodBlackList = methodBlackList;
    }

    public boolean isEnablePackageName() {
        return enablePackageName;
    }

    public void setEnablePackageName(boolean enablePackageName) {
        this.enablePackageName = enablePackageName;
    }

    public boolean isModifyManifest() {
        return modifyManifest;
    }

    public void setModifyManifest(boolean modifyManifest) {
        this.modifyManifest = modifyManifest;
    }

    private String advanceStringName;

    public String getSuperObfuscateKey() {
        return superObfuscateKey;
    }

    public void setSuperObfuscateKey(String superObfuscateKey) {
        this.superObfuscateKey = superObfuscateKey;
    }

    public String getSuperObfuscatePackage() {
        return superObfuscatePackage;
    }

    public void setSuperObfuscatePackage(String superObfuscatePackage) {
        this.superObfuscatePackage = superObfuscatePackage;
    }

    public boolean isEnableSuperObfuscate() {
        return enableSuperObfuscate;
    }

    public void setEnableSuperObfuscate(boolean enableSuperObfuscate) {
        this.enableSuperObfuscate = enableSuperObfuscate;
    }

    public String getAdvanceStringName() {
        return advanceStringName;
    }

    public void setAdvanceStringName(String advanceStringName) {
        this.advanceStringName = advanceStringName;
    }

    public boolean isShowAllMainMethods() {
        return showAllMainMethods;
    }

    public void setShowAllMainMethods(boolean showAllMainMethods) {
        this.showAllMainMethods = showAllMainMethods;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public boolean isEnableFieldName() {
        return enableFieldName;
    }

    public void setEnableFieldName(boolean enableFieldName) {
        this.enableFieldName = enableFieldName;
    }

    public boolean isEnableClassName() {
        return enableClassName;
    }

    public void setEnableClassName(boolean enableClassName) {
        this.enableClassName = enableClassName;
    }

    public boolean isEnableMethodName() {
        return enableMethodName;
    }

    public void setEnableMethodName(boolean enableMethodName) {
        this.enableMethodName = enableMethodName;
    }

    public boolean isEnableParamName() {
        return enableParamName;
    }

    public void setEnableParamName(boolean enableParamName) {
        this.enableParamName = enableParamName;
    }

    public boolean isEnableEncryptString() {
        return enableEncryptString;
    }

    public void setEnableEncryptString(boolean enableEncryptString) {
        this.enableEncryptString = enableEncryptString;
    }

    public boolean isEnableAdvanceString() {
        return enableAdvanceString;
    }

    public void setEnableAdvanceString(boolean enableAdvanceString) {
        this.enableAdvanceString = enableAdvanceString;
    }

    public boolean isEnableXOR() {
        return enableXOR;
    }

    public void setEnableXOR(boolean enableXOR) {
        this.enableXOR = enableXOR;
    }

    public boolean isEnableJunk() {
        return enableJunk;
    }

    public void setEnableJunk(boolean enableJunk) {
        this.enableJunk = enableJunk;
    }

    public boolean isEnableDeleteCompileInfo() {
        return enableDeleteCompileInfo;
    }

    public void setEnableDeleteCompileInfo(boolean enableDeleteCompileInfo) {
        this.enableDeleteCompileInfo = enableDeleteCompileInfo;
    }

    public int getJunkLevel() {
        return junkLevel;
    }

    public void setJunkLevel(int junkLevel) {
        this.junkLevel = junkLevel;
    }

    public int getMaxJunkOneClass() {
        return maxJunkOneClass;
    }

    public void setMaxJunkOneClass(int maxJunkOneClass) {
        this.maxJunkOneClass = maxJunkOneClass;
    }

    public String[] getObfuscatePackage() {
        return obfuscatePackage;
    }

    public void setObfuscatePackage(String[] obfuscatePackage) {
        this.obfuscatePackage = obfuscatePackage;
    }

    public String[] getObfuscateChars() {
        return obfuscateChars;
    }

    public void setObfuscateChars(String[] obfuscateChars) {
        this.obfuscateChars = obfuscateChars;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
}
