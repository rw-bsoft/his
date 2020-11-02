$package("phis.application.cfg.script")
$import("phis.script.SimpleList")

phis.application.cfg.script.LogisticsInventoryControlLeftList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.isfirst=0;
	phis.application.cfg.script.LogisticsInventoryControlLeftList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.cfg.script.LogisticsInventoryControlLeftList,
		phis.script.SimpleList, {
			// 收费项目下拉框
			getCndBar : function(items) {
				this.selectBox = this.getSFXM();
				var filelable = new Ext.form.Label({
							text : "收费项目"
						})
				var combox = this.selectBox;
				this.selectBox.on("select", this.onCheck, this)
				var _ctr = this;
				this.selectBox.store.on("load", function() {
							if (this.getCount() == 0 || _ctr.isfirst == 1)
								return;
							combox.setValue(this.getAt(0).get('key'))
							_ctr.onCheck(null, this.getAt(0))
							_ctr.isfirst = 1;
						});
				var filelable1 = new Ext.form.Label({
							text : "拼音码"
						})
				var cndField = new Ext.form.TextField({// 拼音查询框
					width : 60,
					selectOnFocus : true,
					name : "dftcndfld"
				})
				cndField.on("specialkey", this.onQueryFieldEnter, this)
				this.cndField=cndField;
				var queryBtn = new Ext.Toolbar.SplitButton({
					text : '',
					iconCls : "query",
					notReadOnly : true, // ** add by yzh **
					menu : new Ext.menu.Menu({
								items : {
									text : "高级查询",
									iconCls : "common_query",
									handler : this.doPYQuery,
									scope : this
								}
							})
				})
				queryBtn.on("click", this.doPYQuery, this);
				this.cndField = cndField
				return [filelable, '-', this.selectBox,'-',filelable1,'-',cndField,'-',queryBtn];
			},
			// 生成收费项目下拉框
			getSFXM : function() {
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == "FYGB") {
						it = items[i]
						break
					}
				}
				it.dic.src = this.entryName + "." + it.id;
				it.dic.defaultValue = it.defaultValue;
				it.dic.width = 150;
				f = this.createDicField(it.dic);
				// f.on("specialkey", this.onQueryFieldEnter, this);
				return f;
			}
			,
			onCheck:function(item, record, e){
			this.checkData=record.data.key;
			this.requestData.cnd=['and',['eq',['$','b.JGID'],['s',this.mainApp['phisApp'].deptId]],['eq',['$','a.FYGB'],['d',record.data.key]]];
			this.loadData();
			},
			doPYQuery:function(){
				var pym=this.cndField.getValue()
				if(pym&&pym!=null&&pym!=""){
					pym=pym.toUpperCase();
				this.requestData.cnd=['and',['eq',['$','a.FYGB'],['d',this.checkData]],['like',['$','a.PYDM'],['s',pym+"%"]],['eq',['$','b.JGID'],['s',this.mainApp['phisApp'].deptId]]];
				}else{
			this.requestData.cnd=['and',['eq',['$','b.JGID'],['s',this.mainApp['phisApp'].deptId]],['eq',['$','a.FYGB'],['d',this.checkData]]];}
			this.loadData();
			}
			,
			onRowClick : function() {
			var r = this.getSelectedRecord();
			if(r==null){
			this.fireEvent("click",null);
			return;}
			this.fireEvent("click",r.data);
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					this.onRowClick();
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
				this.onRowClick();
			},
			onQueryFieldEnter:function(f,e){
				if(e.getKey() == e.ENTER){
					e.stopEvent()
					if (f.getRawValue().indexOf(".") == -1) {
						this.doPYQuery();
			    	}
				}
			}
			
		})