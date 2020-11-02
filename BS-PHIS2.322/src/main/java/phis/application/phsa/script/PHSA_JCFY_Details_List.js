$package("phis.application.phsa.script")

$import("phis.script.SimpleList","phis.script.ux.GroupSummary","phis.script.ux.RowExpander")
/**
 * 均次费用数据明细展示list
 */
phis.application.phsa.script.PHSA_JCFY_Details_List = function(cfg) {
	this.serverParams = {serviceAction:cfg.queryActionId};
	cfg.disablePagingTbr = true;
	cfg.width="900";
	phis.application.phsa.script.PHSA_JCFY_Details_List.superclass.constructor.apply(this, [cfg]);
}
Ext.extend(phis.application.phsa.script.PHSA_JCFY_Details_List,
		phis.script.SimpleList, {
		/**
		 * 扩展顶部工具栏
		 * 		添加开始统计时间和结束统计时间
		 * @param cfg
		 */
		expansion : function(cfg) {
			// 顶部工具栏
			var label = new Ext.form.Label({
						text : "统计日期"
					});
			this.beginDate = new Ext.form.DateField({
					name : 'storeDate',
					value : new Date(),
					width : 100,
					allowBlank : false,
					altFormats : 'Y-m-d',
					format : 'Y-m-d'
				});
			this.endDate = new Ext.form.DateField({
				name : 'storeDate',
				value : new Date(),
				width : 100,
				allowBlank : false,
				altFormats : 'Y-m-d',
				format : 'Y-m-d'
			});
			var tbar = cfg.tbar;
			delete cfg.tbar;
			cfg.tbar = [];
			cfg.tbar.push([label, '-', this.beginDate,' 至 ',this.endDate, tbar]);
//			cfg.bbar = [];
//			this.hj = new Ext.form.Label({
//				text : "",
//				style : "color:#335ebd;font-weight:bold;"
//			});
//			cfg.bbar.push('->','合计：', this.hj);
		},
		
		/**
		 * 刷新按钮实现
		 */
		doRefresh : function() {
			var beginDate = new Date(this.beginDate.getValue()).format("Y-m-d");
			var endDate = new Date(this.endDate.getValue()).format("Y-m-d");
			if (beginDate != null && endDate != null && beginDate != ""
				&& endDate != "" && beginDate > endDate) {
				Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
				return;
			}
			this.requestData.body={
					beginDate : beginDate,
					endDate : endDate,
					TYPE : this.cmd
			}
			this.store.load({
				callback: function(records, options, success){
//					this.setHJValue();    // 调用重新合计
				},
				scope: this//作用域为this。必须加上否则this.setHJValue(); 无法调用
			});
		},
		
		/**
		 * 设置合计值
		 */
		setHJValue : function(){
			var items = this.store.data.items;
			var hj = 0;
			var zfy=0;
			var rs = 0;
			if(items){
				var item;
				for(var i=0; i<items.length ; i++){
					item = items[i];
					zfy += item.data.HJFY;
					rs += item.data.MZRC;
				}
			}
			if(rs!=0){
			//取两位小数
				hj = parseInt(zfy/rs*100)/100;
			}
			this.hj.setText(hj);
		},
		/**
		 * 设置有关信息，包括重新初始化查询条件等
		 */
		setInfo : function(cmd, queryTime){
			this.cmd = cmd;
			this.beginDate.setValue(new Date(queryTime));
			this.endDate.setValue(Date.parseDate(Date.getServerDate(),'Y-m-d').add(Date.DAY,-1));
		},
		showHJ : function(v, params, data) {
			return '合计';
		},
		showJCFY : function(v, params, data) {
			if(data.data.MZRC==0) return 0;
			return (data.data.HJFY/data.data.MZRC).toFixed(2);
		}
});