$package("phis.script.widgets")

phis.script.widgets.TreeLoader = Ext.extend(Ext.tree.TreeLoader, {
			requestData : function(node, callback, scope) {
				if (this.fireEvent("beforeload", this, node, callback) !== false) {
					Ext.apply(this.jsonData, this.baseParams);
					if (this.directFn) {
						var args = this.getParams(node);
						args.push(this.processDirectResponse.createDelegate(
								this, [{
											callback : callback,
											node : node,
											scope : scope
										}], true));
						this.directFn.apply(window, args);
					} else {
						this.transId = Ext.Ajax.request({
									method : this.requestMethod,
									url : this.dataUrl || this.url,
									success : this.handleResponse,
									failure : this.handleFailure,
									scope : this,
									argument : {
										callback : callback,
										node : node,
										scope : scope
									},
									params : this.getParams(node),
									jsonData : this.jsonData
								});
					}
				} else {
					// if the load is cancelled, make sure we notify
					// the node that we are done
					this.runCallback(callback, scope || node, []);
				}
			},
			processResponse : function(response, node, callback, scope) {
				var json = response.responseText;
				try {
					var o = response.responseData || Ext.decode(json);
					if (o.body) {
						o = o.body;
					}
					node.beginUpdate();
					for (var i = 0, len = o.length; i < len; i++) {
						var n = this.createNode(o[i]);
						if (n) {
							node.appendChild(n);
						}
					}
					node.endUpdate();
					this.runCallback(callback, scope || node, [node]);
				} catch (e) {
					this.handleFailure(response);
				}
			}
		})