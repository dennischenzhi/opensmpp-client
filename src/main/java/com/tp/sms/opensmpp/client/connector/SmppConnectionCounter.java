package com.tp.sms.opensmpp.client.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class SmppConnectionCounter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmppConnectionCounter.class);
    
    private Date entrySecond;
    private AtomicInteger cap;
    private AtomicInteger count;

    private String connectorId; //设置成connectorId

    public SmppConnectionCounter(){
        this.entrySecond = new Date();
        this.count = new AtomicInteger(0);
    }

    public SmppConnectionCounter(Integer cap){
        this.entrySecond = new Date();
        this.count = new AtomicInteger(0);
        this.cap = new AtomicInteger(cap);
    }

    public SmppConnectionCounter(String connectorId,Integer cap){
        this.entrySecond = new Date();
        this.count = new AtomicInteger(0);
        this.cap = new AtomicInteger(cap);
        this.connectorId = connectorId;
    }

    public String getConnectorId(){
        return this.connectorId;
    }
    public synchronized Date getEntrySecond(){
        return this.entrySecond;
    }

    public synchronized boolean aboveOneSecond(Date newSecond){
        long between = newSecond.getTime() - entrySecond.getTime();
        if(between> 1000){
            //一秒之外，则肯定大于一秒了
            return true;
        }else{
            //相差一秒之内，看看秒数有没有进一
            int newSec = newSecond.getSeconds();
            int entrySec = entrySecond.getSeconds();
            if(newSec != entrySec){
                return true;
            }
            return false;
        }
    }

    public synchronized boolean tryAdd() {
        if(count.get()>=cap.get()){
            //尽量精确的休眠到下一个一秒的0000毫秒
            Date current = new Date();
            //long between = 1000 - current.get
            long millis = System.currentTimeMillis() % 1000;
            try {
                Thread.sleep(1000-millis);
                LOGGER.info("Connector Counter Max Cap, force to sleep");
                count.set(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            count.addAndGet(1);
            LOGGER.info("tryAdd succ, current counter: " +count);
        }

        return true;
    }
}
