package uk.ac.ebi.spot.security.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @author Simon Jupp
 * @since 22/09/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 *
 * A utility class of hashing algorithms
 *
 */
public class HashUtils {

    public static final EncodingAlgorithm DEFAULT_ENCODING = EncodingAlgorithm.MD5;
    private static final String HEX_CHARACTERS = "0123456789ABCDEF";

    private static Logger log = LoggerFactory.getLogger(HashUtils.class);

    protected static Logger getLog() {
        return log;
    }

    public static MessageDigest generateMessageDigest() {
        return generateMessageDigest(DEFAULT_ENCODING);
    }

    public static MessageDigest generateMessageDigest(EncodingAlgorithm algorithm) {
        try {
            return MessageDigest.getInstance(algorithm.getAlgorithmName());
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(
                    algorithm.getAlgorithmName() + " algorithm not available, this is required to generate ID");
        }
    }

    public static String generateHashEncodedID(String... contents) {
        return generateHashEncodedID(DEFAULT_ENCODING, contents);
    }

    public static String generateHashEncodedID(EncodingAlgorithm algorithm, String... contents) {
        // acquire a message digest for the given algorithm and encode
        return generateHashEncodedID(generateMessageDigest(algorithm), contents);
    }

    public static String generateHashEncodedID(MessageDigest messageDigest, String... contents) {
        return generateHashEncodedID(messageDigest, true, contents);
    }

    public static String generateHashEncodedID(MessageDigest messageDigest, boolean sortContent, String... contents) {
        if (sortContent) {
            Arrays.sort(contents);
        }

        StringBuilder idContent = new StringBuilder();
        for (String s : contents) {
            idContent.append(s);
        }
        try {
            // encode the content using the supplied message digest
            byte[] digest = messageDigest.digest(idContent.toString().getBytes("UTF-8"));

            // now translate the resulting byte array to hex
            String idKey = getHexRepresentation(digest);

            getLog().trace("Generated new " + messageDigest.getAlgorithm() + " based, hex encoded ID string: " + idKey);
            return idKey;
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported!");
        }
    }

    private static String getHexRepresentation(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEX_CHARACTERS.charAt((b & 0xF0) >> 4)).append(HEX_CHARACTERS.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    public enum EncodingAlgorithm {
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA256("SHA-256");

        private final String algorithm;

        private EncodingAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        public String getAlgorithmName() {
            return algorithm;
        }
    }
}
