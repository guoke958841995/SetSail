package com.sxhalo.PullCoal.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by amoldZhang on 2019/3/22.
 */

public class ExpressEntity implements Serializable{

    public String time;
    public String context;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
