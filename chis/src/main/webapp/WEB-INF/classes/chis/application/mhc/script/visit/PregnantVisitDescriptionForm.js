/**
 * 孕妇随访中医辩体表单页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.visit")
$import("chis.script.BizTableFormView")
chis.application.mhc.script.visit.PregnantVisitDescriptionForm = function(cfg) {
    cfg.heigth = 400
	cfg.colCount = 1;
	cfg.fldDefaultWidth = 500
	cfg.autoFieldWidth = false;
	chis.application.mhc.script.visit.PregnantVisitDescriptionForm.superclass.constructor
			.apply(this, [cfg])
	this.on("loadNoData", this.loadNoData, this)
	this.on("winShow", this.onWinShow, this)
}
Ext.extend(chis.application.mhc.script.visit.PregnantVisitDescriptionForm,
		chis.script.BizTableFormView, {
			getWin : function() {
				var win = this.win
				var closeAction = "hide"
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title || this.name,
								width : this.width || 550,
								autoHeight : true,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								closeAction : closeAction,
								constrainHeader : true,
								constrain : true,
								minimizable : true,
								maximizable : true,
								autoScroll : true,
								shadow : false,
								modal : true,
								items : this.initPanel()
							})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("show", function() {
								this.win.doLayout()
								this.fireEvent("winShow", this)
							}, this)
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() {
								this.fireEvent("close", this)
							}, this)
					this.win = win
				}
				win.instance = this;
				return win;
			},

			loadData : function() {
				if (this.__actived) {
					return;
				}
				chis.application.mhc.script.visit.PregnantVisitDescriptionForm.superclass.loadData
						.call(this);
			},

			getLoadRequest : function() {
				this.initDataId = null;
				return {
					"fieldName" : "visitId",
					"fieldValue" : this.exContext.args.visitId
				};
			},

			loadNoData : function() {
				this.data.pregnantId = this.exContext.ids["MHC_PregnantRecord.pregnantId"];
				this.data.visitId = this.exContext.args.visitId;
			},

			doSave : function() {
				this.fireEvent("recordSave");
			},

			onWinShow : function() {
				this.loadData();
			}
		});