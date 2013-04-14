package org.scribe.examples;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi20;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

import java.util.Scanner;

public class LinkedIn20Example
{
  // Replace these with your own api key and secret
  private static final String API_KEY = "your api key";

  private static final String API_SECRET = "your api secret";

  public static void main(String[] args)
  {
    OAuthService service = new ServiceBuilder()
        .provider(LinkedInApi20.class)
        .apiKey(API_KEY)
        .apiSecret(API_SECRET)
        .callback("http://localhost:8000")
        .scope("w_messages rw_groups rw_nus r_contactinfo r_network r_fullprofile r_emailaddress")
        .build();
    Scanner in = new Scanner(System.in);

    System.out.println("=== LinkedIn's OAuth Workflow ===");
    System.out.println();

    // Obtain the Authorization URL
    System.out.println("Fetching the Authorization URL...");
    String authorizationUrl = service.getAuthorizationUrl(Token.empty());
    System.out.println("Got the Authorization URL!");
    System.out.println("Now go and authorize Scribe here:");
    System.out.println(authorizationUrl);
    System.out.println("And paste the authorization code here");
    System.out.print(">>");
    Verifier verifier = new Verifier(in.nextLine());
    System.out.println();

    // Trade the Request Token and Verfier for the Access Token
    System.out.println("Trading the Request Token for an Access Token...");
    Token accessToken = service.getAccessToken(Token.empty(), verifier);
    System.out.println("Got the Access Token!");
    System.out.println("(if your curious it looks like this: " + accessToken + " )");
    System.out.println();

    /**************************
     * 
     * Querying the LinkedIn API
     * 
     **************************/

    OAuthRequest request;
    String url;
    Response response;

    System.out.println("********Get the profile in JSON********");
    // This basic call profile in JSON format
    // You can read more about JSON here http://json.org
    url = "https://api.linkedin.com/v1/people/~";
    request = new OAuthRequest(Verb.GET, url);
    request.addHeader("x-li-format", "json");
    service.signRequest(accessToken, request);
    System.out.println(request.getCompleteUrl());
    response = request.send();
    System.out.println(response.getBody());
    System.out.println();
    System.out.println();
  }

}
