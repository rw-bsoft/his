$package("phis.application.hos.script")

$import("phis.script.SimpleList")

phis.application.fsb.script.FsbPaymentProcessingList = function(cfg) {
	cfg.autoLoadData = false;
	phis.application.fsb.script.FsbPaymentProcessingList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.fsb.script.FsbPaymentProcessingList,
		phis.script.SimpleList, {
			doCanceled : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				if (r.get("ZFPB")) {
					MyMessageTip.msg("提示", "缴款记录已经作废!", true);
					return
				}
				var this_ = this;
				var msg = '<br/><br/>病人姓名：' + r.get("BRXM") + '<br/><br/>缴款日期：'
						+ r.get("JKRQ");
				var navigatorName = "Microsoft Internet Explorer";
				if (navigator.appName == navigatorName) {
					msg += ' <br/>缴款金额：'
				} else {
					msg += '&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<br/><br/>缴款金额：'
				}
				msg += r.get("JKJE") + '<br/><br/>缴款方式：' + r.get("JKFS_text")
						+ '<br/><br/>收据号码：' + r.get("SJHM") + '<br/><br/>票卡号码：'
						+ r.get("ZPHM") + '<br/><br/>'
				Ext.MessageBox.confirm('确认注销下列缴款记录吗?', msg,
						function(btn, text) {
							if (btn == "yes") {
								this_.processRemove(r);
							}
						});
			},
			processRemove : function(r) {
				if (!this.fireEvent("beforeRemove", this.entryName, r)) {
					return;
				}
				this.grid.el.mask("正在注销...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "fsbPaymentProcessingService",
							serviceAction : "updatePayment",
							pkey : r.id
						}, function(code, msg, json) {
							this.grid.el.unmask()
							if (code < 300) {
								this.store.remove(r)
								this.fireEvent("remove", this.entryName,
										'remove', json, r.data)
							} else {
								this.processReturnMsg(code, msg, this.doRemove)
							}
						}, this)
			},
			getCndBar : function(items) {
				var fields = [];
				if (!this.enableCnd) {
					return []
				}
				var selected = null;
				var defaultItem = null;
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!it.queryable) {
						continue
					}
					if (it.selected == "true") {
						selected = it.id;
						defaultItem = it;
					}
					fields.push({
								// change "i" to "it.id"
								value : it.id,
								text : it.alias
							})
				}
				if (fields.length == 0) {
					return [];
				}
				var store = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : fields
						});
				var combox = null;
				if (fields.length > 1) {
					combox = new Ext.form.ComboBox({
								store : store,
								valueField : "value",
								displayField : "text",
								value : selected,
								mode : 'local',
								triggerAction : 'all',
								emptyText : '选择查询字段',
								selectOnFocus : true,
								width : 100
							});
					combox.on("select", this.onCndFieldSelect, this)
					this.cndFldCombox = combox
				} else {
					combox = new Ext.form.Label({
								text : fields[0].text
							});
					this.cndFldCombox = new Ext.form.Hidden({
								value : fields[0].value
							});
				}

				var cndField;
				if (defaultItem) {
					if (defaultItem.dic) {
						defaultItem.dic.src = this.entryName + "." + it.id
						defaultItem.dic.defaultValue = defaultItem.defaultValue
						defaultItem.dic.width = 150
						cndField = this.createDicField(defaultItem.dic)
					} else {
						cndField = this.createNormalField(defaultItem)
					}
				} else {
					cndField = new Ext.form.TextField({
								width : 150,
								selectOnFocus : true,
								name : "dftcndfld"
							})
				}
				this.cndField = cndField
				cndField.on("specialkey", this.onQueryFieldEnter, this)
				var queryBtn = new Ext.Toolbar.Button({
							text : '',
							iconCls : "query",
							notReadOnly : true
						})
				this.queryBtn = queryBtn
				queryBtn.on("click", this.doCndQuery, this);
				return [combox, '-', cndField, '-', queryBtn]
			},
			doCndQuery : function(button, e, addNavCnd) { // ** modified by
				if (!this.cnd) {
					return;
				} // yzh ,
				// 2010-06-09 **
				
				//去掉第二个过滤条件zyh=0
				if(this.cnd[1][1][1]=='a.ZYH'){
					this.cnd.splice(1,1); 
				}
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
							var rcnd = ['and', initCnd, this.navCnd];
							if (rcnd) {
								this.requestData.cnd = ['and', rcnd, this.cnd];
							} else {
								this.requestData.cnd = this.cnd;
							}
						} else {
							if (this.navCnd) {
								this.requestData.cnd = ['and', this.navCnd,
										this.cnd];
							} else {
								this.requestData.cnd = this.cnd;
							}
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
							var rcnd = ['and', initCnd, this.navCnd];
							if (rcnd) {
								this.requestData.cnd = ['and', rcnd, this.cnd];
							} else {
								this.requestData.cnd = this.cnd;
							}
						} else {
							if (this.navCnd) {
								this.requestData.cnd = ['and', this.navCnd,
										this.cnd];
							} else {
								this.requestData.cnd = this.cnd;
							}
						}
						this.refresh()
						return
					} else {
						if (initCnd)
							cnd = initCnd
					}
					var rcnd = cnd.length == 0 ? null : cnd;
					if (rcnd) {
						this.requestData.cnd = ['and', rcnd, this.cnd];
					} else {
						this.requestData.cnd = this.cnd;
					}
					this.refresh()
					return
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
							// add by liyl 07.25 解决拼音码查询大小写问题
							if (it.id == "PYDM" || it.id == "WBDM") {
								v = v.toUpperCase();
							}
							cnd[0] = 'like'
							cnd.push(['s', v + '%'])
							break;
						case "date" :
							v = v.format("Y-m-d")
							cnd[1] = [
									'$',
									"str(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
							break;
					}
				}
				this.queryCnd = cnd
				if (initCnd) {
					cnd = ['and', initCnd, cnd]
				}
				if (addNavCnd) {
					var rcnd = ['and', cnd, this.navCnd];
					if (rcnd) {
						this.requestData.cnd = ['and', rcnd, this.cnd];
					} else {
						this.requestData.cnd = this.cnd;
					}
					this.refresh()
					return
				}
				if (cnd) {
					this.requestData.cnd = ['and', cnd, this.cnd];
				} else {
					this.requestData.cnd = this.cnd;
				}
				this.refresh()
			},
			doPrint : function() {
				var module = this.createModule("paymentprint",
						this.refPaymentList)
				var r = this.getSelectedRecord()
				if (r) {
					var msg = '<br/><br/>病人姓名：' + r.get("BRXM")
							+ '<br/><br/>缴款日期：' + r.get("JKRQ");
					var navigatorName = "Microsoft Internet Explorer";
					if (navigator.appName == navigatorName) {
						msg += ' <br/>缴款金额：'
					} else {
						msg += '&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<br/><br/>缴款金额：'
					}
					msg += +r.get("JKJE") + '<br/><br/>缴款方式：'
							+ r.get("JKFS_text") + '<br/><br/>收据号码：'
							+ r.get("SJHM") + '<br/><br/>票卡号码：' + r.get("ZPHM")
							+ '<br/><br/>';
					Ext.MessageBox
							.confirm(
									'确认重打下列缴款收据吗?',
									msg,
									function(btn, text) {
										if (btn == "yes") {
											module.jkxh = r.get("JKXH");
											module.initPanel();
											module.doPrint();
										}
									})

				} else {
					MyMessageTip.msg("提示", "打印失败：无效的缴款信息!", true);
				}
			}
		});