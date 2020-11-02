<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.pcm.PCM" name="门诊处方点评">
	<catagory id="PCM" name="门诊处方点评">
	<module id="PCM01" name="门诊处方点评"
			script="phis.application.pcm.script.PrescriptionCommentsModule">
			<properties>
				<p name="entryName">phis.application.pcm.schemas.PCIS_CFDP01_CYSJ</p>
				<p name="refList">phis.application.pcm.PCM/PCM/PCM0101</p>
				<p name="refModule">phis.application.pcm.PCM/PCM/PCM0102</p>
				<p name="refCqform">phis.application.pcm.PCM/PCM/PCM0103</p>
				<p name="serviceId">prescriptionCommentsService</p>
				<p name="serviceAction">updateCfdp</p>
			</properties>
			<action id="refresh" name="刷新" />
			<action id="cq" name="抽取" iconCls="ransferred_all"/>
			<action id="remove" name="删除" />
			<action id="wc" name="完成" iconCls="default"/>
			<action id="qxwc" name="取消完成" iconCls="writeoff"/>
			<action id="print" name="打印" />
			<action id="dc" name="导出" iconCls="print"/>
			<action id="zddp" name="自动点评" iconCls="create"/>
		</module>
		<module id="PCM0101" name="处方点评-左边list" type="1"
			script="phis.application.pcm.script.PrescriptionCommentsLeftList">
			<properties>
				<p name="entryName">phis.application.pcm.schemas.PCIS_CFDP01_CYSJ</p>
				<p name="serviceId">phis.prescriptionCommentsService</p>
				<p name="serviceAction">queryCQRQ</p>
			</properties>
		</module>
		<module id="PCM0102" name="处方点评-右边大Module" type="1"
			script="phis.application.pcm.script.PrescriptionCommentsRightModule">
			<properties>
				<p name="entryName">phis.application.pcm.schemas.PCIS_CFDP01</p>
				<p name="refLMoudle">phis.application.pcm.PCM/PCM/PCM010201</p>
				<p name="refModule">phis.application.pcm.PCM/PCM/PCM010202</p>
			</properties>
		</module>
		<module id="PCM010201" name="处方点评-右边大Module-左边的tab" type="1"
			script="phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTab">
			<properties>
				<p name="entryName">phis.application.pcm.schemas.PCIS_CFDP01</p>
				<p name="ServiceId">prescriptionCommentsService</p>
				<p name="serviceAction">saveCfdpWt</p>
			</properties>
			<action id="cyjg" viewType="cyjg" name="抽样结果"
				ref="phis.application.pcm.PCM/PCM/PCM01020101" />
				<action id="dpjg" viewType="dpjg" name="点评结果"
				ref="phis.application.pcm.PCM/PCM/PCM01020102" />
		</module>
		<module id="PCM01020101" name="处方点评-右边大Module-左边的tab-第一个Tab" type="1"
			script="phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTabFirstModule">
			<properties>
				<p name="entryName">phis.application.pcm.schemas.PCIS_CFDP01</p>
				<p name="refForm">phis.application.pcm.PCM/PCM/PCM0102010101</p>
				<p name="refList">phis.application.pcm.PCM/PCM/PCM0102010102</p>
			</properties>
		</module>
		<module id="PCM0102010101" name="处方点评-右边大Module-左边的tab-第一个Tab-上面的form" type="1"
			script="phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTabFirstModuleTopForm">
			<properties>
				<p name="entryName">phis.application.pcm.schemas.PCIS_CFDP01_DFORM</p>
			</properties>
		</module>
		<module id="PCM0102010102" name="处方点评-右边大Module-左边的tab-第一个Tab-下面的list" type="1"
			script="phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTabFirstModuleUnderList">
			<properties>
				<p name="entryName">phis.application.pcm.schemas.PCIS_CFDP02_ULIST</p>
			</properties>
		</module>
		<module id="PCM01020102" name="处方点评-右边大Module-左边的tab-第二个Tab" type="1"
			script="phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTabSecondModule">
			<properties>
				<p name="serviceId">prescriptionCommentsService</p>
				<p name="serviceAction">queryCfdpjg</p>
			</properties>
			
		</module>
		<module id="PCM010202" name="处方点评-右边大Module-右边Module" type="1"
			script="phis.application.pcm.script.PrescriptionCommentsRightModuleRightModule">
			<properties>
				<p name="entryName">phis.application.pcm.schemas.PCIS_CFDP01</p>
				<p name="refList">phis.application.pcm.PCM/PCM/PCM01020201</p>
			</properties>
			<action id="hl" name="合理" iconCls="default"/>
			<action id="bhl" name="不合理" iconCls="writeoff"/>
		</module>
		<module id="PCM01020201" name="处方点评-不合理问题" type="1"
			script="phis.application.pcm.script.PrescriptionCommentsRightModuleBhlList">
			<properties>
				<p name="entryName">phis.application.pcm.schemas.PCIS_WTDM</p>
			</properties>
			<action id="qd" name="确定" iconCls="save"/>
			<action id="close" name="关闭" />
		</module>
		<module id="PCM0103" name="抽样条件选择" type="1"
			script="phis.application.pcm.script.PrescriptionCommentsCQModule">
			<properties>
				<p name="refForm">phis.application.pcm.PCM/PCM/PCM010301</p>
				<p name="refList">phis.application.pcm.PCM/PCM/PCM010302</p>
				<p name="saveServiceId">prescriptionCommentsService</p>
				<p name="serviceAction">saveCfCQ</p>
			</properties>
			<action id="commit" name="确定" iconCls="save"/>
			<action id="close" name="关闭" />
		</module>
		<module id="PCM010301" name="抽样条件选择form" type="1"
			script="phis.application.pcm.script.PrescriptionCommentsCQForm">
			<properties>
				<p name="entryName">phis.application.pcm.schemas.PCIS_CQ</p>
				<p name="saveServiceId">prescriptionCommentsService</p>
				<p name="queryXtcsAction">queryCqsl</p>
			</properties>
		</module>
		<module id="PCM010302" name="抽样条件选择List" type="1"
			script="phis.application.pcm.script.PrescriptionCommentsCQList">
			<properties>
				<p name="entryName">phis.application.pcm.schemas.PCIS_CQ_YP</p>
			</properties>
			<action id="create" name="新增" />
			<action id="remove" name="删除" />
		</module>
		
		
		<module id="PCM02" name="问题代码设置" 
			script="phis.application.pcm.script.PrescriptionCommentsWTWHList">
			<properties>
				<p name="entryName">phis.application.pcm.schemas.PCIS_WTDM</p>
				<p name="refForm">phis.application.pcm.PCM/PCM/PCM0201</p>
				<p name="saveServiceId">prescriptionCommentsService</p>
				<p name="serviceAction">updateCfWT</p>
			</properties>
			<action id="xz" name="新增" iconCls="create"/>
			<action id="xg" name="修改" iconCls="update"/>
			<action id="sc" name="删除" iconCls="remove"/>
			<action id="zx" name="注销" iconCls="default"/>
			<action id="qxzx" name="取消注销" iconCls="writeoff"/>
		</module>
		<module id="PCM0201" name="处方点评问题维护form" type="1"
			script="phis.application.pcm.script.PrescriptionCommentsWTWHForm">
			<properties>
				<p name="entryName">phis.application.pcm.schemas.PCIS_WTDM</p>
				<p name="beforeSaveServiceId">prescriptionCommentsService</p>
				<p name="serviceAction">saveCfWT</p>
			</properties>
			<action id="save" name="保存" />
			<action id="close" name="关闭" />
		</module>
		<module id="PCM03" name="处方点评统计表" 
			script="phis.application.pcm.script.PrescriptionCommentsTjModule">
			<properties>
				<p name="refList">phis.application.pcm.PCM/PCM/PCM0301</p>
				<p name="refPrint">phis.application.pcm.PCM/PCM/PCM0302</p>
				<p name="entryName">phis.application.pcm.schemas.PCIS_CFDP01_CYSJ</p>
			</properties>
			<action id="refresh" name="刷新" iconCls="create"/>
		</module>
		<module id="PCM0301" name="处方点评统计表list" type="1"
			script="phis.application.pcm.script.PrescriptionCommentsTjList">
			<properties>
				<p name="entryName">phis.application.pcm.schemas.PCIS_CFDP02_TJBB</p>
				<p name="fullServiceid">phis.prescriptionCommentsService</p>
				<p name="serviceActionId">queryTJBBSJ</p>
				<p name="refList">phis.application.pcm.PCM/PCM/PCM030101</p>
			</properties>
		</module>
		<module id="PCM030101" name="处方点评统计表明细list" type="1"
			script="phis.application.pcm.script.PrescriptionCommentsTjMXList">
			<properties>
				<p name="entryName">phis.application.pcm.schemas.PCIS_CFDP02_DPJG</p>
				<p name="serviceId">phis.prescriptionCommentsService</p>
				<p name="serviceAction">queryTJBBSJMX</p>
			</properties>
			<action id="print" name="打印" />
		</module>
		<module id="PCM0302" name="处方点评统计表-图形报表" type="1"
			script="phis.application.pcm.script.PrescriptionCommentsTjTx">
		</module>
	</catagory>
</application>