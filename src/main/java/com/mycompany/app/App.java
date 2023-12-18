package com.mycompany.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Sign;

/**
 * Hello world!
 *
 */
public class App 
{
    private static String tokenAddress ="0x"; // change this with the token address the user wants to claim
    private static String destinationContract = "0x"; // change this with the contract that will call be interacted with
    private static String amount = "1";
    private static String executorAddress ="0x"; // change this with your metamask account
    private static String signerPK = "";
    private static String nonce = "1";


    public static void main( String[] args ) throws IOException {
        Credentials signer = Credentials.create(signerPK);
        System.out.println("signer " + signer.getAddress());

        String data = getDataJsonString();
        byte[] signature = getDataSignature(data, signer);
        System.out.println( "Signature " + bytesToHex(signature) );
    }

    public static String getDataJsonString() {
        try {
            String jsonMessageString = Files.readString(Paths.get("src/main/resources/sampleData.json").toAbsolutePath());

            JSONObject jsonObject = new JSONObject(jsonMessageString);
            jsonObject.getJSONObject("domain").put("verifyingContract", destinationContract);
            jsonObject.getJSONObject("message").put("tokenAddress", tokenAddress);
            jsonObject.getJSONObject("message").put("amount", amount);
            jsonObject.getJSONObject("message").put("user", executorAddress);
            jsonObject.getJSONObject("message").put("nonce", nonce);

            String modifiedJsonString = jsonObject.toString();
            return modifiedJsonString;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static byte[] getDataSignature(String dataJsonString, Credentials signer) throws IOException {
        byte[] retval = new byte[65];
        Sign.SignatureData signature = Sign.signTypedData(dataJsonString, signer.getEcKeyPair());

        System.arraycopy(signature.getR(), 0, retval, 0, 32);
        System.arraycopy(signature.getS(), 0, retval, 32, 32);
        System.arraycopy(signature.getV(), 0, retval, 64, 1);
        return retval;
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
