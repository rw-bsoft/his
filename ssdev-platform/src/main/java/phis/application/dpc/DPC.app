<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.dpc.DPC" name="医患沟通">
	<catagory id="DPC" name="医患沟通">
	    <!--清创缝合手术同意书-->
		<module id="DPC01" name="清创缝合手术同意书" type="1" script="phis.application.dpc.script.DebridementAgreementList">
			<properties>
				<p name="createCls">phis.application.dpc.script.DebridementAgreementForm</p>
				<p name="updateCls">phis.application.dpc.script.DebridementAgreementForm</p>
				<p name="entryName">phis.application.dpc.schemas.DPC_DebridementAgreement</p>
				<p name="removeServiceId">phis.simpleRemove</p>
			</properties>
			<action id="create" name="新建" iconCls="create" ref="phis.application.dpc.DPC/DPC/DPC0101"/>
			<action id="update" name="查看" ref="phis.application.dpc.DPC/DPC/DPC0101" />
			<action id="remove" name="删除"/>
			<action id="print" name="打印" />
			<action id="refresh" name="刷新" />
		</module>
		<module id="DPC0101" type="1" name="清创缝合手术同意书表单" script="phis.application.dpc.script.DebridementAgreementForm" icon="default">
			<properties>
				<p name="entryName">phis.application.dpc.schemas.DPC_DebridementAgreement</p>
				<p name="saveAction">saveDebridementAgreement</p>
				<p name="saveServiceId">phis.DebridementAgreementService</p>
			</properties>
			<action id="save" name="确定" />
			<action id="cancel" name="取消" iconCls="common_cancel" />
		</module>
        <!--拔牙知情同意书病历版-->
		<module id="DPC02" name="拔牙知情同意书病历版" type="1" script="phis.application.dpc.script.ExtractionAgreementList">
        	<properties>
        		<p name="createCls">phis.application.dpc.script.ExtractionAgreementForm</p>
        		<p name="updateCls">phis.application.dpc.script.ExtractionAgreementForm</p>
        		<p name="entryName">phis.application.dpc.schemas.DPC_ExtractionAgreement</p>
        		<p name="removeServiceId">phis.simpleRemove</p>
        	</properties>
        	<action id="create" name="新建" iconCls="create" ref="phis.application.dpc.DPC/DPC/DPC0201"/>
        	<action id="update" name="查看" ref="phis.application.dpc.DPC/DPC/DPC0201" />
        	<action id="remove" name="删除"/>
        	<action id="print" name="打印" />
        	<action id="refresh" name="刷新" />
        </module>
        <module id="DPC0201" type="1" name="拔牙知情同意书病历版表单" script="phis.application.dpc.script.ExtractionAgreementForm" icon="default">
        	<properties>
        		<p name="entryName">phis.application.dpc.schemas.DPC_ExtractionAgreement</p>
        		<p name="saveAction">saveExtractionAgreement</p>
        		<p name="saveServiceId">phis.ExtractionAgreementService</p>
        	</properties>
        	<action id="save" name="确定" />
        	<action id="cancel" name="取消" iconCls="common_cancel" />
        </module>

        <!--固定修复知情同意书-->
		<module id="DPC03" name="固定修复知情同意书" type="1" script="phis.application.dpc.script.FixedAgreementList">
        	<properties>
        		<p name="createCls">phis.application.dpc.script.FixedAgreementForm</p>
        		<p name="updateCls">phis.application.dpc.script.FixedAgreementForm</p>
        		<p name="entryName">phis.application.dpc.schemas.DPC_FixedAgreement</p>
        		<p name="removeServiceId">phis.simpleRemove</p>
        	</properties>
        	<action id="create" name="新建" iconCls="create" ref="phis.application.dpc.DPC/DPC/DPC0301"/>
        	<action id="update" name="查看" ref="phis.application.dpc.DPC/DPC/DPC0301" />
        	<action id="remove" name="删除"/>
        	<action id="print" name="打印" />
        	<action id="refresh" name="刷新" />
        </module>
        <module id="DPC0301" type="1" name="固定修复知情同意书表单" script="phis.application.dpc.script.FixedAgreementForm" icon="default">
        	<properties>
        		<p name="entryName">phis.application.dpc.schemas.DPC_FixedAgreement</p>
        		<p name="saveAction">saveFixedAgreement</p>
        		<p name="saveServiceId">phis.FixedAgreementService</p>
        	</properties>
        	<action id="save" name="确定" />
        	<action id="cancel" name="取消" iconCls="common_cancel" />
        </module>

        <!--活动修复知情同意书-->
		<module id="DPC04" name="活动修复知情同意书" type="1" script="phis.application.dpc.script.ActivityAgreementList">
        	<properties>
        		<p name="createCls">phis.application.dpc.script.ActivityAgreementForm</p>
        		<p name="updateCls">phis.application.dpc.script.ActivityAgreementForm</p>
        		<p name="entryName">phis.application.dpc.schemas.DPC_ActivityAgreement</p>
        		<p name="removeServiceId">phis.simpleRemove</p>
        	</properties>
        	<action id="create" name="新建" iconCls="create" ref="phis.application.dpc.DPC/DPC/DPC0401"/>
        	<action id="update" name="查看" ref="phis.application.dpc.DPC/DPC/DPC0401" />
        	<action id="remove" name="删除"/>
        	<action id="print" name="打印" />
        	<action id="refresh" name="刷新" />
        </module>
        <module id="DPC0401" type="1" name="活动修复知情同意书表单" script="phis.application.dpc.script.ActivityAgreementForm" icon="default">
        	<properties>
        		<p name="entryName">phis.application.dpc.schemas.DPC_ActivityAgreement</p>
        		<p name="saveAction">saveActivityAgreement</p>
        		<p name="saveServiceId">phis.ActivityAgreementService</p>
        	</properties>
        	<action id="save" name="确定" />
        	<action id="cancel" name="取消" iconCls="common_cancel" />
        </module>

        <!--牙周治疗知情同意书病历版-->
		<module id="DPC05" name="牙周治疗知情同意书病历版" type="1" script="phis.application.dpc.script.PeriodontalAgreementList">
        	<properties>
        		<p name="createCls">phis.application.dpc.script.PeriodontalAgreementForm</p>
        		<p name="updateCls">phis.application.dpc.script.PeriodontalAgreementForm</p>
        		<p name="entryName">phis.application.dpc.schemas.DPC_PeriodontalAgreement</p>
        		<p name="removeServiceId">phis.simpleRemove</p>
        	</properties>
        	<action id="create" name="新建" iconCls="create" ref="phis.application.dpc.DPC/DPC/DPC0501"/>
        	<action id="update" name="查看" ref="phis.application.dpc.DPC/DPC/DPC0501" />
        	<action id="remove" name="删除"/>
        	<action id="print" name="打印" />
        	<action id="refresh" name="刷新" />
        </module>
        <module id="DPC0501" type="1" name="牙周治疗知情同意书病历版表单" script="phis.application.dpc.script.PeriodontalAgreementForm" icon="default">
        	<properties>
        		<p name="entryName">phis.application.dpc.schemas.DPC_PeriodontalAgreement</p>
        		<p name="saveAction">savePeriodontalAgreement</p>
        		<p name="saveServiceId">phis.PeriodontalAgreementService</p>
        	</properties>
        	<action id="save" name="确定" />
        	<action id="cancel" name="取消" iconCls="common_cancel" />
        </module>

	</catagory>
</application>