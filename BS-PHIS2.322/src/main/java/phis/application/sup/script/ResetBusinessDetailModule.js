$package("phis.application.sup.script");
$import("phis.script.SimpleModule");

phis.application.sup.script.ResetBusinessDetailModule = function(cfg) {
	this.width = 1024;
	this.height = 500;
	phis.application.sup.script.ResetBusinessDetailModule.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.sup.script.ResetBusinessDetailModule,
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
										title : '',
										region : 'north',
										width : 960,
										height : 85,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										width : 960,
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
				return this.form.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				this.list.zblb = this.zblb;
				this.list.grid = this.list.initPanel();
				return this.list.grid;
			},
			doAction : function(item, e) {
				var cmd = item.cmd
				var script = item.script
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if (script) {
					$require(script, [function() {
								eval(script + '.do' + cmd + '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			doNew : function() {
				this.changeButtonState("blank");
				this.form.op = "create";
				this.form.doNew();
				this.list.op = "create";
				this.list.clear();
			},
			doSave : function(winclose) {
				this.panel.el.mask("正在保存...", "x-mask-loading");
				var body = {};
				body["WL_CZ02"] = [];
				var count = this.list.store.getCount();
				for (var i = 0; i < count; i++) {
					var CZFS = this.form.getFormData().CZFS;
					if(CZFS != 1){
						if(this.list.store.getAt(i).data["ZCYZ"] < this.list.store.getAt(i).data["ZCXZ"]){
							Ext.Msg.alert("提示", "第" + (i + 1) + "行增減值不能大于原值!");
							this.panel.el.unmask();
							return false;
						}
					}
					if (this.list.store.getAt(i).data["ZCXZ"] == 0
							|| this.list.store.getAt(i).data["ZCXZ"] == null) {
						Ext.Msg.alert("提示", "第" + (i + 1) + "行增減值为0!");
						this.panel.el.unmask();
						return false;
					}
					body["WL_CZ02"].push(this.list.store.getAt(i).data);
				}
				if (body["WL_CZ02"].length < 1) {
					this.panel.el.unmask();
					Ext.Msg.alert("提示", "没有明细信息,保存失败");
					return false;
				}
				body["WL_CZ01"] = this.form.getFormData();
				if (!body["WL_CZ02"]) {
					this.panel.el.unmask();
					return false;
				}
				body["WL_CZ01"].ZBLB = this.zblb;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "resetBusinessService",
							serviceAction : "saveCheckIn",
							body : body,
							op : this.op
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					this.panel.el.unmask();
					return false;
				} else {
					if (winclose != false) {
						this.fireEvent("winClose", this);
					}
					this.fireEvent("save", this);
				}
				this.panel.el.unmask();
				return true;
			},
			doClose : function() {
				this.fireEvent("winClose", this);
			},
			beforeClose : function(winclose) {
				if (this.list.editRecords && this.list.editRecords.length > 0) {
					if (confirm('数据已经修改，是否保存?')) {
						return this.doSave(winclose)
					} else {
						return true;
					}
				}
				return true;
			},
			changeButtonState : function(state) {
				var actions = this.actions;
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					this.setButtonsState([action.id], false);
				}
				if (state == "blank") {
					this.setButtonsState(["create", "import", "save","close"], true);
				}
				if (state == "new") {
					this.setButtonsState(["create", "import", "save","verify","close"], true);
				}
				if (state == "verified") {
					this.setButtonsState(["cancelVerify", "commit","close"], true);
				}
				if (state == "commited") {
					this.setButtonsState(["close"], true);
				}
			},
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
			//引入
			doImport : function(){
				this.resetBusinessDetailCZList = this.createModule("resetBusinessDetailCZList", this.refImport);
				this.resetBusinessDetailCZList.on("save", this.onSave, this);
				this.resetBusinessDetailCZList.on("winClose", this.onClose, this);
				this.resetBusinessDetailCZList.on("checkData", this.onCheckData,this);
                this.resetBusinessDetailCZList.requestData.cnd =['and',['and',['and',['eq', ['$', 'a.KFXH'],['$','%user.properties.treasuryId']]],
                                                               ['eq', ['$', 'a.WZZT'],['i',1]]],
                                                               ['eq', ['$', 'a.ZBLB'],['i',this.zblb]]];
				this.resetBusinessDetailCZList.initPanel();
				var win = this.resetBusinessDetailCZList.getWin();
				win.add(this.resetBusinessDetailCZList.initPanel());
				this.resetBusinessDetailCZList.loadData();
				win.show()
				win.center()
				if (!win.hidden) {
					this.resetBusinessDetailCZList.op = "create";
				}
			},
			// 审核
			doVerify : function() {
				if (this.form.getFormData().DJZT != 0) {
					Ext.Msg.alert("提示", "非新增状态，不能审核");
					return;
				}
				// 判断单据状态是否异步改变
				var body = {}
				body["DJXH"] = this.form.getFormData().DJXH;
				var r1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : "resetBusinessService",
							serviceAction : "getDjztByDjxh",
							body : body,
							op : this.op
						});
				if (r1.json.djzt != this.form.getFormData().DJZT) {
					Ext.Msg.alert("提示", "单据状态已改变，不能审核");
					return;
				}
				
				var body = {};
				body["WL_CZ02"] = [];
				var count = this.list.store.getCount();
				if(count == 0){
					return ;
				}
				var CZFS =  this.form.getFormData().CZFS;
				for (var i = 0; i < count; i++) {
					this.list.store.getAt(i).data["CZFS"] = CZFS ;
					this.list.store.getAt(i).data["ZBLB"] = this.zblb;
					body["WL_CZ02"].push(this.list.store.getAt(i).data);
				}

				// 增加库存,记账
				body["WL_CZ01"] = this.form.getFormData();
				body["WL_CZ01"].ZBLB = this.zblb;
				
				if (!body["WL_CZ01"]) {
					return;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "resetBusinessService",
							serviceAction : "verify",
							body : body,
							op : this.op
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					MyMessageTip.msg("提示", "审核成功!", true);
					this.loadData(this.initDataBody);
					this.changeButtonState("verified");
					this.fireEvent("save", this);
				}
			},
			// 反审核
			doCancelVerify : function() {
				if (this.form.getFormData().DJZT != 1) {
					Ext.Msg.alert("提示", "非审核状态，不能弃审");
					return;
				}
				// 判断单据状态是否异步改变
				var body = {}
				body["DJXH"] = this.form.getFormData().DJXH;
				var r1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : "resetBusinessService",
							serviceAction : "getDjztByDjxh",
							body : body,
							op : this.op
						});
				if (r1.json.djzt != this.form.getFormData().DJZT) {
					Ext.Msg.alert("提示", "单据状态已改变，不能审核");
					return;
				}
				body["WL_CZ01"] = this.form.getFormData();
				if (!body["WL_CZ01"]) {
					return;
				}
				body["WL_CZ02"] = [];
				var count = this.list.store.getCount();
				if(count == 0){
					return ;
				}
				for (var i = 0; i < count; i++) {
					body["WL_CZ02"].push(this.list.store.getAt(i).data);
				}
				if (!body["WL_CZ02"]) {
					return;
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "resetBusinessService",
							serviceAction : "cancelVerify",
							body : body,
							op : this.op
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					MyMessageTip.msg("提示", "弃审成功!", true);
					this.loadData(this.initDataBody);
					this.changeButtonState("new");
					this.fireEvent("save", this);
				}
			},
			// 记账
			doCommit : function() {
				if (this.form.getFormData().DJZT != 1) {
					Ext.Msg.alert("提示", "非审核状态，不能记账");
					return;
				}
				// 判断单据状态是否异步改变
				var body = {}
				body["DJXH"] = this.form.getFormData().DJXH;
				var r1 = phis.script.rmi.miniJsonRequestSync({
							serviceId : "resetBusinessService",
							serviceAction : "getDjztByDjxh",
							body : body,
							op : this.op
						});
				if (r1.json.djzt != this.form.getFormData().DJZT) {
					Ext.Msg.alert("提示", "单据状态已改变，不能审核");
					return;
				}
				
				var body = {};
				body["WL_CZ02"] = [];
				var count = this.list.store.getCount();
				if(count == 0){
					return ;
				}
				var CZFS =  this.form.getFormData().CZFS;
				for (var i = 0; i < count; i++) {
					this.list.store.getAt(i).data["CZFS"] = CZFS ;
					this.list.store.getAt(i).data["ZBLB"] = this.zblb;
					body["WL_CZ02"].push(this.list.store.getAt(i).data);
				}

				// 增加库存,记账
				body["WL_CZ01"] = this.form.getFormData();
				body["WL_CZ01"].ZBLB = this.zblb;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "resetBusinessService",
							serviceAction : "saveCommit",
							body : body,
							op : this.op
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					this.panel.el.unmask();
					return false;
				} else {
					this.fireEvent("winClose", this);
					this.fireEvent("save", this);
				}
			},
			// 修改
			loadData : function(initDataBody) {
				this.listIsUpdate = true;
				this.doLoad(initDataBody, true);
			},
			// 修改,查看,提交数据回填
			doLoad : function(initDataBody) {
				this.form.op = "update";
				this.form.initDataId = initDataBody.DJXH;
				this.form.loadData();
				this.list.op = "create";
				this.list.requestData.cnd = ['eq', ['$', 'DJXH'],['i', initDataBody.DJXH]];
				this.list.loadData();
			},
			onCheckData : function(records) {
				var store = this.list.grid.getStore();
				this.list.clear();
				for(var i = 0 ;i<records.length;i++){
					var record = records[i];
					record.data.ZCYZ = record.data.CZYZ;
				}
				store.add(records);
				this.onClose();
			},
			onClose : function() {
				this.resetBusinessDetailCZList.getWin().hide();
			},
			onSave : function() {
				this.fireEvent("save", this);
			}
			
		});