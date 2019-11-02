package com.bits.cps.Helper;

public class Routes {
//    ``````````````````` ``````````````````````````````domain````````````````````````
    public static final String domain = "http://cpsgroups.in/";
    //    -----------------------------------------------insideroot----------------------------------------------
    public static final String controller = domain+"controller/api1/";
    public static final String img = domain+"img/";
    //    -----------------------------------------------controller/app/api1/folders----------------------------
    public static final String user = controller + "user/";
    public static final String common = controller + "common/";
    public static final String logiin_logout = controller + "logiin_logout/";

    //   ---------------------------------------------controller/app/api1/files------------------------------
    public static final String insert = user + "insert.php";
    public static final String insert2 = common + "insert2.php";
    public static final String insert3 = common + "insert.php";
    public static final String update= common + "UpdateData.php";
    public static final String selectAll = common + "selectAll.php";
    public static final String selectAllByQuery = common + "selectAllByQuery.php";
    public static final String selectAllByColumn = common + "selectAllByColumn.php";
    public static final String multipleTablesData = common + "multipleTablesData.php";
    public static final String selectOne = common + "selectOne.php";
    public static final String selectOneByColumn = common + "selectOneByColumn.php";
    public static final String selectAllbyDate = common + "selectAllbyDate.php";
    public static final String countData = common + "countdata.php";
    public static final String LoginByApp = logiin_logout + "LoginByApp.php";

    //------------------------------------------------------------------shared preferences---------------------------
    public static final String sharedPrefForLogin = "logindetail";
}
