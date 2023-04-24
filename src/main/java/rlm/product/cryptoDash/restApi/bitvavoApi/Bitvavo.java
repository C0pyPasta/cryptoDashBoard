package rlm.product.cryptoDash.restApi.bitvavoApi;

import java.net.*;
import javax.net.ssl.*;
import java.io.*;
import org.json.*;
import java.util.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import org.apache.commons.io.IOUtils;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Bitvavo {
  String apiKey;
  String apiSecret;
  String restUrl;
  String wsUrl;
  boolean debugging = true;
  int window;
  volatile int rateLimitRemaining = 1000;
  volatile long rateLimitReset = 0;
  volatile boolean rateLimitThreadStarted = false;

  public Bitvavo(JSONObject options) {
    JSONArray keys = options.names();
    boolean apiKeySet = false;
    boolean apiSecretSet = false;
    boolean windowSet = false;
    boolean debuggingSet = false;
    boolean restUrlSet = false;
    boolean wsUrlSet = false;
    for (int i = 0; i < keys.length(); ++i) {
      String key = keys.getString(i);
      if(key.toLowerCase().equals("apikey")) {
        this.apiKey = options.getString(key);
        apiKeySet = true;
      } else if(key.toLowerCase().equals("apisecret")) {
        this.apiSecret = options.getString(key);
        apiSecretSet = true;
      } else if(key.toLowerCase().equals("accesswindow")) {
        this.window = options.getInt(key);
        windowSet = true;
      } else if(key.toLowerCase().equals("debugging")) {
        this.debugging = options.getBoolean(key);
        debuggingSet = true;
      } else if(key.toLowerCase().equals("resturl")) {
        this.restUrl = options.getString(key);
        restUrlSet = true;
      } else if(key.toLowerCase().equals("wsurl")) {
        this.wsUrl = options.getString(key);
        wsUrlSet = true;
      }
    }
    if (!apiKeySet) {
      this.apiKey = "";
    }
    if (!apiSecretSet) {
      this.apiSecret = "";
    }
    if (!windowSet) {
      this.window = 10000;
    }
    if (!debuggingSet) {
      this.debugging = false;
    }
    if (!restUrlSet) {
      this.restUrl = "https://api.bitvavo.com/v2";
    }
    if (!wsUrlSet) {
      this.wsUrl = "wss://ws.bitvavo.com/v2/";
    }
  }

  private String createPostfix(JSONObject options) {
    ArrayList<String> array = new ArrayList<>();
    Iterator<?> keys = options.keys();
    while(keys.hasNext()) {
      String key = (String) keys.next();
      array.add(key + "=" + options.get(key).toString());
    }
    String params = String.join("&", array);
    if(options.length() > 0) {
      params = "?" + params;
    }
    return params;
  }

  public String createSignature(long timestamp, String method, String urlEndpoint, JSONObject body) {
    if(this.apiSecret == null || this.apiKey == null) {
      errorToConsole("The API key or secret has not been set. Please pass the key and secret when instantiating the bitvavo object.");
      return "";
    }
    try {
      String result = String.valueOf(timestamp) + method + "/v2" + urlEndpoint;
      if(body.length() != 0) {
        result = result + bodyToJsonString(body);
      }
      Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
      SecretKeySpec secret_key = new SecretKeySpec(this.apiSecret.getBytes("UTF-8"), "HmacSHA256");
      sha256_HMAC.init(secret_key);
      return new String(Hex.encodeHex(sha256_HMAC.doFinal(result.getBytes("UTF-8"))));
    }
    catch(Exception ex) {
      errorToConsole("Caught exception in createSignature " + ex);
      return "";
    }
  }

  public String bodyToJsonString(JSONObject body) {
    Iterator<String> keys = body.keys();
    DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    df.setMaximumFractionDigits(340);
    String jsonString = "{";
    Boolean first = true;

    while(keys.hasNext()) {
      String key = keys.next();
      if (!first) {
        jsonString = jsonString + ",";
      } else {
        first = false;
      }

      if ((body.get(key) instanceof Double) || (body.get(key) instanceof Float)) {
        jsonString = jsonString + "\"" + key + "\":" + df.format(body.get(key));
      } else if ((body.get(key) instanceof Integer) || (body.get(key) instanceof Long)) {
        jsonString = jsonString + "\"" + key + "\":" + body.get(key).toString();
      } else if (body.get(key) instanceof Boolean) {
        jsonString = jsonString + "\"" + key + "\":" + body.get(key);
      } else {
        jsonString = jsonString + "\"" + key + "\":\"" + body.get(key).toString() + "\"";
      }
    }
    jsonString = jsonString + "}";
    return jsonString;
  }

  public void debugToConsole(String message) {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    if(this.debugging) {
      System.out.println(sdf.format(cal.getTime()) + " DEBUG: " + message);
    }
  }

  public void errorToConsole(String message) {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    System.out.println(sdf.format(cal.getTime()) + " ERROR: " + message);
  }

  public void errorRateLimit(JSONObject response) {
    if (response.getInt("errorCode") == 105) {
      rateLimitRemaining = 0;
      String message = response.getString("error");
      String placeHolder = message.split(" at ")[1].replace(".", "");
      rateLimitReset = Long.parseLong(placeHolder);
      if (!rateLimitThreadStarted) {
        new Thread(new Runnable() {
          public void run() {
            try {
              long timeToWait = rateLimitReset - System.currentTimeMillis();
              rateLimitThreadStarted = true;
              debugToConsole("We are waiting for " + ((int) timeToWait / (int) 1000) + " seconds, untill the rate limit ban will be lifted.");
              Thread.sleep(timeToWait);
            } catch (InterruptedException ie) {
              errorToConsole("Got interrupted while waiting for the rate limit ban to be lifted.");
            }
            rateLimitThreadStarted = false;
            if (System.currentTimeMillis() >= rateLimitReset) {
              debugToConsole("Rate limit ban has been lifted, resetting rate limit to 1000.");
              rateLimitRemaining = 1000;
            }
          }
        }).start();
      }
    }
  }

  public void updateRateLimit(Map<String,List<String>> response) {
    String remainingHeader = response.get("bitvavo-ratelimit-remaining").get(0);
    String resetHeader = response.get("bitvavo-ratelimit-resetat").get(0);
    if(remainingHeader != null) {
      rateLimitRemaining = Integer.parseInt(remainingHeader);
    }
    if(resetHeader != null) {
      rateLimitReset = Long.parseLong(resetHeader);
      if (!rateLimitThreadStarted) {
        new Thread(new Runnable() {
          public void run() {
            try {
              long timeToWait = rateLimitReset - System.currentTimeMillis();
              rateLimitThreadStarted = true;
              debugToConsole("We started a thread which waits for " + ((int) timeToWait / (int) 1000) + " seconds, untill the rate limit will be reset.");
              Thread.sleep(timeToWait);
            } catch (InterruptedException ie) {
              errorToConsole("Got interrupted while waiting for the rate limit to be reset.");
            }
            rateLimitThreadStarted = false;
            if (System.currentTimeMillis() >= rateLimitReset) {
              debugToConsole("Resetting rate limit to 1000.");
              rateLimitRemaining = 1000;
            }
          }
        }).start();
      }
    }
  }

  public int getRemainingLimit() {
    return rateLimitRemaining;
  }

  public JSONObject publicRequest(String urlString, String method, JSONObject data) {
    try {
      URL url = new URL(urlString);
      HttpsURLConnection httpsCon = (HttpsURLConnection) url.openConnection();
      httpsCon.setRequestMethod(method);
      if (this.apiKey != "") {
        long timestamp = System.currentTimeMillis();
        String signature = createSignature(timestamp, method, urlString.replace(this.restUrl, ""), new JSONObject());
        httpsCon.setRequestProperty("bitvavo-access-key", this.apiKey);
        httpsCon.setRequestProperty("bitvavo-access-signature", signature);
        httpsCon.setRequestProperty("bitvavo-access-timestamp", String.valueOf(timestamp));
        httpsCon.setRequestProperty("bitvavo-access-window", String.valueOf(this.window));
        httpsCon.setRequestProperty("content-type", "application/json");
      }
      int responseCode = httpsCon.getResponseCode();
      InputStream inputStream;
      if(responseCode == 200) {
        inputStream = httpsCon.getInputStream();
        updateRateLimit(httpsCon.getHeaderFields());
      }
      else {
        inputStream = httpsCon.getErrorStream();
      }
      StringWriter writer = new StringWriter();
      IOUtils.copy(inputStream, writer, "utf-8");
      String result = writer.toString();

      JSONObject response = new JSONObject(result);
      if (result.contains("errorCode")) {
        errorRateLimit(response);
      }
      return response;
    }
    catch(IOException ex) {
      errorToConsole("Caught IOerror, " + ex);
    }
    return new JSONObject("{}");
  }

  public JSONArray publicRequestArray(String urlString, String method, JSONObject data) {
    try {
      URL url = new URL(urlString);
      HttpsURLConnection httpsCon = (HttpsURLConnection) url.openConnection();
      httpsCon.setRequestMethod(method);
      if (this.apiKey != "") {
        long timestamp = System.currentTimeMillis();
        String signature = createSignature(timestamp, method, urlString.replace(this.restUrl, ""), new JSONObject());
        httpsCon.setRequestProperty("bitvavo-access-key", this.apiKey);
        httpsCon.setRequestProperty("bitvavo-access-signature", signature);
        httpsCon.setRequestProperty("bitvavo-access-timestamp", String.valueOf(timestamp));
        httpsCon.setRequestProperty("bitvavo-access-window", String.valueOf(this.window));
        httpsCon.setRequestProperty("content-type", "application/json");
      }
      int responseCode = httpsCon.getResponseCode();
      InputStream inputStream;
      if(responseCode == 200) {
        inputStream = httpsCon.getInputStream();
        updateRateLimit(httpsCon.getHeaderFields());
      }
      else {
        inputStream = httpsCon.getErrorStream();
      }

      StringWriter writer = new StringWriter();
      IOUtils.copy(inputStream, writer, "utf-8");
      String result = writer.toString();
      if(result.indexOf("error") != -1) {
        errorRateLimit(new JSONObject(result));
        return new JSONArray("[" + result + "]");
      }
      debugToConsole("FULL RESPONSE: " + result);

      JSONArray response = new JSONArray(result);
      return response;
    }
    catch(MalformedURLException ex) {
      errorToConsole("Caught Malformed Url error, " + ex);
    }
    catch(IOException ex) {
      errorToConsole("Caught IOerror, " + ex);
    }
    return new JSONArray("[{}]");
  }

  /**
   * Returns the current time in unix time format (milliseconds since 1 jan 1970)
   * @return JSONObject response, get time through response.getLong("time")
   */
  public JSONObject time() {
    return publicRequest((this.restUrl + "/time"), "GET", new JSONObject());
  }

  /**
   * Returns the ticker price
   * @param options optional parameters: market
   * @return JSONArray response, get individual prices by iterating over array: response.getJSONObject(index)
   */
  public JSONArray tickerPrice(JSONObject options) {
    String postfix = createPostfix(options);
    if(options.has("market")) {
      JSONArray returnArray = new JSONArray();
      returnArray.put(publicRequest((this.restUrl + "/ticker/price" + postfix), "GET", new JSONObject()));
      return returnArray;
    } else {
      return publicRequestArray((this.restUrl + "/ticker/price" + postfix), "GET", new JSONObject());
    }
  }
}