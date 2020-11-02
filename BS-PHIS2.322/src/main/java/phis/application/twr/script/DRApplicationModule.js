$package("phis.application.twr.script");

$import("phis.script.SimpleModule", "org.ext.ux.layout.TableFormLayout",
		"app.desktop.Module");

phis.application.twr.script.DRApplicationModule = function(cfg) {
	phis.application.twr.script.DRApplicationModule.superclass.constructor
			.apply(this, [ cfg ]);
}

Ext.extend(phis.application.twr.script.DRApplicationModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					this.jcmodule.doNew();
					return this.panel;
				}
				var panel = new Ext.Panel({
					border : false,
					width : 1000,
					height : 500,
					frame : true,
					layout : 'border',
					defaults : {
						border : false
					},
					buttonAlign : 'center',
					items : [ {
						layout : "fit",
						border : false,
						split : true,
						title : '',
						region : 'north',
						height : 250,
						items : this.getForm()
					}, {
						layout : "fit",
						border : false,
						split : true,
						title : '',
						region : 'center',
						items : this.getList()
					} ]
				});
				this.panel = panel;
				return panel;
			},

			getForm : function() {
				var jcmodule = this.createModule("form", this.refForm);
				jcmodule.empiId = this.exContext.ids.empiId;
				jcmodule.on("submit", this.onSubmit, this);
				jcmodule.on("cancel", this.onCancel, this);
				jcmodule.on("print", this.onPrint, this);
				jcmodule.on("close", this.onClose, this);
				jcmodule.exContext = this.exContext;
				jcmodule.opener=this;
				this.jcmodule = jcmodule;
				var form = jcmodule.initPanel();
				jcmodule.loadData();
				return form;
			},
			getList : function() {
				var jcList = this.createModule("List", this.refList);
				jcList.exContext = this.exContext;
				jcList.opener=this;
				this.jcList = jcList;
				var list = jcList.initPanel();
				return list;
			},
			onSubmit : function() {
				this.jcList.brxxform = this.jcmodule.form.getForm();
				this.jcList.brid = this.jcmodule.brid;
				this.jcList.mzhm = this.jcmodule.mzhm;
				this.jcList.csny = this.jcmodule.csny;
				this.jcList.brxb = this.jcmodule.brxb;
				this.jcList.empiId = this.exContext.ids.empiId;
				this.jcList.zzzd = this.jcmodule.zzzd;
				this.jcList.doSave();
			},
			onClose : function() {
				this.getWin().hide();
				return true;
			}
		});