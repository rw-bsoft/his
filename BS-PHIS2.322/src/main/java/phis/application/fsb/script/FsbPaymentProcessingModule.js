$package("phis.application.fsb.script")

$import("phis.script.SimpleModule")

phis.application.fsb.script.FsbPaymentProcessingModule = function(cfg) {
	phis.application.fsb.script.FsbPaymentProcessingModule.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(phis.application.fsb.script.FsbPaymentProcessingModule, phis.script.SimpleModule, {
	initPanel : function() {
		if (this.panel) {
			return this.panel;
		}
		var panel = new Ext.Panel({
					border : false,
					width : this.width,
					height : this.height,
					frame : true,
					layout : 'border',
					defaults : {
						border : false
					},
					items : [{
								layout : "fit",
								border : false,
								split : true,
								region : 'center',

								items : this.getList()
							}, {
								layout : "fit",
								border : false,
								split : true,
								region : 'east',
								width : '300',
								items : this.getForm()
							}]
				});

		this.panel = panel;
		this.panel.on("afterrender", this.onReady, this)
		return panel;
	},
	onReady : function() {
		this.form.loadData();
	},
	getList : function() {
		var module = this
				.createModule("refPaymentProcessingList", this.refList);

		this.list = module;
		this.list.cnd = ['and', ['eq', ['$', 'a.ZYH'], ['i', 0]],
				['eq', ['$', 'a.ZFPB'], ['i', 0]],
				['eq', ['$', 'a.JSCS'], ['i', 0]],
				['eq', ['$', 'a.JGID'], ['s', this.mainApp['phisApp'].deptId]]];
		this.list.requestData.cnd = this.list.cnd;
		module.opener = this;
		return module.initPanel();
	},
	getForm : function() {
		var module = this
				.createModule("refPaymentProcessingForm", this.refForm);
		module.on("loadData", this.listLoadData, this);
		module.on("reSet", this.listClear, this);
		module.opener = this;
		var formModule = module.initPanel();
		var form = module.form.getForm();
		var ZYHM = form.findField("ZYHM")
		ZYHM.un("specialkey", module.onFieldSpecialkey, module)
		ZYHM.on("specialkey", function(ZYHM, e) {
					var key = e.getKey()
					if (key == e.ENTER) {
						if (ZYHM.getValue()) {
							module.doEnter(ZYHM)
						} else {
							var form = module.form.getForm();
							form.findField("JKJE").focus(false, 100);
						}
					}
				}, module);
		ZYHM.focus(false, 200);
		this.form = module;
		return formModule
	},
	listClear : function() {
		this.list.cnd = ['and', ['eq', ['$', 'a.ZYH'], ['i', 0]],
				['eq', ['$', 'a.ZFPB'], ['i', 0]],
				['eq', ['$', 'a.JSCS'], ['i', 0]],
				['eq', ['$', 'a.JGID'], ['s', this.mainApp['phisApp'].deptId]]];
		this.list.requestData.cnd = this.list.cnd;
		this.list.clear();
	},
	listLoadData : function(zyh) {
		this.list.cnd = ['and', ['eq', ['$', 'a.ZYH'], ['i', zyh]],
				['eq', ['$', 'a.ZFPB'], ['i', 0]],
				['eq', ['$', 'a.JSCS'], ['i', 0]],
				['eq', ['$', 'a.JGID'], ['s', this.mainApp['phisApp'].deptId]]];
		this.list.requestData.cnd = this.list.cnd;
		this.list.refresh();
	}

});