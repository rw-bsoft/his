$package("phis.application.cic.script");

$import("phis.script.SimpleList");
/**
 * 就诊记录列表
 */
phis.application.cic.script.PrescriptionCopyJZLSList = function(cfg) {
//	cfg.listServiceId = "medicalTechnicalSectionService";
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = true;
	phis.application.cic.script.PrescriptionCopyJZLSList.superclass.constructor.apply(
			this, [cfg]);
	this.on("firstRowSelected", this.onRowClick);
},
Ext.extend(phis.application.cic.script.PrescriptionCopyJZLSList,
		phis.script.SimpleList,{ 
	initPanel : function(brid){
		this.brid = brid;
		return phis.application.cic.script.PrescriptionCopyJZLSList.superclass.initPanel.call(this);
	},
	// 加载数据时增加机构和科室过滤
	loadData : function() {
		this.requestData.cnd = ['and',
		                        ['eq', ['$', 'BRBH'], ['d', this.brid]],
		                        ['eq', ['$', 'JGID'], ['s', this.mainApp['phisApp'].deptId]]];
//				['eq', ['$', 'KSDM'], ['d', this.mainApp['phis'].MedicalId]]]
				
		phis.application.cic.script.PrescriptionCopyJZLSList.superclass.loadData
				.call(this);
	},
	onRowClick : function(){
		var selectData = this.getSelectData();
		if(selectData && selectData.length > 0){
			jzxh = selectData[0].data.JZXH;
			this.opener.showCF01Data(jzxh);
		}else{
			this.opener.showCF01Data();
		}
	},
	getSelectData : function(){
		var selectList = [];
		var allItems=this.store.data.items;
		var tmp;
		for (var i=0; i<allItems.length; i++ ) {
			tmp = allItems[i];
			if(this.grid.getSelectionModel().isSelected(i)){
				selectList.push(tmp);
			}
		}
		return selectList;
	}
});