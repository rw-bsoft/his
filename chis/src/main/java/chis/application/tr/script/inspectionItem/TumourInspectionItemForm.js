$package("chis.application.tr.script.inspectionItem");

$import("chis.script.BizTableFormView", "util.widgets.LookUpField",
		"chis.script.util.query.QueryModule");

chis.application.tr.script.inspectionItem.TumourInspectionItemForm = function(
		cfg) {
	chis.application.tr.script.inspectionItem.TumourInspectionItemForm.superclass.constructor
			.apply(this, [cfg]);
	this.on("doNew", this.onDoNew, this);
	this.on("loadData", this.onLoadData, this);
}

Ext.extend(chis.application.tr.script.inspectionItem.TumourInspectionItemForm,
		chis.script.BizTableFormView, {
			onDoNew : function() {
				this.onBigItemIdChange();
			},
			onLoadData : function(entryName, body) {
				var bigItemIdVal = {
					key : body.bigItemId.key,
					text : body.bigItemName
				};
				var frm = this.form.getForm();
				var bigItemIdFld = frm.findField("bigItemId");
				if (bigItemIdFld) {
					bigItemIdFld.setValue(bigItemIdVal);
				}
				this.onBigItemIdChange();
			},
			onReady : function() {
				var frm = this.form.getForm();
				var bigItemIdFld = frm.findField("bigItemId");
				if (bigItemIdFld) {
					bigItemIdFld.on("select", this.onBigItemIdChange, this);
				}
				var definiteItemNameFld = frm.findField("definiteItemName");
				if (definiteItemNameFld) {
					definiteItemNameFld.disable();
					definiteItemNameFld.on("lookup", this.doQuery, this);
					definiteItemNameFld.on("clear", this.clearDN, this);
					definiteItemNameFld.validate();
				}
				chis.application.tr.script.inspectionItem.TumourInspectionItemForm.superclass.onReady
						.call(this);
			},
			clearDN : function() {
				var frm = this.form.getForm();
				var definiteItemNameFld = frm.findField("definiteItemName");
				if (definiteItemNameFld) {
					definiteItemNameFld.setValue();
				}
			},
			onBigItemIdChange : function() {
				var frm = this.form.getForm();
				var bigItemIdFld = frm.findField("bigItemId");
				if (bigItemIdFld) {
					this.bigItemIdVal = bigItemIdFld.getValue();
					var bigItemName = bigItemIdFld.getRawValue();
					this.data.bigItemName = bigItemName;
					this.FYGB = {
						key : this.bigItemIdVal,
						text : bigItemName
					};
					var definiteItemNameFld = frm.findField("definiteItemName");
					if (definiteItemNameFld) {
						if (!this.bigItemIdVal) {
							definiteItemNameFld.disable();
						} else {
							definiteItemNameFld.enable();
						}
					}
				}
			},

			doQuery : function(field) {
				if (!field.disabled) {
					var definiteItemQuery = this.midiModules["definiteItemQuery"];
					if (!definiteItemQuery) {
						definiteItemQuery = new chis.script.util.query.QueryModule(
								{
									title : "检查项目查询",
									autoLoadSchema : true,
									isCombined : true,
									autoLoadData : false,
									mutiSelect : false,
									queryCndsType : "1",
									entryName : "chis.application.tr.schemas.GY_YLSF_Query",
									buttonIndex : 3,
									itemHeight : 125
								});
						this.midiModules["MasterplateMaintainQuery"] = definiteItemQuery;
					}
					definiteItemQuery.on("recordSelected", function(r) {
								if (!r) {
									return;
								}
								var frmData = r[0].data;
								this.initDataBefShow(frmData);
							}, this);
					definiteItemQuery.on("reset",function(form){
						var FYGBFld = form.getForm().findField("FYGB");
						if (FYGBFld) {
							FYGBFld.setValue(this.FYGB);
							FYGBFld.disable();
						}
					},this)
					var win = definiteItemQuery.getWin();
					win.setPosition(250, 100);
					win.show();
					var FYGBFld = definiteItemQuery.form.form.getForm()
							.findField("FYGB");
					if (FYGBFld) {
						FYGBFld.setValue(this.FYGB);
						FYGBFld.disable();
					}
					definiteItemQuery.form.doSelect();
				}
			},

			initDataBefShow : function(frmData) {
				var FYXH = frmData.FYXH;
				var FYMC = frmData.FYMC;
				var frm = this.form.getForm();
				var definiteItemNameFld = frm.findField("definiteItemName");
				if (definiteItemNameFld) {
					definiteItemNameFld.setValue(FYMC);
				}
				this.data.definiteItemId = FYXH;
			},

			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
								title : this.title || this.name,
								width : this.width,
								autoHeight : this.autoHeight || true,
								iconCls : 'icon-form',
								bodyBorder : false,
								closeAction : 'hide',
								shim : true,
								layout : "fit",
								plain : true,
								autoScroll : false,
								// minimizable: true,
								maximizable : true,
								constrain : true,
								shadow : false,
								buttonAlign : 'center',
								modal : true
							})
//					win.on({
//								beforehide : this.confirmSave,
//								beforeclose : this.confirmSave,
//								scope : this
//							})
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("restore", function(w) {
								this.form.onBodyResize()
								this.form.doLayout()
								this.win.doLayout()
							}, this)

					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					this.win = win
				}
				this.afterCreateWin(win);
				return win;
			}
		});