package com.example.lasting.downloaddemo.entities;
/**
 * 用于储存线程信息
 * 注：由于下载是耗时操作，所以必须使用多线程处理
 */
public class ThreadInfo {
    private int id;
    private String url;
    private int start;
    private int end;
    private int finished;

    public ThreadInfo() {
        super();
    }

    public ThreadInfo(int id, String url, int start, int end, int finished) {
        this.id = id;
        this.url = url;
        this.start = start;
        this.end = end;
        this.finished = finished;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }
}
