package network;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.*;
import java.util.*;

import static java.util.Collections.sort;

/**
 * Created by Rafal on 2016-12-29.
 */
public class User {

    static Integer K = 5;
    static BigInteger Q = new BigInteger("245733731021662473457347562398375784363");
    static BigInteger G = new BigInteger("192131622250640046321617811902039558669");
    PrivateKey privateKey;
    Map<String, PublicKey> publicKeyMap = new HashMap<>();
    String userID = new String();
    String r = new String();
    List<String> possibleUsers = new ArrayList<>();
    List<Nonce> nonceList = new ArrayList<>();
    Nonce myNonce;
    BigInteger Z;
    Random random = new Random();
    List<BigInteger> otherZ = new ArrayList<>();
    BigInteger computeKey = new BigInteger("1");
    Key sessionKey;

    public User() {
        for (int i = 0; i < K; i++) {
            Integer n = random.nextInt(10);
            userID += n.toString();
        }
    }

    public String getR() {
        return r;
    }

    public void setR() {

        for (int i = 0; i < K; i++) {
            Integer n = random.nextInt(2);
            r += n.toString();
        }
        myNonce = new Nonce(userID, r);
    }

    public Map<String, PublicKey> getPublicKeyMap() {
        return publicKeyMap;
    }

    public void setPublicKeyMap(Map<String, PublicKey> publicKeyMap) {
        this.publicKeyMap = publicKeyMap;
    }


    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public List<Nonce> getNonceList() {
        return nonceList;
    }

    public void setNonceList(List<Nonce> nonceList) {
        this.nonceList = nonceList;
    }

    public List<String> getPossibleUsers() {
        return possibleUsers;
    }

    public void setPossibleUsers(List<String> possibleUsers) {
        this.possibleUsers = possibleUsers;
    }

    public void addNonce(String message) {
        int rStart = message.length() - K;
        String nonceR = message.substring(rStart, message.length());
        String nonceUID = message.substring(0, rStart - 1);

        Nonce nonce = new Nonce(nonceUID, nonceR);
        nonceList.add(nonce);
    }

    //STEP 2
    public String computeSignature() {
        String message = new String();


        boolean duplicate = false;
        Random rnd = new Random();
        BigInteger s = new BigInteger(16, rnd);


        while (!duplicate) {
            if (s.compareTo(Q) == -1) {
                duplicate = true;
            }
            else
            s = new BigInteger(16, rnd);

        }      //Compute Z
        Z = new BigInteger("1");

        Z = G.modPow(s, Q);






        String sigma = "1" + (Z);
        Collections.sort(nonceList);
        for (Nonce nonce : nonceList) {
            sigma += nonce.toString();
        }
        try {
            otherZ.add(Z);
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(privateKey, new SecureRandom());
            byte[] arr = sigma.getBytes();
            signature.update(arr);
            byte[] sigBytes = signature.sign();
            String sig = new String(Base64.getEncoder().encode(sigBytes));

            message += userID + "1" + Z.toString() + sig;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    //STEP 3A
    //TODO:check if it works!!
    public void recieveSignature(String message) {
        boolean firstCondition = false, secondCondition = false, thirdCondition = false;
        String idUser = new String();
        int zStartIndex = 0, zEndIndex = 0;
        char one = '1';
        String Zj = new String();
        for (String userId : possibleUsers) {
            if (userId.equals(message.substring(0, userId.length())) && (message.charAt(userId.length()) == one)) {
                firstCondition = true;
                secondCondition = true;
                idUser = userId;
                zStartIndex = userId.length() + 1;
                zEndIndex = message.length() - 172;
                Zj = message.substring(zStartIndex, zEndIndex);
            }
        }
        try {

            Collections.sort(nonceList);
            String nonceStr = new String();
            for (Nonce nonce : nonceList) {
                nonceStr += nonce.toString();
            }

            String verify = "1" + Zj + nonceStr;
            Signature signature = Signature.getInstance("SHA1withRSA");
            PublicKey pubKey = publicKeyMap.get(idUser);
            signature.initVerify(pubKey);
            String sigma = message.substring(zEndIndex);

            byte[] arr = Base64.getDecoder().decode(sigma.getBytes());
            signature.update(verify.getBytes());
            boolean result = signature.verify(arr);

            otherZ.add(new BigInteger(Zj));


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //STEP 3B
    public String sendX() {
        //
        String X = getX(Z);
        String message = new String();
        Collections.sort(nonceList);
        String nonceStr = new String();
        for (Nonce nonce : nonceList) {
            nonceStr += nonce.toString();
        }
        String sig = "2" + X + nonceStr;

        try {

            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(privateKey, new SecureRandom());
            byte[] toSign = sig.getBytes();
            signature.update(toSign);
            byte[] signed = signature.sign();
            String sgm1 = new String(Base64.getEncoder().encode(signed));
            message = userID + "2" + X + sgm1;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    //TODO: also test
    public void reciveX(String message) {
        boolean firstCondition = false, secondCondition = false, thirdCondition = false;
        String idUser = new String();
        int xStartIndex = 0, xEndIndex = 0;
        char two = '2';
        String Xj = new String();
        for (String userId : possibleUsers) {
            if (userId.equals(message.substring(0, userId.length())) && (message.charAt(userId.length()) == two)) {
                firstCondition = true;
                secondCondition = true;
                idUser = userId;

                xStartIndex = userId.length() + 1;
                xEndIndex = message.length() - 172;
                Xj = message.substring(xStartIndex, xEndIndex);
            }
        }
        try {
            String nonceStr = new String();
            Collections.sort(nonceList);
            for (Nonce nonce : nonceList) {
                nonceStr += nonce.toString();
            }
            String verify = "2" + Xj + nonceStr;

            Signature signature = Signature.getInstance("SHA1withRSA");
            PublicKey pubKey = publicKeyMap.get(idUser);
            signature.initVerify(pubKey);
            String sigma = message.substring(xEndIndex);

            byte[] arr = Base64.getDecoder().decode(sigma.getBytes());

            signature.update(verify.getBytes());
            boolean result = signature.verify(arr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //TODO: ask if it is correct way, and then how to encrypt and decrypt message
    public void computeSessionKey() {
        BigInteger n = new BigInteger("0");
        Collections.sort(otherZ);
        for (int i = 0; i < otherZ.size() - 1; i++) {
            BigInteger si1 = getSi(otherZ.get(i));
            BigInteger si2 = getSi(otherZ.get(i + 1));
            n = n.add(si1.multiply(si2));
        }
        BigInteger si1 = getSi(otherZ.get(0));
        BigInteger si2 = getSi(otherZ.get(otherZ.size() - 1));
        n = n.add(si1.multiply(si2));
        // n = n.mod(Q);
        BigInteger bi = new BigInteger("1");


        bi = G.modPow(n, Q);

        System.out.println(userID + "sessionKey: " + bi);

        if(bi.bitLength() > 16){
            bi = bi.shiftRight(1);
        }

        sessionKey = new SecretKeySpec(bi.toByteArray(), "AES");


    }


    public BigInteger getSi(BigInteger Zi) {
        BigInteger OZ = G.mod(Q);
        boolean pow = false;
        BigInteger si = new BigInteger("2");

        while(!pow){
            if(OZ.compareTo(Zi) == 0){
                pow=true;
                si = si.subtract(new BigInteger("1"));

            }
            else {
                OZ = G.modPow(si,Q);
                si = si.add(new BigInteger("1"));
            }
        }
        return si;
    }

    public String getX(BigInteger k) {
        //TODO: check if it is correct way to compute X
        sort(otherZ);
        int index = otherZ.indexOf(k);
        int prevIndex = index - 1;
        int aftIndex = index + 1;
        if (prevIndex < 0) {
            prevIndex = otherZ.size() - 1;
        }
        if (aftIndex > otherZ.size() - 1) {
            aftIndex = 0;
        }
        BigInteger Z1 = otherZ.get(prevIndex);
        BigInteger Z2 = otherZ.get(aftIndex);

        BigInteger s2 = getSi(Z2);
        BigInteger n = (Q.subtract(new BigInteger("1"))).subtract(s2);

        if (n.compareTo(BigInteger.valueOf(0)) == 0) {
            n = n.add(Q);
        }

        BigInteger otherZ2 = new BigInteger("1");
        otherZ2 = G.modPow(n,Q);

        BigInteger Xi = (Z1.multiply(otherZ2)).modPow(new BigInteger(r),Q);

        return Xi.toString();
    }

    public int getIntR() {
        char[] cA = r.toCharArray();
        int result = 0;
        for (int i = cA.length - 1; i >= 0; i--) {
            if (cA[i] == '1') result += Math.pow(2, cA.length - i - 1);
        }
        return result;
    }

    public String encryptMessage(String message) {
        String encryptedValue = null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sessionKey);
            byte[] encrytionValue = cipher.doFinal(message.getBytes());
            encryptedValue = new BASE64Encoder().encode(encrytionValue);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedValue;
    }

    public String decryptMessage(String message) {
        String decryptedValue = null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sessionKey);
            byte[] decodeValue = new BASE64Decoder().decodeBuffer(message);
            byte[] decodedValue = cipher.doFinal(decodeValue);
            decryptedValue = new String(decodedValue);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedValue;
    }
}
