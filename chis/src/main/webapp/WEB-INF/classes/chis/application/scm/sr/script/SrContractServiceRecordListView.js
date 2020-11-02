$package("chis.application.scm.sr.script");

$import("chis.script.BizSimpleListView");

chis.application.scm.sr.script.SrContractServiceRecordListView = function(cfg) {			
	cfg.autoLoadData = false;
	cfg.closeAction='hide';
	cfg.modal=true;
	cfg.width=600;
	cfg.height=450;
	chis.application.scm.sr.script.SrContractServiceRecordListView.superclass.constructor
			.apply(this, [cfg]);
	this.listServiceId = "chis.signContractRecordService";
	this.entryName = "chis.application.scm.schemas.SCM_NewServiceQuery";
	this.serviceAction = "loadServiceRecord";
}

Ext.extend(chis.application.scm.sr.script.SrContractServiceRecordListView,
		chis.script.BizSimpleListView, {
			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : this.pageSize || 25,
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录"
				}
				if (this.showButtonOnPT) {
					cfg.items = this.createButtons();
				}
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg)
				this.pagingToolbar = pagingToolbar
				return pagingToolbar
			},
			doEditor:function(){
				this.onRowClick();
			},
			showColor : function(value, metaData, r, row, col) {
				if(value==undefined){
					return "";
				}
				if(value.indexOf("(已删除)")>-1){
					return value.replace("(已删除)","<font style='color:red'>(已删除)</font>");
				}
				else if(value.indexOf("门诊收费履约")>-1){
					return "<font style='color:blue'>"+value+"</font>";
				}
				return value;
			},
			onRowClick:function(grid,index,e){
				var selectRecord = this.grid.getSelectionModel().getSelected();
				if(selectRecord.data.dataSource !="0"){
		            MyMessageTip.msg("提示", "只有手动履约记录才能修改！", true);
					return;
				}
				var module = this.createModule("NewServiceForm", this.refModule);
				selectRecord.data.op = "update";
				module.selectedR = selectRecord;
				module.personInfo = this.personInfo;
				module.initDataId = null;
				module.on("afterSave", this.afterSave, this);
				Ext.apply(module.exContext, this.exContext);
				module.exContext.control = {};
				var win = module.getWin();
				var x = (document.body.clientWidth - win.getWidth()) / 2;
				var y = (document.body.clientHeight - 400) / 2;
				win.setTitle(module.title)
				win.setPosition(x, y);
				win.add(module.initPanel())
				win.show();
				module.setDatas();
			},
			afterSave : function(){
				this.refresh();
			},
			loadData : function() {
				this.requestData.serviceId = this.listServiceId;
				this.requestData.serviceAction = this.serviceAction;
				this.requestData.body = this.selectedR.data;
				chis.application.scm.sr.script.SrContractServiceRecordListView.superclass.loadData
						.call(this);
			}
		});