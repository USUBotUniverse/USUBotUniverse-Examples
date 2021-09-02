package com.simplebot.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unymira.kbot.metabot.protocol.client.MetabotProtocolClient;
import com.unymira.kbot.metabot.protocol.client.MetabotProtocolClient.MetabotProtocolException;
import com.unymira.kbot.metabot.protocol.client.request.Payload;
import com.unymira.kbot.metabot.protocol.client.request.Request;
import com.unymira.kbot.metabot.protocol.client.request.RequestRootObject;
import com.unymira.kbot.metabot.protocol.client.request.RequestType;
import com.unymira.kbot.metabot.protocol.client.request.Session;
import com.unymira.kbot.metabot.protocol.client.response.ResponseRootObject;


import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.Scanner;


public class App 
{
    int id=1;
    public static void main( String[] args ) throws URISyntaxException, MetabotProtocolException
    {
        String message;
        System.out.println( "Hello World!" );
        App app = new App();

        String uriString = app.enterUri();
        URI uri = app.buildUri(uriString);
        MetabotProtocolClient l_client = app.buildClient(uri);

        ResponseRootObject l_response = l_client.startSession();
        Session session = app.buildSession(l_response);
        
        while(session.getSessionId() != null){
            System.out.println(session.getSessionId());
            message = app.inputMessage();
            
            if(message.contains("/end")){
                System.out.println(session.getSessionId());
                l_response = l_client.finishSession();
                System.out.println("Session ended");
                session.setSessionId(null);
                System.out.println(session.getSessionId());
    
            }
            else{
                RequestRootObject l_request = app.buildRequestRootObject(l_response, session, message);
            
            
            ResponseRootObject response = l_client.communicate(l_request.getRequest().getPayload());

            message = response.getResponse().getPayload().get(0).getConversation().getBubbles().get(0).getContent();
            app.outputBotMessage(message);
            }
        }      
    }

    private String enterUri(){
        Scanner input = new Scanner(System.in);
        System.out.println("Enter your Bot-Uri: ");
        String uriString= input.nextLine();
        return uriString;
    }

    private URI buildUri(String uriString) throws URISyntaxException{
        URI uri = new URI(uriString);
        System.out.println("URI build successful");
        return uri;
    }

    private MetabotProtocolClient buildClient(URI uri){
        MetabotProtocolClient l_client = MetabotProtocolClient.newMetabotProtocolClient(uri);
        l_client.withSmallTalk( false );
        System.out.println("Client build successful");
        return l_client;
    }

    private void printResponseRootObject(ResponseRootObject l_response){
        try {
            System.out.println( new ObjectMapper().writeValueAsString(l_response) );
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    private void printRequestRootObject(RequestRootObject l_request){
        try {
            System.out.println( new ObjectMapper().writeValueAsString(l_request) );
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    private Session buildSession(ResponseRootObject l_response){
        Session l_session = new Session();
        l_session.withSessionId(l_response.getSessionAttributes().getSessionId());
        return l_session;
    }

    private RequestRootObject buildRequestRootObject(ResponseRootObject p_response, Session l_session, String message){
 

        Payload l_payload = new Payload().withType("message");
		l_payload.setText(message);

        Request l_request = new Request().withType(RequestType.MESSAGE)

				.withRequestId(String.valueOf(id))
		        .withTimestamp(OffsetDateTime.now())
				.withSmallTalk(Boolean.valueOf("true"))
		        .withPayload(l_payload);
            id++;

		return new RequestRootObject().withSession(l_session).withRequest(l_request);
    }

    private String inputMessage(){
        Scanner input = new Scanner(System.in);
        System.out.println("Enter your message: ");
        System.out.print("You: ");
        String message= input.nextLine();
        return message;
    } 

    private void outputBotMessage(String botMessage){;
        System.out.println("Bot: "+botMessage);
    }

}
