$package("phis.application.cfg.script")

/**
 * 医院库房维护from zhangyq 2012.5.25
 * 
 */
$import("phis.script.TableForm")

phis.application.cfg.script.ConfigTreasuryInformationForm = function(cfg) {
	cfg.colCount = 1;
	cfg.width = 325;
	phis.application.cfg.script.ConfigTreasuryInformationForm.superclass.constructor
			.apply(this, [cfg])
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("loadData", this.onLoadData, this);
}
Ext.extend(phis.application.cfg.script.ConfigTreasuryInformationForm,
		phis.script.TableForm, {
			onBeforeSave : function(entryName, op, saveData) {
				if (saveData.KFLB == 1) {
					if (!saveData.KFZB) {
						MyMessageTip.msg("提示", "普通库房必须选择账簿类别", true);
						return false;
					}
					if (saveData.LBXH == 0 && saveData.LBXH1 == 0) {
						MyMessageTip.msg("提示", "普通库房必须选择分类类别", true);
						return false;
					}
				}
				if (saveData.KFLB == 2 || saveData.KFLB == 3) {
					if (!saveData.EJKF) {
						MyMessageTip.msg("提示", "二级库房必须选择对应科室", true);
						return false;
					}
				}
				var data = {
					"KFMC" : saveData.KFMC
				};
				if (this.initDataId) {
					data["KFXH"] = this.initDataId;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configTreasuryInformationService",
							serviceAction : "kfmcVerification",
							schemaDetailsList : "WL_KFXX",
							op : this.op,
							body : data
						});
				if (r.code == 612) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
				if (r.code == 613) {
					MyMessageTip.msg("提示", r.msg, true);
					return false;
				}
			},
			onReady : function() {
				phis.application.cfg.script.ConfigTreasuryInformationForm.superclass.onReady
						.call(this)
				var form = this.form.getForm();
				this.KFMC = form.findField("KFMC");
				this.KFLB = form.findField("KFLB");
				this.EJKF = form.findField("EJKF");
				this.LBXH = form.findField("LBXH");
				this.KFZB = form.findField("KFZB");
				this.LBXH1 = form.findField("LBXH1");
				this.GLKF = form.findField("GLKF");
				this.WXKF = form.findField("WXKF");
				this.CKFS = form.findField("CKFS");
				this.SXH = form.findField("SXH");
				if (this.KFMC) {
					this.KFMC.on("render", this.onRender, this);
				}
				if (this.KFLB) {
					this.KFLB.on("render", this.onRender1, this);
					this.KFLB.on("select", this.onSelect, this);
				}
				if (this.EJKF) {
					this.EJKF.on("render", this.onRender2, this);
					this.EJKF.on("select", this.onSelectEjkf, this);
				}
				if (this.LBXH) {
					this.LBXH.on("render", this.onRender3, this);
					this.LBXH.on("select", this.onSelectLbxh, this);
				}
				if (this.GLKF) {
					this.GLKF.on("render", this.onRender4, this);
				}
				if (this.WXKF) {
					this.WXKF.on("render", this.onRender5, this);
				}
				if (this.CKFS) {
					this.CKFS.on("render", this.onRender6, this);
				}
				if (this.SXH) {
					this.SXH.on("render", this.onRender7, this);
				}
				if (this.LBXH1) {
					this.LBXH1.on("render", this.onRender8, this);
				}
			},
			onRender : function() {
				this.KFMC.getEl().on("mouseover", this.onMouseover, this);
			},
			onRender1 : function() {
				this.KFLB.getEl().on("mouseover", this.onMouseover, this);
			},
			onRender2 : function() {
				this.EJKF.getEl().on("mouseover", this.onMouseover, this);
			},
			onRender3 : function() {
				this.LBXH.getEl().on("mouseover", this.onMouseover, this);
			},
			onRender4 : function() {
				this.GLKF.getEl().on("mouseover", this.onMouseover, this);
			},
			onRender5 : function() {
				this.WXKF.getEl().on("mouseover", this.onMouseover, this);
			},
			onRender6 : function() {
				this.CKFS.getEl().on("mouseover", this.onMouseover, this);
			},
			onRender7 : function() {
				this.SXH.getEl().on("mouseover", this.onMouseover, this);
			},
			onRender8 : function() {
				this.LBXH1.getEl().on("click", this.onClickLbxh1, this);
			},
			onMouseover : function() {
				if (this.KFMC.getName() == "KFMC") {
					this.KFMC.getEl().set({
						qtip : '<div style="font-size: 12;">说明:设置库房的名称,启用之后不能修改. </div>'
					}, false);
				}
				if (this.KFLB.getName() == "KFLB") {
					this.KFLB.getEl().set({
						qtip : '<div style="font-size: 12;">说明:库房类别确定库房建房的类型,如普通库房、<br/>二级库房、供应室、被服库房、启用之后不能修改,<br/>当库房类别为普通库房或被服库房时,才能选取库房账簿 </div>'
					}, false);
				}
				if (this.EJKF.getName() == "EJKF") {
					this.EJKF.getEl().set({
						qtip : '<div style="font-size: 12;">说明:当库房类别是二级库房时,才能维护相应的科室.</div>'
					}, false);
				}
				if (this.LBXH.getName() == "LBXH") {
					this.LBXH.getEl().set({
						qtip : '<div style="font-size: 12;">说明:分类类别是为了方便物资信息管理的一种分类方式.</div>'
					}, false);
				}
				if (this.GLKF.getName() == "GLKF") {
					this.GLKF.getEl().set({
						qtip : '<div style="font-size: 12;">说明:库房具备管理功能,可设置维护所管理的物资信息.</div>'
					}, false);
				}
				if (this.WXKF.getName() == "WXKF") {
					this.WXKF.getEl().set({
						qtip : '<div style="font-size: 12;">说明:库房具备维修属性,供各科室提交保修信息时使用.</div>'
					}, false);
				}
				if (this.CKFS.getName() == "CKFS") {
					this.CKFS.getEl().set({
						qtip : '<div style="font-size: 12;">说明:二级库房出库默认出库方式,出库消耗,领用消耗等.</div>'
					}, false);
				}
				if (this.SXH.getName() == "SXH") {
					this.SXH.getEl().set({
						qtip : '<div style="font-size: 12;">说明:调拨等业务二级库房显示顺序.</div>'
					}, false);
				}
			},
			onSelect : function() {
				if (this.KFLB.getValue() == 1) {
					this.EJKF.disable();
					this.EJKF.setValue("");
				} else {
					this.EJKF.enable();
				}
				if (this.KFLB.getValue() == 2 || this.KFLB.getValue() == 3) {
					this.LBXH.disable();
					this.LBXH1.disable();
					this.KFZB.disable();
					this.KFZB.setValue("");
					this.LBXH.setValue("");
					this.LBXH1.setValue("");
				} else {
					this.LBXH.enable();
					this.LBXH1.enable();
					this.KFZB.enable();
				}
			},
			setButtonsState : function(m, enable) {
				var btns;
				var btn;
				if (this.showButtonOnTop) {
					btns = this.form.getTopToolbar().items;
				} else {
					btns = this.form.buttons;
				}
				if (!btns) {
					return;
				}
				if (this.showButtonOnTop) {
					for (var j = 0; j < m.length; j++) {
						btn = btns.item(j);
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				} else {
					for (var j = 0; j < m.length; j++) {
						if (!isNaN(m[j])) {
							btn = btns[m[j]];
						} else {
							for (var i = 0; i < this.actions.length; i++) {
								if (this.actions[i].id == m[j]) {
									btn = btns[i];
								}
							}
						}
						if (btn) {
							(enable) ? btn.enable() : btn.disable();
						}
					}
				}
			},
			onLoadData : function() {
				var form = this.form.getForm();
				this.KFZT = form.findField("KFZT");
				this.LBXH = form.findField("LBXH");
				this.KFLB = form.findField("KFLB");
				this.LBXH1 = form.findField("LBXH1");
				if (this.KFLB.getValue() == 1) {
					if (this.LBXH.getValue() == 0) {
						this.LBXH1.setValue(1);
					}
				}
				if (this.KFZT.getValue() == 1) {
					this.setButtonsState(['new', 'save'], false);
				}
				this.onSelect();
			},
			onSelectEjkf : function() {
				var kfxh = 0;
				if (this.initDataId) {
					kfxh = this.initDataId;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configTreasuryInformationService",
							serviceAction : "queryEJKF",
							schemaDetailsList : "WL_KFXX",
							EJKF : this.EJKF.getValue(),
							KFXH : kfxh
						});
				if (r.code == 615) {
					MyMessageTip.msg("提示", "该科室已经使用!", true);
					this.EJKF.setValue("");
					return false;
				}
			},
			onSelectLbxh : function() {
				this.LBXH1.setValue("");
			},
			onClickLbxh1 : function() {
				if (this.LBXH1.getValue() == true) {
					this.LBXH.setValue("");
				}
			}
		})