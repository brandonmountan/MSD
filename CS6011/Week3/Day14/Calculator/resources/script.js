let x = document.getElementById("xVal");
let y= document.getElementById("yVal");
let resultTextArea= document.getElementById("result");
let calculateButton = document.getElementById("calculate");
let ws = new WebSocket("ws://localhost:8080");

ws.onopen= function(){
    console.log( "WebSocket connection is created" );
};

ws.onmessage = function( messageEvent ){
    resultTextArea.textContent = "Result = " + messageEvent.data;
    alert( messageEvent.data );
};

ws.onerror= e => {
    console.log( "error in the websocket" );
    console.log( e );
};

calculateButton.addEventListener( "click", function() {
    let ajaxRequest = new XMLHttpRequest();
    ajaxRequest.open( "Get","http://localhost:8080/calculate?x="+x.value+"&y="+y.value);
    ajaxRequest.addEventListener("load",function (){
        alert( "I am doing an ajax request" );
        resultTextArea.textContent = this.responseText;
    })
    ajaxRequest.send()
});

// calculateButton.addEventListener("click", function (){
//     if(ws.readyState == WebSocket.OPEN) {
//         ws.send(x.value + " " + y.value);
//     }
// });

// calculateButton.addEventListener("click", ev => {
//     let xValue = Number(x.value);
//     let yValue = Number(y.value);
//     if(!(isNaN(xValue) || isNaN(yValue))){
//         fetch("http://localhost:8080/calculate?x="+xValue+"&y="+yValue)
//             .then(response =>{
//                 if(!response.ok){
//                     console.log("Something went wrong.");
//                     console.log(response);
//                 }
//                 return response.text();
//             }).then(data =>{
//                 alert(data);
//                 resultTextArea.textContent="result=" + data;
//         }).catch(error =>{ console.log("crashed");
//             console.log(error.data)});
//     }
//     else {
//         alert("please only enter numbers")
//     }
// });


// server response
// HTTP/1.1 101 Switching Protocols
// Upgrade: websocket
// Connection: Upgrade
// Sec-WebSocket-Accept: dGhlIHNhbXBsZSBub25jZQ== // Example hash of Sec-WebSocket-Key
// Sec-WebSocket-Protocol: chat // Optional, only if subprotocols were requested

// Client Request
// GET /chat HTTP/1.1
// Host: example.com
// Upgrade: websocket
// Connection: Upgrade
// Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==
// Sec-WebSocket-Version: 13
// Sec-WebSocket-Protocol: chat
// Origin: http://example.com

// Structure: Each WebSocket message is framed and can contain a payload, which is defined in the following structure:
//
//     Frame Header: Contains control information (e.g., FIN, opcode, masking key) about the message.
//     Payload Length: Indicates the length of the payload data.
//     Payload Data: The actual data being transmitted.
