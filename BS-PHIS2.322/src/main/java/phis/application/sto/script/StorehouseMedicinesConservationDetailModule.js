/**
 * 采购入库详细module
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.application.sto.script.StorehouseMySimpleDetailModule");

phis.application.sto.script.StorehouseMedicinesConservationDetailModule = function(cfg) {
	cfg.title = "药品养护单";// module的标题
	phis.application.sto.script.StorehouseMedicinesConservationDetailModule.superclass.constructor.apply(
			this, [cfg]);
			this.on("winShow", this.onWinShow, this)
}
Ext.extend(phis.application.sto.script.StorehouseMedicinesConservationDetailModule,
				phis.application.sto.script.StorehouseMySimpleDetailModule, {
			//保存
			doSave : function() {
				var ed=this.list.grid.activeEditor;
				if (!ed) {
						ed = this.list.grid.lastActiveEditor;
					}
					if(ed){
					ed.completeEdit();
					}
				var body = {};
				body["YK_YH01"] = this.form.getFormData();
				if (!body["YK_YH01"]) {
					return;
				}
				body["YK_YH01"].XTSB=this.mainApp['phis'].storehouseId;
				body["YK_YH02"] = this.getListData();
				if(body["YK_YH02"]==0){
				return;
				}
				if(body["YK_YH02"]==null){
				MyMessageTip.msg("提示", "没有需要养护的明细", true);
				return;
				}
				this.panel.el.mask("正在保存...", "x-mask-loading");
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.saveActionId,
							body : body,
							op:this.op
						});
					if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.doSave);
					this.panel.el.unmask();
					return;
				}
				this.fireEvent("close", this);
				this.fireEvent("save", this);
				this.panel.el.unmask();
				MyMessageTip.msg("提示", "保存成功!", true);
				this.doLoad(r.json.body,"update");
						
			},//提交
			doCommit:function(){
			var ed=this.list.grid.activeEditor;
				if (!ed) {
						ed = this.list.grid.lastActiveEditor;
					}
					if(ed){
					ed.completeEdit();
					}
				var body = {};
				body["YK_YH01"] = this.form.getFormData();
				if (!body["YK_YH01"]) {
					return;
				}
				body["YK_YH02"] = this.getListData();
				if(body["YK_YH02"]==null){
				MyMessageTip.msg("提示", "没有需要养护的明细", true);
				return;
				}
				this.panel.el.mask("正在保存...", "x-mask-loading");
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.commitActionId,
							body : body
						});
					if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.doSave);
					this.panel.el.unmask();
					return;
				}
				this.fireEvent("close", this);
				this.fireEvent("save", this);
				this.panel.el.unmask();
				MyMessageTip.msg("提示", "确定成功!", true);
			},
			// 修改,查看,提交数据回填
			doLoad : function(initDataBody,op) {
				this.op="update";
				this.form.op = op;
				this.form.initDataBody = initDataBody;
				this.form.loadData();
				this.list.op = op;
				this.list.requestData.body ={"YHDH":initDataBody.YHDH,"XTSB":initDataBody.XTSB};
				this.list.loadData();
			},
			afterLoad:function(entryName,body){
				this.panel.items.items[0].setTitle("NO: "+body.YHDH);
			},
			//新增
			doNew : function() {
				this.op="create";
				this.changeButtonState("new")
				this.form.op = "create";
				this.panel.items.items[0].setTitle(this.title);
				this.form.selectValue = this.selectValue;
				this.form.doNew();
				this.list.op = "create";
				this.list.doNew();
			},
			//获取list的值
			getListData:function(){
			var ck02=new Array();
			var tag=false;
			var count = this.list.store.getCount();
			for (var i = 0; i < count; i++) {
				var d=this.list.store.getAt(i).data
				if(d.SHSL!=0){
				tag=true;
				}
				if(d.TYPE==null||d.TYPE==0||d.TYPE==undefined){
				MyMessageTip.msg("提示", "第"+(i+1)+"行损坏原因不能为空", true);
				return 0;
				}
			ck02.push(this.list.store.getAt(i).data);
			}
			if(!tag){
			return null;
			}
			return ck02;
			},
			//关闭
			doCancel:function(){
			//this.fireEvent("close", this);
				this.getWin().hide();
			},
			//重写 为了双击打开时显示保存和提交按钮
			loadData : function(initDataBody) {
				this.changeButtonState("xg")
				this.doLoad(initDataBody,"update");
			},
			doRead:function(initDataBody){
			this.changeButtonState("read")
			this.doLoad(initDataBody,"read");
			},
			onWinShow:function(){
			this.list.cndField.setValue("");
			this.list.requestData.cnd=null;
			}
});