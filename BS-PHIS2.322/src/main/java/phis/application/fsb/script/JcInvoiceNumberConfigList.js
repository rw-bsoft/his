$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.JcInvoiceNumberConfigList = function(cfg) {
	cfg.selectFirst = false
	phis.application.fsb.script.JcInvoiceNumberConfigList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.fsb.script.JcInvoiceNumberConfigList,
		phis.script.SimpleList, {
			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("QSHM") != r.get("DQHM")) {
					Ext.Msg.alert("提示", "起始号码与使用号码不相同 ,号码已使用,无法删除!");
					return false;
				}
				var title = r.id;
				if (this.isCompositeKey) {
					title = "";
					for (var i = 0; i < this.schema.pkeys.length; i++) {
						title += r.get(this.schema.pkeys[i])
					}
				}
				// add by liyl 2012-06-17 提示信息增加名称显示功能
				if (this.removeByFiled && r.get(this.removeByFiled)) {
					title = r.get(this.removeByFiled);
				}
				Ext.Msg.show({
							title : '确认删除记录[' + title + ']',
							msg : '删除操作将无法恢复，是否继续?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processRemove(r);
								}
							},
							scope : this
						})
			},
			selectRow : function(v) {
				if (!this.grid.hidden) {
					this.grid.el.focus()
				}
				try {
					if (this.grid && this.selectFirst) {
						var sm = this.grid.getSelectionModel()
						if (sm.selectRow) {
							sm.selectRow(v)
						}
						this.fireEvent("firstRowSelected", this)
					}
				} catch (e) {
				}
			},
			getStore : function(items) {
				this.requestData.cnd = ['and',
						['eq', ['$', 'PJLX'], ['d', this.PJLX]],
						['eq', ['$', 'JGID'], ['s', this.mainApp.deptId]]];
				this.initCnd = ['and', ['eq', ['$', 'PJLX'], ['d', this.PJLX]],
						['eq', ['$', 'JGID'], ['s', this.mainApp.deptId]]];
				var o = this.getStoreFields(items)
				var readCfg = {
					root : 'body',
					totalProperty : 'totalCount',
					fields : o.fields
				}
				if (!this.isCompositeKey) {
					readCfg.id = o.pkey;
				}
				var reader = new Ext.data.JsonReader(readCfg)
				var url = ClassLoader.serverAppUrl || "";
				// add by yangl 请求统一加前缀
				if (this.requestData && this.requestData.serviceId
						&& this.requestData.serviceId.indexOf(".") < 0) {
					this.requestData.serviceId = 'phis.'
							+ this.requestData.serviceId
				}
				var proxy = new Ext.data.HttpProxy({
							url : url + '*.jsonRequest',
							method : 'POST',
							jsonData : this.requestData,
							timeout : this.timeout || 30000
						})
				proxy.on("loadexception", function(proxy, o, response, arg, e) {
							if (response.status == 200) {
								var json = eval("(" + response.responseText
										+ ")")
								if (json) {
									var code = json["code"]
									var msg = json["msg"]
									// modified by gaof 2015-1-4
									// 解决msg为undefined时报错的问题
									this.processReturnMsg(code, msg ? msg : "",
											this.refresh)
								}
							} else {
								this.processReturnMsg(404, 'ConnectionError',
										this.refresh)
							}
						}, this)

				if (this.group) {
					var store = new Ext.data.GroupingStore({
								reader : reader,
								proxy : proxy,
								sortInfo : {
									field : this.groupSort || this.group,
									direction : "ASC"
								},
								groupField : this.group
							});
				} else {
					var store = new Ext.data.Store({
								proxy : proxy,
								reader : reader,
								remoteSort : this.remoteSort || false
							})
				}
				store.on("load", this.onStoreLoadData, this)
				store.on("beforeload", this.onStoreBeforeLoad, this)
				return store
			}
		});