package com.jobayer.springbootlibrary.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;

public class ExtractJWT {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String payloadJWTExtraction(String token, String extraction) {
        
        try {
            // Remove Bearer prefix if present
            token = token.replace("Bearer ", "");

            // Split the JWT into its three parts
            String[] chunks = token.split("\\.");
            if (chunks.length < 2) {
                return null;
            }
            
            // Decode the payload (second part)
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));

            // Parse JSON using Jackson
            JsonNode jsonNode = objectMapper.readTree(payload);
            
            // Remove quotes from extraction key if present (e.g., "\"sub\"" -> "sub")
            String cleanKey = extraction.replace("\"", "");
            
            // Extract the value
            if (jsonNode.has(cleanKey)) {
                return jsonNode.get(cleanKey).asText();
            }
            
        } catch (Exception e) {
            // Log error but don't spam console
            // System.err.println("Error extracting JWT payload: " + e.getMessage());
        }

        return null;
    }
}
