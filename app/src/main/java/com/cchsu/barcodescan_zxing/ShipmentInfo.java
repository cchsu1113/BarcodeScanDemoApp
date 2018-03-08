package com.cchsu.barcodescan_zxing;

import java.util.Date;

/**
 * Created by cchsu on 2017/9/14.
 */

public class ShipmentInfo {
    String strOrderNo;
    Date brushDate;

    public ShipmentInfo(String no, Date time) {
        strOrderNo = no;
        brushDate = time;
    }
}
