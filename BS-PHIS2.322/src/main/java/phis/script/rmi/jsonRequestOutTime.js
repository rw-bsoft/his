$package("phis.script.rmi")

phis.script.rmi.jsonRequestOutTime = function(jsonData, timeout,callback, scope) {
	var con = new Ext.data.Connection();
	var url = jsonData.url || "*.jsonRequest";
	var method = jsonData.httpMethod || "POST";
	jsonData.method = jsonData.method || 'execute'
	if (jsonData.serviceId && jsonData.serviceId.indexOf('.') < 0) {
		jsonData.serviceId = "phis." + jsonData.serviceId
	}
	con.request({
				url : url,
				method : method,
				disableCaching : false,
				callback : complete,
				scope : this,
				jsonData : jsonData,
				timeout : timeout || 600000
			});
	function complete(ops, sucess, response) {
		var json = {};
		var code = 200;
		var msg = "";

		if (sucess) {
			try {
				json = $decode(response.responseText);
				code = json["code"];
				msg = json["msg"];
			} catch (e) {
				code = 500;
				msg = "ParseResponseError";
			}
		} else {
			code = 400;
			msg = "ConnectionError";
		}
		if (typeof callback == "function") {
			var ctx = typeof scope == "object" ? scope : this;
			callback.call(ctx, code, msg, json, response);
		}
	}
};