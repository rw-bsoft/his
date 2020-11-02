/**
 * 药房模块,右边list模版
 * 
 * @author caijy
 */
$package("phis.application.sto.script")
$import("phis.script.SimpleList")

phis.application.sto.script.StorehouseMySimpleRightList = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	this.cnds = ['eq', ['$', '1'], ['i', 1]];// 页面查询的条件,需要重写
	this.imgUrl='images/grid.png';//页面记录图片地址
	this.serviceId="storehouseManageService";
	this.dateQueryActionId="dateQuery";//查询时间范围
	this.initDateQueryActionId="initDateQuery";//查询页面初始时间
	this.readRef="";
	this.modal = this.modal = true;
	cfg.autoLoadData = false;
	phis.application.sto.script.StorehouseMySimpleRightList.superclass.constructor.apply(this,
			[cfg])
	this.on("beforeclose", this.doCancel, this);
}
Ext.extend(phis.application.sto.script.StorehouseMySimpleRightList, phis.script.SimpleList, {// 生成日期框
	getCndBar : function(items) {
//		var simple = new Ext.FormPanel({
//					labelWidth : 50,
//					title : '',
//					defaults : {},
//					defaultType : 'textfield',
//					width : 190,
//					height : 25,
//					items : [new Ext.ux.form.Spinner({
//								fieldLabel : '财务月份',
//								name : 'storeDate',
//								value : this.getDate(),
//								strategy : {
//									xtype : "month"
//								}
//							})]
//				});
		var simple =new Ext.ux.form.Spinner({
								fieldLabel : '财务月份',
								name : 'storeDate',
								value : this.getDate(),
								strategy : {
									xtype : "month"
								}
							});
		this.simple = simple;
		return [simple];
	},
	// 条件查询
	doCndQuery : function(button, e, addNavCnd) {
		var initCnd = this.initCnd;
		var simple = this.simple;
		var cnd = null;
		if (simple) {
			var stroeDate = simple.getValue();
			if (stroeDate != null && stroeDate != "") {
				var dateSplit = stroeDate.split("-");
				var body = {};
				body["year"]=dateSplit[0];
				body["month"]=dateSplit[1];
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.dateQueryActionId,
							body : body
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					var dates = r.json.body;
					if (dates.length != 2) {
						return;
					}
					cnd = this.getQueryCnd(1,dates);
					if (initCnd) {
						cnd = ['and', initCnd, cnd];
					}

				}

			}
		}
		// 没选财务月份不让查询
		if (cnd == null) {
			return;
		}
		cnd = ['and', cnd, this.cnds];
		if (this.selectValue) {
			cnd = ['and', this.getQueryCnd(2,null),
					cnd];
		}
		this.requestData.cnd = cnd;
		this.refresh();
	},
	//条件, 需重写
	getQueryCnd : function(tag,dates) {
		if(tag==1){
		return [
				'and',
				['ge', ['$', "str(CKRQ,'yyyy-mm-dd hh24:mi:ss')"],
						['s', dates[0]]],
				['le', ['$', "str(CKRQ,'yyyy-mm-dd hh24:mi:ss')"],
						['s', dates[1]]]];
		}else{
		return ['eq', ['$', 'CKFS'], ['i', this.selectValue]]
		}
		
	},
	// 页面打开时记录前增加确认图标
	onRenderer : function(value, metaData, r) {
		return "<img src='"+this.imgUrl+"'/>";
		//return "<img src='images/grid.png'/>";
	},
	// 页面打开时默认的时间
	getDate : function() {
		var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : this.initDateQueryActionId
				});
		if (r.code > 300) {
			this.processReturnMsg(r.code, r.msg, this.getDate);
			return;
		}
		return r.json.date;
	},
	// 获取该月最后一天
	getLastDay : function(year, month) {
		var new_year = year; // 取当前的年份
		var new_month = month;// 取下一个月的第一天，方便计算（最后一天不固定）
		if (new_month >= 12) // 如果当前大于12月，则年份转到下一年
		{
			new_month -= 12; // 月份减
			new_year++; // 年份增
		}
		var newnew_date = new Date(new_year, new_month, 1); // 取当年当月中的第一天
		return year
				+ "-"
				+ month
				+ "-"
				+ (new Date(newnew_date.getTime() - 1000 * 60 * 60 * 24))
						.getDate();// 获取当月最后一天日期
	},
	// 获取界面财务月份前一个月的月初时间
	getLastMonth : function(year, month) {
		var new_year = year; // 取当前的年份
		var new_month = month - 1;// 取上一个月
		if (month == 1) {
			new_month = 12;
			new_year--;
		}
		return new_year + "-" + new_month + "-01";
	},
	// 查看
	doLook : function() {
		var r = this.getSelectedRecord()
		if (r == null) {
			return;
		}
		var initDataBody = this.getInitDataBody(r);
		this.module = this.createModule("module", this.readRef);
		this.module.on("winClose", this.onClose, this);
		var win = this.getWin();
		win.add(this.module.initPanel());
		this.module.condition=this.getReadCondition();
		win.show()
		win.center()
		if (!win.hidden) {
			this.module.isRead=true;
			this.module.doRead(initDataBody);
		}

	},
	getReadCondition:function(){
	return {};
	},
	onDblClick : function(grid, index, e) {
		var item = {};
		item.text = "查看";
		item.cmd = "look";
		this.doAction(item, e)

	},
	onClose : function() {
		this.getWin().hide();
	},
	doCancel : function() {
		if (this.module) {
			return this.module.doClose();
		}
	},
	getWin : function() {
		var win = this.win
		if (!win) {
			win = new Ext.Window({
				id : this.id,
				title : this.title||this.name,
				width : this.width,
				iconCls : 'icon-grid',
				shim : true,
				layout : "fit",
				animCollapse : true,
				closeAction : "hide",
				constrainHeader : true,
				constrain : true,
				minimizable : true,
				maximizable : true,
				shadow : false,
				modal : this.modal || false
					// add by huangpf.
				})
			var renderToEl = this.getRenderToEl()
			if (renderToEl) {
				win.render(renderToEl)
			}
			win.on("show", function() {
						this.fireEvent("winShow")
					}, this)
			win.on("add", function() {
						this.win.doLayout()
					}, this)
			win.on("beforeclose", function() {
						return this.fireEvent("beforeclose", this);
					}, this);
			win.on("beforehide", function() {
						return this.fireEvent("beforeclose", this);
					}, this);
			win.on("close", function() {
						this.fireEvent("close", this)
					}, this)
			win.on("hide", function() { // ** add by yzh 2010-06-24 **
						this.fireEvent("hide", this)
					}, this)
			this.win = win
		}
		return win;
	}
})