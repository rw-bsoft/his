$package("phis.application.emr.script");

$import("phis.script.SimpleModule");
/**
 * 需传入病历编号，BLBH
 * 
 * @param {}
 *            cfg
 */
phis.application.emr.script.EMRMedicalDetailsModule = function(cfg) {
	phis.application.emr.script.EMRMedicalDetailsModule.superclass.constructor
			.apply(this, [cfg]);
	this.width = 450;
	this.height = 500;
	this.on("beforeWinShow", this.onBeforeWinShow, this);
},

Ext.extend(phis.application.emr.script.EMRMedicalDetailsModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (!this.opener.emr) {
					this.actions.shift();// 删除第一个按钮
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							tbar : this.createButtons(),
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'north',
										height : 270,
										items : this.getRecordInfoForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										items : this.getReviewList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			doSaveAsFile : function() {
				var blmc = this.form.form.getForm().findField("BLMC")
						.getValue();
				var bllb = this.form.form.getForm().findField("BLLB")
						.getRawValue();
				var bllx = this.form.form.getForm().findField("BLLX")
						.getValue();
				this.opener.emr.FunActiveXInterface('BsDocSaveAs',
						this.exContext.empiData.BRXM + "-"
								+ (bllx == 1 ? bllb : blmc)
								+ new Date().format('Ymd'), '3')
			},
			getObject : function(id) {
				if (window.document[id]) {
					return window.document[id];
				}
				if (Ext.isIE) {
					if (document.embeds && document.embeds[id])
						return document.embeds[id];
				} else {
					return document.getElementById(id);
				}
			},
			getRecordInfoForm : function() {
				var module = this.createModule("recordInfo", this.recordInfo);
				this.form = module;
				var gForm = module.initPanel();
				this.gForm = gForm;
				return gForm;
			},
			getReviewList : function() {
				var module = this.createModule("reviewList", this.reviewList);
				this.list = module;
				var grid = module.initPanel();
				this.grid = grid;
				return grid;
			},
			doCancel : function() {
				this.win.close();
			},
			onBeforeWinShow : function() {
				if (!this.BLBH || this.initDataId == this.BLBH) {
					return;
				}
				this.initDataId = this.BLBH;
				this.form.initDataId = this.initDataId
				this.form.loadData();
				this.list.requestData.serviceId = "phis.medicalRecordsQueryService";
				this.list.requestData.serviceAction = "getReviewRecord";
				this.list.requestData.pkey = this.initDataId;
				this.list.loadData();
			},
			// loadData:function(){
			// this.onWinShow();
			// },

			getWin : function() {
				var win = this.win;
				var closeAction = "close";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.name,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								constrain : true,
								resizable : false,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : this.modal || true,
								items : this.initPanel()
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("beforeshow", function() {
								this.fireEvent("beforeWinShow");
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this);
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				return win;
			}

		});