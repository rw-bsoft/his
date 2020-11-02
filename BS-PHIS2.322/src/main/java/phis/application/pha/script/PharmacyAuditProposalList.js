/**
 * 审方意见列表
 * @param {} cfg
 */
$package("phis.application.pha.script")
$import("phis.script.SimpleList")

phis.application.pha.script.PharmacyAuditProposalList = function(cfg) {
	var defaultCfg = {
		selectFirst : false,
		valueSplit : ';',
		selectId : 'SFYJ',	//要获取的字段名
		typeItemId : 'SFLX',	//类型字段名称
		disablePagingTbr : true
	}
	cfg.entryName="phis.application.pha.schemas.GY_SFYJ"; 
	Ext.apply(cfg, defaultCfg); 
	phis.application.pha.script.PharmacyAuditProposalList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(phis.application.pha.script.PharmacyAuditProposalList, phis.script.SimpleList, {
	initPanel : function(sc) {
		var grid = phis.application.pha.script.PharmacyAuditProposalList.superclass.initPanel.call(this,sc); 
		this.grid = grid; 
		return grid; 
	},
	getCndBar : function() { 
		var radioGroup = new Ext.form.RadioGroup({
			width : 160,
			disabled : false,
			items : [{
				boxLabel : '书写规范',
				inputValue : 1,
				name : "stack",
				clearCls : true
			},{
				boxLabel : '用药适宜性',
				inputValue : 2,
				name : "stack",
				clearCls : true
			}],
			listeners : {
				change : function(group, checked) { 
					this.loadData(checked.inputValue); 
				},
				scope : this
			}
		});
		this.radioGroup = radioGroup
		radioGroup.setValue(1); 
		return [radioGroup]; 
	},
	loadData : function(type) { 
		if(!type) {
			type = 1;
		}
		var cnd = ['eq',['$','a.SFLX'], ['i', type]];
		this.requestData.cnd = cnd;
		phis.application.pha.script.PharmacyAuditProposalList.superclass.loadData.call(this);
	},
	onDblClick : function(grid, index, e) { 
		var rs = this.getSelectedRecord();
		var selValue = rs.get(this.selectId)
		this.fireEvent('proposalClick', selValue);
	},
	//清除grid所有选择区域
	clearSelections : function() { 
		this.grid.getSelectionModel().clearSelections();
	}
});