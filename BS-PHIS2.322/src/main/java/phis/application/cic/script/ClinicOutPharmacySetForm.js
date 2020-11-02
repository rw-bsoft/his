$package("phis.application.cic.script")

$import("phis.script.TableForm")

phis.application.cic.script.ClinicOutPharmacySetForm = function(cfg) {
	cfg.loadServiceId = "outPharmacyLoad";
	cfg.saveServiceId = "outPharmacySave";
	cfg.autoLoadData = false;
	phis.application.cic.script.ClinicOutPharmacySetForm.superclass.constructor.apply(
			this, [cfg])
	
}

Ext.extend(phis.application.cic.script.ClinicOutPharmacySetForm,
		phis.script.TableForm, {
			onWinShow : function() {
				if(this.mainApp.phis.reg_departmentId==null){//判断科室是否为空，为空关闭窗口
					MyMessageTip.msg("提示", "当前不存在门诊科室，请先选择门诊科室!", true);
				    this.doCancel();
					return;
				}
				this.onCyLoad();
			},
			onReady : function() {
				var cy = this.form.getForm().findField("YS_MZ_FYYF_XY");
				//cy.getStore().on("load", this.onCyLoad, this);
				this.on("winShow", this.onWinShow, this);
			},
			getFormData : function() {
				var ac = util.Accredit;
				var form = this.form.getForm()
				if (!this.validate()) {
					return
				}
				if (!this.schema) {
					return
				}
				var values = {};
				var items = this.schema.items
				Ext.apply(this.data, this.exContext.empiData)
				if (items) {
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						if (this.op == "create" && !ac.canCreate(it.acValue)) {
							continue;
						}
						var v = this.data[it.id] // ** modify by yzh 2010-08-04
						if (v == undefined) {
							v = it.defaultValue
							if (it.type == "datetime" && this.op == "create") {//
								// update
								// by
								// caijy
								// 2013-3-21
								// for
								// 新增页面的时间动态生成
								v = Date.getServerDateTime();
							}
						}
						if (v != null && typeof v == "object") {
							v = v.key
						}
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue()
							// add by caijy from checkbox
							if (f.getXType() == "checkbox") {
								var checkValue = 1;
								var unCheckValue = 0;
								if (it.checkValue && it.checkValue.indexOf(",") > -1) {
									var c = it.checkValue.split(",");
									checkValue = c[0];
									unCheckValue = c[1];
								}
								if (v == true) {
									v = checkValue;
								} else {
									v = unCheckValue;
								}
							}
							// add by huangpf
							if (f.getXType() == "treeField") {
								var rawVal = f.getRawValue();
								if (rawVal == null || rawVal == "")
									v = "";
							}
							if (f.getXType() == "datefield" && v != null && v != "") {
								v = v.format('Y-m-d');
							}
							// end
						}

						if (v == null || v === "") {
							if (!it.pkey && it["not-null"] && !it.ref) {
								Ext.Msg.alert("提示", it.alias + "不能为空")
								return;
							}
						}
						if (it.type && it.type == "int") {
							v = (v == "0" || v == "" || v == undefined)
									? 0
									: parseInt(v);
						}
						if(it.id.indexOf('YS_MZ_FYYF')>=0){
							values[it.id.substring(0,10)+'_'+this.mainApp.phis.reg_departmentId+it.id.substring(10)] = v;
						}else{
							values[it.id] = v;
						}
					}
				}
				return values;
			},
			onCyLoad : function() {
				this.initDataId = "YS_MZ_FYYF_"+this.mainApp.phis.reg_departmentId;
				this.loadData();
			},
			initFormData : function(data) {
				Ext.apply(this.data, data)
				this.initDataId = this.data[this.schema.pkey]
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						f.focus();
						var v
						if(it.id.indexOf('YS_MZ_FYYF')>=0){
							v = data[it.id.substring(0,10)+'_'+this.mainApp.phis.reg_departmentId+it.id.substring(10)]
						}else{
							v = data[it.id]
						}
						this.afterinitFormData(f, v);
					}
				}
				this.setKeyReadOnly(true)
				this.focusFieldAfter(-1, 800)
			},
			afterinitFormData : function(f, v) {
				var count = f.getStore().getCount();
				//alert(count + "@@@" + f.id)
				if (count > 0) {
					if (v != undefined) {
						f.setValue(v)
						if(f.isExpanded()){
							f.collapse();
						}
					}
				} else {
					var this1 = this;
					setTimeout(function() {
								this1.afterinitFormData(f, v);
							}, "100");
				}
			},
			getWin : function() {
				var win = this.win
				var closeAction = this.closeAction || "hide";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								closeAction : closeAction,
								constrainHeader : true,
								constrain : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : this.modal || true,
								items : this.initPanel()
							})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() { // ** add by yzh 2010-06-24 **
								this.fireEvent("hide", this)
							}, this)
					this.win = win
				}
				return win;
			}
		})
