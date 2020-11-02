$package("phis.application.war.script");

$import("phis.script.EditorList", "phis.script.rmi.miniJsonRequestSync",
		"phis.script.util.DateUtil","phis.script.widgets.DatetimeField");

phis.application.war.script.NurseRecordDataView = function(cfg) {
	// 添加日历控件
	cfg.disablePagingTbr = true;
	this.dateField1 = new phis.script.widgets.DateTimeField({
				id : 'EXT_RYRQ',
				xtype : 'datetimefield',
				width : 145,
				value : Date.getServerDateTime(),
				allowBlank : false,
				altFormats : 'Y-m-d H:i:s',
				format : 'Y-m-d H:i:s'
			});

	cfg.tbar = ['时间:', this.dateField1];
	// cfg.pageSize = 25;
	this.serverParams = {
		serviceAction : cfg.serviceAction
	};
	// cfg.autoLoadData = true;{'ZYH':'123', 'BRBQDM':'312'}
	phis.application.war.script.NurseRecordDataView.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(phis.application.war.script.NurseRecordDataView, phis.script.EditorList, {
			initPanel : function(sc) {
				this.zyh = sc.zyh;
				this.brbqdm = sc.brbqdm;
				this.param = {
					'ZYH' : this.zyh,
					'BRBQDM' : this.brbqdm
				};
				var grid = phis.application.war.script.NurseRecordDataView.superclass.initPanel
						.call(this);
				//添加回车事件按钮，以达到导航效果
				grid.onEditorKey = function(field, e) {
					var k = e.getKey();
					if (k == e.ENTER) {//判断是否为Enter键
						var sm = this.getSelectionModel();
						g = sm.grid;
						var cell = sm.getSelectedCell();//获取选择列cell[x,y] x:行 y:列
						var count = this.store.data.items.length;
						if (cell[0] + 1 >= count ) {//到达最后一行时直接返回
							return;
						}
						//设置焦点行
						g.startEditing(cell[0] + 1, cell[1]);
					}
					
				}
				return grid;
			},
			doAction : function(item, e) {
				/*Ext.Msg.show({
					   title:'确认保存护理记录?',
					   msg: '是否保存护理记录?',
					   buttons: Ext.Msg.YESNO,
					   fn: function(btnId,text,opt){
						   if(btnId=="yes"){//选择是
							   this.commit();
						   }else{
							   return;
						   }
					   },
					   scope: this,
					   icon: Ext.MessageBox.QUESTION
					});*/
				this.commit();
			},
			/**
			 * 保存按钮
			 */
			commit : function() {
				var json = [];
				var allData = this.store.data.items;
				var flag = true;
				// 将数据临时保存到数据集中
				for (var i = 0; i < allData.length; i++) {
					if(allData[i].data.XMQZ){
						json.push(allData[i].data);
						flag = false;
					}
				}
				if(flag){
					MyMessageTip.msg("提示", '请录入护理记录!', true);
					return;
				}
				this.param.TIME = Ext.getCmp('EXT_RYRQ').getValue();
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.listServiceId,
							serviceAction : this.saveAction,
							body : json,
							PARAM : this.param
						});
				if (result.code == 200) {
					MyMessageTip.msg("提示", '保存成功!', true);
					// Ext.Msg.alert('状态:', "保存成功!");
					this.fireEvent("refreshTree", this);
					//新增完后清空提交表单
					this.addJl02();
					//保存完成后提交改变
					this.grid.getStore().commitChanges();
				} else {
					this.processReturnMsg(code, msg);
					return;
					// if (result.msg && result.msg != "") {
					// Ext.Msg.alert('状态:', result.msg);
					// } else {
					// Ext.Msg.alert('状态:', "执行失败!");
					// }
				}
			},
			/**
			 * 选择左边树列表中的叶子节点后显示在右边窗口中的护理记录录入
			 * 
			 * @param node
			 */
			doNodeClick : function(node) {
				var jlbh = '';
				var jlsj = '';
				if (node.attributes.children) {
					jlbh = node.attributes.children[0].attributes['JLBH'];
					jlsj = node.attributes.children[0].attributes['JLSJ'];
				} else {
					jlbh = node.attributes.attributes['JLBH'];
					jlsj = node.attributes.attributes['JLSJ'];
				}
				Ext.getCmp('EXT_RYRQ').setValue(jlsj);
				// 禁用日期控件
				Ext.getCmp('EXT_RYRQ').disable();
				this.param = {
					'ZYH' : this.zyh,
					'BRBQDM' : this.brbqdm,
					'JLBH' : jlbh
				};
				// alert(jlbh);
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.listServiceId,
							serviceAction : this.queryJL02ByJLBH,
							body : {
								'JLBH' : jlbh
							}
						});
				if (result.code == 200) {
					var json = result.json.body;
					this.jsonData2Store(json);
				} else {
					if(result.msg == "NotLogon"){
	    		         this.mainApp.logon();
					}else if (result.msg && result.msg != "") {
						MyMessageTip.msg("提示", result.msg, true);
						// Ext.Msg.alert('状态:', result.msg);
					} else {
						this.processReturnMsg(code, msg);
						return;
						// Ext.Msg.alert('状态:', "执行失败!");
					}
				}
			},
			/**
			 * 将服务端返回的json数据插入到store中
			 * 
			 * @param json
			 */
			jsonData2Store : function(json) {
				var allData = this.store.data.items;
				var xmbh = '';
				var tmpXmbh = '';
				var flag = true;
				// 将数据临时保存到数据集中
				for (var i = 0; i < allData.length; i++) {
					xmbh = allData[i].data['XMBH'];
					flag = true;
					for (var j = 0; j < json.length; j++) {
						tmpXmbh = json[j].XMBH;
						if (xmbh == tmpXmbh) {
							this.store.data.items[i].data['XMQZ'] = json[j].XMQZ;
							flag = false;
							// allData[i].data['XMQZ'] = json[j].XMQZ;
						}
					}
					if (flag) {
						this.store.data.items[i].data['XMQZ'] = '';
					}
				}
				this.grid.reconfigure(this.store, this.cm);
			},
			/**
			 * 新增
			 */
			addJl02 : function() {
				Ext.getCmp('EXT_RYRQ').setValue(Date.getServerDateTime());
				Ext.getCmp('EXT_RYRQ').enable();
				this.param = {
					'ZYH' : this.zyh,
					'BRBQDM' : this.brbqdm
				};
				var allData = this.store.data.items;
				for (var i = 0; i < allData.length; i++) {
					this.store.data.items[i].data['XMQZ'] = '';
				}
				this.grid.reconfigure(this.store, this.cm);
			},
			/**
			 * 改变病人信息
			 * 
			 * @param data
			 */
			changeBrInfo : function(data) {
				// {'zyh': this.zyh, 'brbqdm' : this.brbqdm}
				this.zyh = data.zyh;
				this.brbqdm = data.brbqdm;
				this.param = {
					'ZYH' : this.zyh,
					'BRBQDM' : this.brbqdm
				};
			},
			onEditorKey : function(field, e){
				alert(11);
			},
			//体温（36-37.5） 脉搏（60-100） 呼吸（16-20） 收缩压（100-120）舒张压（60-80）
			yyxjy : function(v, params, reocrd) {
				if(v&&reocrd){
					if(isNaN(v) && reocrd.data.XSMC!='意识' 
						&& reocrd.data.XSMC!='颜色形状' && reocrd.data.XSMC!='皮肤情况' 
						&& reocrd.data.XSMC!='管道护理' && reocrd.data.XSMC!='病情观察及措施'){
						params.style = "color:red;";
					}else if(reocrd.data.XSMC=='体温'){
						if((parseFloat(v)<36||parseFloat(v)>37.5)){
							params.style = "color:red;";
						}
					}else if(reocrd.data.XSMC=='脉搏'){
						if((parseFloat(v)<60||parseFloat(v)>100)){
							params.style = "color:red;";
						}
					}else if(reocrd.data.XSMC=='呼吸'){
						if((parseFloat(v)<16||parseFloat(v)>20)){
							params.style = "color:red;";
						}
					}else if(reocrd.data.XSMC=='收缩压'){
						if((parseFloat(v)<100||parseFloat(v)>120)){
							params.style = "color:red;";
						}
					}else if(reocrd.data.XSMC=='舒张压'){
						if((parseFloat(v)<60||parseFloat(v)>80)){
							params.style = "color:red;";
						}
					}
				}
				return v;
			}
		});