$package("phis.application.fsb.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FsbApplicationList = function(cfg) {
	phis.application.fsb.script.FsbApplicationList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.fsb.script.FsbApplicationList,
		phis.script.SimpleList, {
			
			brnlRender : function(value, metaData, r) {
				var Y = new Date().getFullYear();
				var briY = r.get("CSNY").substr(0, 4);
				return Y - briY;
										
			},
			doCndQuery : function(button, e, addNavCnd) { // ** modified by
				// yzh ,
				// 2010-06-09 **
				var initCnd = this.initCnd
				var itid = this.cndFldCombox.getValue()
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == itid) {
						it = items[i]
						break
					}
				}
				if (!it) {
					if (addNavCnd) {
						if (initCnd) {
							this.requestData.cnd = ['and', initCnd, this.navCnd];
						} else {
							this.requestData.cnd = this.navCnd;
						}
						this.refresh()
						return
					} else {
						return;
					}
				}
				this.resetFirstPage()
				var v = this.cndField.getValue()
				var rawV = this.cndField.getRawValue();
				if (v == null || v == "" || rawV == null || rawV == "") {
					var cnd = []
					this.queryCnd = null;
					if (addNavCnd) {
						if (initCnd) {
							this.requestData.cnd = ['and', initCnd, this.navCnd];
						} else {
							this.requestData.cnd = this.navCnd;
						}
						this.refresh()
						return
					} else {
						if (initCnd)
							cnd = initCnd
					}
					this.requestData.cnd = cnd.length == 0 ? null : cnd;
					this.refresh()
					return
				}
				// 替换'，解决拼sql语句查询的时候报错
				if (typeof v == 'string') {
					v = (v + "").replace(/'/g, "''")
				}
				var refAlias = it.refAlias || "a"
				var cnd = ['eq', ['$', refAlias + "." + it.id]]
				if (it.dic) {
					if (it.dic.render == "Tree") {
						// var node = this.cndField.selectedNode
						// @@ modified by chinnsii 2010-02-28, add "!node"
						cnd[0] = 'eq'
						// if (!node || !node.isLeaf()) {
						// cnd[0] = 'like'
						// cnd.push(['s', v + '%'])
						// } else {
						cnd.push(['s', v])
						// }
					} else {
						cnd.push(['s', v])
					}
				} else {
					switch (it.type) {
						case 'int' :
							cnd.push(['i', v])
							break;
						case 'double' :
						case 'bigDecimal' :
							cnd.push(['d', v])
							break;
						case 'string' :
							// add by liyl 07.25 解决拼音码查询大小写问题 //modifyed by zhangxw
							// 2014.10.16解决ICD10查询大小写问题
							if(it.id=="ZYHM" || it.id=="MZHM"){
								v='%'+v;
							}
							if (it.id == "PYDM" || it.id == "WBDM" || it.id == "ICD10") {
								v = v.toUpperCase();
							}
							cnd[0] = 'like'
							cnd.push(['s', v + '%'])
							break;
						case "date" :
							v = v.format("Y-m-d")
							cnd[1] = ['$',
									"str(" + refAlias + "." + it.id + ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
							break;
						case "datetime" :
							v = v.format("Y-m-d H:i:s")
							cnd[1] = [
									'$',
									"str(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd hh:mm:ss')"]
							cnd.push(['s', v])
							break;
					}
				}
				this.queryCnd = cnd
				if (initCnd) {
					cnd = ['and', initCnd, cnd]
				}
				if (addNavCnd) {
					this.requestData.cnd = ['and', cnd, this.navCnd];
					this.refresh()
					return
				}
				this.requestData.cnd = cnd
				this.refresh()
			},
			doAdd : function() {
				this.fsbApplicationForm = this.createModule(
						"fsbApplicationForm", this.refForm);
				// this.fsbApplicationForm.on("save", this.onSave, this);
				this.fsbApplicationForm.opener = this
				var win = this.fsbApplicationForm.getWin();
				win.add(this.fsbApplicationForm.initPanel());
				win.show();
				win.center();
				if (!win.hidden) {
					this.fsbApplicationForm.op = "create";
					this.fsbApplicationForm.doNew();
					this.fsbApplicationForm.changeButtonState("editable");
				}
			},
			doUpd : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return;
				}
				this.fsbApplicationForm = this.createModule(
						"fsbApplicationForm", this.refForm);

				// this.fsbApplicationForm.on("save", this.onSave, this);
				var win = this.fsbApplicationForm.getWin();
				win.add(this.fsbApplicationForm.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.fsbApplicationForm.op = "update";
					this.fsbApplicationForm.initDataId = r.id;
					this.fsbApplicationForm.loadData();
					var sqzt = r.data.SQZT;
					if (sqzt == 1 || sqzt == 3) {
						this.fsbApplicationForm.changeButtonState("editable");
					} else if (sqzt == 2 || sqzt == 4) {
						this.fsbApplicationForm.changeButtonState("uneditable");
					}
				}

			},
			doTjsq : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return
				}
				if (r.get("SQZT") == 4) {
					MyMessageTip.msg("提示", "该单据已登记", true);
					return;
				}
				if (r.get("SQZT") == 2) {
					MyMessageTip.msg("提示", "该单据已提交", true);
					return;
				}
				var obj = {
					ID : r.get("ID"),
					SQZT : "2"
				}
				var title = r.get("BRXM");
				Ext.Msg.show({
							title : '确认提交',
							msg : title + '的家床申请?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processUpdate(obj);
								}
							},
							scope : this
						})
			},
			doThsq : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return
				}
				if (r.get("SQZT") == 4) {
					MyMessageTip.msg("提示", "该单据已登记", true);
					return;
				}
				if (r.get("SQZT") == 3) {
					MyMessageTip.msg("提示", "该单据已退回", true);
					return;
				}
				var obj = {
					ID : r.get("ID"),
					SQZT : "3"
				}
				var title = r.get("BRXM");
				Ext.Msg.show({
							title : '确认退回',
							msg : title + '的家床申请?',
							modal : true,
							width : 300,
							buttons : Ext.MessageBox.OKCANCEL,
							multiline : false,
							fn : function(btn, text) {
								if (btn == "ok") {
									this.processUpdate(obj);
								}
							},
							scope : this
						})
			},
			processUpdate : function(obj) {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				this.mask("正在操作数据...");
				var cfg = {
					serviceId : "phis.simpleSave",
					method : "execute",
					op : "update",
					schema : this.entryName,
					module : this.grid._mId,
					body : obj
				}
				phis.script.rmi.jsonRequest(cfg, function(code, msg, json) {
							this.unmask()
							if (code < 300) {
								MyMessageTip.msg("提示", "操作成功!", true);
								this.refresh()
							} else {
								this.processReturnMsg(code, msg);
							}
						}, this)
			},
			doRemove : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("SQZT") == 2) {
					MyMessageTip.msg("提示", "已提交的单据无法删除", true);
					return;
				}
//				if (r.get("SQZT") == 3) {
//					MyMessageTip.msg("提示", "已退回的单据无法删除", true);
//					return;
//				}
				if (r.get("SQZT") == 4) {
					MyMessageTip.msg("提示", "已登记的单据无法删除", true);
					return;
				}
				var title = r.id;
				if (this.isCompositeKey) {
					title = "";
					for (var i = 0; i < this.schema.keys.length; i++) {
						title += r.get(this.schema.keys[i])
					}
				}
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
			doPrint : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var module = this.createModule("jcsqPrint", this.refPrint);
				module.ID = r.data.ID;
				// var form = this.form.getForm();
				module.initPanel();
				module.doPrint();
			},
			onDblClick : function(grid, index, e) {
				this.doUpd();
			},
			getStore : function(items) {
				if (this.key == "C02" || this.key == "C07") {
					this.requestData.cnd = ['and',
							['eq', ['$', 'JGID'], ['s', this.mainApp.deptId]],
							['eq', ['$', 'SQYS'], ['s', this.mainApp.uid]]];
					this.initCnd = ['and',
							['eq', ['$', 'JGID'], ['s', this.mainApp.deptId]],
							['eq', ['$', 'SQYS'], ['s', this.mainApp.uid]]];
				} else {
					this.requestData.cnd = ['eq', ['$', 'JGID'],
							['s', this.mainApp.deptId]];
					this.initCnd = ['eq', ['$', 'JGID'],
							['s', this.mainApp.deptId]];
				}
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