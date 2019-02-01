package com.pspl.apple.accesspoint;

public class OfflineVisitors {


    private String emp_name;

    private String v_id;
    private String e_id;
    private String time;



    public OfflineVisitors(String vid,String emp, String eid, String time) {
        this.emp_name = emp;

        this.v_id = vid;
        this.e_id = eid;
        this.time = time;
    }

    public String getV_id() {
        return v_id;
    }

    public void setV_id(String v_id) {
        this.v_id = v_id;
    }

    public String getE_id() {
        return e_id;
    }

    public void setE_id(String e_id) {
        this.e_id = e_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

}
