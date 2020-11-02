$package("chis.application.tr.script.sms")

$import("chis.script.BizCombinedModule3");

chis.application.tr.script.sms.TumourQuestionnaireCriterionDetailModule=function(cfg){
	chis.application.tr.script.sms.TumourQuestionnaireCriterionDetailModule.superclass.constructor.apply(this,[cfg]);
	this.westWidth=200;
	this.itemWidth=180;
}

Ext.extend(chis.application.tr.script.sms.TumourQuestionnaireCriterionDetailModule,chis.script.BizCombinedModule3,{
	initPanel : function() {
		var panel = chis.application.tr.script.sms.TumourQuestionnaireCriterionDetailModule.superclass.initPanel.call(this);
		this.panel = panel;
		this.QFieldList = this.midiModules[this.actions[0].id];
		this.QFieldList.on("loadData",this.onQFieldListLoadData,this);
		this.QFieldList.on("rowclick",this.QFieldListRowClick,this);
		this.QItemList = this.midiModules[this.actions[1].id];
		this.QItemList.on("loadData",this.onQItemListLoadData,this);
		this.QItemList.on("rowdblclick",this.QItemListRowDblClick,this);
		this.QCDList = this.midiModules[this.actions[2].id];
		return panel;
	},
	
	QFieldListReLoad : function(masterplateId){
		this.masterplateId = masterplateId;
		var cnd = ['eq',['$','b.masterplateId'],['s',masterplateId||'']]
		this.QFieldList.requestData.cnd = cnd;
		this.QFieldList.loadData();
	},
	
	onQFieldListLoadData : function(){
		var qfsr = this.QFieldList.store.getAt(0);
		if(qfsr){
			var fieldId = qfsr.get("fieldId");
			var fieldName = qfsr.get("alias");
			this.QFieldListRowClick(fieldId,fieldName);
		}
	},
	
	QFieldListRowClick : function(fieldId,fieldName){
		this.fieldId = fieldId;
		this.fieldName = fieldName;
		var cnd = ['eq',['$','fieldId'],['s',fieldId||'']]
		this.QItemList.requestData.cnd = cnd;
		this.QItemList.selectedIndex=null;
		this.QItemList.loadData();
	},
	
	onQItemListLoadData : function(){
		var QCDListStore = this.QCDList.store;
		var existIndex = QCDListStore.find("QFId",this.fieldId);
		if(existIndex >= 0){
			this.QCDList.selectRow(existIndex);
			var sr = this.QCDList.getSelectedRecord();
			var dicId = sr.get("dicId");
			var itemIdx = this.QItemList.store.find("dicId",dicId);
			if(itemIdx >= 0){
				this.QItemList.selectedIndex=itemIdx;
				this.QItemList.selectRow(itemIdx);
				this.QItemList.grid.getView().focusRow(itemIdx);
			}else{
				this.QItemList.grid.getSelectionModel().clearSelections();
			}
		}else{
			this.QCDList.grid.getSelectionModel().clearSelections();
			this.QItemList.selectedIndex=null;
			this.QItemList.grid.getSelectionModel().clearSelections();
		}
	},
	
	QItemListRowDblClick : function(data){
		var QitemStore = this.QCDList.store;
		var existRecord = false;
		for(var i=0,len=QitemStore.getCount();i<len;i++){
			var r = QitemStore.getAt(i);
			var QFId = r.data.QFId;
			if(QFId == this.fieldId){
				existRecord = true;
			}
		}
		if(existRecord){
			Ext.Msg.alert("提示","该题已经存在参照标准！");
		}else{
			var cdata = {};
			cdata.QMId=this.masterplateId;
			cdata.QFId=this.fieldId;
			cdata.fieldName=this.fieldName;
			cdata.dicId = data.dicId;
			cdata.keys = data.keys;
			cdata.text = data.text;
			cdata.createUser=this.mainApp.uid;
			cdata.createUser_text = this.mainApp.uname;
			cdata.createUnit=this.mainApp.deptId;
			cdata.createUnit_text=this.mainApp.dept;
			cdata.createDate = this.mainApp.serverDate;
			cdata.lastModifyUser=this.mainApp.uid;
			cdata.lastModifyUser_text = this.mainApp.uname;
			cdata.lastModifyUnit=this.mainApp.deptId;
			cdata.lastModifyUnit_text=this.mainApp.dept;
			cdata.lastModifyDate = this.mainApp.serverDate;
			var record = new Ext.data.Record(cdata);
			this.QCDList.store.add(record);
			this.QCDList.store.commitChanges();
			this.QCDList.selectRow(this.QCDList.store.getCount()-1);
		}
	},
	
	getQCDListData : function(){
		var array = [];
		for (var i = 0,len=this.QCDList.store.getCount(); i < len; i++) {
			var r = this.QCDList.store.getAt(i);
			array.push(r.data);
		}
		return array;
	},
	
	doNew : function(){
		this.QFieldList.clear();
		this.QItemList.clear();
		this.QCDList.clear();
	},
	
	loadData : function(){
		this.doNew();
		var QMId = this.exContext.args.QMId;
		this.QFieldListReLoad(QMId);
		var QCId = this.exContext.args.recordId;
		var cnd = ['and',
			['eq',['$','QCId'],['s',QCId||'']],
			['eq',['$','QMId'],['s',QMId||'']]
		]
		this.QCDList.requestData.cnd = cnd;
		this.QCDList.loadData();
	}
});