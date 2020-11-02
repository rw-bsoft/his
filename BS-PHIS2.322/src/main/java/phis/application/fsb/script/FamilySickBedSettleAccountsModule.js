$package("phis.application.fsb.script")

$import("phis.script.SimpleModule")

phis.application.fsb.script.FamilySickBedSettleAccountsModule = function(cfg) {
	cfg.width = 830;
	cfg.height = 490;
	cfg.modal = true;
	Ext.apply(this, app.modules.common)
	phis.application.fsb.script.FamilySickBedSettleAccountsModule.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.fsb.script.FamilySickBedSettleAccountsModule,
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
										region : 'west',
										width : '51%',
										items : this.getList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										width : '49%',
										items : this.getForm()
									}]
						});

				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this)
				return panel;
			},
			onReady : function() {
				this.on("winShow",this.onWinShow,this);
			},
			round2 : function(number, fractionDigits) {
				with (Math) {
					return (round(number * pow(10, fractionDigits)) / pow(10,
							fractionDigits)).toFixed(fractionDigits);
				}
			},
			onWinShow : function(){
				this.list.JKHJ = this.data.JKHJ;
				this.form.doNew();
				this.form.JSLX = this.JSLX;
//				this.form.loadData();
				if(this.data.FYHJ){
					this.data.FYHJ = this.round2(this.data.FYHJ,2);
				}else{
					this.data.FYHJ = "0.00";
				}
				if(this.data.ZFHJ){
					this.data.ZFHJ = this.round2(this.data.ZFHJ,2);
				}else{
					this.data.ZFHJ = "0.00";
				}
				if(this.data.JKHJ){
					this.data.JKHJ = this.round2(this.data.JKHJ,2);
				}else{
					this.data.JKHJ = "0.00";
				}
				this.form.initFormData(this.data)
				this.form.data = this.data;
				this.form.loadData();
//				this.form.form.getForm().findField("BRXZ").getStore().on("load",function(){this.form.form.getForm().findField("BRXZ").setValue(this.data.BRXZ)},this);
				this.list.requestData.cnd = ['and',['eq', ['$', 'a.ZYH'],['i', this.data.ZYH]],['eq', ['$', 'a.JSCS'],['i', 0]],['eq', ['$', 'a.ZFPB'],['i', 0]]];
				this.list.refresh();
			},
			getList : function() {
				var module = this.createModule("refSettleAccountsList",
						this.refList);
				// module.on("loadData", this.initFormData, this);
				this.list = module;
				module.opener = this;
				return module.initPanel();
			},
			getForm : function() {
				var module = this.createModule("refSettleAccountsForm",
						this.refForm);
				module.on("settlement", this.settlement,this);
				module.data = this.data;
				this.form = module;
				var form = module.initPanel();
				var JKJE = module.form.getForm().findField('JKJE');
				JKJE.un("specialkey", module.onFieldSpecialkey, this)
				JKJE.on("specialkey", module.JKJECommit, module);
				JKJE.on("blur", module.JKJEblur, module);
				module.opener = this;
				return form
			},
			settlement : function(){
				this.fireEvent("settlement",this);
				this.doCancel();
			},
			doCancel : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			}

		});