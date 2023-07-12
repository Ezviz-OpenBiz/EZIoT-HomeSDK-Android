package com.eziot.demo.ble.skip;

import java.util.List;

public class GetOfflineResp {

    boolean hasNext;

    int total;

    public List<OfflineItem> data;

    public static class OfflineItem{
        public String m;

        public int c;

        public int t;

        public int s;

        public int d;
    }


}
