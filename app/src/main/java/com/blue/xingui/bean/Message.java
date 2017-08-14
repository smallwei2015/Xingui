package com.blue.xingui.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by cj on 2017/7/24.
 */

@Table(name = "message")
public class Message implements Serializable {
    @Column(name = "id",isId = true,autoGen = true)
    private int id;

    @Column(name = "content")
    private String content;
    @Column(name = "date")
    private String date;
    @Column(name = "state")
    private int state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
