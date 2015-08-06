package com.example.lasting.downloaddemo.databases;

import com.example.lasting.downloaddemo.entities.ThreadInfo;

import java.util.List;

/**
 * 数据访问接口
 */
public interface ThreadDAO {
    //加入线程
    public void insertThread(ThreadInfo threadInfo);
    //删除线程
    public void deleteThread(String url,int thread_id);
    //更新线程
    public void updateThread(String url,int thread_id,int finished);
    //查询文件的线程信息
    public List<ThreadInfo>getThreads(String url);
    //线程信息是否存在
    public boolean isExists(String url,int thread_id);
}
