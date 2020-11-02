$package("phis.application.cic.script");

$import("phis.script.SimpleList");
/**
 * 就诊记录列表
 */
phis.application.cic.script.PrescriptionCopyCF01List = function(cfg) {
//	cfg.listServiceId = "medicalTechnicalSectionService";
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	phis.application.cic.script.PrescriptionCopyCF01List.superclass.constructor.apply(
			this, [cfg]);
	//处方第一行选择事件
	this.on("firstRowSelected", this.onRowClick);
},
Ext.extend(phis.application.cic.script.PrescriptionCopyCF01List,
		phis.script.SimpleList,{ 
	initPanel : function(brid, clinicType){
		this.brid = brid;
		this.clinicType = clinicType;
		return phis.application.cic.script.PrescriptionCopyCF01List.superclass.initPanel.call(this);
	},
	// 加载数据时增加机构和科室过滤
	loadData : function() {
		this.requestData.cnd = ['and',
		                        ['eq', ['$', 'BRID'], ['d', this.brid]],
		                        ['eq', ['$', 'JGID'], ['s', this.mainApp['phisApp'].deptId]]];
//				['eq', ['$', 'KSDM'], ['d', this.mainApp['phis'].MedicalId]]]
				
		phis.application.cic.script.PrescriptionCopyCF01List.superclass.loadData
				.call(this);
	},
	onRowClick : function(){
		var selectData = this.getSelectData();
		if(selectData && selectData.length > 0){
			cfsb = selectData[0].data.CFSB;
			this.opener.showCF02Data(cfsb);
		}else{
			this.opener.showCF02Data();
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
	},
	/**
	 * 重新加载处方01表
	 * @param jzxh
	 * @param brid
	 */
	reloadData : function(jzxh, brid, clinicType){
		this.clear();
		if(jzxh){
			this.requestData.cnd = ['and',
			                        ['eq', ['$', 'BRID'], ['d', brid]],
			                        ['eq', ['$', 'JZXH'], ['d', jzxh]],
			                        ['eq', ['$', 'CFLX'], ['d', clinicType]],
			                        ['eq', ['$', 'JGID'], ['s', this.mainApp['phisApp'].deptId]]];
			this.store.load({
				callback: function(records, options, success){
					if(records.length < 1){//处方01没有记录时，调用cf02，将数据清空
						this.opener.showCF02Data();
					}
				},
				scope: this//作用域为this。必须加上，否则this.opener.showCF02Data(); 无法调用
			});
		}else{
			this.opener.showCF02Data();
		}
	}
});