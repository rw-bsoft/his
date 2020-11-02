$package("phis.application.cic.script");

$import("phis.script.TableForm", "phis.script.util.DateUtil");

phis.application.cic.script.IdrReport_MD = function(cfg) {
	cfg.actions = [{
				id : "confirm",
				name : "保存",
				iconCls : "common_add"
			},{
				id : "close",
				name : "关闭",
				iconCls : "common_cancel"
			}];
	cfg.colCount = cfg.colCount || 3;
	cfg.fldDefaultWidth = cfg.fldDefaultWidth || 150;
	cfg.width = cfg.width || 760;
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	//cfg.schema = "";
	phis.application.cic.script.IdrReport_MD.superclass.constructor
			.apply(this, [ cfg ]);
	this.on("winShow", this.onWinShow, this);
};
Ext.extend(phis.application.cic.script.IdrReport_MD,
	phis.script.TableForm,{
			doConfirm : function() {
				debugger
				var r = this.data;//IdrReport_MD//this.opener.opener.getSelectedRecord();
				
				if (r == null) {
					return;
				}
				var _this = this;
				body = {};
				debugger
				body["IDR_Report"] = this.getFormData();
				body["IDR_Report"].idCard = r.empiData.idCard;
				//body=this.data.empiData;
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : "verify_MD",
					body : body
				});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg,
							this.onBeforeSave);
					return;
				} else {
		//			this.panel.el.unmask();
					Ext.Msg.alert("提示", "保存成功！");
					_this.doClose();
					if(_this.opener.opener){
						_this.opener.doClose();
						_this.opener.opener.refresh();
					}else{
						_this.opener.refresh();
					}
				}
			},
			onWinShow : function() {
				this.win.doLayout();
			},
			setButton : function() {
				if (!this.getTopToolbar()) {
					return;
				}
				var bts = this.getTopToolbar().items;
				if (this.op == "read") {
					bts.items[0].disable();
				} else {
					bts.items[0].enable();
				}
			},
//			initFormData : function(data) {
//				debugger
//				this.initDataId = ''
//				var form = this.form.getForm()
//				var items = this.schema.items
//				var n = items.length
//				for (var i = 0; i < n; i++) {
//					var it = items[i]
//					var f = form.findField(it.id)
//					if (f) {
//						var v = data[it.id]
//						if (v != undefined) {
//							if (f.getXType() == "checkbox") {
//								var setValue = "";
//								if (it.checkValue
//										&& it.checkValue.indexOf(",") > -1) {
//									var c = it.checkValue.split(",");
//									checkValue = c[0];
//									unCheckValue = c[1];
//									if (v == checkValue) {
//										setValue = true;
//									} else if (v == unCheckValue) {
//										setValue = false;
//									}
//								}
//								if (setValue == "") {
//									if (v == 1) {
//										setValue = true;
//									} else {
//										setValue = false;
//									}
//								}
//								f.setValue(setValue);
//							} else {
//								if (it.dic && v !== "" && v === 0) {
//									v = "0";
//								}
//								f.setValue(v)
//								if (it.dic && v != "0" && f.getValue() != v) {
//									f.counter = 1;
//									this.setValueAgain(f, v, it);
//								}
//							}
//						}
//						if (it.update == "false") {
//							f.disable();
//						}
//					}
//					this.setKeyReadOnly(true);
//				}
//			},
		doClose : function() {
				var win = this.getWin();
				if (win) {
					win.hide();
				}
			}
	});