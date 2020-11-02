/**
 * 药品调价新增修改界面
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleModule");

phis.application.sto.script.StorehousePharmacyMedicinesPriceAdjustDetailModule = function(cfg) {
	this.width = 1020;
	this.height = 550;
	phis.application.sto.script.StorehousePharmacyMedicinesPriceAdjustDetailModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehousePharmacyMedicinesPriceAdjustDetailModule,
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
										title : '药品调价单',
										region : 'north',
										height : 100,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getList()
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				return panel;
			},
			getForm : function() {
				this.form = this.createModule("form", this.refForm);
				this.form.on("loadData",this.afterLoad,this);
				return this.form.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				return this.list.initPanel();
			},
			doCancel : function() {
				this.fireEvent("winClose", this);
			},
			doOpneCommit : function(initDataBody) {
				this.initDataBody = initDataBody;
				this.form.op = "update";
				this.form.initDataBody = initDataBody;
				this.form.loadData();
				this.list.requestData.serviceId = this.fullserviceId;
				this.list.requestData.serviceAction = this.queryExecutionDataActionId;
				this.list.requestData.cnd = [
						'and',
						['eq', ['$', 'a.TJFS'], ['i', initDataBody.TJFS]],
						[
								'and',
								['eq', ['$', 'a.TJDH'],
										['i', initDataBody.TJDH]],
								['eq', ['$', 'a.XTSB'],
										['s', initDataBody.XTSB]]]];
				this.list.loadData();
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var script = item.script
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if (script) {
					$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},

			// 保存
			doSave : function() {
				var body=new Array();
				var record=this.list.getSelectedRecords();
				var length=record.length;
				for(var i=0;i<length;i++){
//					if(record[i].data.KCSB==0){
//					continue;
//					}
//					body[record[i].data.KCSB]=record[i].data.YFSB;
					var b={};
					b["YPXH"]=record[i].data.YPXH;
					b["YPCD"]=record[i].data.YPCD;
					b["KCSB"]=record[i].data.KCSB;
					if(record[i].data.YFSB==0){//药库
					b["TAG"]=1;
					b["YKSB"]=record[i].data.YKSB;
					}else{
					b["TAG"]=2;
					b["YFSB"]=record[i].data.YFSB;
					}
					body.push(b)
				}
				this.panel.el.mask("正在保存数据...", "x-mask-loading");
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.savePriceAdjustActionId,
							cnd : this.list.requestData.cnd,
							body:body
						});
				this.panel.el.unmask();
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					this.fireEvent("winClose", this);
					this.fireEvent("commit", this);
				}
			},
			//查看
			doRead : function(initDataBody) {
				this.setButtonsState(["save"],false);
				this.initDataBody = initDataBody;
				this.form.op = "update";
				this.form.initDataBody = initDataBody;
				this.form.loadData();
				this.list.requestData.serviceId = this.fullserviceId;
				this.list.requestData.serviceAction = this.queryExecutionedDataActionId;
				this.list.requestData.cnd = [
						'and',
						['eq', ['$', 'a.TJFS'], ['i', initDataBody.TJFS]],
						[
								'and',
								['eq', ['$', 'a.TJDH'],
										['i', initDataBody.TJDH]],
								['eq', ['$', 'a.JGID'],
										['s', initDataBody.JGID]]]];
				this.list.loadData();
			},
			// 改变按钮状态公用方法
			 setButtonsState : function(m, enable) {
			 var btns;
			 var btn;
			 btns = this.panel.getTopToolbar();
			 if (!btns) {
			 return;
			 }
			 for (var j = 0; j < m.length; j++) {
			 if (!isNaN(m[j])) {
			 btn = btns.items.item(m[j]);
			 } else {
			 btn = btns.find("cmd", m[j]);
			 btn = btn[0];
			 }
			 if (btn) {
			 (enable) ? btn.enable() : btn.disable();
			 }
			 }
			 },
			afterLoad:function(entryName,body){
				this.panel.items.items[0].setTitle("NO: "+body.TJDH);
			}
			 
		});