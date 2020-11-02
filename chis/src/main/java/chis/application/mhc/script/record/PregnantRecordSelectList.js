/**
 * 孕妇选择列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.mhc.script.record")
$import("chis.script.BizSimpleListView")
chis.application.mhc.script.record.PregnantRecordSelectList = function(cfg) {
	cfg.entryName = "chis.application.mhc.schemas.MHC_PregnantRecordSelect";
	cfg.actions = [{
				id : "save",
				name : "查看"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}]
	if (cfg.formRecordList) {
		var newAct = {
			id : "createNew",
			name : "新建",
			iconCls : "create"
		}
		cfg.actions.splice(0, 0,newAct)
	}

	cfg.autoLoadData = false;
	cfg.autoLoadSchema = false;
	cfg.disablePagingTbr = true;
	cfg.enableCnd = false;
	cfg.width = 800;
	cfg.autoHeight = false
	cfg.height = 400;
	cfg.modal = true;
	cfg.title = "孕妇档案列表";
	chis.application.mhc.script.record.PregnantRecordSelectList.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(chis.application.mhc.script.record.PregnantRecordSelectList,
		chis.script.BizSimpleListView, {

			doCreateNew : function() {
				this.fireEvent("create");
				this.getWin().hide();
			},

			doSave : function() {
				var r = this.getSelectedRecord()
				if (!r) {
					return;
				}
				var recordId = r.get("pregnantId");
				this.fireEvent("update", recordId);
				this.getWin().hide();
			},

			doCancel : function() {
				this.getWin().hide();
			},

			onDblClick : function() {
				this.doSave();
			},

			setData : function(array) {
				if (!this.grid) {
					this.grid = this.initPanel();
					this.store = this.grid.store;
				}
				this.store.removeAll();
				if (!array || array.length == 0) {
					return;
				}
				for (var i = 0; i < array.length; i++) {
					var r = new Ext.data.Record(array[i]);
					this.store.add(r);
				}
				this.store.commitChanges();

			}
		});