/**
 * 网格地址Form
 * 
 * @author : chenhb
 */
$package("chis.application.ag.script")
$import("chis.script.BizTableFormView")

chis.application.ag.script.AreaGridMaintenanceForm = function(cfg) {
	cfg.actions = [{
				id : "save",
				name : "确定"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}]
	cfg.colCount = 2;
	cfg.loadServiceId = "chis.agService";
	cfg.loadAction = "getAreaGridFormData";
	cfg.width = 540;
	chis.application.ag.script.AreaGridMaintenanceForm.superclass.constructor.apply(
			this, [cfg])
	this.on("winShow", this.onWinShow, this)
	this.on("doNew", this.onDoNew, this)
	this.on("loadData", this.onLoadData, this);
	this.on("beforeSave", this.onBeforeSave, this);
}

Ext.extend(chis.application.ag.script.AreaGridMaintenanceForm, chis.script.BizTableFormView,
		{
			onReady : function() {
				chis.application.ag.script.AreaGridMaintenanceForm.superclass.onReady
						.call(this);
				var checkBox = this.form.findById("_checkOverwrite");
				if (checkBox) {
					checkBox.on("check", this.chekBoxCheck, this);
				}
				var isFamily = this.form.getForm().findField("isFamily");
				var regionNo = this.form.getForm().findField("regionNo");
				regionNo.on("blur",function(){
					this.isFamilyNode = this.findIsFamilyNode();
					this.formatRegionNo();
				},this);
				isFamily.on("select",this.isFamilySelected,this);
				isFamily.on("expand",function(combo){
					var tree = combo.tree ;
					tree.expandAll();
				},this);
				if (isFamily) {
					this.tree = isFamily.tree;
					var root = this.tree.getRootNode();
					this.tree.on("load", function() {
								this.tree.filter
										.filterBy(this.filterTree, this)
							}, this)
				}
			},
			findIsFamilyNode:function(){
				var isFamilyField = this.form.getForm().findField("isFamily");
				var isFamily = isFamilyField.getValue();
				var root = isFamilyField.tree.root;
				var types = root.childNodes ;
				for(var i = 0 ;i<types.length ;i++){
					var type = types[i];
					var nodes = type.childNodes ;
					for (var j = 0 ;j<nodes.length;j++){
						var node = nodes[j];
						if(node.attributes["key"] == isFamily){
							return node ;
						}
					}
				}
			},
			formatRegionNo:function(){
				var regionNoField = this.form.getForm().findField("regionNo");
				regionNoField.maxLength = 4 
				regionNoField.validate();
				var isFamilyField = this.form.getForm().findField("isFamily");
				var regionNo = regionNoField.getValue();
				if(!regionNo || regionNo ==0){
					return ;
				}
				
				var isFamily = isFamilyField.getValue();
				var layLen = 3 ;
				if(regionNo && regionNo!=""){
					if(isFamily && isFamily=="1"){
						layLen = 4 ;
					}
					var len = regionNo.length;
					if(len<layLen){
						for(var i=0;i<layLen-len ;i++){
							regionNo="0"+regionNo;
						}
					}else if(len > layLen){
						for(var i=0;i<len-layLen && regionNo[0]=="0";i++){
							regionNo=regionNo.substring(1);
						}
					}
					regionNoField.setValue(regionNo);
				}
				regionNoField.maxLength = layLen; 
				var max = this.getMaxCodeValue();
				var min = this.getMinCodeValue();
				if(!this.toIntVal(regionNo) || this.toIntVal(regionNo)<min || this.toIntVal(regionNo)>max){
					regionNoField.maxLength = 0; 
					regionNoField.maxLengthText = ("编码取值范围应该在"+min+"-"+max);
				}
				regionNoField.validate();
			},
			toIntVal:function(v){
				if(!v || ""==v){
					return -1;
				}
				for(var i=0;i < v.length ;){
					if(v[0]=="0"){
						v=v.substring(1)
					}else{
						break;
					}
				}
				return parseInt(v);
			},
			getLimitFromServer:function(){
				var isFamilyField = this.form.getForm().findField("isFamily");
				var isFamily = isFamilyField.getValue();
				if(!isFamily || ""==isFamily){
					return ;
				}
				var result = util.rmi.miniJsonRequestSync({
					serviceId : "chis.agService",
					serviceAction : "getLimitInfo",
					method:"execute",
					body:{
						isFamily : isFamily
					}
				})
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return
				}
				return result.json.body.limit;
				
			},
			getMinCodeValue:function(){
				var limit ;
				if(!this.isFamilyNode){
					limit= this.getLimitFromServer();
				}else{
					limit  = this.isFamilyNode.attributes["limit"];
				}
				
				if(!limit){
					return 0;
				}
				var arr= limit.split("-");
				return arr[0];
			},
			getMaxCodeValue:function(){
				var limit ;
				if(!this.isFamilyNode){
					limit= this.getLimitFromServer();
				}else{
					limit  = this.isFamilyNode.attributes["limit"];
				}
				if(!limit){
					return 999;
				}
				var arr= limit.split("-");
				return arr[1];
			},
			isFamilySelected:function(combox,r,index){
				this.isFamilyNode = r;
				var isFamily = combox.getValue();
				var regionNoField = this.form.getForm().findField("regionNo");
				var regionNo = regionNoField.getValue();
				var max = this.getMaxCodeValue();
				var min = this.getMinCodeValue();
				if(this.toIntVal(regionNo)>=min && this.toIntVal(regionNo)<=max){
					this.formatRegionNo();
					return ;
				}
				util.rmi.jsonRequest({
					serviceId:"chis.agService",
					serviceAction:"getDefaultId",
					method:"execute",
					body:{
						isFamily:isFamily,
						parentCode:this.parentCode
					}
				}, function(code, msg, json) {
					this.form.el.unmask()
					this.saving = false
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return
					}
					var defaultRegionNo = json.body.regionNo;
					var regionNoField = this.form.getForm().findField("regionNo");
					regionNoField.setValue(defaultRegionNo);
					this.formatRegionNo();
				}, this);
			},
			filterTree : function(node) {
				var isFamily
				if (this.node) {
					isFamily = this.node.attributes["isFamily"]
				} else {
					if (this.data["isFamily"])
						isFamily = this.data["isFamily"].key
					if (isFamily) {
						var chr = isFamily.substring(0, 1);
						if (chr == 1) {
							isFamily = '1'
						} else {
							isFamily.replace(chr, String.fromCharCode(eval(chr
											.charCodeAt()
											- 1)))
						}
					}
				}
				//alert('isFamily:'+isFamily)
				if (!isFamily)
					return
				var thisFamily = isFamily;	
				if(this.data["isFamily"]){
					thisFamily = this.data["isFamily"].key
				}
				//alert(Ext.encode(this.data))
				var nodeIsFamily= node.attributes.key
				//修改时 不能农村城市交叉互改.
				//alert(this.op + "-"+isFamily.length)
				if(this.op =="update" ){
					if(thisFamily.length ==1){
						thisFamily = this.node.attributes["isFamily"];
					}
					var type = thisFamily.substring(thisFamily.length -1 , thisFamily.length);
					if("1" == type && "country" == nodeIsFamily){
						return false ;
					}
					if("2" == type && "city" == nodeIsFamily){
						return false ;
					}
					
				}
				//alert('nodeIsFamily:'+nodeIsFamily)
				var p1 = /^[e-g]\s*[a-b]?\s*2$/; // 农村
				if (p1.exec(isFamily)) {
					if (nodeIsFamily == "city")
						return false
					if (p1.exec(nodeIsFamily) && nodeIsFamily.substring(0,1) <= isFamily.substring(0,1)) {
						return false
					}
				}
				var p2 = /^[g-i]\s*1$/ // 社区以下
				if (p2.exec(isFamily)) {
					if (nodeIsFamily == "country")
						return false
				}
				var p3 = /^[e-i]\s*1$/ // 城市
				if (p3.exec(isFamily)) {
					if (p3.exec(nodeIsFamily) && nodeIsFamily <= isFamily)
						return false
					if (p1.exec(nodeIsFamily)
							&& nodeIsFamily.substring(0, 1) <= isFamily.substring(0,
									1)) {
						return false
					}
				}
				var p4 = /^[d]\s*1$/; //小区*只能创建-- 城市【街道】   或  农村【乡、镇】
				if(p4.exec(isFamily)){
					if(nodeIsFamily == '1'){
						return false;
					}
					var p41 = /^[f-i]\s*[1-2]$/; 
					if(p41.exec(nodeIsFamily)){
						return false;
					}
				}
				// 子节点有城市节点，中间节点不能修改为农村节点
				if (this.hasCityChildNode) {// 过虑掉农村层次属性选择
					if (nodeIsFamily == "country")
						return false
				}
				return true
			},
			chekBoxCheck : function(field, isCheck) {
				var isFamily = this.form.getForm().findField("isFamily");
				if (!isFamily)
					return;
				if (isCheck) {
					isFamily.disable()
				} else {
					isFamily.enable();
				}
			},
			onBeforeSave : function(entryName, op, saveData) {
				if ((saveData["orderNo"] + "").length > 5) {
					Ext.Msg.alert('提示', '【排列序号】允许的最大长度是5位！');
					return false;
				}
				if (op == "update" && saveData["regionCode"].length < 7) {
					Ext.Msg.alert('提示', '县区级以上网格地址不允许修改！');
					return false;
				}
				var childNodes = this.selectedNodeChilds;
				if (childNodes) {
					var theIsFamily = saveData["isFamily"];
					for (var i = 0; i < childNodes.length; i++) {
						var childIsFamily = childNodes[i].attributes["isFamily"];
						if (childIsFamily != '1'
								&& theIsFamily >= childIsFamily) {
							Ext.Msg.alert('提示', '层次属性级别必须高于其所有子节点的层次属性级别！');
							return false;
						}
						if (childIsFamily == '1' && theIsFamily == '1') {
							Ext.Msg.alert('提示', '层次属性级别必须高于其所有子节点的层次属性级别！');
							return false;
						}
						continue;
					}
				}
				return true;
			},
			saveToServer : function(saveData) {
				if (!this.fireEvent("beforeSave", this.entryName, this.op,
						saveData)) {
					return;
				}
				if (this.initDataId == null) {
					this.op = "create";
				}
				saveData["parentBottom"] = this.parentBottom;
				request = {
					serviceId : "chis.agService",
					method:"execute",
					op : this.op,
					schema : this.entryName,
					body : saveData
				}
				if (this.op == "create") {
					saveData["parentCode"] = this.parentCode
					request.serviceAction = "saveAreaGrid"
					this.saveData(request);
				} else {
					var check = this.form.findById("_checkOverwrite");
					var value = check.getValue()
					saveData["overWrite"] = value;
					request.serviceAction = "updateAreaGrid"
					if (value = "true") {
						var result = util.rmi.miniJsonRequestSync({
									serviceId : "chis.agService",
									serviceAction : "checkOverwrite",
									method:"execute",
									regionCode : this.data["regionCode"]
								})
						if (result.code > 300) {
							this.processReturnMsg(result.code, result.msg);
							return
						}
						var count = result.json.body.count;
						if (count > 1) {
							Ext.Msg.show({
										title : '提示信息',
										msg : '该网格地址下存在两个以上的责任医生,是否保存修改?',
										modal : true,
										minWidth : 300,
										maxWidth : 600,
										buttons : Ext.MessageBox.OKCANCEL,
										multiline : false,
										fn : function(btn, text) {
											if (btn == "ok") {
												this.saveData(request);
											}
											return;
										},
										scope : this
									})
						} else {
							this.saveData(request);
						}
					} else {
						this.saveData(request);
					}
				}
			},
			saveData : function(request) {
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				util.rmi.jsonRequest(request, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [request.body]);
								return
							}
							Ext.apply(request.body, json.body);
							this.fireEvent("save", request.body, this.op)
							this.getWin().hide();
						}, this);
			},
			onWinShow : function() {
				this.doNew();
			},
			onDoNew : function() {
				this.setKeyReadOnly(true);
			},
			onLoadData : function() {
				this.form.getForm().findField("path").setValue(this.data.path);
				// chb 控制覆盖子节点按钮
				var _checkOverwrite = this.form.getForm()
						.findField("_checkOverwrite");
				if (this.data["isFamily"].key == 1) {
					_checkOverwrite.disable();
				} else
					_checkOverwrite.enable();
				this.tree.filter.filterBy(this.filterTree, this)
				
				var regionNoField = this.form.getForm().findField("regionNo");
				regionNoField.maxLength = 4 
				regionNoField.maxLengthText = "";
				regionNoField.validate();
			},

			fillGrid : function(grid) {
				if (grid.length < 6)
					return "00";
				else
					return "000";
			},
			setNodeInfo : function(node, op) {
				if (!node) {
					return;
				}
				this.node = node;
				this.parentCode = node.attributes["key"];
				this.parentBottom = node.attributes["isBottom"];
				this.form.getForm().findField("path").setValue("");
				if (op && op == "create") {
					var newRegionCode = this.parentCode
							+ this.fillGrid(this.parentCode);
					this.getFullPath(newRegionCode, "");
				}
				this.tree.filter.filterBy(this.filterTree, this);
			},
			getFullPath : function(regionCode, isFamily) {
				util.rmi.jsonRequest({
							serviceId : "chis.agService",
							serviceAction : "getFullPath",
							method:"execute",
							body : {
								"regionCode" : regionCode,
								"isFamily" : isFamily
							}
						}, function(code, msg, json) {
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							if (code == 200) {
								if (json.body.path) {
									this.form.getForm().findField("path")
											.setValue(json.body.path);
									this.form.getForm().findField("parentCode")
											.setValue(this.parentCode);		
								}
							}
						}, this)
			}

		})