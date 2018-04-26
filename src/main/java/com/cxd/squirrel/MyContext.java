package com.cxd.squirrel;

/**
 * desc
 *
 * @author childe
 * @date 2018/4/25 15:23
 **/
public class MyContext {
    private String no;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    @Override
    public String toString() {
        return "MyContext{" +
                "no='" + no + '\'' +
                '}';
    }
}
