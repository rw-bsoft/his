$package("phis.application.reg.script");

$import("phis.script.SimpleList");

phis.application.reg.script.RegistrationPreflightList = function(cfg) {
	phis.application.reg.script.RegistrationPreflightList.superclass.constructor
			.apply(this, [ cfg ])
	this.autoLoadData = false;
	this.width = 215;
	this.modal = true;
	this.disablePagingTbr = true;
	this.on("winShow", this.onWinShow, this);
}

Ext.extend(phis.application.reg.script.RegistrationPreflightList,
		phis.script.SimpleList, {
			onWinShow : function() {
				var _this = this;
				var deferFunction = function(){
					_this.cndField.focus();
				}
				deferFunction.defer(1000);
			},
			onRowClick : function() {
				this.cndField.focus();
			},
			onDblClick : function(grid, index, e) {
				var lastIndex = grid.getSelectionModel().lastActive;
				var record = grid.store.getAt(lastIndex);
				if (record) {
					this.fireEvent("ghyjChoose", record);
				}
			},
			getCndBar : function(items) {
				var fields = [];
				if (!this.enableCnd) {
					return []
				}
				var selected = null;
				var defaultItem = null;
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!it.queryable) {
						continue
					}
					if (it.selected == "true") {
						selected = it.id;
						defaultItem = it;
					}
					fields.push({
								// change "i" to "it.id"
								value : it.id,
								text : it.alias
							})
				}
				if (fields.length == 0) {
					return [];
				}
				var store = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : fields
						});
				var combox = null;
				if (fields.length > 1) {
					combox = new Ext.form.ComboBox({
								store : store,
								valueField : "value",
								displayField : "text",
								value : selected,
								mode : 'local',
								triggerAction : 'all',
								emptyText : '选择查询字段',
								selectOnFocus : true,
								width : 80
							});
					combox.on("select", this.onCndFieldSelect, this)
					this.cndFldCombox = combox
				} else {
					combox = new Ext.form.Label({
								text : fields[0].text
							});
					this.cndFldCombox = new Ext.form.Hidden({
								value : fields[0].value
							});
				}

				var cndField = new Ext.form.TextField({
								width : 120,
								selectOnFocus : true,
								name : "dftcndfld",
								enableKeyEvents : true
							})
				this.cndField = cndField
				cndField.on("specialkey", this.onQueryFieldEnter, this)
				cndField.on("keyup", this.oncndKeypress, this);
				return [combox, '-', cndField]
			},
			oncndKeypress : function(f,e){
				if ((e.getKey() >= 48 && e.getKey() <= 57)
						|| (e.getKey() >= 65 && e.getKey() <= 90)
						|| (e.getKey() >= 96 && e.getKey() <= 111)
						|| e.getKey() == 8 || e.getKey() == 32
						|| e.getKey() == 47 || e.getKey() == 59
						|| e.getKey() == 61 || e.getKey() == 173
						|| e.getKey() == 188 || (e.getKey() >= 190 && e.getKey() <= 192)
						|| (e.getKey() >= 219 && e.getKey() <= 222)) {
					var store = this.grid.getStore();
					var n = store.getCount()
					var PYDM = this.cndField.getValue().toUpperCase();
					for (var i = 0; i < n; i++) {
						var r = store.getAt(i);
						if (r.get("PYDM").toUpperCase().indexOf(PYDM) >= 0) {
							this.grid.getSelectionModel().selectRow(i);
							return;
						}
						if (r.get("PYDM").toUpperCase().substring(0,PYDM.length)==PYDM) {
							this.grid.getSelectionModel().selectRow(i);
							return;
						}
					}
				}
				
			},
			onQueryFieldEnter : function(f, e) {
				if (e.getKey() == e.ENTER) {
					e.stopEvent()
					var lastIndex = this.grid.getSelectionModel().lastActive;
					var record = this.grid.store.getAt(lastIndex);
					if (record) {
						this.fireEvent("ghyjChoose", record);
					}
				}else if (e.getKey() == 40) {
					e.stopEvent()
					var lastIndex = this.grid.getSelectionModel().lastActive;
					this.grid.getStore();
					var n = this.grid.store.getCount()
					if(lastIndex<n){
						this.grid.getSelectionModel().selectRow(lastIndex+1)
					}else{
						this.grid.getSelectionModel().selectRow(lastIndex)
					}
				}else if(e.getKey() == 38){
					e.stopEvent()
					var lastIndex = this.grid.getSelectionModel().lastActive;
					if(lastIndex>0){
						this.grid.getSelectionModel().selectRow(lastIndex-1)
					}else{
						this.grid.getSelectionModel().selectRow(lastIndex)
					}
				}
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.grid.getSelectionModel().selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
			}
		});