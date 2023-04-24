
function createCryptoDash() { 
    
    // Create Header
    var cryptoDashTableHeader = "<table class=mainTable><tr>" + 
    "<td></td>" +
    "<td id=\"cryptoDashTableDivision\"> Amount </td>" +
    "<td id=\"cryptoDashTableDivision\"> BuyPrice </td>" +
    "<td id=\"cryptoDashTableDivision\"> SellPrice </td>" +
    "<td id=\"cryptoDashTableDivision\"> total </td>" + 
    "<td id=\"cryptoDashTableDivision\"> price </td>" + 
    "<td id=\"cryptoDashTableDivision\"> dca  </td>" +
    "<td id=\"cryptoDashTableDivision\"> result </td>" + 
    "<td id=\"cryptoDashTableDivision\"> Low  </td>" +
    "<td id=\"cryptoDashTableDivision\"> High </td>" +
    "<td></td>" + 
    "</tr></table>";

    // Request all dashboard rules from the database
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if(this.readyState == 4) {
            var allDashRules = JSON.parse(this.responseText);

            var cryptoDashTable = "<table class=mainTable>";
            for(var x = 0; x < allDashRules.length; x++) {
                cryptoDashTable += "<tr id=\"cryptoDashTableRow\">" + 
                "<td id=\"cryptoDashTableDivision\">" + allDashRules[x].cryptoName + "</td>" +
                "<td id=\"cryptoDashTableDivision\">" + allDashRules[x].totalAmount + "</td>" +
                "<td id=\"cryptoDashTableDivision\">" + allDashRules[x].totalBuyPrice + "</td>" +
                "<td id=\"cryptoDashTableDivision\">" + allDashRules[x].totalValueOut + "</td>" +
                "<td id=\"cryptoDashTableDivision\">" + allDashRules[x].totalOut + "</td>" + 
                "<td id=\"cryptoDashTableDivision\">" + allDashRules[x].currentUpdatedValue + "</td>" + 
                "<td id=\"cryptoDashTableDivision\">" + allDashRules[x].dollarCostAverage + "</td>" +
                "<td id=\"cryptoDashTableDivision\">" + allDashRules[x].resultInPercentage + "</td>" + 
                "<td id=\"cryptoDashTableDivision\">" + allDashRules[x].sessionLow + "</td>" +
                "<td id=\"cryptoDashTableDivision\">" + allDashRules[x].sessionHigh + "</td>" +
                "<td id=\"cryptoDashTableDivision\"><button onclick=deleteDashRule("+ allDashRules[x].id +")>delete</button></td>" +
                "</tr>";
            }
            cryptoDashTable += "</table>";

            document.getElementById("cryptoDashTableHeader").innerHTML = cryptoDashTableHeader;
            document.getElementById("cryptoDashTable").innerHTML = cryptoDashTable;
        }
    }
    xhr.open("GET", "http://localhost:8082/getDashBoard", true);
    xhr.send();
}

function addCryptoDashRule() {
    var newDashRule = {};
    newDashRule.cryptoName = document.getElementById("cryptoName").value;
    newDashRule.totalAmount = document.getElementById("cryptoAmount").value;
    newDashRule.totalBuyPrice = document.getElementById("totalBuyPrice").value;
    var theJSON = JSON.stringify(newDashRule);
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if(this.readyState == 4) {
            console.log("added...");
            createCryptoDash();
        }
    }
    xhr.open("post", "http://localhost:8082/addDashRule", true);
    xhr.setRequestHeader("Content-Type","application/json");
    xhr.send(theJSON);
}

function deleteDashRule(id) {
    console.log("I am deleting...");
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if(this.readyState == 4) {
            console.log(this.responseText);
            createCryptoDash();
        }
    }
    xhr.open("DELETE", "http://localhost:8082/deleteDashRule/" + id, true);
    xhr.send();
}