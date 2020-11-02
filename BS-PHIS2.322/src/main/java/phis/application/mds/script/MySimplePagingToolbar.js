$package("phis.application.mds.script")
/**
 * 重写的分页栏 增加金额统计,需要传的参数 cfg.divHtml; 字符串里面的div的id需要全系统唯一
 * @param {} cfg
 */
phis.application.mds.script.MySimplePagingToolbar = function(cfg) {
	this.divHtml="<div id='FPCX_JE' align='center' style='color:blue'>合计金额:￥0.00&nbsp;&nbsp;&nbsp;&nbsp;医保统筹:￥0.00</div>";
	phis.application.mds.script.MySimplePagingToolbar.superclass.constructor.apply(this, [cfg])
	this.addEvents({
				"beforePageChange" : true,
				"pageChange" : true
			})
}
Ext.extend(phis.application.mds.script.MySimplePagingToolbar, Ext.util.Observable, {})
Ext.extend(phis.application.mds.script.MySimplePagingToolbar, Ext.PagingToolbar, {
	initComponent : function() {
		var store = new Ext.data.SimpleStore({
					fields : ['value', 'text'],
					data : [[5, '每页5条'], [25, '每页25条'], [50, '每页50条'],
							[100, '每页100条']
					// , [300, '每页300条'],
					// [500, '每页500条']
					]
				});

		var combox = new Ext.form.ComboBox({
					store : store,
					valueField : "value",
					displayField : "text",
					editable : false,
					selectOnFocus : true,
					triggerAction : 'all',
					mode : 'local',
					emptyText : '',
					width : 80,
					value : this.requestData.pageSize || 25
				})
		combox.on("select", function(combo, record, index) {
					var pageSize = parseInt(record.data.value)
					var total = this.store.getTotalCount()

					if (this.pageSize >= total && pageSize > this.pageSize) {
						combox.setValue(this.pageSize)
						return
					}
					this.pageSize = pageSize
					this.doLoad(0);
				}, this)
		var label = new Ext.form.Label({
			html : this.divHtml
		})
		this.items = (["-", label]).concat(this.items || [])
		this.items = (["-", combox]).concat(this.items || [])
		util.widgets.MyPagingToolbar.superclass.initComponent.call(this);
	},
	updateInfo : function() {
		if (this.displayItem) {
			var count = this.store.getCount();
			var end = this.cursor + count
			var total = this.store.getTotalCount()
			if (end > total) {
				end = total
			}
			var msg = count == 0 ? this.emptyMsg : String.format(
					this.displayMsg, this.cursor + 1, end, total);
			this.displayItem.setText(msg);
		}
	},
	doLoad : function(start) {
		var pageSize = this.pageSize
		var pageNo = Math.ceil((start + pageSize) / pageSize)
		this.requestData.pageSize = pageSize
		this.requestData.pageNo = pageNo

		var o = {}, pn = this.getParams();
		o[pn.start] = start;
		o[pn.limit] = pageSize;
		if (this.fireEvent('beforechange', this, o) !== false) {
			this.store.load({
						params : o
					});
		}
	},
	onClick : function(which) {
		this.fireEvent("beforePageChange", which)
		phis.application.mds.script.MySimplePagingToolbar.superclass.onClick.call(this, which)
		this.fireEvent("pageChange", which)
	},
	firstPage : function() {
		this.onClick("first")
	},
	lastPage : function() {
		this.onClick("last")
	},
	nextPage : function() {
		if (this.next.disabled) {
			return
		}
		this.onClick('next')
	},
	prevPage : function() {
		if (this.prev.disabled) {
			return
		}
		this.onClick('prev')
	}
})
