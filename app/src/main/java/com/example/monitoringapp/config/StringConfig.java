package com.example.monitoringapp.config;

public class StringConfig {
    public static String SERVER             = "http://192.168.43.180/monitoring/rest_api/";
    //public static String SERVER             = "https://monitoring.codingindo.id/rest_api/";

    /*KUNJUNGAN RUTIN*/
    public static String GET_DATA             = SERVER+"get_data/";
    public static String IJINKAN_RUTIN        = SERVER+"ijinkan_rutin/";

    /*new API*/
    public static String SCAN               = SERVER+"scan_barcode/";
    public static String CEK_MANUAL         = SERVER+"cek_input_manual/";
    public static String KELUAR_MANUAL      = SERVER+"aksi_keluar_manual/";
    public static String MASUK_MANUAL       = SERVER+"aksi_masuk_manual/";

}
