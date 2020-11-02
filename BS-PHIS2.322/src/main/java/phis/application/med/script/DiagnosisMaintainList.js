$package("phis.application.med.script");

$import("phis.script.EditorList","phis.script.widgets.MyMessageTip");

phis.application.med.script.DiagnosisMaintainList = function(cfg) {
	this.isEdit = false;
	phis.application.med.script.DiagnosisMaintainList.superclass.constructor.apply(this,
			[cfg]);
	this.on("afterCellEdit", this.onAfterCellEdit, this);

},

Ext.extend(phis.application.med.script.DiagnosisMaintainList, phis.script.EditorList,
		{
			/**
			 * 初始化面板
			 * 
			 * @param sc
			 * @returns
			 */
			initPanel : function(sc) {
				this.medicalId = this.mainApp['phis'].MedicalId;
				//this.medicalId = 32;
				if (!this.medicalId) {
					MyMessageTip.msg("提示", "请选择医技科室!", true);
					return;
				}
				this.removeList = [];
				this.updateList = [];
				grid = phis.application.med.script.DiagnosisMaintainList.superclass.initPanel
						.call(this, sc);
				grid.on("beforeclose", this.onBeforeClose, this);
				return grid;
			},
			/**
			 * 保存操作
			 */
			doSave : function() {
				this.updateList.length = 0;
				var allItems = this.store.data.items;
				var it;
				for (var i = 0; i < allItems.length; i++) {
					it = allItems[i];
					if (it.dirty) {
						if(it.data.ZDMC==null||it.data.ZDMC==undefined||it.data.ZDMC==""){
						MyMessageTip.msg("提示", "第"+(i+1)+"行诊断名称不能为空", true);
						return;
						}
						this.updateList.push(it.data);
						//alert(this.updateList.length);
					}
				}

				if (this.removeList.length > 0 || this.updateList.length > 0) {
					var result = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.sID,
								serviceAction : this.sAction,
								medicalId : this.medicalId,
								removeList : this.removeList,
								updateList : this.updateList
							});
					if (result.code == 200) {
						MyMessageTip.msg("提示", '保存成功!', true);
						this.isEdit = false;
					} else {
						if (result.msg && result.msg != "") {
							MyMessageTip.msg("提示", result.msg, true);
						} else {
							MyMessageTip.msg("提示", "执行失败!", true);
						}
					}
					this.removeList = [];
					this.updateList = [];
					this.store.load();
				}
			},
			/**
			 * 删除行
			 */
			doRemove : function() {
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord();
				if (r == null) {
					return
				}
				this.store.remove(r);
				this.isEdit=true;
				this.grid.getView().refresh();
				// 移除之后焦点定位
				var count = this.store.getCount();
				if (count > 0) {
					cm.select(cell[0] < count ? cell[0] : (count - 1), cell[1]);
				}
				// 如果删除的行不是新创建出来的需要添加到removeList集合中
				if (r != null) {
					if (r.data._opStatus != "create") {
						this.removeList.push(r.data);
					}
				}
			},
			// 加载数据时增加机构和科室过滤
			loadData : function() {
				this.requestData.cnd = ['and',
						['eq', ['$', 'JGID'], ['s', this.mainApp['phisApp'].deptId]],
						['eq', ['$', 'KSDM'], ['d', this.medicalId]]];
				phis.application.med.script.DiagnosisMaintainList.superclass.loadData
						.call(this);
			},
			// 关闭前,判断是否有数据修改
			onBeforeClose : function() {
				if(this.isEdit){
				if (!confirm('数据已修改,是否直接关闭?')) {
				return false;
				}
				}
				return true;
			},
			onAfterCellEdit:function(it, record, field, v){
			this.isEdit=true;
			}
		});