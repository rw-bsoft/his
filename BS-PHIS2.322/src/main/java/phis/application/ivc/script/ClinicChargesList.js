$package("phis.application.ivc.script")

$import("phis.script.SimpleList")

phis.application.ivc.script.ClinicChargesList = function(cfg) {
	// 由于开始只做界面,暂时不去查数据
	cfg.disablePagingTbr = true;
	cfg.showButtonOnTop = true;
	cfg.selectOnFocus = true;
	cfg.autoLoadData = false;
	cfg.showRowNumber = true;
	// cfg.summaryable = true;
	this.tbar = []
	cfg.group = "CFSB";
	cfg.groupTextTpl = "<table width='60%' style='color:#3764a0;font:bold !important;' border='0' cellspacing='0' cellpadding='0'><tr><td width='20%'>&nbsp;&nbsp;<b>{[values.rs[0].data.DJLX_text]}{[values.rs[0].data.CFSB]}</b></td><td width='10%'><b>{[values.rs[0].data.KSDM_text]}</b></td><td width='10%'><b>{[values.rs[0].data.YSDM_text]}</b></td><tpl if=\"values.rs[0].data.CFLX &gt; 2\"><td width='10%'><b>帖数：{[values.rs[0].data.CFTS]}</b></td></tpl><tpl if=\"values.rs[0].data.CFLX < 3\"><td width='10%'></td></tpl><td width='10%'><div align='left'><b>&nbsp;&nbsp;({[values.rs.length]} 条记录)</b></div></td></tr></table>"
	// cfg.groupTextTpl =
	// "{[values.rs[0].data.DJLX_text]}{[values.rs[0].data.CFHM]} ｜ 就诊科室 :
	// {[values.rs[0].data.KSDM_text]} ｜ 开方医生 : {[values.rs[0].data.YSDM_text]}
	// ｜ ({[values.rs.length]} 条记录)";
	phis.application.ivc.script.ClinicChargesList.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.ivc.script.ClinicChargesList, phis.script.SimpleList, {
	loadData : function() {
		this.clear(); // ** add by yzh , 2010-06-09 **
		this.requestData.serviceId = "phis.clinicChargesProcessingService";
		this.requestData.serviceAction = "queryDJDetails";
		this.requestData.body = this.djs;
		if(this.fpcx){
			this.requestData.fpcx = this.fpcx;
		}else{
			this.requestData.fpcx = 0;
		}
		this.requestData.brxz = this.brxz;
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
		// ** add by yzh **
		this.resetButtons();
	},
	onDblClick : function(grid, index, e) {
		var r = this.grid.getSelectionModel().getSelected();
		if (r.get("DJLY") == 6) {
			if (r.get("DJLX") == "1") {
				this.opener.doCflr(this.getSelectId(r.get("CFSB")));
			} else {
				this.opener.doCzlr();
			}
		}
	},
	getSelectId : function(cfsb) {
		for (var i = 0; i < this.CFSBS.length; i++) {
			if (this.CFSBS[i] == cfsb) {
				this.selectId = i;
			}
		}
		return this.selectId;
	},
	getCM : function(items) {
		var cm = []
		var ac = util.Accredit;
		var expands = []
		if (this.showRowNumber) {
			cm.push(new Ext.grid.RowNumberer())
		}
		if (this.mutiSelect) {
			cm.push(this.sm);
		}
		for (var i = 0; i < items.length; i++) {
			var it = items[i]
			if ((it.display <= 0 || it.display == 2) || !ac.canRead(it.acValue)) {
				continue
			}
			if (it.expand) {
				var expand = {
					id : it.dic ? it.id + "_text" : it.id,
					alias : it.alias,
					xtype : it.xtype
				}
				expands.push(expand)
				continue
			}
			if (!this.fireEvent("onGetCM", it)) { // **
				// fire一个事件，在此处可以进行其他判断，比如跳过某个字段
				continue;
			}
			var width = parseInt(it.width || 80)
			// if(width < 80){width = 80}
			var c = {
				header : it.alias,
				width : width,
				sortable : true,
				dataIndex : it.dic ? it.id + "_text" : it.id
			}
			if (!this.isCompositeKey && it.pkey == "true") {
				c.id = it.id
			}
			if (it.summaryType) {
				c.summaryType = it.summaryType;
				if (it.summaryRenderer) {
					var func = eval("this." + it.summaryRenderer)
					if (typeof func == 'function') {
						c.summaryRenderer = func
					}
				}
			}
			switch (it.type) {
				case 'int' :
				case 'double' :
				case 'bigDecimal' :
					if (!it.dic) {
						c.css = "color:#00AA00;font-weight:bold;"
						c.align = "right"
						if (it.type == 'double' || it.type == 'bigDecimal') {
							c.precision = it.precision;
							c.nullToValue = it.nullToValue;
							c.renderer = function(value, metaData, r, row, col,
									store) {
								if (value == null && this.nullToValue) {
									value = parseFloat(this.nullToValue)
									var retValue = this.precision ? value
											.toFixed(this.precision) : value;
									try {
										r.set(this.id, retValue);
									} catch (e) {
										// 防止新增行报错
									}
									return retValue;
								}
								if (value != null) {
									value = parseFloat(value);
									var retValue = this.precision ? value
											.toFixed(this.precision) : value;
									return retValue;
								}
							}
						}
					}
					break
				case 'timestamp' :
					// c.renderer = Ext.util.Format.dateRenderer('Y-m-d HH:m:s')
			}
			if (it.renderer) {
				var func
				func = eval("this." + it.renderer)
				if (typeof func == 'function') {
					c.renderer = func
				}
			}
			if (this.fireEvent("addfield", c, it)) {
				cm.push(c)
			}
		}
		if (expands.length > 0) {
			this.rowExpander = this.getExpander(expands)
			cm = [this.rowExpander].concat(cm)
			this.array.push(this.rowExpander)// add by taoy
		}
		return cm
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
		var xyf = 0;
		var zyf = 0;
		var cyf = 0;
		var jcf = 0;
		var hjje = 0;
		var zfje = 0;
		this.CFSBS = new Array();
		var detal = [];
		for (var i = 0; i < n; i++) {
			var r = store.getAt(i);
			detal.push(r.data);
			if (r.get("CFLX") == 1) {
				xyf += r.get("HJJE");
			} else if (r.get("CFLX") == 2) {
				zyf += r.get("HJJE");
			} else if (r.get("CFLX") == 3) {
				cyf += r.get("HJJE");
			} else {
				jcf += r.get("HJJE");
			}
			hjje += r.get("HJJE");
			zfje += parseFloat((parseFloat(r.get("HJJE")) * parseFloat(r
					.get("ZFBL"))).toFixed(2));
			var b = false;
			for (var j = 0; j < this.CFSBS.length; j++) {
				if (this.CFSBS[j] == r.get("CFSB")) {
					b = true;
				}
			}
			if (!b && (r.get("DJLX") == 1) && (r.get("DJLY") == 6)) {
				this.CFSBS.push(r.get("CFSB"))
			}
		}
		if (this.openby == "MZSF") {
			this.opener.doSPLR(this.djs, detal);
		}
		// document.getElementById("MZSH_TJXX_" + this.openby).innerHTML =
		// "统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
		// + "西药费："
		// + parseFloat(xyf).toFixed(2)
		// + "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
		// + "中药费："
		// + parseFloat(zyf).toFixed(2)
		// + "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
		// + "草药费："
		// + parseFloat(cyf).toFixed(2)
		// + "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
		// + "检查费："
		// + parseFloat(jcf).toFixed(2)
		// + "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
		// + "合计金额："
		// + parseFloat(hjje).toFixed(2)
		// + "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
		// + "自负金额："
		// + parseFloat(zfje).toFixed(2) + "&nbsp;&nbsp;￥";
		// 江西要求去掉自负金额
		document.getElementById("MZSH_TJXX_" + this.openby).innerHTML = "统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "西药费："
				+ parseFloat(xyf).toFixed(2)
				+ "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "中药费："
				+ parseFloat(zyf).toFixed(2)
				+ "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "草药费："
				+ parseFloat(cyf).toFixed(2)
				+ "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "检查费："
				+ parseFloat(jcf).toFixed(2)
				+ "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "合计金额："
				+ parseFloat(hjje).toFixed(2)
				+ "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;";
	},
	doTJXXclear : function() {
		// document.getElementById("MZSH_TJXX_" + this.openby).innerHTML =
		// "统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
		// + "西药费：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
		// + "中药费：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
		// + "草药费：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
		// + "检查费：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
		// + "合计金额：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
		// + "自负金额：0.00&nbsp;&nbsp;￥";
		// 江西要求去掉自负金额
		// document.getElementById("MZSH_TJXX_" + this.openby).innerHTML =
		// "统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
		// + "西药费：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
		// + "中药费：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
		// + "草药费：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
		// + "检查费：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
		// + "合计金额：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
		// 确定后保留上一个病人的结算信息
		if (this.opener && this.opener.JSXX) {
			// alert(this.opener.JSXX.TZJE)
			document.getElementById("MZSH_TJXX_" + this.openby).innerHTML = "<span style='color:red'>总计金额："
					+ parseFloat(this.opener.JSXX.ZJJE).toFixed(2)
//					+ "&nbsp;￥&nbsp;&nbsp;"
//					+ "统筹支出："
//					+ parseFloat(this.opener.JSXX.JJZF).toFixed(2)
//					+ "&nbsp;￥&nbsp;&nbsp;"
//					+ "帐户支付："
//					+ parseFloat(this.opener.JSXX.ZHZF).toFixed(2)
					+ "&nbsp;￥&nbsp;&nbsp;"
					+ "自负金额："
					+ parseFloat(this.opener.JSXX.ZFJE).toFixed(2)
					+ "&nbsp;￥&nbsp;&nbsp;"
					+ "应收款："
					+ parseFloat(this.opener.JSXX.YSK).toFixed(2)
					+ "&nbsp;￥&nbsp;&nbsp;"
					+ "缴款："
					+ parseFloat(this.opener.JSXX.JKJE).toFixed(2)
					+ "&nbsp;￥&nbsp;&nbsp;"
					+ "退找："
					+ parseFloat(this.opener.JSXX.TZJE).toFixed(2)
					+ "&nbsp;￥&nbsp;&nbsp;</span>";
		}
	},
	expansion : function(cfg) {
		// 底部 统计信息,未完善

		var label = new Ext.form.Label({
			// html : "<div id='MZSH_TJXX_"
			// + this.openby
			// + "' align='center'
			// style='color:blue'>统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
			// + "西药费：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
			// + "中药费：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
			// + "草药费：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
			// + "检查费：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
			// + "合计金额：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
			// + "自负金额：0.00&nbsp;&nbsp;￥</div>"
			// 江西要求去掉自负金额
			html : "<div id='MZSH_TJXX_"
					+ this.openby
					+ "' align='center' style='color:blue'>统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "西药费：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "中药费：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "草药费：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "检查费：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "合计金额：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "</div>"
		})
		cfg.bbar = [];
		cfg.bbar.push(label);
	},
	showColor : function(v, params, data) {
		var YPZH = data.get("YPZH") % 2 + 1;
		var PSPB = data.get("PSPB");
		switch (YPZH) {
			case 1 :
				params.css = "x-grid-cellbg-1";
				break;
			case 2 :
				params.css = "x-grid-cellbg-2";
				break;
			case 3 :
				params.css = "x-grid-cellbg-3";
				break;
			case 4 :
				params.css = "x-grid-cellbg-4";
				break;
			case 5 :
				params.css = "x-grid-cellbg-5";
				break;
		}
		var msg = data.get("msg");
		if (msg) {
			if (msg.error_kc) {
				return "<img src='images/i_error.gif' title='错误："
						+ msg.error_kc + "!'/>"
			}
		}
		return PSPB > 0 ? "<h2 style='color:red'>皮</h2>" : "";
	}
});
