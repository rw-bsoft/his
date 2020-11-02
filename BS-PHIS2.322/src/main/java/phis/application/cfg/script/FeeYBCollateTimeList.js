$package("phis.application.cfg.script");

$import("phis.script.EditorList", "phis.script.util.DateUtil",
		"phis.application.cfg.script.yb.YbUtil", "util.widgets.MyMessageTip");
/**
 * 药品对照列表
 */
phis.application.cfg.script.FeeYBCollateTimeList = function(cfg) {
	this.serverParams = {serviceAction:cfg.serviceAction};
	cfg.disablePagingTbr = true;
	Ext.apply(this,phis.application.cfg.script.YbUtil);
	phis.application.cfg.script.FeeYBCollateTimeList.superclass.constructor.apply(
			this, [cfg]);
			this.on("afterCellEdit",this.afterTimeEdit,this);
},

Ext.extend(phis.application.cfg.script.FeeYBCollateTimeList,
		phis.script.EditorList, {
		
		/**
		 * 重写创建方法
		 */
		doCreate : function(item, e){
			if(this.createFlag){
				MyMessageTip.msg("提示", "请先保存医保费用对照!", true);
				return;
			}
			var serverDate = Date.getServerDate();
			var store = this.grid.getStore();
			var o = this.getStoreFields(this.schema.items)
			var Record = Ext.data.Record.create(o.fields)
			var items = this.schema.items
			var factory = util.dictionary.DictionaryLoader
			var data = {
				'_opStatus' : 'create',
				'KSSJ' : serverDate
			}
			for (var i = 0; i < items.length; i++) {
				var it = items[i]
				var v = null
				if (it.defaultValue) {
					v = it.defaultValue
					data[it.id] = v
					var dic = it.dic
					if (dic) {
						data[it.id] = v.key;
						var o = factory.load(dic)
						if (o) {
							var di = o.wraper[v.key];
							if (di) {
								data[it.id + "_text"] = di.text
							}
						}
					}
				}
				if (it.type && it.type == "int") {
					data[it.id] = (data[it.id] == "0" || data[it.id] == "" || data[it.id] == undefined)
							? 0
							: parseInt(data[it.id]);
				}
	
			}
			var r = new Record(data)
			try {
				store.add([r])
				this.grid.getView().refresh();
			} catch (e) {
				store.removeAll();// 解决处方录入模块双击插入操作报错问题
			}
			this.createFlag = true;
//			this.afterTimeEdit();
			this.opener.setFormTime(r);
		},
			
		/**
			 * 删除行
			 */
		doRemove : function() {
//			var cm = this.grid.getSelectionModel();
//			var cell = cm.getSelectedCell();
			var r = this.getSelectedRecord();
			if (r == null) {
				return
			}
			if(r.data._opStatus != 'create'){
				if (confirm('确定删除后台数据?')) {
					this.requestServer('deleteFYDZ', r.data);
					this.opener.opener.refreshDates();
				}else{
					return;
				}
			}else{
				this.createFlag = false;
			}
			this.refresh();
		},
		/**
		 * 时间列表编辑后触发方法
		 * @param {} it
		 * @param {} record
		 * @param {} field
		 * @param {} v
		 */
		afterTimeEdit : function(it,record,field,v){
			var cm = this.grid.getSelectionModel();
			var cell = cm.getSelectedCell();
			var rs = this.getSelectedRecord();
			if(rs == null){
				return;
			}
			this.opener.setFormTime(rs);
		},
		/**
		 * 行选择触发方法
		 */
		onRowClick : function() {
			this.afterTimeEdit();
		},
		/**
		 * 数据加载后选择第一行
		 * @param {} store
		 * @param {} records
		 * @param {} ops
		 */
		onStoreLoadData : function(store, records, ops){
			this.createFlag = false;
			if(this.grid.store.getCount() > 0){
            	var rs = this.grid.store.getAt(0);
            	this.opener.setFormTime(rs);
			}
		}
});