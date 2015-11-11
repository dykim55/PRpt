var ws = null;

function connect(s) {
	
	if (ws != null) return;
	
	ws = new WebSocket('ws://localhost:8080/websocket');
	ws.onopen = function () {
    	console.log('websocket opened');
		//ws.send(JSON.stringify({"open" : s}));
    };

    ws.onmessage = function (message) {
	    console.log(message);
	    console.log('receive message : ' + message.data);
    };

    ws.onclose = function (event) {
    	console.log(event);
    	console.log('websocket closed');
    };
}
 
function disconnect() {
    if (ws) {
    	ws.close();
    	ws = null;
    }
}