$package("util.rmi")

util.rmi.miniJsonRequestAsync = function(jsonData, callback, scope,
		reLogonFunc, relogonArgs) {
	var con = ClassLoader.createNewTransport.apply();

	var url = jsonData.url || "*.jsonRequest";
	var method = jsonData.httpMethod || "POST";
	jsonData.method = jsonData.method || 'execute'
	try {
		con.open(method, url, true);
		con.onreadystatechange = complete;
		con.setRequestHeader('encoding', 'utf-8');
		con.setRequestHeader("content-Type", 'application/json');
		con.send($encode(jsonData));
	} catch (e) {
		if (typeof callback == "function") {
			var ctx = typeof scope == "object" ? scope : this;
			callback.call(ctx, 500, "ConnectionError", null, con);
		}
	}

	function complete() {
		var readyState = con.readyState;
		if (readyState == 4) {
			var json = {};
			var code = 400;
			var msg = "";
			con.onreadystatechange = ClassLoader.emptyFunction;
			var status = con.status;
			if (status == 200) {
				try {
					json = $decode(con.responseText);
					code = json["code"] || 200;
					msg = json["msg"];
				} catch (e) {
					code = 500;
					msg = "ParseResponseError";
				}
			} else if (status == 403) {
				var notLogonCall = $AppContext.notLogonCallback
				if (typeof notLogonCall == "function") {
					notLogonCall(reLogonFunc, relogonArgs, scope);
				}
				code = 403;
				msg = "AccessDenied";
			}

			if (typeof callback == "function") {
				var ctx = typeof scope == "object" ? scope : this;
				callback.call(ctx, code, msg, json, con);
			}

		}

	}// complete
};