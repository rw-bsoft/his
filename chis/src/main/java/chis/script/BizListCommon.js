/**
 * 公共文件(表单页面公共函数)
 * 
 * @author : yaozh
 */
$package("chis.script")

chis.script.BizListCommon = {
// showButtonOnTop : true,

	changeButtonState : function(disable, index) {
		if(this.grid.getTopToolbar().items.length>0){
			if(disable){
				this.grid.getTopToolbar().items.item(index).disable()
			}else{
				this.grid.getTopToolbar().items.item(index).enable()
			}
		}else{
			var btns = this.grid.buttons;
			if (!btns) {
				return;
			}
			if(disable){
				btns[index].disable()
			}else{
				btns[index].enable()
			}
			
		}
	},
	
	/**
	 * 控制页面按钮权限
	 */
	resetButtons : function() {
		if (!this.grid) {
			return
		}
		if (this.showButtonOnTop && this.grid.getTopToolbar()) {
			var btns = this.grid.getTopToolbar().items;
			if (!btns) {
				return;
			}
			var n = btns.getCount()
			for (var i = 0; i < n; i++) {
				var btn = btns.item(i)
				if (btn.type != "button") {
					continue;
				}
				this.setButtonControl(btn);
			}
		} else {
			var btns = this.grid.buttons
			if (!btns) {
				return;
			}
			for (var i = 0; i < btns.length; i++) {
				var btn = btns[i]
				this.setButtonControl(btn);
			}
		}
	},

	/**
	 * 获取列表中的所有数据
	 * 
	 * @return {}
	 */
	getListData : function() {
		var data = [];
		for (var i = 0; i < this.store.data.length; i++) {
			var storeItem = this.store.getAt(i);
			var storeData = storeItem.data
			data.push(storeData);
		}
		return data;
	},
	
	fixRemoveCfg : function(removeCfg){
		removeCfg.serviceAction = this.removeAction || "";
	},
	
	fixRequestData : function(requestData){
		requestData.serviceAction = this.listAction || "";
	},

	loadDataByLocal : function(body) {
		this.store.removeAll();
		this.resetButtons()
		var data = {};
		if (body && body[this.entryName + "_list"]) {
			data = body[this.entryName + "_list"];
		}
		var records = this.getExtRecord(data);
		this.store.add(records);
		this.store.commitChanges();
		this.fireEvent("loadDataByLocal", this.store, this.selectedIndex)
		this.selectRow(this.selectedIndex)
	},

	getExtRecord : function(data) {
		var records = [];
		for (var i = 0; i < data.length; i++) {
			var record = new Ext.data.Record(data[i], data[i][this.schema.pkey]);
			records.push(record);
		}
		return records;
	},

	updateView : function(result) {

	},
	/**
	 * 获取一年的开始日期，返回自定义的开始月份的1号日期，默认为自然年分的1月1日
	 * @param {} businessType--【planType.dic中key值】(业务)计划类型编码
	 * @return Date startDate---返回日期类型
	 */
	getStartDate : function(businessType){
		var curDate = Date.parseDate(this.mainApp.serverDate,'Y-m-d');
		var curYear = curDate.getFullYear();
		var curMonth = curDate.getMonth();
		var startMonth = 1;
		if(businessType == "1"){//高血压
			startMonth = parseInt(this.mainApp.exContext.hypertensionStartMonth);
		}else if(businessType == "2"){//糖尿病
			startMonth = parseInt(this.mainApp.exContext.diabetesStartMonth);
		}else if(businessType == "4" || businessType == "14"){//老年人 、  离休干部
			startMonth = parseInt(this.mainApp.exContext.oldPeopleStartMonth);
		}else if(businessType == "10"){//精神病
			startMonth = parseInt(this.mainApp.exContext.psychosisStartMonth);
		}else if(businessType == "13"){//高血压高危
			startMonth = parseInt(this.mainApp.exContext.hypertensionRiskStartMonth);
		}else if(businessType == "15"){//肿瘤高危
			startMonth = parseInt(this.mainApp.exContext.tumourHighRiskStartMonth);
		}else if(businessType == "16"){//肿瘤患者(现患)
			startMonth = parseInt(this.mainApp.exContext.tumourPatientVisitStartMonth);
		}else if(businessType == "17"){//糖尿病高危
			startMonth = parseInt(this.mainApp.exContext.diabetesRiskStartMonth);
		}
		if(!startMonth){
			startMonth = 1;
		}
		if(curMonth < (startMonth-1)){
			//如果当前月份小于“年度开始月份”，默认格式是上一年份-开始月份-1号
			curYear = curYear - 1;
		}
		return new Date(curYear,(startMonth-1),1);
	}
}