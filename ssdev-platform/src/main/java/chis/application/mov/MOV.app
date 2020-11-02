<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.mov.MOV" name="档案迁移" type="1">
	<catagory id="MOV" name="档案迁移">
		<module id="R01" name="档案迁移管理"
			script="chis.application.mov.script.ehr.EHRMoveList">
			<properties>
				<p name="entryName">chis.application.mov.schemas.MOV_EHRConfirm</p>
				<p name="navField">Status</p>
				<p name="navDic">chis.dictionary.archiveMoveStatus</p>
				<p name="refApplyModule">chis.application.mov.MOV/MOV/R01_1</p>
				<p name="refConfirmModule">chis.application.mov.MOV/MOV/R01_2</p>
			</properties>
			<action id="request" name="申请" iconCls="archiveMove_apply" />
			<action id="modify" name="查看" iconCls="update" />
			<action id="confirm" name="确认" iconCls="archiveMove_commit" />
			<action id="remove" name="删除" />
			<action id="print" name="打印" />
		</module>
		<module id="R01_1" name="档案迁移申请表单" type="1"
			script="chis.application.mov.script.ehr.EHRMoveApplyForm">
			<properties>
				<p name="entryName">chis.application.mov.schemas.MOV_EHRApply</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="R01_2" name="档案迁移确认表单" type="1"
			script="chis.application.mov.script.ehr.EHRMoveConfirmForm">
			<properties>
				<p name="entryName">chis.application.mov.schemas.MOV_EHRConfirm</p>
			</properties>
			<action id="confirm" name="执行" iconCls="confirm" group="confirm" />
			<action id="reject" name="退回" iconCls="reject" group="confirm" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="R02" name="儿童户籍地址迁移"
			script="chis.application.mov.script.cdh.CDHMoveList">
			<properties>
				<p name="entryName">chis.application.mov.schemas.MOV_CDH</p>
				<p name="refApplyModule">chis.application.mov.MOV/MOV/R02_1</p>
				<p name="refConfirmModule">chis.application.mov.MOV/MOV/R02_2</p>
			</properties>
			<action id="request" name="申请" iconCls="archiveMove_apply" />
			<action id="modify" name="查看" iconCls="update" />
			<action id="confirm" name="确认" iconCls="archiveMove_commit" />
			<action id="remove" name="删除" />
		</module>
		<module id="R02_1" name="儿童户籍地址迁移管理申请表单"
			script="chis.application.mov.script.cdh.CDHMoveApplyForm" type="1">
			<properties>
				<p name="entryName">chis.application.mov.schemas.MOV_CDHApply</p>
				<p name="saveServiceId">chis.cdhMoveService</p>
				<p name="saveAction">saveCDHMoveApplyRecord</p>
				<p name="loadServiceId">chis.cdhMoveService</p>
				<p name="loadAction">getCDHMoveRecord</p>
			</properties>
			<action id="save" name="确定" group="apply" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="R02_2" name="儿童户籍地址迁移管理确认表单"
			script="chis.application.mov.script.cdh.CDHMoveConfirmForm" type="1">
			<properties>
				<p name="entryName">chis.application.mov.schemas.MOV_CDHConfirm</p>
				<p name="saveServiceId">chis.cdhMoveService</p>
				<p name="loadServiceId">chis.cdhMoveService</p>
				<p name="loadAction">getCDHMoveRecord</p>
			</properties>
			<action id="confirm" name="执行" iconCls="confirm" group="confirm" />
			<action id="reject" name="退回" iconCls="reject" group="confirm" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="R03" name="孕妇户籍地址迁移"
			script="chis.application.mov.script.mhc.MHCMoveList">
			<properties>
				<p name="entryName">chis.application.mov.schemas.MOV_MHC</p>
				<p name="refApplyModule">chis.application.mov.MOV/MOV/R03_1</p>
				<p name="refConfirmModule">chis.application.mov.MOV/MOV/R03_2</p>
			</properties>
			<action id="request" name="申请" iconCls="archiveMove_apply" />
			<action id="modify" name="查看" iconCls="update" />
			<action id="confirm" name="确认" iconCls="archiveMove_commit" />
			<action id="remove" name="删除" />
		</module>
		<module id="R03_1" name="孕妇户籍地址迁移管理申请表单"
			script="chis.application.mov.script.mhc.MHCMoveApplyForm" type="1">
			<properties>
				<p name="entryName">chis.application.mov.schemas.MOV_MHCApply</p>
				<p name="saveServiceId">chis.mhcMoveService</p>
				<p name="saveAction">saveMHCMoveApplyRecord</p>
				<p name="loadServiceId">chis.mhcMoveService</p>
				<p name="loadAction">getMHCMoveRecord</p>
			</properties>
			<action id="save" name="确定" group="apply" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="R03_2" name="孕妇户籍地址迁移管理确认表单"
			script="chis.application.mov.script.mhc.MHCMoveConfirmForm" type="1">
			<properties>
				<p name="entryName">chis.application.mov.schemas.MOV_MHCConfirm</p>
				<p name="saveServiceId">chis.mhcMoveService</p>
				<p name="loadServiceId">chis.mhcMoveService</p>
				<p name="loadAction">getMHCMoveRecord</p>
			</properties>
			<action id="confirm" name="执行" iconCls="confirm" group="confirm" />
			<action id="reject" name="退回" iconCls="reject" group="confirm" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="R04" name="批量修改管理医生"
			script="chis.application.mov.script.batch.ManaInfoBatchChangeList">
			<properties>
				<p name="entryName">chis.application.mov.schemas.MOV_ManaInfoBatchChange</p>
				<p name="removeServiceId">chis.manaInfoBatchChangeService</p>
				<p name="removeAction">removeMoveRecord</p>
				<p name="refModule">chis.application.mov.MOV/MOV/R04_1</p>
			</properties>
			<action id="EHRRequest" name="个档批量申请" iconCls="archiveMove_apply" />
			<action id="CDHRequest" name="儿保批量申请" iconCls="archiveMove_apply" />
			<action id="MHCRequest" name="妇保批量申请" iconCls="archiveMove_apply" />
			<action id="modify" name="查看" iconCls="update" />
			<action id="confirm" name="确认" iconCls="archiveMove_commit" />
			<action id="remove" name="删除" />
		</module>
		<module id="R04_1" name="批量修改管理医生"
			script="chis.application.mov.script.batch.ManaInfoBatchChangeModule"
			type="1">
			<properties>
				<p name="saveServiceId">chis.manaInfoBatchChangeService</p>
				<p name="saveAction">saveBatchChangeRecord</p>
				<p name="loadServiceId">chis.manaInfoBatchChangeService</p>
				<p name="loadAction">getMoveRecord</p>
			</properties>
			<action id="BatchChangeForm" name="批量修改管理医生表单页面"
				ref="chis.application.mov.MOV/MOV/R04_2" />
			<action id="BatchChangeDetailList" name="批量修改管理医生明细列表页面"
				ref="chis.application.mov.MOV/MOV/R04_3" />
		</module>
		<module id="R04_2" name="批量修改管理医生表单页面"
			script="chis.application.mov.script.batch.ManaInfoBatchChangeForm"
			type="1">
			<action id="save" name="确定" group="apply" />
			<action id="confirm" name="执行" iconCls="confirm" group="confirm" />
			<action id="reject" name="退回" iconCls="reject" group="confirm" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="R04_3" name="批量修改管理医生明细列表页面"
			script="chis.application.mov.script.batch.ManaInfoBatchChangeDetailList"
			type="1">
			<properties>
				<p name="entryName">chis.application.mov.schemas.MOV_ManaInfoBatchChangeDetail
				</p>
				<p name="removeServiceId">chis.manaInfoBatchChangeService</p>
				<p name="removeAction">removeMoveRecordDetail</p>
			</properties>
			<action id="add" name="新增" iconCls="create" group="apply" />
			<action id="remove" name="删除" group="apply" />
		</module>
		<module id="R05" name="修改各档责任医生"
			script="chis.application.mov.script.manage.ManaInfoChangeList">
			<properties>
				<p name="entryName">chis.application.mov.schemas.MOV_ManaInfoChange</p>
				<p name="removeServiceId">chis.manaInfoChangeService</p>
				<p name="removeAction">removeMoveRecord</p>
				<p name="refModule">chis.application.mov.MOV/MOV/R05_1</p>
			</properties>
			<action id="request" name="申请" iconCls="archiveMove_apply" />
			<action id="modify" name="查看" iconCls="update" />
			<action id="confirm" name="确认" iconCls="archiveMove_commit" />
			<action id="remove" name="删除" />
		</module>
		<module id="R05_1" name="修改各档责任医生"
			script="chis.application.mov.script.manage.ManaInfoChangeModule"
			type="1">
			<properties>
				<p name="saveServiceId">chis.manaInfoChangeService</p>
				<p name="saveAction">saveChangeRecord</p>
				<p name="loadServiceId">chis.manaInfoChangeService</p>
				<p name="loadAction">getMoveRecord</p>
			</properties>
			<action id="ManageChangeForm" name="修改各档责任医生表单页面"
				ref="chis.application.mov.MOV/MOV/R05_2" />
			<action id="ManageChangeDetailList" name="修改各档责任医生明细列表页面"
				ref="chis.application.mov.MOV/MOV/R05_3" />
		</module>
		<module id="R05_2" name="修改各档责任医生表单页面"
			script="chis.application.mov.script.manage.ManaInfoChangeForm" type="1">
			<action id="save" name="确定" group="apply" />
			<action id="confirm" name="执行" iconCls="confirm" group="confirm" />
			<action id="reject" name="退回" iconCls="reject" group="confirm" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="R05_3" name="修改各档责任医生明细列表页面"
			script="chis.application.mov.script.manage.ManaInfoChangeDetailList"
			type="1">
			<properties>
				<p name="entryName">chis.application.mov.schemas.MOV_ManaInfoChangeDetail</p>
				<p name="removeServiceId">chis.manaInfoChangeService</p>
				<p name="removeAction">removeMoveRecordDetail</p>
			</properties>
			<action id="add" name="新增" iconCls="create" group="apply" />
			<action id="remove" name="删除" group="apply" />
		</module>
		<module id="R06" name="按网格地址更改责任医生"
			script="chis.application.mov.script.batch.ManaInfoChangebyareagridList">
			<properties>
				<p name="entryName">chis.application.mov.schemas.MOV_Manachangebyareagrid</p>
				<p name="navField">Status</p>
				<p name="navDic">chis.dictionary.archiveMoveStatus</p>
				<p name="refApplyModule">chis.application.mov.MOV/MOV/R06_1</p>
				<p name="refConfirmModule">chis.application.mov.MOV/MOV/R06_2</p>
			</properties>
			<action id="request" name="申请" iconCls="archiveMove_apply" />
			<action id="modify" name="查看" iconCls="update" />
			<action id="confirm" name="确认" iconCls="archiveMove_commit" />
			<action id="remove" name="删除" />
		</module>
		<module id="R06_1" name="按网格地址批量修改责任医生申请表单" type="1"
			script="chis.application.mov.script.batch.ManaInfoBatchChangebyareagridForm">
			<properties>
				<p name="entryName">chis.application.mov.schemas.MOV_Manachangebyareagrid</p>
			</properties>
			<action id="save" name="确定" group="update" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
		<module id="R06_2" name="按网格地址批量修改责任医生确认表单" type="1"
			script="chis.application.mov.script.batch.ManaInfoBatchChangebyareagridConfirmForm">
			<properties>
				<p name="entryName">chis.application.mov.schemas.MOV_Manachangebyareagridconfirm</p>
			</properties>
			<action id="confirm" name="执行" iconCls="confirm" group="confirm" />
			<action id="reject" name="退回" iconCls="reject" group="confirm" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
	</catagory>
</application>