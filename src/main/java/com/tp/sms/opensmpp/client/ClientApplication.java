package com.tp.sms.opensmpp.client;

import com.tp.sms.opensmpp.client.service.MultiTaskClient;
import com.tp.sms.opensmpp.client.service.OpenSmppClient;
import org.smpp.NotSynchronousException;
import org.smpp.TimeoutException;
import org.smpp.WrongSessionStateException;
import org.smpp.pdu.PDUException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) throws PDUException, TimeoutException, WrongSessionStateException, IOException, NotSynchronousException {
        SpringApplication.run(ClientApplication.class, args);
//        OpenSmppClient client = new OpenSmppClient();
//        client.sendSMS();
        MultiTaskClient client = new MultiTaskClient();
        client.sendMulti();
    }

}
