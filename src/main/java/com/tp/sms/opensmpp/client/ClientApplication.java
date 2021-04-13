package com.tp.sms.opensmpp.client;

import com.tp.sms.opensmpp.client.service.MultiTaskClient;
import com.tp.sms.opensmpp.client.service.OpenSmppClient;
import org.smpp.NotSynchronousException;
import org.smpp.TimeoutException;
import org.smpp.WrongSessionStateException;
import org.smpp.pdu.PDUException;
import org.smpp.util.NotEnoughDataInByteBufferException;
import org.smpp.util.TerminatingZeroNotFoundException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Date;

@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) throws PDUException, TimeoutException, WrongSessionStateException, IOException, NotSynchronousException, InterruptedException, NotEnoughDataInByteBufferException, TerminatingZeroNotFoundException {
        SpringApplication.run(ClientApplication.class, args);
        OpenSmppClient client = new OpenSmppClient();

        client.sendSMS();
        while(1 == 1){
            System.out.println("Thread Wake at"+new Date());
            Thread.sleep(3000);
        }
//        MultiTaskClient client = new MultiTaskClient();
//        client.sendMulti();
    }

}
