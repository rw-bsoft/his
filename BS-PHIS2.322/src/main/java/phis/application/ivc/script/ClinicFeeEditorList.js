$package("phis.application.ivc.script");

$import("phis.script.EditorList");

phis.application.ivc.script.ClinicFeeEditorList = function(cfg) {
	// 由于开始只做界面,暂时不去查数据
	cfg.disablePagingTbr = true;
	cfg.showButtonOnTop = true;
	cfg.selectOnFocus = true;
	cfg.autoLoadData = false;
	cfg.showRowNumber = true;
	cfg.sortable = false;
	cfg.minListWidth = 570;
	cfg.remoteUrl = 'MedicineFEE';
	cfg.remoteTpl = '<td width="18px" style="background-color:#deecfd">{numKey}.</td><td width="60px" style="color:red">{YYZBM}</td><td width="160px" title="{YPMC}">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW:nl2br("")}</td><td width="80px">{CDMC}</td><td width="50px">{LSJG}</td><td width="50px">{KCSL}</td><td width="50px">{LSJG}</td>';
	phis.application.ivc.script.ClinicFeeEditorList.superclass.constructor.apply(this,
			[cfg]);
//	this.on("shortcutKey",this.shortcutKeyFunc,this);//监听快捷键
};
Ext.extend(phis.application.ivc.script.ClinicFeeEditorList, phis.script.EditorList, {
	initPanel : function(sc) {
		this.type = 1;
		var grid = phis.application.ivc.script.ClinicFeeEditorList.superclass.initPanel
				.call(this, sc);
		var sm = grid.getSelectionModel();
		var _ctx = this;
		// 重写grid的onEditorKey事件
		grid.onEditorKey = function(field, e) {
			if (e.getKey() == e.ENTER && !e.shiftKey) {
//				var r = _ctx.getSelectedRecord();
				if((_ctx.type+"")=="1"||(_ctx.type+"")=="2"||(_ctx.type+"")=="3"){
					var sm = this.getSelectionModel();
					var cell = sm.getSelectedCell();
					var count = this.colModel.getColumnCount();
					if (cell[1]+1 >= count) {// 实现倒数第二格单元格回车新增行操作
						_ctx.insert(_ctx.type,0);
						return;
					}
				}else{
					var sm = this.getSelectionModel();
					var cell = sm.getSelectedCell();
					var count = this.colModel.getColumnCount();
					if (cell[1]+3 >= count) {// 实现倒数第二格单元格回车新增行操作
						_ctx.insert(_ctx.type,0);
						return;
					}
				}
			}
			this.selModel.onEditorKey(field, e);
		};
		// 重写onEditorKey方法，实现Enter键导航功能
		sm.onEditorKey = function(field, e) {
			var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
			if (k == e.ENTER) {
				e.stopEvent();
				if (!ed) {
					ed = g.lastActiveEditor;
				}
				ed.completeEdit();
				if (e.shiftKey) {
					newCell = g.walkCells(ed.row, ed.col - 1, -1,
							sm.acceptsNav, sm);
				} else {
					newCell = g.walkCells(ed.row, ed.col + 1, 1,
							sm.acceptsNav, sm);
				}

			} else if (k == e.TAB) {
				e.stopEvent();
				ed.completeEdit();
				if (e.shiftKey) {
					newCell = g.walkCells(ed.row, ed.col - 1, -1,
							sm.acceptsNav, sm);
				} else {
					newCell = g.walkCells(ed.row, ed.col + 1, 1,
							sm.acceptsNav, sm);
				}
			} else if (k == e.ESC) {
				ed.cancelEdit();
			}
			if (newCell) {
				r = newCell[0];
				c = newCell[1];
				// 判断单价是否为0
				if (c == 7) {
					if (field.getValue() == 0) {
						c--;
					}
				}
				this.select(r, c);
				if (g.isEditor && !g.editing) {
					ae = g.activeEditor;
					if (ae && ae.field.triggerBlur) {
						ae.field.triggerBlur();
					}
					g.startEditing(r, c);
				}
			}

		};
		this.on("afterCellEdit", this.afterGridEdit, this);
		return grid;
	},
//	loadSystemParam : function() {
//		var resData = phis.script.rmi.miniJsonRequestSync({
//					serviceId : "publicService",
//					serviceAction : "loadSystemParams",
//					body : {
//						commons : ['XYF', 'ZYF', 'CYF'],
//						privates : ['YS_MZ_FYYF_XY', 'YS_MZ_FYYF_ZY',
//								'YS_MZ_FYYF_CY', 'XSFJJJ', 'HQFYYF']
//					}
//				});
//		this.exContext.systemParams = resData.json.body;
//	},
//	onReady : function() {
//		this.loadSystemParam();
//		document.onkeydown = function(e){
//			var oEvent=window.event||e;
//			if(oEvent.keyCode==13 && oEvent.altKey){
//				alert("你按下了");
//			}
//		};
//	},
	doJX : function(){
		this.remoteDic.lastQuery = "";
		this.type="0";
		this.insert("0",1,1);
//		this.opener.opener.reloadPharmacy(1);
	},
	doXY : function(){
		this.remoteDic.lastQuery = "";
		this.type="1";
//		this.opener.opener.onClinicSelect(1);
		this.insert("1",1,1);
	},
	doZY : function(){
		this.remoteDic.lastQuery = "";
		this.type="2";
//		this.opener.opener.onClinicSelect(2);
		this.insert("2",1,1);
	},
	doCY : function(){
		this.remoteDic.lastQuery = "";
		this.type="3";
//		this.opener.opener.onClinicSelect(3);
		this.insert("3",1,1);
	},
	getPharmacyIdByCFLX : function(type) {
		type = parseInt(type);
		var PharmacyId = null;
		switch (type) {
			case 1 :
				PharmacyId = this.exContext.systemParams.YS_MZ_FYYF_XY;
				break;
			case 2 :
				PharmacyId = this.exContext.systemParams.YS_MZ_FYYF_ZY;
				break;
			case 3 :
				PharmacyId = this.exContext.systemParams.YS_MZ_FYYF_CY;
				break;
			default :
				PharmacyId = this.exContext.systemParams.YS_MZ_FYYF_XY;
		}
		return PharmacyId;
	},
	beforeCellEdit : function(e) {
		var f = e.field
		var record = e.record
		this.type=record.get("CFLX");
		if((!this.opener.opener.form.getForm().findField("KSDM").getValue())||(!this.opener.opener.form.getForm().findField("YSDM").getValue())){
			if(!this.opener.opener.form.getForm().findField("YSDM").getValue()){
				this.opener.opener.form.getForm().findField("YSDM").focus(false,200);
				MyMessageTip.msg("提示", "请先选择当前医生!", true);
			}else{
				this.opener.opener.form.getForm().findField("KSDM").focus(false,200);
				MyMessageTip.msg("提示", "请先选择当前科室!", true);
			}
			return false;
		}
		if(record.get("DJLY")!=6){
			return false
		}
		var op = record.get("_opStatus")
		var cm = this.grid.getColumnModel()
		var c = cm.config[e.column]
		var enditor = cm.getCellEditor(e.column)
		var it = c.schemaItem
		var ac = util.Accredit;
		if (op == "create") {
			if (!ac.canCreate(it.acValue)) {
				return false
			}
		} else {
			if (!ac.canUpdate(it.acValue)) {
				return false
			}
		}
		if (it.id == "YPDJ") {
			var r = this.getSelectedRecord();
			if (r.get("YPDJ_Y") != 0.00)
				return false;
		}
		if(it.id == "YPMC"){
			this.remoteDicStore.baseParams.type = this.type;
			this.remoteDicStore.baseParams.pharmacyId = this.getPharmacyIdByCFLX(this.type);
		}
	},
	afterGridEdit : function(it, record, field, v) {
		if(it.id=='YPSL'){
			this.opener.opener.MZXX.mxsave = true;
			if(record.get("YPSL")==0){
				record.set("YPXH","");
				this.doRemove();
				this.totalCount();
				return;
			}
			if(record.get("YPDJ")&&record.get("YPSL")){
				record.set("HJJE",parseFloat(parseFloat(v)*parseFloat(record.get("YPDJ"))*parseFloat(record.get("CFTS"))).toFixed(2));
				this.totalCount();
			}
			var YYZL = v;
			if (record.get("CFLX") == 3) {
				var CFTS = record.get("CFTS");
				if (CFTS > 0) {
					YYZL = YYZL * parseInt(CFTS);
				}
			}
			if(record.get("CFLX")>0){
				this.checkInventory(YYZL, record);
			}else{
				this.checkInventoryCz(YYZL, record);
			}
		}else if(it.id=='YPDJ'){
			this.opener.opener.MZXX.mxsave = true;
			if(record.get("YPDJ")&&record.get("YPSL")){
				record.set("HJJE",parseFloat(parseFloat(v)*parseFloat(record.get("YPSL"))*parseFloat(record.get("CFTS"))).toFixed(2));
				this.totalCount();
			}
		}else if(it.id=='YPYF'){
			this.opener.opener.MZXX.mxsave = true;
			this.store.each(function(r) {
				if (r.get('YPZH_show') == record
						.get('YPZH_show')) {
						r.set(it.id, v);
						r.set(it.id + '_text', record
										.get(it.id
												+ '_text'));
				}
			}, this)
		}
	},
	checkInventory : function(YYZL, record) {
		//alert(record.NHBM_BSOFT)
		if (YYZL == null || YYZL == 0)
			return;
		var type = record.get("CFLX");
		var pharmId = this.getPharmacyIdByCFLX(type);
		var data = {};
		data.medId = record.get("YPXH");
		if (data.medId == null || data.medId == "") {
			return;
		}
		data.medsource = record.get("YPCD");
		if (data.medsource == null || data.medsource == "") {
			MyMessageTip.msg("警告", record.get("msg"), true);
			return;
		}
		data.quantity = YYZL;
		data.pharmId = pharmId;
		data.ypmc = record.get("YPMC");
		if(record.get("SBXH")!=null&&record.get("SBXH")!=""){
		data.jlxh=record.get("SBXH");
		}
		data.lsjg = record.get("YPDJ");
		// 校验是否有足够药品库存
		phis.script.rmi.jsonRequest({
					serviceId : "clinicManageService",
					serviceAction : "checkInventory",
					body : data
				}, function(code, mmsg, json) {
					if (code >= 300) {
						this.processReturnMsg(code, mmsg);
						return;
					}
					if (json.sign > 0) {
						record.set("msg", "");
						var ypzh = record.get("YPZH_show");
						record.set("YPZH_show", 0);
						record.set("YPZH_show", ypzh);// 刷新提示信息
						return;
					}
					var msg = "药品【" + data.ypmc
					+ "】库存不足!库存数量：" + parseFloat(json.KCZL)
					+ ",实际数量：" + parseFloat(data.quantity);
					MyMessageTip.msg("警告", msg, true);
					record.set("msg", msg);
					var ypzh = record.get("YPZH_show");
					record.set("YPZH_show", 0);
					record.set("YPZH_show", ypzh);// 刷新提示信息
				}, this)
	},
	checkInventoryCz : function(YYZL, record) {
		if (YYZL == null || YYZL == 0)
			return;
		var data = {};
		data.medId = record.get("YPXH");
		if (data.medId == null || data.medId == "") {
			return;
		}
		data.KSDM = this.opener.opener.form.getForm().findField("KSDM").getValue();
		data.YPXH = record.get("YPXH");
		data.LSJG = record.get("YPDJ");
		data.YPSL = YYZL;
		var ret = phis.script.rmi.miniJsonRequestSync({
			serviceId : "clinicManageService",
			serviceAction : "queryMaterialsPrice",
			body : data
		});
		if (ret.code > 300) {
			var msg = "费用【" + record.get("YPMC")
			+ "】的物资," + ret.msg;
			MyMessageTip.msg("警告", msg, true);
			record.set("msg", msg);
			var ypzh = record.get("YPZH_show");
			record.set("YPZH_show", 0);
			record.set("YPZH_show", ypzh);// 刷新提示信息
			return;
		} else {
			record.set("msg", "");
			var ypzh = record.get("YPZH_show");
			record.set("YPZH_show", 0);
			record.set("YPZH_show", ypzh);// 刷新提示信息
		}
	},
	totalCount : function(){
		this.opener.module.list.loadData();
	},
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
				this.store.load();
			} else {
				var pt = this.grid.getBottomToolbar();
				if (this.requestData.pageNo == 1) {
					pt.cursor = 0;
				}
				pt.doLoad(pt.cursor);
			}
		}
		// ** add by yzh **
		this.resetButtons();
	},
	onStoreLoadData : function(store, records, ops) {
		this.fireEvent("loadData", store); // ** 不管是否有记录，都fire出该事件
		if (records.length == 0) {
			this.doXY();
			return
		}
		if (!this.selectedIndex || this.selectedIndex >= records.length) {
		} else {
			this.selectedIndex = 0;
		}
		var store = this.grid.getStore();
		var n = store.getCount();
		var row = store.getAt(n-1);
		this.type = row.data.CFLX;
		this.opener.opener.formModule.setData(row.data);
		this.insert("1",1,1);
		this.reloadYPZH();
		this.totalCount();
	},
	setBackSet1 : function(type,datas,i) {
		if(i==datas.length){
			this.endZT();
			return;
		}
		datas[i].YPDJ_Y = datas[i].YPDJ;
		if(!this.opener.opener.MZXX)return;
		if(datas[i]&&datas[i].PSPB==1){
			Ext.MessageBox.confirm('提示', '【'+datas[i].YPMC+'】使用前需要做皮试处理，是否确认录入？',function(button){
				if(button=='yes'){
					if(this.YPZH){
						if(datas[i].YPZH!=this.YPZH){
							this.setBackSet(type,1,datas,i);
						}else{
							this.setBackSet(type,0,datas,i);
						}
					}else{
						this.YPZH = datas[i].YPZH;
						datas[i].CF_NEW = this.ztCF_NEW;
						this.setBackSet(type,1,datas,i);
					}
				}else{
					i++;
					if(this.YPZH){
						if(datas[i].YPZH!=this.YPZH){
							this.setBackSet1(type,datas,i);
						}else{
							this.setBackSet1(type,datas,i);
						}
					}else{
						this.YPZH = datas[i].YPZH;
						datas[i].CF_NEW = this.ztCF_NEW;
						this.setBackSet1(type,datas,i);
					}
				}
			},this);
		}else{
			if(this.YPZH){
				if(datas[i].YPZH!=this.YPZH){
					this.setBackSet(type,1,datas,i);
				}else{
					this.setBackSet(type,0,datas,i);
				}
			}else{
				this.YPZH = datas[i].YPZH;
				datas[i].CF_NEW = this.ztCF_NEW;
				this.setBackSet(type,1,datas,i);
			}
		}
	},
	setBackSet : function(type,newGroup,datas,i) {
		var data = datas[i];
		if(!this.opener.opener.MZXX)return;
		var selectdRecord = this.getSelectedRecord();
		this.opener.opener.formModule.setCFLX(type);
		this.remoteDicStore.baseParams.type = type;
		this.remoteDicStore.baseParams.pharmacyId = this.getPharmacyIdByCFLX(type);
		var store = this.grid.getStore();		
		var selectRow = 0;
		if (selectdRecord) {
			selectRow = this.store.indexOf(selectdRecord);
			this.removeEmptyRecord();
			if ((selectdRecord.get("YPXH") == null
					|| selectdRecord.get("YPXH") == "" || selectdRecord
					.get("YPXH") == 0)) {
				if(newGroup){
					var n = store.getCount();
					if(n-1>selectRow){
						for (var i = selectRow+1; i < n; i++) {
							var rowItem = store.getAt(i);
							if(rowItem.get("CF_NEW")){
								selectRow = i;
								break;
							}
							if(i==n-1){
								selectRow = n;
							}
						}
					}else if(n-1==selectRow){
						selectRow = n;
					}
				}
			} else {
				if(newGroup){
					var n = store.getCount();
					if(n-1>selectRow){
						for (var i = selectRow+1; i < n; i++) {
							var rowItem = store.getAt(i);
							if(rowItem.get("CF_NEW")){
								selectRow = i;
								break;
							}
							if(i==n-1){
								selectRow = n;
							}
						}
					}else if(n-1==selectRow){
						selectRow = n;
					}
				}else{
					selectRow = this.store.indexOf(selectdRecord) + 1;
				}
			}
		} else {
			this.removeEmptyRecord();
			if (this.store.getCount() > 0) {
				selectRow = this.store.getCount();
			}
		}
		var row = selectRow;
		/******************begin 2019-07-23 zhaojian 收费结算：医保自费分离**************************/
		if(data.msg!=undefined && data.msg.length>0){
			MyMessageTip.msg("提示", data.msg, true);
			return false;
		}
		var n = store.getCount();
		if(n>0){
			for (var j = 0; j < n; j++) {
				var rr = store.getAt(j);
				if (j != row && rr.get("YPZH") == data.YPZH) {
					if (rr.get("YPXH") == data.YPXH) {
						MyMessageTip.msg("提示", "\"" + data.YPMC
										+ "\"在这组中已存在，请进行修改!", true);
						return false;
					}
				}
				//根据病人性质及药品对照情况进行判定提醒
				if((!row || j != row) && this.brxz==2000 && rr.get("YYZBM")!=undefined && data.YYZBM!=undefined &&  rr.get("YYZBM").replace(/\s+/g,"").length != data.YYZBM.replace(/\s+/g,"").length){
						MyMessageTip.msg("提示", "医保病人的单次结算只能开全自费或全医保药品/收费项目！", true);
						return false;
				}
			}
		}
		/******************end 2019-07-23 zhaojian 收费结算：医保自费分离**************************/
		var o = this.getStoreFields(this.schema.items);
		var Record = Ext.data.Record.create(o.fields);
		data._opStatus='create';
		this.opener.opener.MZXX.mxsave = true;
		data.DJLY = 6;
		data.CFLX = type;
		if((type+"")=="3"){
			data.CFTS = this.opener.opener.form.getForm().findField("CFTS").getValue();
		}else{
			data.CFTS = 1;
		}
		data.YSDM = this.opener.opener.form.getForm().findField("YSDM").getValue();
		data.KSDM = this.opener.opener.form.getForm().findField("KSDM").getValue();
		var r = new Record(data);
		if(type>0){
			this.checkInventory((parseFloat(r.get("YPSL"))*parseFloat(r.get("CFTS"))).toFixed(2), r);
		}else{
			this.checkInventoryCz((parseFloat(r.get("YPSL"))*parseFloat(r.get("CFTS"))).toFixed(2), r);
		}		
		this.selectedIndex = row;
		store.insert(row, [r]);
		this.grid.getView().refresh();// 刷新行号
		var storeData = store.data;
		var maxIndex = store.getCount();
		if (maxIndex == 1) {// 处方的第一条记录或者自动新组
			var rowItem = storeData.itemAt(maxIndex - 1);
			rowItem.set("YPZH", 1);
			
		} else {
			var upRowItem = storeData.itemAt(row - 1);
			var rowItem = storeData.itemAt(row);
			if(newGroup=="1"){
				rowItem.set("YPZH", upRowItem.get("YPZH")+1);
			}else{
				rowItem.set("YPZH", upRowItem.get("YPZH"));
			}
			if(!newGroup){
				rowItem.set("YPYF", upRowItem.get("YPYF"));
				rowItem.set("YPYF_text", upRowItem.get("YPYF_text"));
			}
		}
		if(row==0){
			if(!this.opener.opener.form.getForm().findField("YSDM").getValue()){
				this.opener.opener.form.getForm().findField("YSDM").focus();
			}else if(!this.opener.opener.form.getForm().findField("KSDM").getValue()){
				this.opener.opener.form.getForm().findField("KSDM").focus();
			}
		}
		this.reloadYPZH();
		i++;
		this.setBackSet1(type,datas,i);
	},
	endZT : function(){
		this.totalCount();
		this.doNewGroup();
	},
	setBackInfo : function(obj, record) {
		//如果是非原液药品就停止操作
		if(record.data.ISZT){
			var cell = this.grid.getSelectionModel().getSelectedCell();
			var row = cell[0];
			var count = this.store.getCount();
			if(row+1!=count){
				obj.collapse();
				obj.triggerBlur();
				obj.hasFocus = false;// add by yangl 2013.02.18
				MyMessageTip.msg("提示", "请在最后一行增加组套!", true);
				return;
			}
			if(record.data.TYPE!=0){
				this.serviceAction = "loadMedcineSet";
			}else{
				this.serviceAction = "loadProjectSet";
			}
			record.data.pharmacyId = this.remoteDicStore.baseParams.pharmacyId;
			record.data.BRXZ = this.opener.opener.MZXX.BRXZ;
			var ret = phis.script.rmi.miniJsonRequestSync({
				serviceId : "clinicManageService",
				serviceAction : this.serviceAction,
				body : record.data
			});
			if (ret.code > 300) {
				MyMessageTip.msg("提示", ret.msg, true);
				return;
			} else {
				if (ret.json.body) {
					var r = this.getSelectedRecord();
					this.ztCF_NEW = false;
					if(r.data.CF_NEW){
						this.ztCF_NEW = true;
					}
					this.YPZH = "";
					this.setBackSet1(record.data.TYPE,ret.json.body,0);
				}
			}
			obj.collapse();
			obj.triggerBlur();
			obj.hasFocus = false;// add by yangl 2013.02.18
			return;
		}
		var me=this;
		if(record.data&&record.data.PSPB==1)
		{
			obj.collapse();
			obj.triggerBlur();
			Ext.MessageBox.confirm('提示', '该药品使用前需要做皮试处理，是否确认录入？',function(button){
				if(button=='yes')
				{
					me.opener.opener.MZXX.mxsave = true;
					record.data.DJLY = 6;
					me.setData(obj, record);
					obj.hasFocus = false;// add by yangl 2013.02.18
				}
			});
		}else
		{
			me.opener.opener.MZXX.mxsave = true;
			record.data.DJLY = 6;
			me.setData(obj, record);
			obj.hasFocus = false;// add by yangl 2013.02.18
		}
	},
	setData : function(obj, record) {
		debugger;
		Ext.EventObject.stopEvent();// 停止事件
		var MZXX = this.opener.opener.MZXX;
		var GHXH = MZXX.GHGL;
		var TYPE = record.data.TYPE;
		var valid = true;
		if(TYPE == 1 || TYPE == 2 || TYPE == 3){
			//查询是否有处方
			var res = phis.script.rmi.miniJsonRequestSync({
				serviceId : "clinicManageService",
				serviceAction : "checkCF",
				body : {"GHXH":GHXH}
			});
			if(res.code < 300){
				valid = res.json.body;
			}
		}
		
		if(valid){
			var cell = this.grid.getSelectionModel().getSelectedCell();
			var row = cell[0];
			var griddata = this.grid.store.data;
			var rowItem = griddata.itemAt(row);
			record.data.YPZH = rowItem.get("YPZH");
//			if(rowItem.data.CFLX>0){
			record.data.YFSB = this.remoteDicStore.baseParams.pharmacyId
//			}
			if (!this.setMedRecordIntoList(record.data, rowItem, row)) {
				obj.collapse();
				obj.triggerBlur();
				return;
			}
			obj.setValue(record.get("YPMC"));
			// 获取药品自负比例信息
			this.getPayProportion(record.data, rowItem);
			if (!record.data.LSJG) {
				this.grid.startEditing(row, 6);
			}else{
				this.grid.startEditing(row, 7);
			}
		}else{
			this.doRemove();			
			Ext.Msg.alert("提示","请到'门诊诊疗'->'病人列表'->'待诊',双击该病人记录,弹框后点击右上角'处方'按钮,录入相应的药物,之后点击'保存'按钮");
		}
		obj.collapse();
		obj.triggerBlur();
	},
	//重要 选择的数据放到list中
	setMedRecordIntoList : function(data, rowItem, curRow) {
		// 将选中的记录设置到行数据中
		var store = this.grid.getStore();
		var n = store.getCount();
		for (var i = 0; i < n; i++) {
			var r = store.getAt(i);
			if (i != curRow
					&& r.get("YPZH") == rowItem.get("YPZH")) {
				if (r.get("YPXH") == data.YPXH) {
					MyMessageTip.msg("提示", "\"" + data.YPMC
									+ "\"在这组中已存在，请进行修改!", true);
					return false;
				}
			}
			//根据病人性质及药品对照情况进行判定提醒
			if((!curRow || i != curRow) && this.brxz==2000 && r.get("YYZBM")!=undefined && data.YYZBM!=undefined && r.get("YYZBM").replace(/\s+/g,"").length != data.YYZBM.replace(/\s+/g,"").length){
					MyMessageTip.msg("提示", "医保病人的单次结算只能开全自费或全医保药品/收费项目！", true);
					return false;
			}
		}
		var lastrow
		if(curRow>=1){
			lastrow=store.getAt(curRow-1);
			//alert(lastrow.get("YPMC")+lastrow.get("CFLX"));
		}
//		data.YPDJ = data.LSJG;
		if(data.TYPE==0){
			if (data.FYKS) {
				rowItem.set('ZXKS', data.FYKS);
			}
			data.YPSL = 1;
			data.KSDM = this.opener.opener.form.getForm().findField("KSDM").getValue();
			var ret = phis.script.rmi.miniJsonRequestSync({
				serviceId : "clinicManageService",
				serviceAction : "queryMaterialsPrice",
				body : data
			});
			if (ret.code > 300) {
				MyMessageTip.msg("提示", ret.msg, true);
	//			this.processReturnMsg(ret.code, ret.msg);
				return;
			} else {
				if (ret.json.body) {
					data.LSJG = ret.json.body;
				}
			}
		}
		rowItem.set('YYZBM', data.YYZBM);
		rowItem.set('GYTJ', data.GYFF);
		rowItem.set('YPMC', data.YPMC);
		rowItem.set('YFGG', data.YFGG);
		rowItem.set('JLDW', data.JLDW);
		rowItem.set('YFDW', data.YFDW);
		rowItem.set('YPXH', data.YPXH);
		rowItem.set('YPJL', data.YPJL);
		rowItem.set('YCJL', data.YPJL);
		rowItem.set('YFBZ', data.YFBZ);
		rowItem.set('YPDJ', data.LSJG);
		rowItem.set('YPDJ_Y', data.LSJG);
		rowItem.set('TYPE', data.TYPE);
		rowItem.set('CFLX', data.TYPE);
		if(data.TYPE=="0"){
			rowItem.set('CFLX', data.TYPE);
			if((lastrow && (lastrow.get("CFLX")!=data.TYPE || lastrow.get("DJLY")!=6))||curRow==0){
				rowItem.set('CF_NEW', "检");
			}else{
				rowItem.set('CF_NEW', "");
			}
			rowItem.set('YPYF', "");
			rowItem.set('YPYF_text', "");
		}else{
			rowItem.set('CFLX', data.TYPE);
			if((lastrow && (lastrow.get("CFLX")!=data.TYPE || lastrow.get("DJLY")!=6 ))||curRow==0){
				rowItem.set('CF_NEW', this.getCFLX(data.TYPE));
			}else{
				rowItem.set('CF_NEW', "");
			}
		}
		rowItem.set('YPCD', data.YPCD);
		rowItem.set('YFSB', data.YFSB);
		rowItem.set('YPSL', 1);
		rowItem.set('HJJE', parseFloat(data.LSJG*rowItem.get("CFTS")).toFixed(2));
		rowItem.set('ZFPB', data.ZFPB);// 2013-09-10 add by gejj 添加自负判别
		// 添加输液判别
		if (data.YPCD_text) {
			rowItem.set('YPCD_text', data.YPCD_text);
		} else {
			rowItem.set('YPCD_text', data.CDMC);
		}
//		if(data.TYPE=='0'){
		rowItem.set('FYGB', data.FYGB);
		rowItem.set('GBMC', data.GBMC);
		rowItem.set('NHBM_BSOFT', data.NHBM_BSOFT);
//		}else{
//			rowItem.set('FYGB', this.getFygb(data.TYPE));
//		}
//		var zxks = this.grid.getColumnModel().getColumnById("ZXKS").editor;
		this.totalCount();
		return true;
	},
//	getFygb : function(type) {
//		switch (type) {
//			case 1 :
//				return this.exContext.systemParams.XYF;
//			case 2 :
//				return this.exContext.systemParams.ZYF;
//			case 3 :
//				return this.exContext.systemParams.CYF;
//		}
//	},
	getPayProportion : function(data, rowItem) {
		var body = {};
		body.BRXZ = this.opener.opener.MZXX.BRXZ;
		body.TYPE = data.TYPE;
		if(!body.TYPE){
			body.TYPE=0;
		}
		body.FYGB = rowItem.get("FYGB");
		body.FYXH = data.YPXH;
		phis.script.rmi.jsonRequest({
					serviceId : "clinicManageService",
					serviceAction : "getPayProportion",
					body : body
				}, function(code, msg, json) {
					if (code >= 300) {
						this.processReturnMsg(code, msg);
						return;
					}
					var zfbl = json.body.ZFBL;
					rowItem.set('ZFBL', zfbl);
				}, this);

	},
	showColor : function(v, params, data) {
		var YPZH = data.get("YPZH_show") % 2 + 1;
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
//			if (msg.error_kc) { 
				return "<img src='" + ClassLoader.appRootOffsetPath
							+ "resources/phis/resources/images/i_error.gif' title='错误："
						+ msg.error_kc + "!'/>";
//			}
		}
	},
	doInsert : function(){
		var row = this.getSelectedRecord();
		if(!row){
			var n = this.store.getCount();
			if(n>0){
				row = this.store.getAt(n-1);
			}
		}
		if(row.data.YPXH){
			if((row.data.DJLY+"")!=6){
				return;
			}else{
				this.insert(row.get("CFLX"),0,0);
				return;
			}
		}
		var n = this.store.indexOf(row);
		if(n>0){
			for (var i = n-1; i >= 0; i--) {
				var r = this.store.getAt(i);
				if(r.data.YPXH){
					if((r.data.DJLY+"")!=6){
						return;
					}else{
						this.insert(this.type,0,0);
						return;
					}
				}
			}
		}
	},
	doNewGroup : function(){
		var row = this.getSelectedRecord();
		if(!row){
			var n = this.store.getCount();
			if(n>0){
				row = this.store.getAt(n-1);
			}
		}
		if(row.data.YPXH){
			if((row.data.DJLY+"")!=6){
				return;
			}else{
				this.insert(row.get("CFLX"),1,0);
				return;
			}
		}
		var n = this.store.indexOf(row);
		if(n>0){
			for (var i = n-1; i >= 0; i--) {
				var r = this.store.getAt(i);
				if(r.data.YPXH){
					if((r.data.DJLY+"")!=6){
						return;
					}else{
						this.insert(this.type,1,0);
						return;
					}
				}
			}
		}
	},
	onRowClick : function(){
		var r = this.getSelectedRecord();
//		this.newYPZH = r.data.YPZH_show;
//		if(this.oldYPZH&&this.oldYPZH==this.newYPZH){
//			this.type = r.data.CFLX;
//		}else{
//			this.oldYPZH = r.data.YPZH_show;
			this.type = r.data.CFLX;
			this.opener.opener.formModule.setData(r.data);
//		}
	},
	setCFTS : function(cfts){
		var r = this.getSelectedRecord();
		if(!r){
			var n = this.store.getCount();
			if(n==0)return;
			var rowItem = this.store.getAt(n-1);
			if(rowItem.get("CFLX")==3){
				r = rowItem;
			}else{
				return;
			}
		}else{
			if(r.get("CFLX")!=3){
				return
			}
		}
		r.set("CFTS",cfts);
		//为什么注释了？
		if(r.get("YPDJ")&&r.get("YPSL")){
			r.set("HJJE",parseFloat(parseFloat(r.get("YPSL"))*parseFloat(r.get("YPDJ"))*parseFloat(r.get("CFTS"))).toFixed(2));
			this.checkInventory((parseFloat(r.get("YPSL"))*parseFloat(r.get("CFTS"))).toFixed(2), r);
		}
		var store = this.grid.getStore();
		var n = store.getCount();
		if(r.data.CF_NEW){
			selectRow = this.store.indexOf(r);
			for(var i = selectRow+1 ; i < n ; i ++){
				var rowItem = store.getAt(i);
				if(rowItem.data.CF_NEW){
					break;
				}else{
					rowItem.set("CFTS",cfts);
					if(rowItem.get("YPDJ")&&rowItem.get("YPSL")){
						rowItem.set("HJJE",parseFloat(parseFloat(rowItem.get("YPSL"))*parseFloat(rowItem.get("YPDJ"))*parseFloat(rowItem.get("CFTS"))).toFixed(2));
						this.checkInventory((parseFloat(rowItem.get("YPSL"))*parseFloat(rowItem.get("CFTS"))).toFixed(2), rowItem);
					}
				}
			}
		}else{
			selectRow = this.store.indexOf(r);
			for(var i = selectRow ; i >= 0 ; i --){
				var rowItem = store.getAt(i);
				if(rowItem.data.CF_NEW){
					rowItem.set("CFTS",cfts);
					if(rowItem.get("YPDJ")&&rowItem.get("YPSL")){
						rowItem.set("HJJE",parseFloat(parseFloat(rowItem.get("YPSL"))*parseFloat(rowItem.get("YPDJ"))*parseFloat(rowItem.get("CFTS"))).toFixed(2));
						this.checkInventory((parseFloat(rowItem.get("YPSL"))*parseFloat(rowItem.get("CFTS"))).toFixed(2), rowItem);
					}
					break;
				}else{
					rowItem.set("CFTS",cfts);
					if(rowItem.get("YPDJ")&&rowItem.get("YPSL")){
						rowItem.set("HJJE",parseFloat(parseFloat(rowItem.get("YPSL"))*parseFloat(rowItem.get("YPDJ"))*parseFloat(rowItem.get("CFTS"))).toFixed(2));
						this.checkInventory((parseFloat(rowItem.get("YPSL"))*parseFloat(rowItem.get("CFTS"))).toFixed(2), rowItem);
					}
				}
			}
			for(var i = selectRow+1 ; i < n ; i ++){
				var rowItem = store.getAt(i);
				if(rowItem.data.CF_NEW){
					break;
				}else{
					rowItem.set("CFTS",cfts);
					if(rowItem.get("YPDJ")&&rowItem.get("YPSL")){
						rowItem.set("HJJE",parseFloat(parseFloat(rowItem.get("YPSL"))*parseFloat(rowItem.get("YPDJ"))*parseFloat(rowItem.get("CFTS"))).toFixed(2));
						this.checkInventory((parseFloat(rowItem.get("YPSL"))*parseFloat(rowItem.get("CFTS"))).toFixed(2), rowItem);
					}
				}
			}
		}
		this.opener.module.list.loadData();
	},
	setYSDM : function(ysdm){
		var r = this.getSelectedRecord();
		if(!r){
			var n = this.store.getCount();
			if(n==0)return;
			var rowItem = this.store.getAt(n-1);
			r = rowItem;
		}
		r.set("YSDM",ysdm)
		var store = this.grid.getStore();
		var n = store.getCount();
		if(r.data.CF_NEW){
			selectRow = this.store.indexOf(r);
			for(var i = selectRow+1 ; i < n ; i ++){
				var rowItem = store.getAt(i);
				if(rowItem.data.CF_NEW){
					break;
				}else{
					rowItem.set("YSDM",ysdm);
				}
			}
		}else{
			selectRow = this.store.indexOf(r);
			for(var i = selectRow ; i >= 0 ; i --){
				var rowItem = store.getAt(i);
				if(rowItem.data.CF_NEW){
					rowItem.set("YSDM",ysdm);
					break;
				}else{
					rowItem.set("YSDM",ysdm);
				}
			}
			for(var i = selectRow+1 ; i < n ; i ++){
				var rowItem = store.getAt(i);
				if(rowItem.data.CF_NEW){
					break;
				}else{
					rowItem.set("YSDM",ysdm);
				}
			}
		}
	},
	setKSDM : function(ksdm){
		var r = this.getSelectedRecord();
		if(!r){
			var n = this.store.getCount();
			if(n==0)return;
			var rowItem = this.store.getAt(n-1);
			r = rowItem;
		}
		r.set("KSDM",ksdm)
		var store = this.grid.getStore();
		var n = store.getCount();
		if(r.data.CF_NEW){
			selectRow = this.store.indexOf(r);
			for(var i = selectRow+1 ; i < n ; i ++){
				var rowItem = store.getAt(i);
				if(rowItem.data.CF_NEW){
					break;
				}else{
					rowItem.set("KSDM",ksdm);
				}
			}
		}else{
			selectRow = this.store.indexOf(r);
			for(var i = selectRow ; i >= 0 ; i --){
				var rowItem = store.getAt(i);
				if(rowItem.data.CF_NEW){
					rowItem.set("KSDM",ksdm);
					break;
				}else{
					rowItem.set("KSDM",ksdm);
				}
			}
			for(var i = selectRow+1 ; i < n ; i ++){
				var rowItem = store.getAt(i);
				if(rowItem.data.CF_NEW){
					break;
				}else{
					rowItem.set("KSDM",ksdm);
				}
			}
		}
	},
	insert : function(type,newGroup,newCF) {
		if(!this.opener.opener.MZXX)return;
		var selectdRecord = this.getSelectedRecord();
//		var selectRow = 0;
		if (selectdRecord) {
			if((selectdRecord.get("DJLY")+"")!="6"){
				if(!newCF)return;
			}
		}
		this.opener.opener.formModule.setCFLX(type);
		this.remoteDicStore.baseParams.type = type;
		this.remoteDicStore.baseParams.pharmacyId = this.getPharmacyIdByCFLX(type);
		var store = this.grid.getStore();
//		var selectdRecord = this.getSelectedRecord();
		var selectRow = 0;
		if (selectdRecord) {
			selectRow = this.store.indexOf(selectdRecord);
			this.removeEmptyRecord();
			if ((selectdRecord.get("YPXH") == null
					|| selectdRecord.get("YPXH") == "" || selectdRecord
					.get("YPXH") == 0)) {
				if(newGroup){
					var n = store.getCount();
					if(n-1>selectRow){
						for (var i = selectRow+1; i < n; i++) {
							var rowItem = store.getAt(i);
							if(rowItem.get("CF_NEW")){
								selectRow = i;
								break;
							}
							if(i==n-1){
								selectRow = n;
							}
						}
					}else if(n-1==selectRow){
						selectRow = n;
					}
				}
			} else {
				if(newGroup){
					var n = store.getCount();
					if(n-1>selectRow){
						for (var i = selectRow+1; i < n; i++) {
							var rowItem = store.getAt(i);
							if(rowItem.get("CF_NEW")){
								selectRow = i;
								break;
							}
							if(i==n-1){
								selectRow = n;
							}
						}
					}else if(n-1==selectRow){
						selectRow = n;
					}
				}else{
					selectRow = this.store.indexOf(selectdRecord) + 1;
				}
			}
		} else {
			this.removeEmptyRecord();
			if (this.store.getCount() > 0) {
				selectRow = this.store.getCount();
			}
		}
		var row = selectRow;
		var o = this.getStoreFields(this.schema.items);
		var Record = Ext.data.Record.create(o.fields);
		var items = this.schema.items;
		var factory = util.dictionary.DictionaryLoader;
		var data = {
			'_opStatus' : 'create'
		};
		for (var i = 0; i < items.length; i++) {
			var it = items[i];
			var v = null;
			if (it.defaultValue) {
				v = it.defaultValue;
				data[it.id] = v;
				var dic = it.dic;
				if (dic) {
					data[it.id] = v.key;
					var o = factory.load(dic);
					if (o) {
						var di = o.wraper[v.key];
						if (di) {
							data[it.id + "_text"] = di.text;
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
		data.YPMC="";
		data.DJLY = 6;
		data.CFLX = type;
		if((type+"")=="3"){
			data.CFTS = this.opener.opener.form.getForm().findField("CFTS").getValue();
		}else{
			data.CFTS = 1;
		}
		data.YSDM = this.opener.opener.form.getForm().findField("YSDM").getValue();
		data.KSDM = this.opener.opener.form.getForm().findField("KSDM").getValue();
		if(newCF){
			data.CF_NEW = this.getCFLX(type);
			var n = store.getCount();
			row = n;
		}
		var r = new Record(data);
		this.selectedIndex = row;
		store.insert(row, [r]);
		this.grid.getView().refresh();// 刷新行号
		var storeData = store.data;
		var maxIndex = store.getCount();
		if (maxIndex == 1) {// 处方的第一条记录或者自动新组
			var rowItem = storeData.itemAt(maxIndex - 1);
			rowItem.set("YPZH", 1);
			
		} else {
			var upRowItem = storeData.itemAt(row - 1);
			var rowItem = storeData.itemAt(row);
			if(newGroup=="1"){
				rowItem.set("YPZH", upRowItem.get("YPZH")+1);
			}else{
				rowItem.set("YPZH", upRowItem.get("YPZH"));
			}
			if(!newGroup&&!newCF){
				rowItem.set("YPYF", upRowItem.get("YPYF"));
				rowItem.set("YPYF_text", upRowItem.get("YPYF_text"));
			}
		}
		if(row==0){
			if(!this.opener.opener.form.getForm().findField("YSDM").getValue()){
				this.opener.opener.form.getForm().findField("YSDM").focus();
			}else if(!this.opener.opener.form.getForm().findField("KSDM").getValue()){
				this.opener.opener.form.getForm().findField("KSDM").focus();
			}else if((type+"")=="3"&&newCF){
				this.opener.opener.form.getForm().findField("CFTS").focus(true,200);
			}else{
				this.grid.startEditing(row, 3);
			}
		}else{
			if((type+"")=="3"&&newCF){
				this.opener.opener.form.getForm().findField("CFTS").focus(true,200);
			}else{
				this.grid.startEditing(row, 3);
			}
		}
		this.reloadYPZH();
	},
	removeEmptyRecord : function() {
		var store = this.grid.getStore();
		for (var i = 0; i < store.getCount(); i++) {
			var r = store.getAt(i);
			if ((r.get("YPXH") == null || r.get("YPXH") == "" || (r
					.get("YPXH") == 0))) {
				store.remove(r);
			}
		}
	},
	getRemoteDicReader : function() {
		return new Ext.data.JsonReader({
					root : 'mds',
					totalProperty : 'count',
					id : 'mdssearch'
				}, [{
							name : 'numKey'
						}, {
							name : 'YPXH'
						}, {
							name : 'YPMC'
						}, {
							name : 'YFGG'
						}, {
							name : 'YPDW'
						}, {
							name : 'YPSL'
						}, {
							name : 'JLDW'
						}, {
							name : 'YPJL'
						}, {
							name : 'PSPB'
						},// 判断是否皮试药品
						{
							name : 'YFBZ'
						}, {
							name : 'GYFF'
						},// 药品用法
						{
							name : 'LSJG'
						}, {
							name : 'YPCD'
						}, {
							name : 'CDMC'
						}, {
							name : 'TYPE'
						}, {
							name : 'TSYP'
						}, {
							name : 'YFDW'
						}, {
							name : 'YBFL'
						}, {
							name : 'YBFL_text'
						}, {
							name : 'GYFF_text'
						}, {
							name : 'JYLX'
						}, {
							name : 'KCSL'
						}, {
							name : 'FYGB'
						}, {
							name : 'GBMC'
						}, {
							name : 'FYKS'
						}, {
							name : 'ISZT'
						}, {
							name : 'NHBM_BSOFT'
						}, {
							name : 'YYZBM'
						}]);
	},
	YYZBMRenderer : function(value, metaData, r, row, col) {
		if(value==undefined){
			return "";
		}
		return "<font style='color:red;font-weight:bold'>"+value+"</font>";
	},
	reloadYPZH : function() {
		ypzh = 0;
		var store = this.grid.getStore();
		var n = store.getCount();
		for (var i = 0; i < n; i++) {
			var r = store.getAt(i);
			if (i == 0) {
				ypzh = 1;
				r.set('YPZH_show', ypzh);
				r.set('CF_NEW', this.getCFLX(r.get("CFLX")));
			} else {
				var r1 = store.getAt(i - 1);
				if(r.get("CFSB")&&r1.get("CFSB")){
					if (r1.get('CFSB') == r.get('CFSB')) {
						if (r1.get('YPZH') == r.get('YPZH')) {
							r.set('YPZH_show', ypzh);
							r.set('CF_NEW', "");
						} else {
							r.set('YPZH_show', ++ypzh);
							r.set('CF_NEW', "");
						}
					} else {
						r.set('YPZH_show', ++ypzh);
						r.set('CF_NEW', this.getCFLX(r.get("CFLX")));
					}
				}else{
					if(r.get("CF_NEW")){
						r.set('YPZH_show', ++ypzh);
						r.set('CF_NEW', this.getCFLX(r.get("CFLX")));
					}else{
						if (r1.get('YPZH') == r.get('YPZH')) {
							r.set('YPZH_show', ypzh);
							r.set('CF_NEW', "");
						} else {
							r.set('YPZH_show', ++ypzh);
							r.set('CF_NEW', "");
						}
					}
				}
			}
		}
	},
	getCFLX : function(type){
		type = parseInt(type);
		var CFLXSTR = null;
		switch (type) {
			case 1 :
				CFLXSTR = "西";
				break;
			case 2 :
				CFLXSTR = "成";
				break;
			case 3 :
				CFLXSTR = "草";
				break;
			default :
				CFLXSTR = "检";
		}
		return CFLXSTR;
	},
	doRemove : function(item,e,row) {
		
		var cm = this.grid.getSelectionModel();
		var cell = cm.getSelectedCell();
		var r = this.getSelectedRecord();
		if(row){
			r = row;
		}
		if (r == null) {
			return
		}
		if(r.data.DJLY!=6){
			MyMessageTip.msg("提示", "当前信息不可编辑!", true);
			return
		}
		this.opener.opener.MZXX.mxsave = true;
		if(r.data.CF_NEW){
			var n = this.store.getCount();
			var selectRow = this.store.indexOf(r);
			if(selectRow < n-1){
				var nextrow = this.store.getAt(selectRow+1);
				nextrow.set('CF_NEW',this.getCFLX(nextrow.get('CFLX')));
			}
		}
		if(r.data.SBXH){
			this.store.remove(r);
			if(this.opener.opener.MZXX.removeData){
				this.opener.opener.MZXX.removeData.push(r.data);
				if(parseInt(r.data.CFLX)>0){
					this.opener.opener.MZXX.removeCFSB.push(r.data.CFSB);
				}
			}else{
				this.opener.opener.MZXX.removeData = [];
				this.opener.opener.MZXX.removeCFSB = [];
				this.opener.opener.MZXX.removeData.push(r.data);
				if(parseInt(r.data.CFLX)>0){
					this.opener.opener.MZXX.removeCFSB.push(r.data.CFSB);
				}
			}
			this.opener.opener.doRemove(r.data.CFSB);
			// 移除之后焦点定位
			var count = this.store.getCount();
			if (count > 0) {
				this.reloadYPZH();
				cm.select(cell[0] < count ? cell[0] : (count - 1),
						cell[1]);
			} else {
				this.insert(this.type,1,1);
			}
		}else{
			this.store.remove(r);
			this.insert(this.type,1,1);
		}
		this.totalCount();
	},
	doDelGroup : function(){
		
		var r = this.getSelectedRecord();
		if (r == null) {
			return
		}
		if(r.data.DJLY!=6){
			MyMessageTip.msg("提示", "当前信息不可编辑!", true);
			return
		}
		this.opener.opener.MZXX.mxsave = true;
		Ext.Msg.confirm("确认", "确认删除【" + r.get("YPMC")
										+ "】所在的组的数据吗？",function(btn) {
			if (btn == 'yes') {
				var r = this.getSelectedRecord();
				var store = this.grid.getStore();
				var n = store.getCount();
				if(!this.opener.opener.MZXX.removeData){
					this.opener.opener.MZXX.removeData = [];
					this.opener.opener.MZXX.removeCFSB = [];
				}
				if(r.data.CF_NEW){
					selectRow = this.store.indexOf(r);
					for(var i = selectRow+1 ; i < n ; i ++){
						var rowItem = store.getAt(i);
						if(rowItem&&rowItem.data.YPZH_show==r.data.YPZH_show){
							if(rowItem.data.SBXH){
								this.opener.opener.MZXX.removeData.push(rowItem.data);
								if(parseInt(rowItem.data.CFLX)>0){
									this.opener.opener.MZXX.removeCFSB.push(rowItem.data.CFSB);
								}
								this.store.remove(rowItem);
								this.opener.opener.doRemove(rowItem.data.CFSB);
								i--;
								n--;
							}else{
								this.store.remove(rowItem);
								i--;
								n--;
							}
						}else if(rowItem){
							if(!rowItem.data.CF_NEW){
								rowItem.set('CF_NEW',this.getCFLX(r.get('CFLX')));
							}
							break;
						}
					}
				}else{
					selectRow = this.store.indexOf(r);
					for(var i = selectRow-1 ; i >= 0 ; i --){
						var rowItem = store.getAt(i);
						if(rowItem&&rowItem.data.YPZH_show==r.data.YPZH_show){
							if(rowItem.get('CF_NEW')){
								this.CF_NEW = true;
							}
							if(rowItem.data.SBXH){
								this.opener.opener.MZXX.removeData.push(rowItem.data);
								if(parseInt(rowItem.data.CFLX)>0){
									this.opener.opener.MZXX.removeCFSB.push(rowItem.data.CFSB);
								}
								this.store.remove(rowItem);
								this.opener.opener.doRemove(rowItem.data.CFSB);
							}else{
								this.store.remove(rowItem);
							}
						}else{
							break;
						}
					}
					selectRow = this.store.indexOf(r);
					for(var i = selectRow+1 ; i < n ; i ++){
						var rowItem = store.getAt(i);
						if(rowItem&&rowItem.data.YPZH_show==r.data.YPZH_show){
							if(rowItem.data.SBXH){
								this.opener.opener.MZXX.removeData.push(rowItem.data);
								if(parseInt(rowItem.data.CFLX)>0){
									this.opener.opener.MZXX.removeCFSB.push(rowItem.data.CFSB);
								}
								this.store.remove(rowItem);
								i--;
								n--;
								this.opener.opener.doRemove(rowItem.data.CFSB);
							}else{
								this.store.remove(rowItem);
								i--;
								n--;
							}
						}else if(rowItem){
							if(!rowItem.data.CF_NEW&&this.CF_NEW){
								this.CF_NEW = false;
								rowItem.set('CF_NEW',this.getCFLX(r.get('CFLX')));
							}
							break;
						}
					}
				}
				this.store.remove(r);
				if(r.data.SBXH){
					this.opener.opener.MZXX.removeData.push(r.data);
					if(parseInt(r.data.CFLX)>0){
						this.opener.opener.MZXX.removeCFSB.push(r.data.CFSB);
					}
					this.opener.opener.doRemove(r.data.CFSB);
				}
				this.insert(this.type,1,1);
			}
		}, this);
		this.totalCount();
	}
});
