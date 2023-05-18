package rlm.product.cryptoDash.restApi.bitvavoApi;

import org.json.*;

/*
* Price ticker for (near) real time values of crypto you
* are able to choose from the selection menu on the website
* */

public class BitvavoPriceTicker {
  private static double btcPrice;
  private static double adaPrice;
  private static double xtzPrice;
  private static double maticPrice;
  private static double ethPrice;
  private static double vetPrice;
  private static double algoPrice;
  private static double ontPrice;
  private static double solPrice;
  private static double linkPrice;
  private static double icxPrice;


  public void startTicking(){
    // Create Bitvavo object
    Bitvavo bitvavo = null;
    try {
      bitvavo = new Bitvavo(new JSONObject("{" +
          "RESTURL: 'https://api.bitvavo.com/v2'," +
          "ACCESSWINDOW: 10000, " +
          "DEBUGGING: false }"));
    } catch (JSONException e) {
      e.printStackTrace();
    }
    // testREST with the Bitvavo object
    this.testREST(bitvavo);
  }

  public void testREST(Bitvavo bitvavo) {
    JSONArray response;

    // Times you can send requests are capped at a thousand per second(?)
    int remaining = bitvavo.getRemainingLimit();
    System.out.println("remaining limit is " + remaining);

    response = bitvavo.tickerPrice(new JSONObject());
    for(int i = 0; i < response.length(); i ++) {
      String market = response.getJSONObject(i).get("market").toString();
      String price = response.getJSONObject(i).get("price").toString();

      // Same choices and order as selection menu on the website
      switch (market) {
        case "BTC-EUR":
          this.btcPrice = Double.parseDouble(price);
          break;
        case "ADA-EUR":
          this.adaPrice = Double.parseDouble(price);
          break;
        case "XTZ-EUR":
          this.xtzPrice = Double.parseDouble(price);
          break;
        case "MATIC-EUR":
          this.maticPrice = Double.parseDouble(price);
          break;
        case "ETH-EUR":
          this.ethPrice = Double.parseDouble(price);
          break;
        case "VET-EUR":
          this.vetPrice = Double.parseDouble(price);
          break;
        case "ALGO-EUR":
          this.algoPrice = Double.parseDouble(price);
          break;
        case "SOL-EUR":
          this.ontPrice = Double.parseDouble(price);
          break;
        case "ONT-EUR":
          this.solPrice = Double.parseDouble(price);
          break;
        case "LINK-EUR":
          this.linkPrice = Double.parseDouble(price);
          break;
        case "ICX-EUR":
          this.icxPrice = Double.parseDouble(price);
          break;
      }
    }
  }

  // Getters for actively calculating values and updating the database periodically
  public double getBtcPrice() {
    return btcPrice;
  }

  public double getAdaPrice() {
    return adaPrice;
  }

  public double getXtzPrice() {
    return xtzPrice;
  }

  public double getMaticPrice() {
    return maticPrice;
  }

  public double getEthPrice() {
    return ethPrice;
  }

  public double getVetPrice() {
    return vetPrice;
  }

  public double getAlgoPrice() {
    return algoPrice;
  }

  public double getOntPrice() {
    return ontPrice;
  }

  public double getSolPrice() {
    return solPrice;
  }

  public double getLinkPrice() {
    return linkPrice;
  }

  public double getIcxPrice() {
    return icxPrice;
  }
}