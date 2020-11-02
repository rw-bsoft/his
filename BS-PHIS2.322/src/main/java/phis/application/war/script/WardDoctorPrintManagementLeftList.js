/**
 * 药房模块,左边list模版
 * 
 * @author caijy
 */
$package("phis.application.war.script")
$import("phis.script.SimpleList")

phis.application.war.script.WardDoctorPrintManagementLeftList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	cfg.isfirst=0
	phis.application.war.script.WardDoctorPrintManagementLeftList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.war.script.WardDoctorPrintManagementLeftList,
		phis.script.SimpleList, {
			// 生成条件组件
			getCndBar : function(items) {
				this.selectBox = this.getSelectBox();
				this.selectBox.on("select", this.loadData, this)
				var combox = this.selectBox;
				var _ctr = this;
				var filelable = new Ext.form.Label({
							text : "床号"
						})
				this.textField = new Ext.form.TextField({
							width : 60
						})
				this.textField.on("specialkey", this.loadData, this);
				this.selectBox.store.on("load", function() {
							if (this.getCount() == 0 || _ctr.isfirst == 1)
								return;
							combox.setValue(this.getAt(0).get('key'))
							//_ctr.onSelect(null, this.getAt(0))
							_ctr.loadData();
							_ctr.isfirst = 1;
						});
//				this.radioGroup = new Ext.form.RadioGroup({
//							width : 180,
//							items : [{
//										boxLabel : '在院',
//										inputValue : '1',
//										checked : true,
//										name : 'project'
//									}, {
//										boxLabel : '出院',
//										inputValue : '2',
//										name : 'project'
//									}]
//						});
//				this.radioGroup.on("change", this.onRadioChange, this)
//				this.bbar = [this.radioGroup]
				var zyhmlable = new Ext.form.Label({
							text : "住院号码(出院和转科)"
						}) 
				this.zyhmText=new Ext.form.TextField({width : 100});
				this.zyhmText.on("specialkey", this.loadData, this);
				this.bbar = [zyhmlable,this.zyhmText]
				return [this.selectBox, '-', filelable, this.textField];
			},
			// 生成条件下拉框
			getSelectBox : function() {
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == "BQ") {
						it = items[i]
						break
					}
				}
				it.dic.src = this.entryName + "." + it.id;
				it.dic.defaultValue = it.defaultValue;
				it.dic.width = 100;
				if (this.getDicFitle() != null) {
					it.dic.filter = this.getDicFitle();
				}
				f = this.createDicField(it.dic);
				f.on("specialkey", this.onQueryFieldEnter, this);
				return f;
			},
			getDicFitle : function() {
				return null;
			},
//			// 在院出院按钮选择时
//			onRadioChange : function() {
//				this.loadData();
//			},
			loadData : function() {
				var body = {
					"ZYHM" : this.zyhmText.getValue(),
					"BRCH" : this.textField.getValue(),
					"BQ" : this.selectBox.getValue()
				}
				this.requestData.body = body;
				this.requestData.serviceId = this.serviceId;
				this.requestData.serviceAction = this.serviceAction;
				phis.application.war.script.WardDoctorPrintManagementLeftList.superclass.loadData
						.call(this)
			},
			//查询
			doQuery : function() {
				this.loadData();
			},
//			//获取在院,出院状态
//			getRadioValue : function() {
//				var v = "";
//				this.radioGroup.eachItem(function(item) {
//							if (item.checked == true) {
//								v = item.inputValue;
//							}
//						});
//						return v;
//			},
			//单击刷新右边数据
			onRowClick:function(){
			var r=this.getSelectedRecord();
			if(r == null){
			return;}
			this.fireEvent("rowClick",r.get("ZYH"));
			}
			,
			//加载数据的时候 如果有数据 默认选中一行 刷新右边
	onStoreLoadData:function(store,records,ops){
		this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
		if(records.length == 0){
			this.fireEvent("noRecord");
			return
		}
		this.totalCount = store.getTotalCount()
		if (!this.selectedIndex || this.selectedIndex >= records.length) {
			this.selectRow(0)
			this.selectedIndex = 0;
		}
		else{
			this.selectRow(this.selectedIndex);
		}
		this.onRowClick();
	}
		})