$package("chis.application.hq.script");

$import("chis.script.BizTableFormView",
		"util.widgets.LookUpField");

chis.application.hq.script.HealthStatusModifyForm = function(cfg) {
	cfg.actions = [{
				id : "confirm",
				name : "确定",
				iconCls : "save"
			},{
				id : "close",
				name : "关闭",
				iconCls : "common_cancel"
			}];
	cfg.colCount = cfg.colCount || 1;
	cfg.fldDefaultWidth = cfg.fldDefaultWidth || 150;
	cfg.width = cfg.width || 300;
	cfg.showButtonOnTop = true;
	cfg.autoFieldWidth = false;
	cfg.autoLoadData = false;
	chis.application.hq.script.HealthStatusModifyForm.superclass.constructor
			.apply(this, [ cfg ]);
	this.on("winShow", this.onWinShow, this);
};
Ext.extend(chis.application.hq.script.HealthStatusModifyForm,
	chis.script.BizTableFormView,{
			doConfirm : function() {
				var r = this.opener.getSelectedRecord();
				if (r == null) {
					return;
				}
				var _this = this;
				var body = this.getFormData();
				body.empiId = r.data.EMPIID;
				var r = util.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : "verify",
					method : "execute",
					body : body
				});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg,
							this.onBeforeSave);
					return;
				} else {
//					Ext.Msg.alert("提示", "修改成功！");
					_this.doClose();
					_this.opener.refresh();
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
			initFormData : function(data) {
				this.initDataId = this.data[this.schema.pkey]
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						var v = data[it.id]
						if (v != undefined) {
							if (f.getXType() == "checkbox") {
								var setValue = "";
								if (it.checkValue
										&& it.checkValue.indexOf(",") > -1) {
									var c = it.checkValue.split(",");
									checkValue = c[0];
									unCheckValue = c[1];
									if (v == checkValue) {
										setValue = true;
									} else if (v == unCheckValue) {
										setValue = false;
									}
								}
								if (setValue == "") {
									if (v == 1) {
										setValue = true;
									} else {
										setValue = false;
									}
								}
								f.setValue(setValue);
							} else {
								if (it.dic && v !== "" && v === 0) {
									v = "0";
								}
								f.setValue(v)
								if (it.dic && v != "0" && f.getValue() != v) {
									f.counter = 1;
									this.setValueAgain(f, v, it);
								}
							}
						}
						if (it.update == "false") {
							f.disable();
						}
					}
					this.setKeyReadOnly(true);
				}
			},
			doClose : function() {
				var win = this.getWin();
				if (win) {
					win.hide();
				}
			}
	});