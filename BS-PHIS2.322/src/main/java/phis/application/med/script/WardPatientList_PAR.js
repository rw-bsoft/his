$package("phis.application.med.script");

$import("phis.script.EditorList");
phis.application.med.script.WardPatientList_PAR = function(cfg) {
	this.serverParams = {serviceAction:cfg.serviceAction};
	cfg.mutiSelect = true;//添加勾选框
	phis.application.med.script.WardPatientList_PAR.superclass.constructor.apply(
			this, [cfg]);
	
},

Ext.extend(phis.application.med.script.WardPatientList_PAR,
		phis.script.EditorList, {
 
	/**
	 * 保存按钮
	 */
	save : function(){
		var json = this.getModifyData();
        if(json.length == 0){
        	Ext.Msg.alert("提示", "请选择需要修改执行科室的项目!");
        	return;
        }
        //回调WardProjectListModule.js中的mimiJsonRequest2Return方法
        this.fireEvent("save","wardProjectService", "save" , json , "保存成功!");
        this.commitDataChange();
	},
	/**
	 * 获取修改的数据
	 * @returns {Array}
	 */
	getModifyData : function(){ 
		var modified = this.grid.store.modified;  
		var json = [];  
        Ext.each(modified, function(item) {  
            json.push(item.data);  
        });
        return json;
	},
	/**
	 * 提交全部有变化的Record集，并将编辑标识(editor)置为false
	 */
	commitDataChange : function(){
		//提交全部有变化的Record集
        this.grid.getStore().commitChanges();
        this.editor = false;
	},
	
	
	init : function() {
		//定义数据加载时的数据集
		this.dataList = [];
		//定义左边列表选取框临时值
		this.leftTemList = [];
		this.flag = "unRefresh";
		//是否已编辑
		this.editor = false;
		this.addEvents({
					"select" : true
				});
		if (this.mutiSelect) {
			this.selectFirst = false;
		}
		this.selects = {};
		 this.singleSelect = {}
		phis.application.med.script.WardPatientList_PAR.superclass.init
				.call(this);
	},
	initPanel : function(schema) {
		var grids =  phis.application.med.script.WardPatientList_PAR.superclass.initPanel.call(
				this, schema);
		this.grid.getBottomToolbar().hide();//隐藏分页bbar
		this.on("afterCellEdit", this.afterGridEdit, this);
		return grids;
	},
	onStoreLoadData : function(store, records, ops) {
		phis.application.med.script.WardPatientList_PAR.superclass.onStoreLoadData.call(this,
				store, records, ops);
//		if (records.length == 0 || !this.selects || !this.mutiSelect) {
		if (records.length == 0 ) {
			this.fireEvent("syncData","patient");
			return
		}
		var selRecords = [];
		for (var id in this.selects) {
			var r = store.getById(id);
			if (r) {
				selRecords.push(r);
			}
		}
		this.grid.getSelectionModel().selectRecords(selRecords);
		var allData=this.store.data.items;
		this.dataList = [];
		//将数据临时保存到数据集中
		for (var i=0; i<allData.length; i++ ) {
			this.dataList.push(allData[i]);
		}
		//回调WardProjectListModule.js中的syncData方法
        this.fireEvent("syncData","patient",this.dataList);
		//当this.leftTemList不为空时表明，左边列表先加载完成，并来显示了
		if(this.leftTemList && this.leftTemList.length > 0){
			this.showProjectByLeftSelect(this.leftTemList);
			//清空保存的leftTemList
			this.leftTemList = [];
		}
		this.grid.getSelectionModel().selectAll();
	},

	/**
	 * 初始化js类时自动调用
	 * @param items
	 * @returns
	 */
	getCM : function(items) {
		var cm = phis.application.med.script.WardPatientList_PAR.superclass.getCM
				.call(this, items);
		var sm = this.sm;
		sm.on("rowselect", function(sm, rowIndex, record) {
//					this.selects[record.id] = record;
					this.addCommonProject(record, rowIndex);
				}, this);
		sm.on("rowdeselect", function(sm, rowIndex, record) {
					delete this.selects[record.id];
					this.delCommonProject(record);
				}, this);
		return cm;
	},
	/**
	 * 添加同组提交项目
	 * @param record
	 */
	addCommonProject : function(record, rowIndex){
		var selectYzzh = record.data.YZZH;
		var tempYzzh ;
//		var allItems=this.getSelectedRecord(false).store.data.items;
//		var allItems = this.grid.getSelectionModel().getSelected().store.data.items;
		var allItems=this.store.data.items;
		var tmp;
		for (var i=0; i<allItems.length; i++ ) {
			tmp = allItems[i];
			tempYzzh = tmp.data.YZZH;
			if(selectYzzh == tempYzzh){
//				if(rowIndex != i){
				if(!this.grid.getSelectionModel().isSelected(i)){
//					this.selects[tmp.id] = tmp;
					this.grid.getSelectionModel().selectRow(i,true);
				}
			}
		}
	},
	/**
	 * 取消选中时触发取消其它同组选择
	 * @param record
	 */
	delCommonProject : function(record){
//		if(this.flag == "unRefresh"){
			var selectYzzh = record.data.YZZH;
			var tempYzzh ;
			var allItems=this.store.data.items;
			var tmp;
			for (var i=0; i<allItems.length; i++ ) {
				tmp = allItems[i];
				tempYzzh = tmp.data.YZZH;
				if(selectYzzh == tempYzzh){//是否为同组
					if(this.grid.getSelectionModel().isSelected(i)){
						this.grid.getSelectionModel().deselectRow(i);//取消同组行
					}
				}
			}
//		}
	},
	/**
	 * 清空
	 */
	clearSelect : function() {
		this.selects = {};
		 this.singleSelect = {};
		this.sm.clearSelections();
	},
	/**
	 * 获取选中记录
	 * @returns {Array}
	 */
	getSelectedRecords : function() {
		var records = [];
//		var selects=this.getSelectedRecord(true);
		var selects = this.grid.getSelectionModel().getSelections();
		for(var i=0; i<selects.length; i++){
			records.push(selects[i]);
		}
//		for (var id in selects) {
//			if(id=="remove"){continue;}
//			records.push(selects[id]);
//		}
		return records;
	},
	/**
	 * 根据左边病人列表显示项目
	 * @param leftSelect
	 */
	showProjectByLeftSelect : function(leftSelect){
		//如果当前右边列表还未加载完成，先讲需要选取的保存到leftTemList中
		if(!this.dataList || this.dataList == 0){
			this.leftTemList = [];
			for(var i=0; i<leftSelect.length; i++){
				this.leftTemList.push(leftSelect[i]);
			}
//			this.reloadRightList(leftSelect);			
		}else{
			this.reloadRightList(leftSelect);
		}
		/*数据重新加载，暂时不需要
		 * this.flag = "refresh";
		this.store.load({
			callback: function(records, options, success){
				this.flag = "unRefresh";
				this.reloadRightList(leftSelect);    // 调用重新加载列表
			},
			scope: this//作用域为this。必须加上否则this.reloadRightList(leftSelect); 无法调用
		
		});//重新加载数据
		 */
		},
	/**
	 * 根据左边的选择，帅选右边复核项
	 * @param leftSelect
	 */
	reloadRightList : function(leftSelect){
		var tmp ;//左边临时选择对象
		var tmpBrch;//病人床号
		this.rightList = [];
		for(var i=0; i<leftSelect.length;i++){
			tmp = leftSelect[i];
//			tmpBrch = tmp.data.BRCH;//获取病人床号
			tmpBrch = tmp.data.ZYH;//获取病人床号
			this.getProjectByBRCH(tmpBrch);
		}
		this.store.removeAll();
		this.store.add(this.rightList);
		this.grid.getSelectionModel().selectAll();
	},
	/**
	 * 根据病人床号显示项目
	 * @param brch
	 * @returns {___anonymous4281_4282}
	 */
	getProjectByBRCH : function(brch){
		/*var allData=this.store.data.items;*/
		var allData = this.dataList;
		for (var i=0; i<allData.length; i++ ) {
//			if(allData[i].data.BRCH == brch){
			if(allData[i].data.ZYH == brch){
				this.rightList.push(allData[i]);
			}
		}
	},
	/**
	 * 重新刷新数据
	 */
	refreshData : function(){
		this.dataList = [];
		this.store.load({
			callback: function(records, options, success){
//				this.initSelect();    // 调用重新加载列表
			},
			scope: this//作用域为this。必须加上否则this.initSelect(); 无法调用
		});
	},
	afterGridEdit : function(it, record, field, v){
		if (it.id == "ZXKS") {
			this.editor = true;
		}
	},
	/**
	 * 判断是否已编辑
	 * @returns {Boolean}
	 */
	isEdit : function(){
		if(this.editor){
//			Ext.Msg.alert("提示", "数据已修改,请先保存!");
			return true;
		}
		return false;
	}
});