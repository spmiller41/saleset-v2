package com.saleset.integration.shorten;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlShortenerImpl implements UrlShortener {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerImpl.class);

    private final TinyUrlShortenService tinyUrlService;
    private final RebrandlyUrlShortenService rebrandlyUrlService;

    @Autowired
    public UrlShortenerImpl(TinyUrlShortenService tinyUrlService, RebrandlyUrlShortenService rebrandlyUrlService) {
        this.tinyUrlService = tinyUrlService;
        this.rebrandlyUrlService = rebrandlyUrlService;
    }

    /**
     * Attempts to shorten the given URL using TinyURL first.
     * Falls back to Rebrandly if TinyURL fails.
     * If both fail, returns the original URL.
     *
     * @param longUrl The full URL to be shortened.
     * @param context A string indicating what the URL is used for (used for logging).
     * @return A shortened version of the URL, or the original if shortening fails.
     */
    @Override
    public String shorten(String longUrl, String context) {
        try {
            return tinyUrlService.create(longUrl);
        } catch (Exception ex) {
            logger.warn("TinyURL failed for {}: {}", context, ex.getMessage());
            try {
                return rebrandlyUrlService.create(longUrl);
            } catch (Exception exc) {
                logger.error("Rebrandly failed for {}: {}", context, exc.getMessage());
                return longUrl;
            }
        }
    }

}
