package com.tp.sms.opensmpp.client.service;

import com.tp.sms.opensmpp.client.connector.SmppConnection;
import com.tp.sms.opensmpp.client.connector.SmppConnectionCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smpp.*;
import org.smpp.pdu.*;
import org.smpp.util.ByteBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiTaskClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiTaskClient.class);

    static BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

    private String oa = "Test";
    private String da = "85261450947";
    private String content = "Long message is really a long message";

    public void sendMulti() throws PDUException, NotSynchronousException, TimeoutException, WrongSessionStateException, IOException {
        Integer innerCounter = 0;
        SmppConnectionCounter connectorCounter = new SmppConnectionCounter(4);


        for(int i=0;i<1;i++){
            connectorCounter.tryAdd();
            sendSMS();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//设置日期格式
            LOGGER.info(df.format(new Date())+" Executor is starting the task by using "+connectorCounter.getConnectorId());// new Date()为获取当前系统时间
            innerCounter++;
        }
        LOGGER.info("Job Finish, Executor is using "+connectorCounter.getConnectorId()+" total run"+innerCounter);
    }


    private String getParam(String prompt, String defaultValue) {
        String value = "";
        String promptFull = prompt;
        promptFull += defaultValue == null ? "" : " [" + defaultValue + "] ";
        System.out.print(promptFull);
        try {
            value = keyboard.readLine();
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
            //event.write(e, "");
            //debug.write("Got exception getting a param. " + e);
        }
        if (value.compareTo("") == 0) {
            return defaultValue;
        } else {
            return value;
        }
    }

    public void sendSMS() throws IOException, WrongSessionStateException, TimeoutException, PDUException, NotSynchronousException {
        //systemId = getParam("systemId", systemId);
        //password = getParam("password", password);
        //host= getParam("host", host);
        //port= Integer.valueOf(getParam("port", String.valueOf(port)));
        //oa = getParam("oa", oa);
        //da = getParam("da", da);
        //content = getParam("content", content);
        //private String systemId = "cloudsmpp";

        //private String password = "cloud123";
        //private String host = "192.168.29.40";
        //private int port = 2775;
        //private String oa = "Test";
        //private String da = "85259846556";


        Session session = SmppConnection.InitConnection();
        SubmitSM request = new SubmitSM();
        request.setSourceAddr((byte) 5, (byte) 0, oa);
        request.setDestAddr((byte) 1, (byte) 1, da);
        if(content.length() < 160){
            request.setShortMessage(content);
        }else{
            ByteBuffer byteBuffer = new ByteBuffer();
            byteBuffer.appendString(content, Data.ENC_UTF8);
            request.setMessagePayload(byteBuffer);
        }
        request.setShortMessage(content);

        SubmitSMResp submitSMResp = session.submit(request);

        LOGGER.info("submitSMResp messageId: " + submitSMResp.getMessageId());
        LOGGER.info("submitSMResp debug: " + submitSMResp.debugString());
        

        if (session != null)
            session.close();

    }
}
