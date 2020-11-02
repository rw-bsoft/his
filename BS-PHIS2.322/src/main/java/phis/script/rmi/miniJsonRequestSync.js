$package("phis.script.rmi")

phis.script.rmi.miniJsonRequestSync = function(jsonData, reLogonFunc,
		relogonArgs, scope) {
	var con = ClassLoader.createNewTransport();
	var url = jsonData.url || "*.jsonRequest";
	var method = jsonData.httpMethod || "POST";
	jsonData.method = jsonData.method || 'execute'
	if (jsonData.serviceId && jsonData.serviceId.indexOf('.') < 0) {
		jsonData.serviceId = "phis." + jsonData.serviceId
	}
	try {
		con.open(method, url, false);
		con.setRequestHeader('encoding', 'utf-8');
		con.setRequestHeader("content-Type", 'application/json');
		con.send($encode(jsonData));
	} catch (e) {
		return {
			code : 500,
			msg : "ConnectionError"
		};
	}

	var json = {};
	var code = 400;
	var msg = "";
	if (con.readyState == 4) {
		var status = con.status;
		if (status == 200 || status == 304) {
			try {
				json = $decode(con.responseText);
				code = json["code"] || 200;
				msg = json["msg"];
			} catch (e) {
				code = 500;
				msg = "ParseResponseError";
			}
		}
		if (status == 403) {
			var notLogonCall = $AppContext.notLogonCallback
			if (typeof notLogonCall == "function") {
				notLogonCall(reLogonFunc, relogonArgs, scope);
			}
		}
	}
	return {
		code : code,
		msg : msg,
		json : json
	};
};