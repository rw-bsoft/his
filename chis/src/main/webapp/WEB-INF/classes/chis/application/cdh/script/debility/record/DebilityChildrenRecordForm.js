/**
 * 体弱儿档案表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.cdh.script.debility.record");
$import("chis.script.BizTableFormView");
chis.application.cdh.script.debility.record.DebilityChildrenRecordForm = function(cfg) {
	cfg.labelWidth = 90;
	cfg.fldDefaultWidth = 180
	cfg.autoFieldWidth = false
	cfg.entryName = "chis.application.cdh.schemas.CDH_DebilityChildren"
	chis.application.cdh.script.debility.record.DebilityChildrenRecordForm.superclass.constructor
			.apply(this, [cfg]);
	this.saveServiceId = "chis.debilityChildrenService";
	this.saveAction = "saveDebilityChildrenRecord";
	this.closeAction = "closeDebilityChildrenRecord";
	this.initAction = "docCreateInitialization";
	this.on("beforeSave", this.onBeforeSave, this);
	this.on("winShow", this.onWinShow, this)
	this.on("loadData", this.onLoadData, this)
	this.on("beforePrint", this.onBeforePrint, this)
	this.on("beforeCreate", this.onBeforeCreate, this) 
};

Ext.extend(chis.application.cdh.script.debility.record.DebilityChildrenRecordForm,
		chis.script.BizTableFormView, {

			onReady : function() {
				chis.application.cdh.script.debility.record.DebilityChildrenRecordForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var debilityReason = form.findField("debilityReason");
				if (debilityReason) {
					debilityReason.on("select", this.onDebilityReasonChange,
							this);
				}

				var deseaseReason = form.findField("deseaseReason");
				if (deseaseReason) {
					deseaseReason
							.on("select", this.onDeseaseReasonChange, this);
				}
				var vestingCode = form.findField("vestingCode");
				if (vestingCode) {
					vestingCode.on("select", this.onVestingCodeChange, this);
					vestingCode.on("blur", this.onVestingCodeChange, this);
//					vestingCode.on("blur", this.onOtherVestingChange, this);
//					vestingCode.on("keyup", this.onOtherVestingChange, this);
//					vestingCode.on("change", this.onOtherVestingChange, this);
				}
			},

			onDebilityReasonChange : function(combo, record, index) {
				var value = this.form.getForm().findField("debilityReason").getValue();
				var valueArray = value.split(",");
				var dorField = this.form.getForm()
						.findField("debilityOtherReason");
				if (valueArray.indexOf("99") != -1) {
					dorField.enable();
				} else {
					dorField.setValue();
					dorField.disable();
				}
			},

			onDeseaseReasonChange : function(combo, record, index) {
				var value = combo.getValue();
				var valueArray = value.split(",");
				var dorField = this.form.getForm()
						.findField("deseaseOtherReason");
				if (valueArray.indexOf("7") != -1) {
					dorField.setValue();
					dorField.enable();
				} else {
					dorField.setValue();
					dorField.disable();
				}
				if (value == "") {
					combo.setValue({
								key : "7",
								text : "其他"
							});
					dorField.enable();
				}
			},

			onVestingCodeChange : function(combo) {
				var value = this.form.getForm().findField("vestingCode").getValue();
				if (value == "9") {
					this.form.getForm().findField("otherVesting").enable();
				} else {
					this.form.getForm().findField("otherVesting").setValue();
					this.form.getForm().findField("otherVesting").disable();
				}
			},

//			onOtherVestingChange : function(field) {
//				if (field.getValue()) {
//					this.form.getForm().findField("vestingCode").setValue({
//								key : "9",
//								text : "其他"
//							});
//				}
//			},

			loadData : function() {
				var datas = this.exContext.args.formDatas
				if (!datas) {
					return;
				}
				this.doNew();
				this.initFormData(datas);  
				this.fireEvent("loadData", datas, this)
			},

			onLoadData : function(data) {
				var drc = this.form.getForm().findField("debilityReason");
				var value = drc.getValue();
				drc.fireEvent("select", drc, new Ext.data.Record(value));
				var vc = this.form.getForm().findField("vestingCode");
				vc.fireEvent("select", vc);
				if (data.closeFlag) {
					if (data.closeFlag.key == 'y') {
						if (this.form.getTopToolbar()) {
							this.form.getTopToolbar().items.item(0).disable()
							this.form.getTopToolbar().items.item(2).disable()
						}
						this.fireEvent("closeFlag", "y")
						return
					}
				} else {
					if (this.form.getTopToolbar()) {
						this.form.getTopToolbar().items.item(0).enable()
						this.form.getTopToolbar().items.item(2).enable()
					}
					this.fireEvent("closeFlag", "n")
				}
				if (this.exContext.args.closeFlag == 'y') {
					this.fireEvent("processButton");
				}
				this.onDebilityReasonChange()
				this.onVestingCodeChange()
			},

			onBeforeSave : function(entryName, op, saveData) {
				saveData.phrId = this.exContext.args.phrId;
				saveData.empiId = this.exContext.args.empiId;
				saveData.recordId = this.exContext.args.recordId
				saveData.birthday = this.exContext.empiData.birthday
			},

			doCreate : function() {
				this.tempId = this.initDataId
				this.initDataId = null;
				this.exContext.args.recordId = null;
				if (!this.fireEvent("doCreate")) {
					this.initDataId = this.tempId
					Ext.Msg.alert("提示", "有未结案的档案所以不能新建档案");
					return
				}
			},

			doClose : function() {
				if (!this.exContext.args.recordId) {
					return
				}
				Ext.Msg.show({
					title : '结案确认',
					msg : '结案操作后无法恢复，是否继续？',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var result = util.rmi.miniJsonRequestSync({
										serviceId : this.saveServiceId,
										serviceAction : this.closeAction,
										method:"execute",
										body : {
											recordId : this.exContext.args.recordId,
											empiId : this.exContext.ids.empiId
										}
									})
							this.fireEvent("close")
						}
					},
					scope : this
				})

			},

			
			onBeforeCreate : function() {
				if (!this.exContext.args.phrId) {
					return;
				}
				this.form.el.mask("正在初始化数据,请稍后...", "x-mask-loading")
				var res = util.rmi.miniJsonRequestSync({
					serviceId : this.saveServiceId,
					serviceAction : this.initAction,
					method:"execute",
					body : {
						phrId : this.exContext.args.phrId
					}
				});
				this.form.el.unmask()
				if (res.code > 300) {
					this.processReturnMsg(res.code, res.msg, this.doNew);
					return;
				}
				if (res.json.body) {
					var data = res.json.body;
					var manaUnitId = data.manaUnitId;
					this.form.getForm().findField("manaUnitId")
					.setValue(manaUnitId);
				}
			},

			getWin : function() {
				var win = this.win;
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
								width : 800,
								autoHeight : true,
								iconCls : 'icon-form',
								closeAction : 'hide',
								shim : true,
								layout : "fit",
								plain : true,
								minimizable : true,
								maximizable : true,
								constrain : true,
								shadow : false,
								buttonAlign : 'center',
								items : this.initPanel(this.schema),
								modal : true
							});
					win.on("show", function() {
								this.fireEvent("winShow", this)
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				win.instance = this;
				return win;
			},

			onWinShow : function() {
				this.doNew();
			},

			onBeforePrint : function(type, pages, ids_str) {
				if (this.exContext.args.recordId == null) {
					return false;
				}
				pages.value = ["chis.prints.template.thick"];
				ids_str.value = "&empiId=" + this.exContext.args.empiId
						+ "&recordId=" + this.exContext.args.recordId;
				return true;
			}
		});