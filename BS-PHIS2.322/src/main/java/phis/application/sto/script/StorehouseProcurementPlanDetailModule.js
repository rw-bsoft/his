/**
 * 采购计划详细module
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.application.sto.script.StorehouseMySimpleDetailModule");

phis.application.sto.script.StorehouseProcurementPlanDetailModule = function(cfg) {
	cfg.title = "采购计划单";// module的标题
	cfg.formHeigh=60;
	phis.application.sto.script.StorehouseProcurementPlanDetailModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseProcurementPlanDetailModule,
		phis.application.sto.script.StorehouseMySimpleDetailModule, {
			// 改变按钮状态
			changeButtonState : function(state) {
				var actions = this.actions;
				this.form.isRead = false;
				this.list.isRead = false;
				if (state == "read" || state == "sp") {
					this.form.isRead = true;
					this.list.isRead = true;
					this.list.setButtonsState(["create", "remove"], false);
				} else {
					this.list.setButtonsState(["create", "remove"], true);
				}
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					if (action.id == "cancel" || action.id == "print") {
						continue;
					}
					if (state == "read") {
						this.setButtonsState([action.id], false);
					} else if (state == "new") {
						if (action.id == "sp") {
							this.setButtonsState([action.id], false);
							continue;
						}
						this.setButtonsState([action.id], true);
					} else if (state == "sp") {
						if (action.id == "sp") {
							this.setButtonsState([action.id], true);
							continue;
						}
						this.setButtonsState([action.id], false);
					} else {
						this.setButtonsState([action.id], true);
					}
				}

			},
			doZdjh:function(){
				this.panel.el.mask("正在生成...", "x-mask-loading");
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.queryZdjhActionId
						});
				if (ret.code > 300) {
					this.panel.el.unmask();
					this.processReturnMsg(ret.code, ret.msg);
					return ;
				}
				var body=ret.json.body;
				var l=body.length;
				var store = this.list.grid.getStore();
				var o = this.list.getStoreFields(this.list.schema.items)
				var Record = Ext.data.Record.create(o.fields)
				store.removeAll();
				for(var i=0;i<l;i++){
				store.add([ new Record(body[i])]);
				this.list.editRecords.push(body[i]);
				}
				this.panel.el.unmask();
			},
			// 保存
			doSave : function() {
				var body = {};
				var d01 = this.form.getFormData();
				if (d01 == null) {
					return;
				}
				var d02 = this.getListData();
				if (d02 == null || d02.length == 0) {
					return;
				}
				body["d01"] = d01;
				body["d02"] = d02;
				body["op"]=this.op
				this.panel.el.mask("正在保存...", "x-mask-loading");
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.saveActionId,
							body : body
						});
				if (r.code > 300) {
					if (r.code == 700) {
						MyMessageTip.msg("提示", r.msg, true);
						this.panel.el.unmask();
						this.fireEvent("winClose", this);
						this.fireEvent("save", this);
						return;
					}
					this.processReturnMsg(r.code, r.msg, this.doSave);
					this.panel.el.unmask();
					return;
				}
				this.list.doInit();
				this.fireEvent("winClose", this);
				this.fireEvent("save", this);
				this.panel.el.unmask();
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
							&& this.list.store.getAt(i).data["JHSL"] != ''
							&& this.list.store.getAt(i).data["JHSL"] != 0
							&& this.list.store.getAt(i).data["JHSL"] != null) {
						if (this.list.store.getAt(i).data.GJJE > 99999999.99) {
							MyMessageTip.msg("提示",
									"第" + (i + 1) + "行参考金额超过最大值", true);
							this.panel.el.unmask();
							return;
						}
						if (this.list.store.getAt(i).data.GJJE > 99999999.99) {
							MyMessageTip.msg("提示",
									"第" + (i + 1) + "行参考金额超过最大值", true);
							this.panel.el.unmask();
							return;
						}
						ck02.push(this.list.store.getAt(i).data);
					}
				}
				return ck02;
			},
			afterLoad : function(entryName, body) {
				this.panel.items.items[0].setTitle("NO: " + body.JHDH);
			},
			doSp:function(){
			this.doSave();
			},
			doPrint:function(){
			var xtsb=this.form.data.XTSB;
			var jhdh=this.form.data.JHDH;
			if(!xtsb||!jhdh){
			return;
			}
			var url = "resources/phis.prints.jrxml.StorehouseProcurementPlan.print?type=1&XTSB="
						+ xtsb+"&JHDH="+jhdh;
				var LODOP = getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0", "", "", "");
				LODOP.ADD_PRINT_HTM("0", "0", "100%", "100%", util.rmi.loadXML(
								{
									url : url,
									httpMethod : "get"
								}));
				LODOP.SET_PRINT_MODE("PRINT_PAGE_PERCENT", "Full-Width");
				LODOP.PREVIEW();
			}
		});