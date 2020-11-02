$package("chis.application.fhr.script")
$import("app.modules.list.EditorListView",
		"chis.application.fhr.script.PersonnalContractForm",
		"chis.application.fhr.script.PersonnalContractServiceForm")
chis.application.fhr.script.PersonnalContractList = function(cfg) {
	cfg.disablePagingTbr = false;
	cfg.autoLoadData = true;
	chis.application.fhr.script.PersonnalContractList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(chis.application.fhr.script.PersonnalContractList,
		app.modules.list.EditorListView, {
			onRowClick : function() {
				this.storeRecord = this.grid.getStore();
				var r = this.getSelectedRecord();
				this.selectRecord = r
			},
			onDblClick : function() {
				this.selectEmpiId = this.selectRecord.get("FS_EmpiId");
				var serviceModule = new chis.application.fhr.script.PersonnalContractServiceForm(
						{
							record : this.selectRecord,
							entryName : "chis.application.fhr.schemas.EHR_FamilyContractServiceDetail",
							title : "服务项目明细",
							modal : true,
							autoLabelWidth : true,
							fldDefaultWidth : 600,
							autoFieldWidth : true,
							width : 800,
							empiId : this.selectEmpiId,
							mainApp : this.mainApp
						})
				serviceModule.on("save", this.setListData, this);
				var win = serviceModule.getWin();
				win.setPosition(150, 100);
				win.show();
			},
			setListData : function(data) {
				this.refresh();
			}
			,onStoreLoadData : function(store,record,ops){
			},
			doSignservice : function() {
				if(!this.selectRecord){
					MyMessageTip.msg("提示", "请先选择一条签约记录", true);
					return;
				}
				this.onDblClick();
			}
//			,doRemove : function() {
//			    var r = this.getSelectedRecord()
//			    if (r == null) {
//					return
//			    }
//				Ext.Msg.show({
//							title : '确认删除此记录',
//							msg : '删除操作将无法恢复，是否继续?',
//							modal : true,
//							width : 300,
//							buttons : Ext.MessageBox.OKCANCEL,
//							multiline : false,
//							fn : function(btn, text) {
//								if (btn == "ok") {
//									this.processRemove();
//								}
//							},
//							scope : this
//						})
//			}
//			,processRemove : function() {
//				var r = this.getSelectedRecord()
//				if (r == null) {
//					return
//				}
//				if (!this.fireEvent("beforeRemove", this.entryName, r)) {
//					return;
//				}
//				this.mask("正在删除数据...")
//				util.rmi.jsonRequest({
//							serviceId : "chis.healthRecordService",
//							pkey : r.id,
//							FC_Id : r.data.FC_Id,
//							FS_EmpiId : r.data.FS_EmpiId,
//							serviceAction : "remove",
//							method : "execute"
//						}, function(code, msg, json) {
//							this.unmask()
//							if (code < 300) {
//								this.store.remove(r)
//								this.fireEvent("remove", this.entryName,
//										'remove', json, r.data)
//								var count = this.store.getTotalCount();
//								var btns = this.grid.getTopToolbar().items;
//								var bts = btns.items[0];
//								if (!btns) {
//									return;
//								}
//								if(count>1){		
//									bts.disable();
//								}else{
//								bts.enable();
//								}
//							} else {
//								this.processReturnMsg(code, msg, this.doRemove)
//							}
//						}, this)
//			}
			,doCreate:function(){
				var serviceModule = new chis.application.fhr.script.PersonnalContractForm(
						{
							entryName : "chis.application.fhr.schemas.EHR_PersonnalContractForm",
							title : "签约信息",
							modal : true,
							autoLabelWidth : true,
							fldDefaultWidth : 600,
							autoFieldWidth : true,
							width : 800,
							mainApp : this.mainApp
						})
				serviceModule.initPanel();
				serviceModule.idcardEnter();
				serviceModule.on("save", this.refresh, this);
				var win = serviceModule.getWin();
				win.setPosition(150, 100);
				win.show();
			}
	,loadData: function(){
		this.clear();
		if(this.store){
			if(this.disablePagingTbr){
				this.store.load()
			}
			else{
				var pt = this.grid.getBottomToolbar()
				if (this.requestData.pageNo == 1) {
					pt.cursor = 0;
				}
				pt.doLoad(pt.cursor)
			}
		}
		}
		,doUpdate: function(){
			if(!this.selectRecord){
				MyMessageTip.msg("提示", "请先选择一条签约记录", true);
				return;
			}
			debugger;
			var serviceModule = new chis.application.fhr.script.PersonnalContractForm(
						{
							entryName : "chis.application.fhr.schemas.EHR_PersonnalContractForm",
							title : "签约信息",
							modal : true,
							autoLabelWidth : true,
							fldDefaultWidth : 600,
							autoFieldWidth : true,
							width : 800,
							mainApp : this.mainApp
						})
				serviceModule.initPanel();
				serviceModule.initFormData(this.selectRecord.data);
				serviceModule.setFormcolumndisable();
				serviceModule.on("save", this.refresh, this);
				var win = serviceModule.getWin();
				win.setPosition(150, 100);
				win.show();
		},
		doSend: function(){
			if(!this.selectRecord){
				MyMessageTip.msg("提示", "请先选择一条签约记录", true);
				return;
			}
			var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.familyRecordService",
					serviceAction : "sendtojsd",
					method:"execute",
					body : this.selectRecord.data
				})
			if(result.code>300){
				MyMessageTip.msg("捷士达返回信息", result.msg, true);
				return;
			}else{
				MyMessageTip.msg("提示", "上传成功", true);
				return;
			}
		}
		//下载签约信息
		,doDown: function(){
			var sfz="3201"
			if(this.selectRecord){
				sfz=this.selectRecord.data.idCard;
			}
			Ext.MessageBox.prompt("提示","请输入身份证号",function(btn,value) {
				if(btn=="ok"){
       				this.loadjsdqyxx(value)
				}
   			},this,false,sfz);
		},
		loadjsdqyxx :function(value){
			if(value.length!=18){
				MyMessageTip.msg("提示", "身份证位数不对！", true);
				return;
			}
			var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.familyRecordService",
					serviceAction : "loadjsdqyxx",
					method:"execute",
					idcard : value
				})
			if(result.code >300){
				MyMessageTip.msg("提示",result.msg, true);
				return;
			}else{
				if(result.json.data){
					var m = this.midiModules["jsdqyxxform"];
					if (!m) {
						$import("chis.application.fhr.script.JsdqyxxForm");
						m = new chis.application.fhr.script.JsdqyxxForm({
							entryName : "chis.application.fhr.schemas.JSD_Qyxx",
							title : "捷士达签约信息",
							height : 450,
							aotoloaddata:false,
							mainApp : this.mainApp
						});
						this.midiModules["jsdqyxxform"] = m;
					}
					m.initPanel();
					m.initFormData(result.json.data);
					var win = m.getWin();
					win.setPosition(250, 100);
					win.show();
				}
			}
		}
		});