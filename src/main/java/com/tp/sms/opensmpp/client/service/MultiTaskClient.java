package com.tp.sms.opensmpp.client.service;

import com.tp.sms.opensmpp.client.connector.SmppConnection;
import com.tp.sms.opensmpp.client.connector.SmppConnectionCounter;
import org.smpp.*;
import org.smpp.pdu.*;

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

    static BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

    private String oa = "Test";
    private String da = "85259846556";
    private String content = "SMPP Client By Java";

    public void sendMulti() throws PDUException, NotSynchronousException, TimeoutException, WrongSessionStateException, IOException {
        Integer innerCounter = 0;
        SmppConnectionCounter connectorCounter = new SmppConnectionCounter(4);


        for(int i=0;i<10;i++){
            connectorCounter.tryAdd();
            sendSMS();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");//设置日期格式
            System.out.println(df.format(new Date())+" Executor is starting the task by using "+connectorCounter.getConnectorId());// new Date()为获取当前系统时间
            innerCounter++;
        }
        System.out.println("Job Finish, Executor is using "+connectorCounter.getConnectorId()+" total run"+innerCounter);
    }


    private String getParam(String prompt, String defaultValue) {
        String value = "";
        String promptFull = prompt;
        promptFull += defaultValue == null ? "" : " [" + defaultValue + "] ";
        System.out.print(promptFull);
        try {
            value = keyboard.readLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
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
        request.setShortMessage(content);

        SubmitSMResp submitSMResp = session.submit(request);

        System.out.println("submitSMResp messageId: " + submitSMResp.getMessageId());
        System.out.println("submitSMResp debug: " + submitSMResp.debugString());
        PDU pdu = session.receive(300000);

        if (pdu instanceof DeliverSM) {
            DeliverSM received = (DeliverSM) pdu;
            if (received.getEsmClass() == 0) {                                                          // new message
                System.out.println("RECEIVE NEW MESSAGE:" + received.debugString());
                String MSG_SENDER = received.getSourceAddr().getAddress();
                String SHORT_MSG = received.getShortMessage();
            } else {                                                                                    // delivry Repport
                System.out.println("RECEIVE NEW DELIVERED REPORT:" + received.debugString());
                String MSG_ID = (new BigInteger(received.getReceiptedMessageId(), 16)) + "";
                int MSG_STATUS = received.getMessageState();
            }
        } else {
            System.out.println("----------------- FF pdu: " + pdu.debugString());
        }

        if (session != null)
            session.close();

    }
}
