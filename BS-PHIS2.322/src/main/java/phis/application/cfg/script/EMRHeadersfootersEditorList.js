$package("phis.application.cfg.script")
$import("phis.script.EditorList")
phis.application.cfg.script.EMRHeadersfootersEditorList = function(cfg) {
	cfg.disablePagingTbr = true;
	phis.application.cfg.script.EMRHeadersfootersEditorList.superclass.constructor
			.apply(this, [ cfg ])
}
Ext.extend(phis.application.cfg.script.EMRHeadersfootersEditorList,
		phis.script.EditorList, {
			loadData : function() {
				this.clear(); // ** add by yzh , 2010-06-09 **
				this.requestData.body = 0;
				if (this.KSFL) {
					this.requestData.body = this.KSFL;
				}
				this.requestData.serviceId = "phis.emrManageService";
				this.requestData.serviceAction = "queryHeadersFootersSet";
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				this.resetButtons();
			},
			doSave : function() {
				if (!this.KSFL) {
					this.KSFL = 0;
				}
				var store = this.grid.getStore();
				var n = store.getCount()
				var data = []
				for ( var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (r.data.MBBH) {
						r.data.KSFL = this.KSFL;
						data.push(r.data);
					}
				}
				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "phis.emrManageService",
					serviceAction : "saveHeadersFootersSet",
					body : data
				});
				this.grid.el.unmask()
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return;
				}
			},
			getCndBar : function(items) {
				var fields = [];
				if (!this.enableCnd) {
					return []
				}
				var selected = null;
				var defaultItem = null;
				for ( var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!it.queryable) {
						continue
					}
					selected = it.id;
					defaultItem = it;
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
					fields : [ 'value', 'text' ],
					data : fields
				});
				var combox = new Ext.form.Label({
					text : fields[0].text
				});
				this.cndFldCombox = new Ext.form.Hidden({
					value : fields[0].value
				});

				var cndField;
				if (defaultItem) {
					if (defaultItem.dic) {
						defaultItem.dic.src = this.entryName + "." + it.id
						defaultItem.dic.defaultValue = defaultItem.defaultValue
						defaultItem.dic.width = 80
						cndField = this.createDicField(defaultItem.dic)
						cndField.on("select", this.doRefresh, this)
						cndField.setEditable(false);
					} else {
						cndField = this.createNormalField(defaultItem)
					}
				} else {
					cndField = new Ext.form.TextField({
						width : 120,
						selectOnFocus : true,
						name : "dftcndfld",
						enableKeyEvents : true
					})
				}
				this.cndField = cndField
				var queryBtn = new Ext.Toolbar.Button({
					text : '',
					iconCls : "query",
					notReadOnly : true
				})
				this.queryBtn = queryBtn
				queryBtn.on("click", this.doRefresh, this);
				return [ combox, '-', cndField, '-', queryBtn ];
			},
			doRefresh : function() {
				this.KSFL = this.cndField.getValue();
				this.loadData();
			}
		})
