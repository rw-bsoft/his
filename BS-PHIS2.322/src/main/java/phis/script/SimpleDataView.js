$package("phis.script")

$import("phis.script.ux.DataView-more", "phis.script.ux.ImageDragZone",
		"phis.script.SimpleList")

$styleSheet("phis.resources.css.ext.ux.data-view")

phis.script.SimpleDataView = function(cfg) {
	this.exContext = {};
	phis.script.SimpleDataView.superclass.constructor.apply(this, [cfg]);
}
Ext.extend(phis.script.SimpleDataView, phis.script.SimpleList, {
	initPanel : function(sc) {
		if (this.panel) {
			return this.panel;
		}
		var schema = sc
		if (!schema) {
			var re = util.schema.loadSync(this.entryName)
			if (re.code == 200) {
				schema = re.schema;
			} else {
				this.processReturnMsg(re.code, re.msg, this.initPanel)
				return;
			}
		}
		this.schema = schema;
		this.isCompositeKey = schema.isCompositeKey;
		var items = schema.items
		if (!items) {
			return;
		}
		this.store = this.getStore(items);
		var panel = new Ext.Panel({
					id : 'images-view',
					frame : true,
					width : 425,
					// autoScroll :true,
					layout : 'fit',
					bbar : this.getPagingToolbar(this.store),
					items : this.getDataView()
				});

		this.store.on("load", this.storeLoadData, this);
		this.panel = panel;
		this.panel.on("afterrender", this.onReady, this)
		return this.panel;
	},
	getDataView : function() {
		var dataview = new Ext.DataView({
					store : this.store,
					tpl : this.getTpl(),
					// autoHeight : true,
					width : 400,
					style : "overflow-x: hidden;overflow-y: auto;",
					// autoScoller : true,
					// multiSelect : true,
					overClass : 'x-view-over',
					itemSelector : 'div.thumb-wrap',
					singleSelect : true,
					emptyText : '没有信息',
					plugins : [new Ext.DataView.DragSelector({
								dragSafe : true
							})]
				});
		this.dataview = dataview;
		this.dataview.on("dblclick", this.onDblClick, this)
		this.dataview.on("click", this.onClick, this)
		this.dataview.on("contextmenu", this.onContextmenu, this)
		this.dataview.on("containerclick", function() {
					return false
				});
		this.dataview.on("afterrender", this.onDataViewReady, this);
		return this.dataview;
	},
	onReady : function() {
		if (this.autoLoadData) {
			this.store.load();
		}
	},
	onDataViewReady : function() {
		var dataview = this.dataview;
		var dragZone = new ImageDragZone(dataview, {
					containerScroll : true,
					ddGroup : 'dataviewDD'
				});
		var _ctx = this;
		var target = new Ext.dd.DropTarget(dataview.getEl(), {
			ddGroup : dataview.ddGroup || 'dataviewDD',
			dataview : dataview,
			// gridDropTarget: this,
			notifyDrop : function(dd, e, data) {
				// 获取选择行
				var t = e.getTarget('.thumb-wrap');
				if (!t)
					return false;
				var rec = dataview.getRecord(t);
				var ds = dataview.store;
				rindex = false;
				if (rec) {
					var rindex = ds.indexOfId(rec.id);
				}
				if (rindex === false)
					return false;
				var rdata = ds.getAt(rindex);
				// 判断插入行是否选择行，如果是不允许插入
				for (i = 0; i < data.nodes.length; i++) {
					var rowIndex = ds.indexOfId(data.nodes[i].id);
					if (rindex == rowIndex)
						rindex = false;
				}
				var recs = [];
				for (var i = 0; i < data.nodes.length; i++) {
					var rec = dataview.getRecord(data.nodes[i]);
					recs.push(rec);
				}
				_ctx.move(rindex, recs)
				return true;
			},

			notifyOver : function(dd, e, data) {
				var t = e.getTarget('.thumb-wrap');
				if (!t)
					return this.dropNotAllowed;
				var rec = dataview.getRecord(t);
				var ds = dataview.store;
				rindex = false;
				if (rec) {
					rindex = ds.indexOfId(rec.id);
				}
				if (rindex === false)
					return this.dropNotAllowed;
				// for (i = 0; i < data.nodes.length; i++) {
				// var rowIndex = ds.indexOf(data.nodes[0]);
				// MyMessageTip.msg("提示", rindex + ":" + rowIndex,
				// true);
				// if (rindex == rowIndex)
				// rindex = false;
				// }
				var record = dataview.getRecord(data.nodes[0]);
				if (record.get("BRCH") && record.get("BRCH") == rec.get("BRCH")) {
					rindex = false;
				}
				return (rindex === false)
						? this.dropNotAllowed
						: this.dropAllowed;
			}
		});
	},
	move : function(rindex, data) {
		var dataview = this.dataview;
		var ds = dataview.store;
		// 获取插入行的记录
		var rdata = ds.getAt(rindex);
		// 默认是上移操作
		var toward = 0;
		var index = ds.indexOfId(data[0].id);
		if (rindex > index)
			toward = 1 // 如果是下移，修改方向值
		// 移除选择行
		for (i = 0; i < data.length; i++) {
			ds.remove(ds.getById(data[i].id));
		}
		// 根据id获取插入行的新位置并根据移动操作计算出插入位置
		rindex = ds.indexOfId(rdata.id) + toward;
		// 从插入位置依次插入选择行
		var start = rindex;
		for (i = 0; i < data.length; i++) {
			ds.insert(rindex, data[i]);
			rindex++;
		}
		// 重新选择选择行
		dataview.selectRange(start, rindex - 1)
	},
	getTpl : function() {
		return new Ext.XTemplate(
				'<tpl for=".">',
				'<div class="thumb-wrap" id="{BRXM}">',
				'<div class="thumb"><img src="images/0.jpg" title="{BRXM}"></div>',
				'<span class="x-editable">床位号:{BRCH}-<font color="red">{BRXM}</font></span></div>',
				'</tpl>', '<div class="x-clear"></div>');
	},
	onDblClick : function(view, index, item, e) {

	},
	onContextmenu : function(view, rowIndex, item, e) {
		this.parent.onContextMenu(view, rowIndex, e);
	},
	storeLoadData : function() {

	},
	getSelectedRecord : function(muli) {
		var selectedRecords = this.dataview.getSelectedRecords();
		if (selectedRecords.length < 1) {
			return null;
		}
		return selectedRecords[0];
	},
	getStore : function(items) {
		var o = this.getStoreFields(items)
		var readCfg = {
			root : 'body',
			totalProperty : 'totalCount',
			fields : o.fields
		}
		if (!this.isCompositeKey) {
			readCfg.id = o.pkey;
		}
		var reader = new Ext.data.JsonReader(readCfg)
		var url = ClassLoader.serverAppUrl || "";
		// add by yangl 请求统一加前缀
		if (this.requestData) {
			this.requestData.serviceId = 'phis.' + this.requestData.serviceId
		}
		var proxy = new Ext.data.HttpProxy({
					url : url + '*.jsonRequest',
					method : 'post',
					jsonData : this.requestData
				})
		proxy.on("loadexception", function(proxy, o, response, arg, e) {
					if (response.status == 200) {
						var json = eval("(" + response.responseText + ")")
						if (json) {
							var code = json["x-response-code"]
							var msg = json["x-response-msg"]
							this.processReturnMsg(code, msg, this.refresh)
						}
					} else {
						this.processReturnMsg(404, 'ConnectionError',
								this.refresh)
					}
				}, this)
		var store = new Ext.data.Store({
					proxy : proxy,
					reader : reader
				})
		return store
	}
});
