/**
 * 门诊收费处理
 * 
 * @author caijy
 */
$package("phis.application.ivc.script");

$import("phis.script.SimpleModule");

phis.application.ivc.script.RefundProcessingModule = function(cfg) {
	cfg.modal = true;
	phis.application.ivc.script.RefundProcessingModule.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.ivc.script.RefundProcessingModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var button = this.createButton();
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
//										split : true,
										title : '',
										region : 'north',
										height : 41,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
//										split : true,
										title : '',
										region : 'center',
										items : this.getModule1()
									}],
							tbar : (this.tbar || [])
									.concat(button)
						});
				this.panel = panel;
				return panel;
			},
			getModule1 : function() {
				this.tfModule1 = this.createModule("tfModule1", this.refModule);
				this.tfModule1.opener = this;
				return this.tfModule1.initPanel();
			},
			getForm : function() {
				this.tfForm1 = this.createModule("tfForm1", this.refForm);
				this.tfForm1.opener = this;
				return this.tfForm1.initPanel();
			},
			createButton:function(){
				if(this.op == 'read'){
					return [];
				}
				var actions = [
				       		{id:"tfcl",name:"退费",iconCls:"writeoff"}
				    	];
				var buttons = [];
				if(!actions){
					return buttons;
				}
				if(this.butRule){
				    var ac = util.Accredit;
				    if(ac.canCreate(this.butRule)){
				    	this.actions.unshift({id:"create",name:"新建"});
				    }
				}
				var f1 = 112;

				for(var i = 0; i < actions.length; i ++){
					var action = actions[i];
					var btn = {}
					btn.accessKey = f1 + i + this.buttonIndex;
					btn.cmd = action.id;
					btn.text = action.name + "(F" + (i + 1) + ")";
					btn.iconCls = action.iconCls || action.id
					btn.script =  action.script;
					btn.handler = this.doAction;
					btn.prop = {};
					Ext.apply(btn.prop, action);
					Ext.apply(btn.prop, action.properties);
					btn.scope = this;
					buttons.push(btn);
				}
				return buttons;
			},
			loadData:function(fphm){
				this.tfModule1.loadData(fphm);
			},
			setDetails : function(details){
				this.details = details;
				this.tfModule1.setDetails(details);
			},
			doTfcl : function(){
				if(!this.details)return;
				var store = this.tfModule1.tfList1.grid.getStore();
				var n = store.getCount();
				var tf01s = [];
				var tf02s = [];
				var tf03s = [];
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					if (!r.data.BZXX || r.data.BZXX=='新处方' || r.data.BZXX=='已发药' || r.data.BZXX=='已执行') {
						tf01s.push(r.data);
					}
					if (!r.data.BZXX || r.data.BZXX=='已发药' || r.data.BZXX=='已执行') {
						tf02s.push(r.data);
					}else if (r.data.BZXX!='新处方') {
						tf03s.push(r.data);
					}
				}
				this.MZXX.FZXX = tf02s;
				this.MZXX.TFXX = tf03s;
				if(tf01s.length==n){
					Ext.Msg.alert("提示", "必须选择退费单据才能退费，可通过空格键或双击进行选择!");
					return;
				}
				var data = [];
				for (var i = 0; i < tf01s.length; i++) {
					var tf01 = tf01s[i];
					for (var j = 0; j < this.details.length; j++) {
						var ddata = this.details[j];
						if(tf01.CFLX==ddata.CFLX && tf01.CFSB==ddata.CFSB&&ddata.ZFYP!=1){
							data.push(ddata);
						}
					}
				}
				var module = this.midiModules["jsModule"];
				if (!module) {
					module = this.createModule("jsModule", this.jsModule);
					module.opener = this;
					this.midiModules["jsModule"] = module;
					module.initPanel();
					module.on("settlement", this.doQx, this);
				}
				module.RightForm.setYfpMsg();
				var win = module.getWin();
				module.LeftForm.setData(data, this.MZXX);
				win.show();
			},
			doQx : function() {
				this.details = false;
				this.tfForm1.doNew();
				this.tfModule1.tfList1.clear();
				this.tfModule1.tfModule2.doNew();
			}
		});