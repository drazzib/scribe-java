package org.scribe.builder.api;

import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.oauth.OAuth20ServiceImpl;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

public class LinkedInApi20 extends DefaultApi20
{

  private static final String AUTHORIZE_URL = "https://www.linkedin.com/uas/oauth2/authorization?response_type=code&client_id=%s&redirect_uri=%s&state=STATE";

  private static final String SCOPED_AUTHORIZE_URL = AUTHORIZE_URL + "&scope=%s";

  @Override
  public AccessTokenExtractor getAccessTokenExtractor()
  {
    return new JsonTokenExtractor();
  }

  @Override
  public String getAccessTokenEndpoint()
  {
    return "https://www.linkedin.com/uas/oauth2/accessToken?grant_type=authorization_code";
  }

  @Override
  public String getAuthorizationUrl(OAuthConfig config)
  {
    Preconditions.checkValidUrl(config.getCallback(), "Must provide a valid url as callback. LinkedIn does not support OOB");

    // Append scope if present
    if (config.hasScope())
    {
      return String.format(SCOPED_AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()), OAuthEncoder.encode(config.getScope()));
    }
    else
    {
      return String.format(AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
    }
  }

  /**
   * {@inheritDoc}
   */
  public OAuthService createService(OAuthConfig config)
  {
    return new LinkedInServiceImpl(config);
  }

  public class LinkedInServiceImpl extends OAuth20ServiceImpl
  {

    /**
     * Default constructor
     * 
     * @param config OAuth 2.0 configuration param object
     */
    public LinkedInServiceImpl(OAuthConfig config)
    {
      super(LinkedInApi20.this, config);
    }

    /**
     * {@inheritDoc}
     */
    public void signRequest(Token accessToken, OAuthRequest request)
    {
      request.addQuerystringParameter("oauth2_access_token", accessToken.getToken());
    }
  }

}
