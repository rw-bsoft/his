/**
 * 待发药处方药品详情列表
 * 
 * @author : caijy
 */
$package("phis.application.pha.script")

$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyDispensingDetailList = function(cfg) {
	cfg.autoLoadData = this.autoLoadData = false;
	cfg.disablePagingTbr = true;
	// cfg.summaryable = true;
	phis.application.pha.script.PharmacyDispensingDetailList.superclass.constructor
			.apply(this, [cfg])

}

Ext.extend(phis.application.pha.script.PharmacyDispensingDetailList,
		phis.script.SimpleList, {
			/*add by zhaojian 2017-05-31 处方明细中增加库存数量 begin*/
			loadData : function() {
				this.loading = true;
				this.requestData.serviceId = this.serviceId;
				this.requestData.serviceAction = this.queryServiceAction;
				this.requestData.body = {
					cfsb : this.requestData.cfsb
				}
				phis.application.pha.script.PharmacyDispensingDetailList.superclass.loadData
						.call(this);
			},
			/* add by zhaojian 2017-05-31 处方明细中增加库存数量 end*/
			/*
			 * //用于判断处方类型,如果是草药显示帖数 cflxsb:function (cflx){ if(cflx==3){
			 * this.grid.getColumnModel().setHidden(
			 * this.grid.getColumnModel().getIndexById("CFTS"), false); }else{
			 * this.grid.getColumnModel().setHidden(
			 * this.grid.getColumnModel().getIndexById("CFTS"), true); }
			 *  }, //重写 为了显示和隐藏处方帖数 getCM : function(items) { var cm = [] var ac =
			 * util.Accredit; var expands = [] if (this.showRowNumber) {
			 * cm.push(new Ext.grid.RowNumberer()) } if (this.mutiSelect) {
			 * cm.push(this.sm); } for (var i = 0; i < items.length; i++) { var
			 * it = items[i] if ((it.display <= 0 || it.display == 2) ||
			 * !ac.canRead(it.acValue)) { continue } if (it.expand) { var expand = {
			 * id : it.dic ? it.id + "_text" : it.id, alias : it.alias, xtype :
			 * it.xtype } expands.push(expand) continue } if
			 * (!this.fireEvent("onGetCM", it)) { // ** //
			 * fire一个事件，在此处可以进行其他判断，比如跳过某个字段 continue; } var width =
			 * parseInt(it.width || 80) // if(width < 80){width = 80} var c = {
			 * id:it.id, header : it.alias, width : width, sortable : true,
			 * dataIndex : it.dic ? it.id + "_text" : it.id } if
			 * (!this.isCompositeKey && it.pkey == "true") { c.id = it.id } if
			 * (it.summaryType) { c.summaryType = it.summaryType; } switch
			 * (it.type) { case 'int' : case 'double' : case 'bigDecimal' : if
			 * (!it.dic) { c.css = "color:#00AA00;font-weight:bold;" c.align =
			 * "right" if (it.type == 'double' || it.type == 'bigDecimal') {
			 * c.precision = it.precision; c.nullToValue = it.nullToValue;
			 * c.renderer = function(value, metaData, r, row, col, store) { if
			 * (value == null && this.nullToValue) { value =
			 * parseFloat(this.nullToValue) var retValue = this.precision ?
			 * value .toFixed(this.precision) : value; try { r.set(this.id,
			 * retValue); } catch (e) { // 防止新增行报错 } return retValue; } if
			 * (value != null) { value = parseFloat(value); var retValue =
			 * this.precision ? value .toFixed(this.precision) : value; return
			 * retValue; } } } } break case 'timestamp' : // c.renderer =
			 * Ext.util.Format.dateRenderer('Y-m-d HH:m:s') } if (it.renderer) {
			 * var func func = eval("this." + it.renderer) if (typeof func ==
			 * 'function') { c.renderer = func } } if(it.summaryType) {
			 * c.summaryType = it.summaryType; if(it.summaryRenderer) { var func =
			 * eval("this." + it.summaryRenderer) if (typeof func == 'function') {
			 * c.summaryRenderer = func } } } if (this.fireEvent("addfield", c,
			 * it)) { cm.push(c) } } if (expands.length > 0) { this.rowExpander =
			 * this.getExpander(expands) cm = [this.rowExpander].concat(cm)
			 * this.array.push(this.rowExpander)// add by taoy } return cm },
			 */
			totalHJJE : function(v, params, data) {
				// return v == null
				// ? '0'
				// : ('<span style="font-size:14px;color:black;">总金额:&#160;' +
				// parseFloat(v).toFixed(2) + '</span>');

			},
			doNew : function() {
				this.clear()
				document.getElementById("ZJFY").innerHTML = "总金额：";
			},
			expansion : function(cfg) {
				// 底部 统计信息,未完善
				var label = new Ext.form.Label({
					html : "<div id='ZJFY' align='center' style='color:blue'>总金额：</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
				var store = this.grid.getStore();
				var n = store.getCount()
				var hjje = 0;
				this.CFSBS = new Array();
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i);
					hjje += r.get("HJJE");
				}
				document.getElementById("ZJFY").innerHTML = "总金额："
						+ hjje.toFixed(2);
			},
			onRenderer : function(value, metaData, r) {
				if (r.get("ZFYP") == 1) {
					return '<span style="font-size:12px;color:red;">(自备)</span>'
							+ value
				}
				return value;
			},
			doPrint : function(){
				var url="resources/phis.prints.jrxml.PharmacyDispensing.print?silentPrint=1";
				url += "&temp=" + new Date().getTime()+"&cfsb="+this.requestData.cfsb;
				var LODOP=getLodop();
				LODOP.PRINT_INIT("打印控件");
				LODOP.SET_PRINT_PAGESIZE("0","","","");
				LODOP.ADD_PRINT_HTM("0","0","100%","100%",util.rmi.loadXML({url:url,httpMethod:"get"}));
				LODOP.SET_PRINT_MODE ("PRINT_PAGE_PERCENT","Full-Width");
				//预览LODOP.PREVIEW();
				//直接打印
				LODOP.PRINT();
			}
		});