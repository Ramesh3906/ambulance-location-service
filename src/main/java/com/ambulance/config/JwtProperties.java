package com.ambulance.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ambulance.security.jwt")
public class JwtProperties {

    /** Base64 HMAC-SHA256 secret, minimum 32 bytes decoded. */
    private String secret;
    private String issuer = "ambulance-location-service";
    private long accessTokenMinutes = 30;
    private long refreshTokenDays = 30;

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
    public long getAccessTokenMinutes() { return accessTokenMinutes; }
    public void setAccessTokenMinutes(long v) { this.accessTokenMinutes = v; }
    public long getRefreshTokenDays() { return refreshTokenDays; }
    public void setRefreshTokenDays(long v) { this.refreshTokenDays = v; }
}