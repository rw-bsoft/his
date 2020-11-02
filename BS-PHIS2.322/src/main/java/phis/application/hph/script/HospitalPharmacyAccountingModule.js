/**
 * 住院记账
 * 
 * @author caijy
 */
$package("phis.application.hph.script");

$import("phis.script.SimpleModule");

phis.application.hph.script.HospitalPharmacyAccountingModule = function(cfg) {
	//cfg.noDefaultBtnKey = true;
	this.noDefaultBtnKey = true;
	phis.application.hph.script.HospitalPharmacyAccountingModule.superclass.constructor.apply(this,
			[cfg]);
	this.on("shortcutKey", this.shortcutKeyFunc, this);// 监听快捷键
}

Ext.extend(phis.application.hph.script.HospitalPharmacyAccountingModule,
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
										height : 69,
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
									.createButton())
						});
				this.panel = panel;
				return panel;
			},
			createButton : function() {
				if (this.op == 'read') {
					return [];
				}
				var actions = this.actions;
				var buttons = [];
				if (!actions) {
					return buttons;
				}
				if (this.butRule) {
					var ac = util.Accredit;
					if (ac.canCreate(this.butRule)) {
						this.actions.unshift({
									id : "create",
									name : "新建"
								});
					}
				}
				// var f1 = 112

				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {};
					// btn.accessKey = f1 + i + this.buttonIndex,
					btn.cmd = action.id;
					btn.text = action.name;
					btn.iconCls = action.iconCls || action.id;
					btn.script = action.script;
					btn.handler = this.doAction;
					btn.prop = {};
					Ext.apply(btn.prop, action);
					Ext.apply(btn.prop, action.properties);
					btn.scope = this;
					buttons.push(btn);
				}
				return buttons;
			},
			// 取消
			doNew : function() {
				this.list.doNew();
				this.form.doNew();
			},
			getForm : function() {
				this.form = this.createModule("form", this.refForm);
				this.form.on("clear",this.onClear,this);
				this.form.on("TSEntry", this.onTSEntry, this);
				this.form.on("YSEntry", this.onYSEntry, this);
				return this.form.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				this.list.opener=this;
				//this.list.on("getZyh",this.onGetZyh,this);

				return this.list.initPanel();
			}
			,
			// form查询,清空List
			onClear : function() {
				this.list.doNew();
			},
			doXy : function() {
				var n = this.list.store.getCount()
				var iscontinue = true;
				for (var i = 0; i < n; i++) {
					var r = this.list.store.getAt(i);
					if(r.get("YPXH")==null||r.get("YPXH")==""||r.get("YPXH")==undefined){
						continue;
					}
					iscontinue = false;
					Ext.MessageBox.confirm('提示', '当前已有药品录入，切换后录入信息将清空是否继续',function(button){
						if(button=='yes'){
							this.list.remoteDic.lastQuery = "";
							this.list.doNew();
							this.list.type = 1;
							this.list.remoteDicStore.baseParams.type=1;
							this.list.doCreate();
							this.form.form.getForm().findField("CFTS").hide();
							this.form.form.getForm().findField("CFTS").setValue(1);
						}
					},this);
				}
				if(iscontinue){
					this.list.remoteDic.lastQuery = "";
					this.list.doNew();
					this.list.type = 1;
					this.list.remoteDicStore.baseParams.type=1;
					this.list.doCreate();
					this.form.form.getForm().findField("CFTS").hide();
					this.form.form.getForm().findField("CFTS").setValue(1);
				}
			},
			doZy : function() {
				var n = this.list.store.getCount()
				var iscontinue = true;
				for (var i = 0; i < n; i++) {
					var r = this.list.store.getAt(i);
					if(r.get("YPXH")==null||r.get("YPXH")==""||r.get("YPXH")==undefined){
						continue;
					}
					iscontinue = false;
					Ext.MessageBox.confirm('提示', '当前已有药品录入，切换后录入信息将清空是否继续',function(button){
						if(button=='yes'){
							this.list.remoteDic.lastQuery = "";
							this.list.doNew();
							this.list.type = 2;
							this.list.remoteDicStore.baseParams.type=2;
							this.list.doCreate();
							this.form.form.getForm().findField("CFTS").hide();
							this.form.form.getForm().findField("CFTS").setValue(1);
						}
					},this);
				}
				if(iscontinue){
					this.list.remoteDic.lastQuery = "";
					this.list.doNew();
					this.list.type = 2;
					this.list.remoteDicStore.baseParams.type=2;
					this.list.doCreate();
					this.form.form.getForm().findField("CFTS").hide();
					this.form.form.getForm().findField("CFTS").setValue(1);
				}
			},
			doCy : function() {
				var n = this.list.store.getCount()
				var iscontinue = true;
				for (var i = 0; i < n; i++) {
					var r = this.list.store.getAt(i);
					if(r.get("YPXH")==null||r.get("YPXH")==""||r.get("YPXH")==undefined){
						continue;
					}
					iscontinue = false;
					Ext.MessageBox.confirm('提示', '当前已有药品录入，切换后录入信息将清空是否继续',function(button){
						if(button=='yes'){
							this.list.remoteDic.lastQuery = "";
							this.list.doNew();
							this.list.type = 3;
							this.list.remoteDicStore.baseParams.type=3;
							this.list.doCreate();
							this.form.form.getForm().findField("CFTS").show();
							this.form.form.getForm().findField("CFTS").setValue(1);
						}
					},this);
				}
				if(iscontinue){
					this.list.remoteDic.lastQuery = "";
					this.list.doNew();
					this.list.type = 3;
					this.list.remoteDicStore.baseParams.type=3;
					this.list.doCreate();
					this.form.form.getForm().findField("CFTS").show();
					this.form.form.getForm().findField("CFTS").setValue(1);
				}
			},
			// 记账
			doSave : function() {
				var d01 = this.form.getFormData();
				if (d01 == null) {
					MyMessageTip.msg("提示", "未调入病人信息,无法记账!", true);
					return;
				}
				var d02 = this.getListData();
				if(!d02){
				return;
				}
				if (d02 == null || d02.length == 0) {
					MyMessageTip.msg("提示", "没有明细记录,无法记账!", true);
					return;
				}
				var body={};
				body["d01"] = d01;
				body["d02"] = d02;
				body["yplx"] = this.list.type;
				this.panel.el.mask("正在保存...", "x-mask-loading");
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.saveActionId,
							body : body
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.doSave);
					this.panel.el.unmask();
					return;
				}
				this.panel.el.unmask();
				this.doNew();
				MyMessageTip.msg("提示", "保存成功!", true);
			},
			getListData : function() {
				var ck02 = new Array();
				var count = this.list.store.getCount();
				for (var i = 0; i < count; i++) {
					if (this.list.store.getAt(i).data["YPXH"] != ''
							&& this.list.store.getAt(i).data["YPXH"] != null
							&& this.list.store.getAt(i).data["YPXH"] != 0
							&& this.list.store.getAt(i).data["YPCD"] != ''
							&& this.list.store.getAt(i).data["YPCD"] != 0
							&& this.list.store.getAt(i).data["YPCD"] != null
							&& this.list.store.getAt(i).data["YPSL"] != ''
							&& this.list.store.getAt(i).data["YPSL"] != 0
							&& this.list.store.getAt(i).data["YPSL"] != null) {
						if (this.list.store.getAt(i).data.LSJE > 99999999.99) {
							MyMessageTip.msg("提示",
									"第" + (i + 1) + "行零售金额超过最大值", true);
							this.panel.el.unmask();
							return false;
						}
						if (this.list.store.getAt(i).data.JHJE > 99999999.99) {
							MyMessageTip.msg("提示",
									"第" + (i + 1) + "行进货金额超过最大值", true);
							this.panel.el.unmask();
							return false;
						}
						if(this.list.store.getAt(i).data.KCSB==0&&this.list.store.getAt(i).data.YPSL>0){
						MyMessageTip.msg("提示",
									"第" + (i + 1) + "行无库存", true);
							this.panel.el.unmask();
							return false;
						}
						if(this.list.store.getAt(i).data.KCSB==0&&this.list.store.getAt(i).data.YPSL<0){
						MyMessageTip.msg("提示",
									"第" + (i + 1) + "行未找到对应的记账记录", true);
							this.panel.el.unmask();
							return false;
						}
						ck02.push(this.list.store.getAt(i).data);
					}
				}
				return ck02;
			},
			//帖数回车后
			onTSEntry:function(){
			var count=this.list.store.getCount();
			if(count==0){
			this.list.doCreate();
			}else{
			var ypxh=this.list.store.getAt(count-1).get("YPXH");
			if(ypxh==null||ypxh==""||ypxh==undefined){
			this.list.grid.startEditing(count-1, 1);
			}else{
			this.list.grid.startEditing(count-1, 6);
			}
			}
			},
			//医生回车后
			onYSEntry:function(){
				if(!this.form.form.getForm().findField("CFTS").hidden){
				this.form.form.getForm().findField("CFTS").focus();
				}else{
				this.onTSEntry();
				}

			},
			getZyh:function(){
			return this.form.data.ZYH;

			},
			alt_1 : function() {
				this.doXy();
			},
			alt_2 : function() {
				this.doZy();
			},
			alt_3 : function() {
				this.doCy();
			},
			alt_Q : function() {
				this.doNew();
			},
			alt_A : function() {
				this.doSave();
			},
			alt_C : function() {
				this.list.doCreate();
			},
			alt_D : function() {
				this.list.doRemove();
			}
		});