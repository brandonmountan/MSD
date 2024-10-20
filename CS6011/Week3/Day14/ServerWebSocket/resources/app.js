let x = document.getElementById("xVal");
let y = document.getElementById("yVal");
let submit = document.getElementById("calculate");
let result = document.getElementById("result")


let ws = new WebSocket("ws://localhost:8080");
let isConnected = false;

ws.onopen = function () {
  isConnected = true;
  console.log("connection is established");
};

ws.onmessage = function (messageEvent) {
  result.textContent = messageEvent.data;
};

ws.onerror = function(e) {

};

ws.onclose = function (e) {
  console.log("connection is now closed")
}

// ws.send("")

submit.addEventListener("click", function() {
  let xValue = Number(x.value);
  let yValue = Number(y.value);

  if(!(isNan(xValue) || isNaN(yValue))) {
    if (isConnected)
    ws.send(xValue+" "+yValue)
  }
})

// submit.addEventListener("click", function() {
//   let ajaxRequest = new XMLHttpRequest();
//
//   let xValue = Number(x.value);
//   let yValue = Number(y.value);
//
//
//   if (!(isNaN(xValue) || isNaN(yValue))) {
//     ajaxRequest.open("GET", "http://localhost:8080/calculate?x=" + x.value + "&y=" + y.value);
//     ajaxRequest.addEventListener("load", function () {
//       result.textContent = "Result = " + this.responseText;
//     })
//     ajaxRequest.send();
//   } else {
//     alert("Please only enter a number")
//   }
// });

