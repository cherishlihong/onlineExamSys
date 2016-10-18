package com.ifox.framework.beans;

/**
 * Created by Administrator on 2016/10/8.
 */
public final class CommonAttributes {

    /** 日期格式配比 */
    public static final String[] DATE_PATTERNS = new String[] { "yyyy", "yyyy-MM", "yyyyMM", "yyyy/MM", "yyyy-MM-dd", "yyyyMMdd", "yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss", "yyyy/MM/dd HH:mm:ss" };

    /** shopxx.xml文件路径 */
    public static final String COMMON_CONFIG_XML_PATH = "/common-config.xml";

    /** shopxx.properties文件路径 */
    public static final String COMMON_CONFIG_PROPERTIES_PATH = "/common-config.properties";

    public static final String API_TOKEN = "token";

    public static final String API_USERID = "user_id";

    public static final String[] systemManage = {"systemManage","admin:user","admin:role","admin:roleAuth","admin:params"};

    public static final String[] endUserManage = {"endUserManage","admin:endUser","admin:feedback"};

    public static final String[] shipInfoManage = {"shipInfoManage","admin:shipInfo","admin:shipGroup"};

    public static final String[] shipOwnerManage = {"shipOwnerManage","admin:shipOwner"};

    public static final String[] alertAreaManage = {"alertAreaManage","admin:alertArea"};

    public static final String[] shipPortManage = {"shipPortManage","admin:shipPort"};

    public static final String[] terminalManage = {"terminalManage","admin:terminalInfo"};

    public static final String[] shipUploadManage = {"shipUploadManage","admin:shipUploadInfo"};

    public static final String[] shipViolationManage = {"shipViolationManage","admin:shipViolationInfo"};

    public static final String[] fishingProhibitManage = {"fishingProhibitManage","admin:fishingInfo"};

    public static final String[][] MENU = {systemManage,endUserManage,shipInfoManage,shipOwnerManage,alertAreaManage,shipPortManage,
            terminalManage,shipUploadManage,shipViolationManage,fishingProhibitManage};

    /**
     * 不可实例化
     */
    private CommonAttributes() {
    }

}
