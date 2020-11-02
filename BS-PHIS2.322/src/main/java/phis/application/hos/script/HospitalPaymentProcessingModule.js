$package("phis.application.hos.script")

$import("phis.script.SimpleModule")

phis.application.hos.script.HospitalPaymentProcessingModule = function(cfg) {
	phis.application.hos.script.HospitalPaymentProcessingModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.hos.script.HospitalPaymentProcessingModule,
		phis.script.SimpleModule, {
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
				var module = this.createModule("refPaymentProcessingList",
						this.refList);

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
				var module = this.createModule("refPaymentProcessingForm",
						this.refForm);
				module.on("loadData", this.listLoadData, this);
				module.on("reSet", this.listClear, this);
				module.opener = this;
				var formModule = module.initPanel();
				var form = module.form.getForm();
				var ZYHM = form.findField("ZYHM")
				var BRCH = form.findField("BRCH")
				var BRXM = form.findField("BRXM")
				ZYHM.un("specialkey", module.onFieldSpecialkey, module)
				ZYHM.on("specialkey", function(ZYHM, e) {
							var key = e.getKey()
							if (key == e.ENTER) {
								if (ZYHM.getValue()) {
									module.doEnter(ZYHM)
								} else {
									var form = module.form.getForm();
									form.findField("BRCH").focus(false, 100);
								}
							}
						}, module);
				ZYHM.focus(false, 200);
				BRCH.un("specialkey", module.onFieldSpecialkey, module)
				BRCH.on("specialkey", function(BRCH, e) {
							var key = e.getKey()
							if (key == e.ENTER) {
								if (BRCH.getValue()) {
									module.doEnter(BRCH)
								}
							}
						}, module);
				BRXM.un("specialkey", module.onFieldSpecialkey, module)
				BRXM.on("specialkey", function(BRXM, e) {
							var key = e.getKey()
							if (key == e.ENTER) {
								if (BRXM.getValue()) {
									this.doEnterBRXM(BRXM.getValue())
								}
							}
						}, this);
				this.form = module;
				return formModule
			},
			doEnterBRXM : function(BRXM){
				var module = this.midiModules["zybrlist"];
				if (!module) {
					module = this.createModule("zybrlist", this.zybrList);
					module.on("loadData", this.zybrloadData, this);
					module.on("zybrChoose", this.zybrChoose, this);
					module.requestData.cnd = ['and',
					                          ['like',[ '$','a.BRXM' ],[ 's', BRXM+'%']],
					                          ['eq',[ '$','a.JGID' ],[ 's', this.mainApp.deptId]],
					                          ['le',[ '$','a.CYPB' ],[ 'i', 8]]
					];
					this.midiModules["zybrlist"] = module;
					module.opener = this;
					var sm = module.initPanel();
					var win = module.getWin();
					module.loadData();
					win.add(sm);
				} else {
					module.requestData.cnd = ['and',
					                          ['like',[ '$','a.BRXM' ],[ 's', BRXM+'%']],
					                          ['eq',[ '$','a.JGID' ],[ 's', this.mainApp.deptId]],
					                          ['le',[ '$','a.CYPB' ],[ 'i', 8]]
					];
					module.loadData();
				}
			},
			zybrloadData : function(store){
				if(store.getCount()>1){
					var win = this.midiModules["zybrlist"].getWin();
					win.show();
				}else if(store.getCount()==1){
					var row = store.getAt(0);
					var form = this.form.form.getForm();
					var ZYHM = form.findField("ZYHM");
					ZYHM.setValue(row.data.ZYHM);
					this.form.doEnter(ZYHM);
				}
			},
			zybrChoose : function(record){
				var form = this.form.form.getForm();
				var ZYHM = form.findField("ZYHM");
				ZYHM.setValue(record.data.ZYHM);
				this.form.doEnter(ZYHM);
				var win = this.midiModules["zybrlist"].getWin();
				win.hide();
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