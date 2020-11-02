$package("phis.application.phsa.script")

$import("phis.script.SimpleList","phis.script.ux.GroupSummary","phis.script.ux.RowExpander")
/**
 * 数据明细展示list
 * 		包括：a_DCFS  大处方数  a_MZRC 门诊人次  a_RYRS 入院人数    a_CYRS  出院人数   a_ZYRS  在院人数  a_WZRS  危重人数
 */
phis.application.phsa.script.PHSA_Data_Details_List = function(cfg) {
	this.serverParams = {serviceAction:cfg.queryActionId};
	cfg.disablePagingTbr = true;
	cfg.width="900";
	phis.application.phsa.script.PHSA_Data_Details_List.superclass.constructor.apply(this, [cfg]);
}
Ext.extend(phis.application.phsa.script.PHSA_Data_Details_List,
		phis.script.SimpleList, {
//		initPanel : function(sc) {
//			
//		},
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
			this.zhi = new Ext.form.Label({
				text : " 至 "
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
			cfg.tbar.push([label, '-', this.beginDate,this.zhi,this.endDate, tbar]);
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
			if(!this.flag){
				if (beginDate != null && endDate != null && beginDate != ""
					&& endDate != "" && beginDate > endDate) {
					Ext.MessageBox.alert("提示", "开始时间不能大于结束时间");
					return;
				}
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
		setHJValue : function(){
			var items = this.store.data.items;
			var hj = 0;
			if(items){
				var item;
				for(var i=0; i<items.length ; i++){
					item = items[i];
					hj += item.data.RS;
				}
			}
			//取两位小数
			hj = parseInt(hj*100)/100;
			this.hj.setText(hj);
		},
		/**
		 * 设置结束日期隐藏
		 * @param flag
		 * 			是否隐藏  true 隐藏  false 不隐藏
		 * @param cmd
		 * 			当前操作模块
		 */
		setEndDataHide : function(flag, cmd, queryTime){
			this.cmd = cmd;
			this.flag = flag;
			if(flag){
				this.beginDate.setValue(new Date(queryTime));
				this.endDate.setValue(Date.parseDate(Date.getServerDate(),'Y-m-d').add(Date.DAY,-1));
				this.endDate.hide();
				this.zhi.hide();
			}else{
				this.beginDate.setValue(new Date(queryTime));
				this.endDate.setValue(Date.parseDate(Date.getServerDate(),'Y-m-d').add(Date.DAY,-1));
				this.endDate.show();
				this.zhi.show();
			}
		},
		showHJ : function(v, params, data) {
			return '合计';
		},
		showZB : function(v, params, data) {
			return '100%';
		}
});