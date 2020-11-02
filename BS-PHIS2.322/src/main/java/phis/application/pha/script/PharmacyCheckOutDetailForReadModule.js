/**
 * 药品出库新增修改界面
 * 
 * @author caijy
 */
$package("phis.application.pha.script");

$import("phis.script.SimpleModule");

phis.application.pha.script.PharmacyCheckOutDetailForReadModule = function(cfg) {
	this.width = 1020;
	this.height = 550;
	cfg.modal = this.modal = true;
	phis.application.pha.script.PharmacyCheckOutDetailForReadModule.superclass.constructor
			.apply(this, [cfg]);
	this.on('doSave', this.doSave, this);
	this.on("beforeclose", this.doClose, this);
}
Ext.extend(phis.application.pha.script.PharmacyCheckOutDetailForReadModule,
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
										title : '药品出库单',
										region : 'north',
										width : 960,
										height : 65,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										width : 960,
										items : this.getList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'south',
										width : 960,
										height : 65,
										items : this.getDyList()
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				return panel;
			},
			doNew : function() {
			},
			getForm : function() {
				this.form = this.createModule("form", this.refForm);
				this.form.on("loadData",this.afterLoad,this);
				return this.form.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				this.list.on("loadData", this.onLoadData, this);
				return this.list.initPanel();
			},
			getDyList : function() {
				this.dyForm = this.createModule("dyForm", this.dyForm);
				return this.dyForm.initPanel();
			},
			doCancel : function() {
				this.fireEvent("winClose", this);
			},
			doClose : function() {
				
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
						// 查看
			doRead : function(initDataBody) {
				this.doLoad(initDataBody);
			},
			// 修改,查看,提交数据回填
			doLoad : function(initDataBody) {
				this.form.op = "update";
				this.form.initDataBody = initDataBody;
				this.form.loadData();
				this.list.op = "create";
				this.list.requestData.cnd = [
						'and',
						['eq', ['$', 'a.CKFS'], ['i', initDataBody.CKFS]],
						[
								'and',
								['eq', ['$', 'a.CKDH'],
										['i', initDataBody.CKDH]],
								['eq', ['$', 'a.YFSB'],
										['i', initDataBody.YFSB]]]];
				this.list.loadData();
				
			},
			// 页面加载的时候计算零售合计和进货合计
			onLoadData : function(store) {
				if (store) {
				var count=store.getCount();
				var allJhje=0;
				var allLsje=0;
				for(var i=0;i<count;i++){
				var jhje= store.getAt(i).data["JHJE"];
				var lsje=store.getAt(i).data["LSJE"];
				allJhje= parseFloat(parseFloat(allJhje) + parseFloat(jhje)).toFixed(4);
				allLsje= parseFloat(parseFloat(allLsje) + parseFloat(lsje)).toFixed(4);
				}
				this.dyForm.doNew();
				this.dyForm.addJe(allJhje, allLsje);
				}
			},
			// 明细增加的时候 增加下面的合计
			onRecordAdd : function(jhje, lsje) {
				this.dyForm.addJe(jhje, lsje);
			},
			afterLoad:function(entryName,body){
				this.panel.items.items[0].setTitle("NO: "+body.CKDH);
			}

		});